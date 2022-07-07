package me.bvn13.fsm.exceptions;

/**
 * is thrown in case of using FSM before being initialized
 */
public class NotInitializedException extends FsmException {
    public NotInitializedException(String message) {
        super(message);
    }
    public NotInitializedException() {
        super("FSM is not inited");
    }
}
