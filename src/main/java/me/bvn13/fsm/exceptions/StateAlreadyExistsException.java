package me.bvn13.fsm.exceptions;

/**
 * is thrown when trying to add the state which was already added before
 */
public class StateAlreadyExistsException extends FsmException {
    public StateAlreadyExistsException(String stateName) {
        super(String.format("State exist: %s", stateName));
    }
}
