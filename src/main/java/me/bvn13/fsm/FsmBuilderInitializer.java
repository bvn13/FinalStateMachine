package me.bvn13.fsm;

import java.util.function.Supplier;

public class FsmBuilderInitializer<T extends Fsm, E> {

    final Supplier<T> supplier;

    FsmBuilderInitializer(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public FsmBuilder<T,E> withStates() {
        return new FsmBuilder<>(supplier);
    }

}
