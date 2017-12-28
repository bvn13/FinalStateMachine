package ru.bvn13.fsm.Exceptions;

/**
 * Created by bvn13 on 28.12.2017.
 */
public class StateExistsException extends FSMException {
    public StateExistsException(String stateName) {
        super(String.format("State exist: %s", stateName));
    }
}
