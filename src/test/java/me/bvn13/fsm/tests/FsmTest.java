package me.bvn13.fsm.tests;

import me.bvn13.fsm.ConditionBuilder;
import me.bvn13.fsm.Fsm;
import me.bvn13.fsm.FsmBuilder;
import me.bvn13.fsm.SimpleFsm;
import me.bvn13.fsm.State;
import me.bvn13.fsm.StateBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bvn13 on 28.12.2017.
 */
public class FsmTest {

    public static class NamedFsm extends Fsm<NamedFsm, String> {

        public NamedFsm() {
            super();
        }

        private String name;

        public NamedFsm setName(String name) {
            this.name = name;
            return this;
        }

        public String toString() {
            return this.name;
        }
    }


    @Test
    public void creatingFSM() {

        NamedFsm namedFsm = (new NamedFsm()).setName("TEST FSM");

        AtomicBoolean initStatedProcessed = new AtomicBoolean(false);
        AtomicBoolean firstStatedProcessed = new AtomicBoolean(false);
        AtomicBoolean anotherStatedProcessed = new AtomicBoolean(false);

        namedFsm.initState(new State<String>("init") {
            @Override
            public void process(String event) {
                initStatedProcessed.set(true);
            }
        });

        namedFsm.addTransition("init", new State<String>("first", true) {
            @Override
            public void process(String event) {
                firstStatedProcessed.set(true);
            }
        });

        namedFsm.addTransition("init", new State<String>("another", true) {
            @Override
            public void process(String event) {
                anotherStatedProcessed.set(true);
            }
        }, (fsm, event) -> false);

        namedFsm.init();
        namedFsm.process(null);

        Assert.assertEquals("first", namedFsm.getCurrentState().getName());
        Assert.assertTrue(initStatedProcessed.get());
        Assert.assertTrue(firstStatedProcessed.get());
        Assert.assertFalse(anotherStatedProcessed.get());
    }

    @Test
    public void newSyntax() {

        AtomicBoolean initBefore = new AtomicBoolean(false);
        AtomicBoolean initAfter = new AtomicBoolean(false);
        AtomicBoolean initProcess = new AtomicBoolean(false);
        AtomicBoolean finishBefore = new AtomicBoolean(false);
        AtomicBoolean finishAfter = new AtomicBoolean(false);
        AtomicBoolean finishProcess = new AtomicBoolean(false);

        // @formatter:off

        SimpleFsm<String> simpleFsm = SimpleFsm
                .<SimpleFsm<String>, String>withStates(SimpleFsm::new)
                .from("init")
                    .withBeforeHandler(fsm -> initBefore.set(true))
                    .withAfterHandler(fsm -> initAfter.set(true))
                    .withProcessor((fsm, event) -> initProcess.set(true))
                .end()
                .finish("finish")
                    .withBeforeHandler(fsm -> finishBefore.set(true))
                    .withAfterHandler(fsm -> finishAfter.set(true))
                    .withProcessor((fsm, event) -> finishProcess.set(true))
                .end()
                .withTransition()
                    .from("init")
                    .to("finish")
                    .checking((fsm, event) -> true)
                .end()
                .create()
                ;

        // @formatter:on

        simpleFsm.process("");

        Assert.assertEquals("finish", simpleFsm.getCurrentState().getName());
        Assert.assertTrue(initBefore.get());
        Assert.assertTrue(initAfter.get());
        Assert.assertFalse(initProcess.get());
        Assert.assertTrue(finishBefore.get());
        Assert.assertFalse(finishAfter.get());
        Assert.assertTrue(finishProcess.get());

    }

}