package me.bvn13.fsm;

public class ConditionBuilder<T extends Fsm, E> {

    private final FsmBuilder<T,E> fsmBuilder;
    private final StateBuilder<T, E> stateBuilder;
    private String from;
    private String to;
    private Condition<T,E> condition;

    ConditionBuilder(FsmBuilder<T,E> fsmBuilder) {
        this.fsmBuilder = fsmBuilder;
        this.stateBuilder = null;
    }

    ConditionBuilder(FsmBuilder<T,E> fsmBuilder, StateBuilder<T, E> stateBuilder, String from) {
        this.fsmBuilder = fsmBuilder;
        this.stateBuilder = stateBuilder;
        this.from = from;
    }

    public ConditionBuilder<T,E> from(String from) {
        this.from = from;
        return this;
    }

    public ConditionBuilder<T,E> to(String to) {
        this.to = to;
        return this;
    }

    public ConditionBuilder<T,E> checking(Condition<T,E> condition) {
        this.condition = condition;
        return this;
    }

    public FsmBuilder<T,E> end() {
        fsmBuilder.addTransition(from, to, condition);
        return fsmBuilder;
    }

    public StateBuilder<T, E> endTransition() {
        if (stateBuilder == null) {
            throw new IllegalStateException("Use '.end()' instead");
        }
        end();
        return stateBuilder;
    }

}
