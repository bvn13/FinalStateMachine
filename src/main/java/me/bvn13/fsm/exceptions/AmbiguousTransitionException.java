package me.bvn13.fsm.exceptions;

import java.util.List;

import static java.lang.String.format;

/**
 * is thrown if there are more than 1 appropriate transition from current state
 */
public class AmbiguousTransitionException extends FsmException {
    public AmbiguousTransitionException(String message) {
        super(message);
    }
    public AmbiguousTransitionException(String from, List<String> next) {
        super(format("Ambiguous transition from state %s. Candidates are: %s", from, join(next)));
    }

    private static String join(List<String> list) {
        StringBuilder msg = new StringBuilder();
        for (String to : list) {
            msg.append(msg.length() > 0 ? ", " : "").append(to);
        }
        return msg.toString();
    }
}
