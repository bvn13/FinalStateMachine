package ru.bvn13.fsm.Exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by bvn13 on 28.12.2017.
 */
public class FSMException extends Exception {
    protected String message;
    public FSMException(String message) {
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
