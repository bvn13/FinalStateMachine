package ru.bvn13.fsm.Exceptions;

/**
 * Created by bvn13 on 28.12.2017.
 */
public class NotInitedException extends FSMException {
    public NotInitedException(String message) {
        super(message);
    }
    public NotInitedException() {
        super("FSM is not inited");
    }
}
