package ru.bvn13.fsm.examples;

import ru.bvn13.fsm.Condition;
import ru.bvn13.fsm.Exceptions.FSMException;
import ru.bvn13.fsm.Exceptions.NotInitedException;
import ru.bvn13.fsm.FSM;
import ru.bvn13.fsm.State;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by bvn13 on 28.12.2017.
 */
public class Application {

    public static class DialogFSM extends FSM {
        public String command;
        private BufferedReader br = null;

        public void readCommand() {
            System.out.print("Command: ");
            br = new BufferedReader(new InputStreamReader(System.in));
            try {
                command = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public static void main(String argc[]) {

        DialogFSM fsm = new DialogFSM();

        try {
            fsm.initState(new State("greeting") {
                @Override
                public void beforeEvent() {
                    System.out.println("Welcome!");
                }
                @Override
                public void process() {
                    fsm.readCommand();
                    try {
                        fsm.next();
                    } catch (FSMException e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                }
                @Override
                public void afterEvent() {
                    System.out.println("Your command: "+fsm.command);
                }
            });
        } catch (FSMException e) {
            e.printStackTrace();
        }

        try {
            fsm.addTransition("greeting", new State("hello", true) {
                @Override
                public void beforeEvent() {
                    System.out.println("Hello!");
                }
                @Override
                public void process() {

                }
                @Override
                public void afterEvent() {
                    System.out.println("DONE");
                }
            }, new Condition() {
                @Override
                public boolean check() {
                    return fsm.command.equalsIgnoreCase("hello");
                }
            });
        } catch (FSMException e) {
            e.printStackTrace();
        }


        try {
            fsm.addTransition("greeting", "greeting", new Condition() {
                @Override
                public boolean check() {
                    return !fsm.command.equalsIgnoreCase("hello");
                }
            });
        } catch (FSMException e) {
            e.printStackTrace();
        }


        try {
            fsm.init();
        } catch (NotInitedException e) {
            e.printStackTrace();
        }

    }

}
