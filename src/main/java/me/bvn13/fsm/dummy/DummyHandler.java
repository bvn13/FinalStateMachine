package me.bvn13.fsm.dummy;

import me.bvn13.fsm.Fsm;
import me.bvn13.fsm.StateHandler;

public class DummyHandler<T extends Fsm> implements StateHandler<T> {
    @Override
    public void handle(T fsm) {

    }
}
