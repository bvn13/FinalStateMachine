package me.bvn13.fsm;

import java.util.function.Supplier;

public class FsmBuilder<T extends Fsm, E> {

    private final T fsm;

    FsmBuilder(Supplier<T> supplier) {
        fsm = supplier.get();
    }

    public StateBuilder<T,E> from(String state) {
        return new StateBuilder<>(this, state, true, false);
    }

    public StateBuilder<T,E> state(String state) {
        return new StateBuilder<>(this, state, false, false);
    }

    public StateBuilder<T,E> finish(String state) {
        return new StateBuilder<>(this, state, false, true);
    }

    public ConditionBuilder<T,E> withTransition() {
        return new ConditionBuilder<>(this);
    }

    public T create() {
        fsm.init();
        return fsm;
    }

    public T startingAt(String name) {
        fsm.setCurrentState(name);
        return fsm;
    }

    T getFsm() {
        return fsm;
    }

    @SuppressWarnings("unchecked")
    void addState(State<E> state, boolean isInitial) {
        if (isInitial) {
            fsm.initState(state);
        } else {
            fsm.addState(state);
        }
    }

    @SuppressWarnings("unchecked")
    void addTransition(String from, String to, Condition<T,E> condition) {
        fsm.addTransition(from, to, condition);
    }

}
