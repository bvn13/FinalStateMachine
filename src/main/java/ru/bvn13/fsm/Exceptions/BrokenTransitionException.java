package ru.bvn13.fsm.Exceptions;

/**
 * Created by bvn13 on 28.12.2017.
 */
public class BrokenTransitionException extends FSMException {
    public BrokenTransitionException(String from) {
        super("Broken transition from: "+from);
    }
}
