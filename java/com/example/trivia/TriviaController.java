package com.example.trivia;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.List;

public class TriviaController {

    @FXML
    private Label questionLabel;

    @FXML
    private Label op1, op2, op3, op4;

    @FXML
    private RadioButton select1, select2, select3, select4;

    ToggleGroup tg = new ToggleGroup();

    @FXML
    private Label timerLabel, scoreLabel;

    @FXML
    private GridPane optionsPane;

    @FXML
    private Button sendButton, startButton;


    private TriviaClient client;

    public void initialize(){
        select1.setToggleGroup(tg);
        select2.setToggleGroup(tg);
        select3.setToggleGroup(tg);
        select4.setToggleGroup(tg);

        select1.setUserData(0);
        select2.setUserData(1);
        select3.setUserData(2);
        select4.setUserData(3);

        setDisableGameUi(true);
    }


    //the following functions are package-accessed and used by the client code
    void setDisableGameUi(boolean val){
        startButton.setDisable(!val);
        questionLabel.setText(val ? "Game has not started yet" : "");
        optionsPane.setDisable(val);
        sendButton.setDisable(val);
    }

    void setQuestionLabel(String text){
        questionLabel.setText(text);
    }

    void setNewQuestion(TriviaQuestion question){
        questionLabel.setText(question.getQuestion());

        List<String> options = question.getOptions();
        op1.setText(options.get(0));
        op2.setText(options.get(1));
        op3.setText(options.get(2));
        op4.setText(options.get(3));
    }

    void setTimerLabel(String text) {
        timerLabel.setText(text);
    }

    void setScoreLabel(String text) {
        scoreLabel.setText(text);
    }

    @FXML
    void onSendClicked(ActionEvent event) {
        Toggle selected = tg.getSelectedToggle();

        //defaulting to first option if no option was chosen
        int answer = selected == null ? 0 : (int)selected.getUserData();

        //passing control to the client code
        client.handleAnswered(answer);
    }

    @FXML
    void onStartClicked(ActionEvent event) {
        //starting client
        client = new TriviaClient(this, TriviaApplication.getHost(), TriviaServer.PORT, TriviaApplication.getDelay());
        new Thread(client).start();
    }
}
