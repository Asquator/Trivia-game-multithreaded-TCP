package com.example.trivia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

/*
    TriviaQuestion communicates question info to trivia players
 */
class TriviaQuestion implements Serializable {

    //number of answer options
    public static int N_OPTIONS = 4;

    private String question;

    //list of options
    private final ArrayList<String> options = new ArrayList<>();

    //correct answer position
    private final int correctPos;

    private static final SecureRandom rnd = new SecureRandom();


    /**
     * Constructs a trivia question
     * @param correctAnswer correct answer string
     * @param otherOptions array of incorrect options
     */
    public TriviaQuestion(String question, String correctAnswer, String... otherOptions){
        this.question = question;

        if(otherOptions.length != N_OPTIONS - 1)
            throw new RuntimeException("Invalid number of options");

        //adding incorrect options
        options.addAll(Arrays.asList(otherOptions));

        //inserting the correct answer at a random position
        correctPos = rnd.nextInt(N_OPTIONS);
        options.add(correctPos, correctAnswer);
    }

    public String getQuestion() {
        return question;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public int getCorrect() {
        return correctPos;
    };

    private static TriviaQuestion readNextQuestion(BufferedReader reader) throws IOException {
        String[] otherOptions = new String[TriviaQuestion.N_OPTIONS - 1];
        String line = reader.readLine();

        //slip blank lines
        while(line.equals(""))
            line = reader.readLine();

        //read question and answer
        String question = line;
        String answer = reader.readLine();

        //read other options
        line = reader.readLine();
        for(int i = 0; i < TriviaQuestion.N_OPTIONS - 1; i++){
            otherOptions[i] = line;
            line = reader.readLine();
        }

        return new TriviaQuestion(question, answer, otherOptions);
    }
}
