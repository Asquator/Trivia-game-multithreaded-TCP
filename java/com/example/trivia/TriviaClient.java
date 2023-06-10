package com.example.trivia;

import java.awt.event.ActionEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import javax.swing.Timer;

import static javafx.application.Platform.runLater;


public class TriviaClient implements Runnable {
    private String host;
    private int port;
    private Socket sc;

    private ObjectInputStream is;
    private DataOutputStream os;

    private final TriviaController uiController;

    private int score = 0;

    private TriviaQuestion currentQuestion;
    private static final int CORRECT_POINTS = 10;
    private static final int WRONG_POINTS = -5;


    //timer to update score and handle timeout
    private class QuestionTimer extends Timer {
        int delay;
        int timeLeft;

        //counts down and updates the score
        private void updateCountdown(ActionEvent event) {
            runLater(() -> uiController.setTimerLabel(String.valueOf(timeLeft)));
            timeLeft--;
            if (timeLeft < 0) {
                ((Timer) event.getSource()).stop();
                uiController.onSendClicked(new javafx.event.ActionEvent());
            }
        }

        public QuestionTimer(int seconds) {
            //setting time delay to one second
            super(1000, null);

            setInitialDelay(0); //start from update actions

            //setting action to update the score
            addActionListener(this::updateCountdown);

            this.delay = seconds;
            timeLeft = delay;
        }

        //resets the timer
        public void reset(){
            timeLeft = delay;
        }
    }

    //timer instance
    private final QuestionTimer timer;

    /**
     * Constructs a client
     * @param controller GUI controller
     * @param host server host
     * @param port server port
     * @param delay allowed time for answering
     */
    public TriviaClient(TriviaController controller, String host, int port, int delay) {
        this.uiController = controller;
        this.host = host;
        this.port = port;
        timer = new QuestionTimer(delay);
    }

    /**
     * run the client connection
     */
    @Override
    public void run() {
        //prepare the GUI
        runLater(() -> {uiController.setQuestionLabel("Connecting to the server");
            uiController.setScoreLabel(String.valueOf(0));
        });

        //try to connect until succeeded
        while (true) {
            try {
                sc = new Socket(host, port);
                break;
            } catch (Exception ignored) {}
        }

        try {
            //acquire streams
            os = new DataOutputStream(sc.getOutputStream());
            is = new ObjectInputStream(sc.getInputStream());

            uiController.setDisableGameUi(false);

            currentQuestion = (TriviaQuestion) is.readObject();

            //settings questions until exhausted
            while (currentQuestion != null) {
                handleQuestion();

                currentQuestion = (TriviaQuestion) is.readObject();
            }

        }

        catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Invalid object received from server");
            System.exit(1);
        }

        finally {
            terminate();
        }
    }

    //terminate this client: prepare the GUI and release resources
    private void terminate(){
        try {
            uiController.setDisableGameUi(true);
            os.close();
            is.close();
            sc.close();
        } catch (IOException ignored) {
        }
    }

    //handle question: start timer and update GUI
    private void handleQuestion() {
        timer.start();
        runLater(() -> uiController.setNewQuestion(currentQuestion));
    }

    //handle answered question
    void handleAnswered(int answer) {
        timer.stop();
        timer.reset();
        runLater(() -> uiController.setTimerLabel(""));

        //update points
        if (answer == currentQuestion.getCorrect())
            score += CORRECT_POINTS;

        else
            score += WRONG_POINTS;

        runLater(() -> uiController.setScoreLabel(String.valueOf(score)));

        try {
            os.writeInt(answer);
        } catch (IOException ex) {
            System.err.println("Couldn't send answer to the server");
            System.exit(1);
        }
    }
}
