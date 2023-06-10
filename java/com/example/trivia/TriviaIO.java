package com.example.trivia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

//IO file used by server to read input file and construct trivia questions
public class TriviaIO {

    /**
     * Read the given stream and construct a list of questions
     * @param is stream
     * @param max max questions to parse
     * @return list of questions
     * @throws IOException if IO error occurred / the file has a wrong format
     */
    public static LinkedList<TriviaQuestion> parseQuestions(InputStream is, int max) throws IOException {
        LinkedList<TriviaQuestion> questions = new LinkedList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        int cnt = 0;

        //read questions and add to the list
        TriviaQuestion q = readNextQuestion(reader);
        while(q != null && cnt < max){
            questions.add(q);
            q = readNextQuestion(reader);
            cnt++;
        }

        return questions;
    }

    //reads next question from the given stream
    private static TriviaQuestion readNextQuestion(BufferedReader reader) throws IOException {
        String[] otherOptions = new String[TriviaQuestion.N_OPTIONS - 1];
        String line = reader.readLine();

        //slip blank lines
        while(line!= null && line.equals(""))
            line = reader.readLine();

        //end of stream encountered
        if(line == null)
            return null;

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
