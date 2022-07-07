package me.bvn13.fsm.tests.examples;

import me.bvn13.fsm.Fsm;
import me.bvn13.fsm.State;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by bvn13 on 28.12.2017.
 */
public class DialogApplication {

    public static class DialogFSM extends Fsm<DialogFSM, String> {
        public String command;

        public void readCommand() {
            System.out.print("Command: ");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                command = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public static void main(String[] argv) {

        DialogFSM fsm = new DialogFSM();

        fsm.initState(new State<String>("greeting") {
            @Override
            public void beforeEvent() {
                System.out.println("Welcome!");
            }

            @Override
            public void process(String event) {
                fsm.readCommand();
                fsm.process(event);
            }

            @Override
            public void afterEvent() {
                System.out.println("Your command: " + fsm.command);
            }
        });

        fsm.addTransition("greeting", new State<String>("hello", true) {
            @Override
            public void beforeEvent() {
                System.out.println("Hello!");
            }

            @Override
            public void process(String event) {

            }

            @Override
            public void afterEvent() {
                System.out.println("DONE");
            }
        }, (fsm12, event) -> fsm12.command.equalsIgnoreCase("hello"));


        fsm.addTransition("greeting", "greeting", (fsm1, event) -> !fsm1.command.equalsIgnoreCase("hello"));


        fsm.init();

    }

}
