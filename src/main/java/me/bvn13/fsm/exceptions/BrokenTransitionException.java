package me.bvn13.fsm.exceptions;

/**
 * is thrown if there are no further states from the current one
 */
public class BrokenTransitionException extends FsmException {
    public BrokenTransitionException(String from) {
        super("Broken transition from: "+from);
    }
}
