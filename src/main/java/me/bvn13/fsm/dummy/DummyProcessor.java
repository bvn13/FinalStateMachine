package me.bvn13.fsm.dummy;

import me.bvn13.fsm.Fsm;
import me.bvn13.fsm.StateProcessor;

public class DummyProcessor<T extends Fsm,E> implements StateProcessor<T,E> {
    @Override
    public void process(T fms, E event) {

    }
}
