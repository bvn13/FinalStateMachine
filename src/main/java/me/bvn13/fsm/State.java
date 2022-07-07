package me.bvn13.fsm;

/**
 * Created by bvn13 on 28.12.2017.
 */
public class State<E> implements StateBehaviour<E> {

    private final String name;
    private boolean finish;

    private Fsm fsm;
    protected void setFSM(Fsm fsm) {
        this.fsm = fsm;
    }
    public Fsm getFSM() {
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

    public String toString() {
        return name;
    }

}
