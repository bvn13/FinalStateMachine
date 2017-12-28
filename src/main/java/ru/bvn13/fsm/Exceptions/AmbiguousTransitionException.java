package ru.bvn13.fsm.Exceptions;

import java.util.List;

/**
 * Created by bvn13 on 28.12.2017.
 */
public class AmbiguousTransitionException extends FSMException {
    public AmbiguousTransitionException(String message) {
        super(message);
    }
    public AmbiguousTransitionException(String from, List<String> next) {
        super("");
        String msg = "";
        for (String to : next) {
            msg += (msg.length() > 0 ? ", " : "") + to;
        }
        this.message = String.format("Ambiguous transition from state %s. Candidates are: %s", from, msg);
    }
}
