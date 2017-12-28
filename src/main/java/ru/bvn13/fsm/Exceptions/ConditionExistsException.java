package ru.bvn13.fsm.Exceptions;

/**
 * Created by bvn13 on 28.12.2017.
 */
public class ConditionExistsException extends FSMException {
    public ConditionExistsException(String from, String to) {
        super(String.format("Condition exists: %s -> %s", from, to));
    }
}
