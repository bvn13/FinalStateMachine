package me.bvn13.fsm.exceptions;

/**
 * is thrown if there are no registered transitions from current state
 */
public class TransitionMissedException extends FsmException {
    public TransitionMissedException(String from) {
        super(String.format("Missed conditions from: %s", from));
    }
}
