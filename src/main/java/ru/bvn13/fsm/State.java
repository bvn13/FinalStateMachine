package ru.bvn13.fsm;

/**
 * Created by bvn13 on 28.12.2017.
 */
public class State implements StateBehaviour {

    private String name;
    private boolean finish;

    private FSM fsm;
    protected void setFSM(FSM fsm) {
        this.fsm = fsm;
    }
    public FSM getFSM() {
        return this.fsm;
    }

    public State(String name) {
        this.name = name;
    }
    public State(String name, boolean finish) {
        this.name = name;
        this.finish = finish;
    }

    public String getName() {
        return this.name;
    }

    public boolean isFinish() {
        return finish;
    }

}
