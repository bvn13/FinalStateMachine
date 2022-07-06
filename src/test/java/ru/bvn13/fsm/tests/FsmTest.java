package ru.bvn13.fsm.tests;

import org.junit.Assert;
import org.junit.Test;
import ru.bvn13.fsm.Condition;
import ru.bvn13.fsm.FSM;
import ru.bvn13.fsm.State;

/**
 * Created by bvn13 on 28.12.2017.
 */
public class FsmTest {

    public static class NamedFSM extends FSM {

        public NamedFSM() {
            super();
        }

        private String name;

        public NamedFSM setName(String name) {
            this.name = name;
            return this;
        }

        public String toString() {
            return this.name;
        }
    }


    @Test
    public void creatingFSM() {

        NamedFSM fsm = (new NamedFSM()).setName("TEST FSM");

        fsm.initState(new State("init") {
            @Override
            public void process() {
                System.out.println("" + fsm + " -> " + getName() + ": processed init");
            }
        });

        fsm.addTransition("init", new State("first", true) {
            @Override
            public void process() {
                System.out.println("" + fsm + " -> " + getName() + ": processed first");
            }
        });

        fsm.addTransition("init", new State("another", true) {
            @Override
            public void process() {
                System.out.println("" + fsm + " -> " + getName() + ": processed first");
            }
        }, new Condition() {
            @Override
            public boolean check() {
                return false;
            }
        });

        fsm.init();
        fsm.next();

        Assert.assertEquals("first", fsm.getCurrentState().getName());

    }

}
