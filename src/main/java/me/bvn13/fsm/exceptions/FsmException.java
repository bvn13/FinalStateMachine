package me.bvn13.fsm.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Parent FSM exception class
 */
public class FsmException extends RuntimeException {
    protected String message;
    public FsmException(String message) {
        this.message = message;
    }
    protected String getStackTraceString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        this.printStackTrace(pw);
        return sw.toString();
    }
    public void printStackTrace() {
        System.out.println(String.format("FSMException: %s / %s", message, getStackTraceString()));
    }
}
