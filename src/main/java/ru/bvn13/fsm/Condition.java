package ru.bvn13.fsm;

/**
 * Created by bvn13 on 28.12.2017.
 */
public class Condition implements ConditionBehaviour {

    private FSM fsm;
    protected void setFSM(FSM fsm) {
        this.fsm = fsm;
    }
    public FSM getFSM() {
        return this.fsm;
    }

    @Override
    public boolean check() {
        return false;
    }
}
