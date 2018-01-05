package ru.bvn13.fsm;

import ru.bvn13.fsm.Exceptions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bvn13 on 28.12.2017.
 */
public class FSM {

    private boolean done = false;
    private State initialState;
    private State currentState;
    private State previousState;
    private Map<String, State> states;
    private Map<String, Map<String, ConditionBehaviour>> transitions;

    public FSM() {
        this.states = new HashMap<>();
        this.transitions = new HashMap<>();
    }

    private void checkStateExist(String name) throws StateExistsException {
        if (this.states.containsKey(name)) {
            throw new StateExistsException(name);
        }
    }

    public void init() throws NotInitedException {
        currentState = initialState;
        if (currentState == null) {
            throw new NotInitedException();
        }
        done = false;
        previousState = null;
        currentState.beforeEvent();
        currentState.process();
    }

    public State getCurrentState() {
        return currentState;
    }

    public State getPreviousState() {
        return previousState;
    }

    public void next() throws FSMException {
        if (done) {
            return;
        }
        currentState.afterEvent();
        if (currentState.isFinish()) {
            done = true;
            return;
        }
        if (!transitions.containsKey(currentState.getName())) {
            throw new TransitionMissedException(currentState.getName());
        }
        Map<String, ConditionBehaviour> conditions = transitions.get(currentState.getName());
        List<String> nextStates = new ArrayList<>();
        for (String key : conditions.keySet()) {
            if (conditions.get(key) == null) {
                nextStates.add(key);
            } else if(conditions.get(key).check()) {
                nextStates.add(key);
            }
        }
        if (nextStates.size() > 1) {
            throw new AmbiguousTransitionException(currentState.getName(), nextStates);
        }
        if (nextStates.size() == 0) {
            throw new BrokenTransitionException(currentState.getName());
        }
        State nextState = states.get(nextStates.get(0));
        nextState(nextState);
    }

    private void nextState(State state) {
        state.beforeEvent();
        previousState = currentState;
        currentState = state;
        currentState.process();
    }

    public void initState(State state) throws FSMException {
        state.setFSM(this);
        addState(state);
        initialState = state;
    }

    public void addState(State state) throws FSMException {
        checkStateExist(state.getName());
        state.setFSM(this);
        this.states.put(state.getName(), state);
    }

    public void addTransition(String fromState, String toState) throws FSMException {
        storeTransition(fromState, toState, null);
    }

    public void addTransition(String fromState, String toState, Condition condition) throws FSMException {
        condition.setFSM(this);
        storeTransition(fromState, toState, condition);
    }

    public void addTransition(String fromState, State toState) throws FSMException {
        addState(toState);
        addTransition(fromState, toState.getName());
    }

    public void addTransition(String fromState, State toState, Condition condition) throws FSMException {
        addState(toState);
        addTransition(fromState, toState.getName(), condition);
    }

    private void storeTransition(String fromState, String toState, Condition condition) throws FSMException {
        if (!transitions.containsKey(fromState)) {
            transitions.put(fromState, new HashMap<>());
        }
        if (transitions.get(fromState).containsKey(toState)) {
            throw new ConditionExistsException(fromState, toState);
        }
        transitions.get(fromState).put(toState, condition);
    }

}
