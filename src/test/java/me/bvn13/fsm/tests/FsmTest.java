package me.bvn13.fsm.tests;

import me.bvn13.fsm.Fsm;
import me.bvn13.fsm.SimpleFsm;
import me.bvn13.fsm.State;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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

        SimpleFsm<String> simpleFsm = Fsm
                .<SimpleFsm<String>, String>from(SimpleFsm::new)
                .withStates()
                .from("init")
                    .withBeforeHandler(fsm -> initBefore.set(true))
                    .withAfterHandler(fsm -> initAfter.set(true))
                    .withProcessor((fsm, event) -> initProcess.set(true))
                .end()
                .state("intermediate")
                .end()
                .finish("finish")
                    .withBeforeHandler(fsm -> finishBefore.set(true))
                    .withAfterHandler(fsm -> finishAfter.set(true))
                    .withProcessor((fsm, event) -> finishProcess.set(true))
                .end()
                .withTransition()
                    .from("init")
                    .to("intermediate")
                    .checking((fsm, event) -> true)
                .end()
                .withTransition()
                    .from("intermediate")
                    .to("finish")
                    .checking((fsm, event) -> true)
                .end()
                .create();

        // @formatter:on
        simpleFsm.process("");
        simpleFsm.process("");

        Assert.assertEquals("finish", simpleFsm.getCurrentState().getName());
        Assert.assertTrue(initBefore.get());
        Assert.assertTrue(initProcess.get());
        Assert.assertTrue(initAfter.get());
        Assert.assertTrue(finishBefore.get());
        Assert.assertFalse(finishProcess.get());
        Assert.assertFalse(finishAfter.get());

    }

    @Test
    public void newSyntaxCustomState() {

        AtomicBoolean initBefore = new AtomicBoolean(false);
        AtomicBoolean initAfter = new AtomicBoolean(false);
        AtomicBoolean initProcess = new AtomicBoolean(false);
        AtomicBoolean intermediateBefore = new AtomicBoolean(false);
        AtomicBoolean intermediateAfter = new AtomicBoolean(false);
        AtomicBoolean intermediateProcess = new AtomicBoolean(false);
        AtomicBoolean finishBefore = new AtomicBoolean(false);
        AtomicBoolean finishAfter = new AtomicBoolean(false);
        AtomicBoolean finishProcess = new AtomicBoolean(false);

        // @formatter:off

        SimpleFsm<String> simpleFsm = Fsm
                .<SimpleFsm<String>, String>from(SimpleFsm::new)
                .withStates()
                .from("init")
                    .withBeforeHandler(fsm -> initBefore.set(true))
                    .withAfterHandler(fsm -> initAfter.set(true))
                    .withProcessor((fsm, event) -> initProcess.set(true))
                .end()
                .state("intermediate")
                    .withBeforeHandler(fsm -> intermediateBefore.set(true))
                    .withAfterHandler(fsm -> intermediateAfter.set(true))
                    .withProcessor((fsm, event) -> intermediateProcess.set(true))
                .end()
                .finish("finish")
                    .withBeforeHandler(fsm -> finishBefore.set(true))
                    .withAfterHandler(fsm -> finishAfter.set(true))
                    .withProcessor((fsm, event) -> finishProcess.set(true))
                .end()
                .withTransition()
                    .from("init")
                    .to("intermediate")
                    .checking((fsm, event) -> true)
                .end()
                .withTransition()
                    .from("intermediate")
                    .to("finish")
                    .checking((fsm, event) -> true)
                .end()
                .startingAt("intermediate")
                ;

        // @formatter:on

        simpleFsm.process("");

        Assert.assertEquals("finish", simpleFsm.getCurrentState().getName());
        Assert.assertFalse(initBefore.get());
        Assert.assertFalse(initProcess.get());
        Assert.assertFalse(initAfter.get());
        Assert.assertFalse(intermediateBefore.get());
        Assert.assertTrue(intermediateAfter.get());
        Assert.assertTrue(intermediateProcess.get());
        Assert.assertTrue(finishBefore.get());
        Assert.assertFalse(finishProcess.get());
        Assert.assertFalse(finishAfter.get());

    }

    @Test
    public void newSyntaxCountingEveryStep() {

        Counter initCounter = new Counter();
        Counter firstIntermediateCounter = new Counter();
        Counter secondIntermediateCounter = new Counter();
        Counter finishCounter = new Counter();

        // @formatter:off

        SimpleFsm<String> simpleFsm = Fsm
                .<SimpleFsm<String>, String>from(SimpleFsm::new)
                .withStates()
                    .from("init")
                        .withBeforeHandler(fsm -> initCounter.before.incrementAndGet())
                        .withAfterHandler(fsm -> initCounter.after.incrementAndGet())
                        .withProcessor((fsm, event) -> initCounter.process.incrementAndGet())
                    .end()
                    .state("intermediate-1")
                        .withBeforeHandler(fsm -> firstIntermediateCounter.before.incrementAndGet())
                        .withAfterHandler(fsm -> firstIntermediateCounter.after.incrementAndGet())
                        .withProcessor((fsm, event) -> firstIntermediateCounter.process.incrementAndGet())
                    .end()
                    .state("intermediate-2")
                        .withBeforeHandler(fsm -> secondIntermediateCounter.before.incrementAndGet())
                        .withAfterHandler(fsm -> secondIntermediateCounter.after.incrementAndGet())
                        .withProcessor((fsm, event) -> secondIntermediateCounter.process.incrementAndGet())
                    .end()
                    .finish("finish")
                        .withBeforeHandler(fsm -> finishCounter.before.incrementAndGet())
                        .withAfterHandler(fsm -> finishCounter.after.incrementAndGet())
                        .withProcessor((fsm, event) -> finishCounter.process.incrementAndGet())
                    .end()
                .withTransition()
                    .from("init")
                    .to("intermediate-1")
                    .checking((fsm, event) -> true)
                .end()
                .withTransition()
                    .from("intermediate-1")
                    .to("intermediate-2")
                    .checking((fsm, event) -> true)
                .end()
                .withTransition()
                    .from("intermediate-2")
                    .to("finish")
                    .checking((fsm, event) -> true)
                .end()
                .create()
                ;

        // @formatter:on

        simpleFsm.process("");
        simpleFsm.process("");
        simpleFsm.process("");

        Assert.assertEquals("finish", simpleFsm.getCurrentState().getName());
        Assert.assertEquals(1, initCounter.before.get());
        Assert.assertEquals(1, initCounter.after.get());
        Assert.assertEquals(1, initCounter.process.get());
        Assert.assertEquals(1, firstIntermediateCounter.before.get());
        Assert.assertEquals(1, firstIntermediateCounter.after.get());
        Assert.assertEquals(1, firstIntermediateCounter.process.get());
        Assert.assertEquals(1, secondIntermediateCounter.before.get());
        Assert.assertEquals(1, secondIntermediateCounter.after.get());
        Assert.assertEquals(1, secondIntermediateCounter.process.get());
        Assert.assertEquals(1, finishCounter.before.get());
        Assert.assertEquals(0, finishCounter.after.get());
        Assert.assertEquals(0, finishCounter.process.get());

    }

    static class Counter {
        final AtomicInteger before = new AtomicInteger(0);
        final AtomicInteger after = new AtomicInteger(0);
        final AtomicInteger process = new AtomicInteger(0);
    }

}
