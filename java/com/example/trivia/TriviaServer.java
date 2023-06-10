package com.example.trivia;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TriviaServer {
    public static int PORT = 3333;

    private static final String QUESTIONS_FILE = "questions.txt";

    private final ExecutorService pool = Executors.newCachedThreadPool();

    private ServerSocket sc;

    /**
     * Constructs a trivia game server
     */
    public TriviaServer() {
        try {
            sc = new ServerSocket(PORT);
            Socket playerSocket;

            //accepting connections and submitting to the pool
            while (true) {
                playerSocket = sc.accept();
                pool.submit(new Player(playerSocket));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Class to manage a player connection
     */
    public class Player implements Runnable {
        private final Socket socket;
        private DataInputStream is;
        private ObjectOutputStream os;
        private final int N_QUESTIONS = 20;
        private int counter = 0;
        private Queue<TriviaQuestion> questionStock;

        /**
         * Constructs a server thread for the given player
         *
         * @param socket player socket
         */
        public Player(Socket socket) {
            this.socket = socket;

            try {
                prepareQuestions();

                //acquire streams
                is = new DataInputStream(socket.getInputStream());
                os = new ObjectOutputStream(socket.getOutputStream());
                os.flush();

            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }

        /**
         * Maintains connection with a client, sends questions and waits for responses
         */
        @Override
        public void run() {
            try {
                //send questions until exhausted
                while (!questionStock.isEmpty()) {
                    sendQuestion();
                    waitForNext();
                }

                //terminate by sending null
                os.writeObject(null);
            } catch (Exception ignored) {}

            finally {
                try {
                    os.close();
                    is.close();
                } catch (IOException ignored) {}
            }

        }

        private void sendQuestion() throws IOException {
            //send next question to the client
            os.writeObject(questionStock.poll());
        }


        private void waitForNext() throws IOException {
            //in this implementation, simply get the answer and ignore it
            is.readInt();
        }

        //auxiliary method to prepare questions for the player
        private void prepareQuestions() throws IOException {

            //opening questions file associated with the server
            InputStream qStream = TriviaServer.class.getResourceAsStream(QUESTIONS_FILE);
            if (qStream == null)
                throw new IOException("Couldn't open questions file");

            //parse the file and shuffle the questions
            LinkedList<TriviaQuestion> questions = TriviaIO.parseQuestions(qStream, N_QUESTIONS);
            Collections.shuffle(questions);

            questionStock = questions;
        }
    }

    public static void main(String[] args) {
        TriviaServer d = new TriviaServer();
    }
}
