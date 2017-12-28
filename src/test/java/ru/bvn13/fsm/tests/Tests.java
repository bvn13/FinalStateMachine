package ru.bvn13.fsm.tests;

import org.junit.Assert;
import org.junit.Test;
import ru.bvn13.fsm.Exceptions.FSMException;
import ru.bvn13.fsm.Exceptions.NotInitedException;
import ru.bvn13.fsm.FSM;
import ru.bvn13.fsm.State;

/**
 * Created by bvn13 on 28.12.2017.
 */
public class Tests {

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
        Assert.assertNotNull(fsm);

        try {
            fsm.initState(new State("init") {
                @Override
                public void process() {
                    System.out.println("" + fsm + " -> " + getName() + ": processed init");
                    try {
                        fsm.next();
                    } catch (FSMException e) {
                        e.printStackTrace();
                        Assert.fail();
                    }
                }
            });
        } catch (FSMException e) {
            e.printStackTrace();
            Assert.fail();
        }

        try {
            fsm.addTransition("init", new State("first", true) {
                @Override
                public void process() {
                    System.out.println("" + fsm + " -> " + getName() + ": processed first");
                    try {
                        fsm.next();
                    } catch (FSMException e) {
                        e.printStackTrace();
                        Assert.fail();
                    }
                }
            });
        } catch (FSMException e) {
            e.printStackTrace();
            Assert.fail();
        }

        try {
            fsm.init();
        } catch (NotInitedException e) {
            e.printStackTrace();
            Assert.fail();
        }

        Assert.assertTrue(fsm.getCurrentState().getName().equals("first"));

    }

}
