package me.bvn13.fsm.exceptions;

/**
 * is thrown in case of adding a transition FROM->TO, but it is already defined
 */
public class ConditionAlreadyExistsException extends FsmException {
    public ConditionAlreadyExistsException(String from, String to) {
        super(String.format("Condition exists: %s -> %s", from, to));
    }
}
