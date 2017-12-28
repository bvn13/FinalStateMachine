package ru.bvn13.fsm.Exceptions;

/**
 * Created by bvn13 on 28.12.2017.
 */
public class TransitionMissedException extends FSMException {
    public TransitionMissedException(String from) {
        super(String.format("Missed conditions from: %s", from));
    }
}
