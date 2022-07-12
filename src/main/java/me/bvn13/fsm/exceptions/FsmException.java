package me.bvn13.fsm.exceptions;

/**
 * Parent FSM exception class
 */
public class FsmException extends RuntimeException {
    public FsmException(String message) {
        super(message);
    }

    public FsmException(String message, Throwable cause) {
        super(message, cause);
    }
}
