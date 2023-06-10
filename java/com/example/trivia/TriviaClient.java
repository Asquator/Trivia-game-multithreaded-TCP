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

    private class QuestionTimer extends Timer {
        int delay;
        int timeLeft;

        private void updateScore(ActionEvent event) {
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
            addActionListener(this::updateScore);

            this.delay = seconds;
            timeLeft = delay;
        }

        public void reset(){
            timeLeft = delay;
        }
    }

    private final QuestionTimer timer;

    public TriviaClient(TriviaController controller, String host, int port, int delay) {
        this.uiController = controller;
        this.host = host;
        this.port = port;
        timer = new QuestionTimer(delay);
    }

    @Override
    public void run() {
        //try to connect until succeeded
        runLater(() -> {uiController.setQuestionLabel("Connecting to the server");
            uiController.setScoreLabel(String.valueOf(0));
        });


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

    private void terminate(){
        try {
            uiController.setDisableGameUi(true);
            os.close();
            is.close();
            sc.close();
        } catch (IOException ignored) {
        }
    }

    private void handleQuestion() {
        timer.start();
        runLater(() -> uiController.setNewQuestion(currentQuestion));
    }

    void handleAnswered(int answer) {
        timer.stop();
        timer.reset();
        runLater(() -> uiController.setTimerLabel(""));

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
