package me.bvn13.fsm;

import me.bvn13.fsm.exceptions.AmbiguousTransitionException;
import me.bvn13.fsm.exceptions.BrokenTransitionException;
import me.bvn13.fsm.exceptions.ConditionAlreadyExistsException;
import me.bvn13.fsm.exceptions.FsmException;
import me.bvn13.fsm.exceptions.NotInitializedException;
import me.bvn13.fsm.exceptions.StateAlreadyExistsException;
import me.bvn13.fsm.exceptions.TransitionMissedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * <p>
 *  <b>Final State Machine</b><br/>
 * </p>
 * <p>
 *  <ol>
 *      Each state machine must be prepared with:
 *      <li>Initial state</li>
 *      <li>Finish state - may be not several states</li>
 *      <li>Intermediate states - optionally</li>
 *      <li>All transitions needed</li>
 *  </ol>
 * </p>
 *
 * <p>
 *  <ol>
 *      Each {@link State} may be specified with handlers:
 *      <li>Before handler - is called right before FSM changes INTO this state</li>
 *      <li>After handler - is called right before FSM changes FROM this state to another</li>
 *      <li>Processor - the method to process events</li>
 *  </ol>
 * </p>
 *
 * <p>
 *  Transition is the Rule providing FSM the possibility to change between states.
 *
 *  Each transition must be determined in terms of:
 *  <ol>
 *      <li>From State - mandatory</li>
 *      <li>To State - mandatory</li>
 *      <li>Condition - optionally. If specified, the FSM will check the condition in order to check the possibility
 *      to change from FROM State into TO State</li>
 *  </ol>
 * </p>
 *
 *
 * Simple way to use it - to construct an inherited class specified with the type of events to be processed
 * during transitions.
 * <pre>
 *  {@code
 *  SimpleFsm<String> simpleFsm = SimpleFsm
 *    .<SimpleFsm<String>, String>withStates(SimpleFsm::new)
 *      .from("init")
 *      .withBeforeHandler(fsm -> initBefore.set(true))
 *      .withAfterHandler(fsm -> initAfter.set(true))
 *      .withProcessor((fsm, event) -> initProcess.set(true))
 *    .end()
 *    .finish("finish")
 *      .withBeforeHandler(fsm -> finishBefore.set(true))
 *      .withAfterHandler(fsm -> finishAfter.set(true))
 *      .withProcessor((fsm, event) -> finishProcess.set(true))
 *    .end()
 *    .withTransition()
 *      .from("init")
 *      .to("finish")
 *      .checking((fsm, event) -> true)
 *    .end()
 *    .create();
 *  }
 * </pre>
 *
 * {@link SimpleFsm}
 */
public class Fsm<T extends Fsm, E> {

    private boolean done = false;
    private State<E> initialState;
    private State<E> currentState;
    private State<E> previousState;
    private final Map<String, State<E>> states  = new HashMap<>();
    private final Map<String, Map<String, Condition<T,E>>> transitions = new HashMap<>();

    /**
     * Initiate a builder
     *
     * @param supplier the original FSM inherited class constructor. You may specify '{@code () -> new SimpleFsm()}' in parameter
     * @return FsmBuilder
     * @param <T> the original FSM inherited class type
     * @param <E> the class type of Events to be processed
     */
    @SuppressWarnings("unchecked")
    public static <T extends Fsm,E> FsmBuilder<T,E> withStates(Supplier<T> supplier) {
        return new FsmBuilder<>(supplier);
    }

    /**
     * To initialize FSM into initial state
     * @throws NotInitializedException
     */
    public void init() throws NotInitializedException {
        currentState = initialState;
        if (currentState == null) {
            throw new NotInitializedException();
        }
        done = false;
        previousState = null;
        currentState.beforeEvent();
    }

    /**
     * Returns current state
     *
     * @return {@link State}
     */
    public State<E> getCurrentState() {
        return currentState;
    }

    /**
     * Returns previous state
     *
     * @return {@link State}
     */
    public State<E> getPreviousState() {
        return previousState;
    }

    /**
     * Main method to handle every event
     *
     * @param event event
     * @throws FsmException
     */
    @SuppressWarnings("unchecked")
    public void process(E event) throws FsmException {
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
        Map<String, Condition<T,E>> conditions = transitions.get(currentState.getName());
        List<String> nextStates = new ArrayList<>();
        for (String key : conditions.keySet()) {
            if (conditions.get(key) == null) {
                nextStates.add(key);
            } else if(conditions.get(key).check((T) this, event)) {
                nextStates.add(key);
            }
        }
        if (nextStates.size() > 1) {
            throw new AmbiguousTransitionException(currentState.getName(), nextStates);
        }
        if (nextStates.size() == 0) {
            throw new BrokenTransitionException(currentState.getName());
        }
        State<E> nextState = states.get(nextStates.get(0));
        nextState(nextState, event);
    }

    /**
     * To specify initial state
     *
     * @param state {@link State}
     * @throws FsmException
     */
    public void initState(State<E> state) throws FsmException {
        state.setFSM(this);
        addState(state);
        initialState = state;
    }

    /**
     * To add another state
     *
     * @param state {@link State}
     * @throws FsmException
     */
    public void addState(State<E> state) throws FsmException {
        checkStateExist(state.getName());
        state.setFSM(this);
        this.states.put(state.getName(), state);
    }

    /**
     * To set the transition up
     *
     * @param fromState {@link State}
     * @param toState {@link State}
     * @throws FsmException
     */
    public void addTransition(String fromState, String toState) throws FsmException {
        storeTransition(fromState, toState, null);
    }

    /**
     * To set the transition up
     *
     * @param fromState {@link State}
     * @param toState {@link State}
     * @param condition {@link Condition}
     * @throws FsmException
     */
    public void addTransition(String fromState, String toState, Condition<T,E> condition) throws FsmException {
        storeTransition(fromState, toState, condition);
    }

    /**
     * To set the transition up
     *
     * @param fromState {@link State}
     * @param toState {@link State}
     * @throws FsmException
     */
    public void addTransition(String fromState, State<E> toState) throws FsmException {
        addState(toState);
        addTransition(fromState, toState.getName());
    }

    /**
     * To set the transition up
     *
     * @param fromState {@link State}
     * @param toState {@link State}
     * @param condition {@link Condition}
     * @throws FsmException
     */
    public void addTransition(String fromState, State<E> toState, Condition<T,E> condition) throws FsmException {
        addState(toState);
        addTransition(fromState, toState.getName(), condition);
    }

    private void nextState(State<E> state, E event) {
        state.beforeEvent();
        previousState = currentState;
        currentState = state;
        currentState.process(event);
    }

    private void checkStateExist(String name) throws StateAlreadyExistsException {
        if (this.states.containsKey(name)) {
            throw new StateAlreadyExistsException(name);
        }
    }

    private void storeTransition(String fromState, String toState, Condition<T,E> condition) throws FsmException {
        if (!transitions.containsKey(fromState)) {
            transitions.put(fromState, new HashMap<>());
        }
        if (transitions.get(fromState).containsKey(toState)) {
            throw new ConditionAlreadyExistsException(fromState, toState);
        }
        transitions.get(fromState).put(toState, condition);
    }

}
