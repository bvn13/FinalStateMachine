package me.bvn13.fsm.exceptions;

import java.util.List;

/**
 * is thrown if there are more than 1 appropriate transition from current state
 */
public class AmbiguousTransitionException extends FsmException {
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
