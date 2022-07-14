package me.bvn13.fsm;

import me.bvn13.fsm.dummy.DummyHandler;
import me.bvn13.fsm.dummy.DummyProcessor;

public class StateBuilder<T extends Fsm, E> {

    private final FsmBuilder<T,E> fsmBuilder;
    private final String name;
    private final boolean isInitial;
    private final boolean isFinishing;
    private StateHandler<T> beforeHandler = new DummyHandler<>();
    private StateHandler<T> afterHandler = new DummyHandler<>();
    private StateProcessor<T,E> processor = new DummyProcessor<>();

    StateBuilder(FsmBuilder<T,E> fsmBuilder, String name, boolean isInitial, boolean isFinishing) {
        assert !(isInitial && isFinishing);
        this.fsmBuilder = fsmBuilder;
        this.name = name;
        this.isInitial = isInitial;
        this.isFinishing = isFinishing;
    }

    public FsmBuilder<T,E> end() {
        fsmBuilder.addState(new State<E>(name, isFinishing) {
            @Override
            public void beforeEvent() {
                beforeHandler.handle(fsmBuilder.getFsm());
            }

            @Override
            public void afterEvent() {
                afterHandler.handle(fsmBuilder.getFsm());
            }

            @Override
            public void process(E event) {
                processor.process(fsmBuilder.getFsm(), event);
            }
        }, isInitial);
        return fsmBuilder;
    }

    public StateBuilder<T,E> withBeforeHandler(StateHandler<T> handler) {
        this.beforeHandler = handler;
        return this;
    }

    public StateBuilder<T,E> withAfterHandler(StateHandler<T> handler) {
        this.afterHandler = handler;
        return this;
    }

    public StateBuilder<T,E> withProcessor(StateProcessor<T,E> processor) {
        this.processor = processor;
        return this;
    }

    public ConditionBuilder<T,E> withTransition() {
        return new ConditionBuilder<>(fsmBuilder, this, name);
    }

}
