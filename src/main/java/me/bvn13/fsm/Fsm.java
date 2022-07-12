package me.bvn13.fsm;

import me.bvn13.fsm.exceptions.AmbiguousTransitionException;
import me.bvn13.fsm.exceptions.BrokenTransitionException;
import me.bvn13.fsm.exceptions.ConditionAlreadyExistsException;
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
 * <b>Final State Machine</b>
 * </p>
 *
 * <p>
 * Each state machine must be prepared with:
 *  <ol>
 *      <li>Initial state</li>
 *      <li>Finish state - may be not several states</li>
 *      <li>Intermediate states - optionally</li>
 *      <li>All transitions needed</li>
 *  </ol>
 *
 * <p>
 *  Each {@link State} may be specified with handlers:
 *  <ol>
 *      <li>Before handler - is called right before FSM changes INTO this state</li>
 *      <li>After handler - is called right before FSM changes FROM this state to another</li>
 *      <li>Processor - the method to process events</li>
 *  </ol>
 *
 * <p>
 *  Transition is the Rule providing FSM the possibility to change between states.
 * <p>
 *  Each transition must be determined in terms of:
 *  <ol>
 *      <li>From State - mandatory</li>
 *      <li>To State - mandatory</li>
 *      <li>Condition - optionally. If specified, the FSM will check the condition in order to check the possibility
 *      to change from FROM State into TO State</li>
 *  </ol>
 *
 * <p>
 * Simple way to use it - to construct an inherited class specified with the type of events to be processed
 * during transitions.
 *
 * <pre>
 *  {@code
 *  SimpleFsm<String> simpleFsm = Fsm
 *    .<SimpleFsm<String>, String>from(SimpleFsm::new)
 *    .withStates()
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
 * <p>
 * Otherwise you are able to use Old syntax:
 *
 * <pre>
 *  NamedFsm namedFsm = new NamedFsm().setName("TEST FSM");
 *  namedFsm.initState(new State&#60;String&#62;("init") {
 *      public void process(String event) {
 *          initStatedProcessed.set(true);
 *      }
 *  });
 *
 *  namedFsm.addTransition("init", new State&#60;String&#62;("first", true) {
 *      public void process(String event) {
 *          firstStatedProcessed.set(true);
 *      }
 *  });
 *
 *  namedFsm.addTransition("init", new State&#60;String&#62;("another", true) {
 *      public void process(String event) {
 *          anotherStatedProcessed.set(true);
 *      }
 *  }, (fsm, event) -&#62; false);
 *
 *  namedFsm.init();
 * </pre>
 * {@link SimpleFsm}
 */
public class Fsm<T extends Fsm, E> {

    protected boolean done = false;
    protected State<E> initialState;
    protected State<E> currentState;
    protected State<E> previousState;
    protected final Map<String, State<E>> states = new HashMap<>();
    protected final Map<String, Map<String, Condition<T, E>>> transitions = new HashMap<>();

    /**
     * Initiate a builder
     *
     * @param supplier the original FSM inherited class constructor. You may specify '{@code () -> new SimpleFsm()}' in parameter
     * @param <T>      the original FSM inherited class type
     * @param <E>      the class type of Events to be processed
     * @return FsmBuilder
     */
    @SuppressWarnings("unchecked")
    public static <T extends Fsm, E> FsmBuilderInitializer<T, E> from(Supplier<T> supplier) {
        return new FsmBuilderInitializer<>(supplier);
    }

    /**
     * To initialize FSM into initial state
     */
    public void init() {
        currentState = initialState;
        if (currentState == null) {
            throw new NotInitializedException();
        }
        done = false;
        previousState = null;
        currentState.beforeEvent();
    }

    /**
     * Main method to handle every event
     *
     * @param event event
     */
    @SuppressWarnings("unchecked")
    public void process(E event) {
        if (done) {
            return;
        }
        currentState.process(event);
        currentState.afterEvent();
        if (currentState.isFinish()) {
            done = true;
            return;
        }
        switchToNextState(event);
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
     * To specify initial state
     *
     * @param state {@link State}
     */
    public void initState(State<E> state) {
        state.setFSM(this);
        addState(state);
        initialState = state;
    }

    /**
     * To add another state
     *
     * @param state {@link State}
     */
    public void addState(State<E> state) {
        checkStateExist(state.getName());
        state.setFSM(this);
        this.states.put(state.getName(), state);
    }

    /**
     * To set the transition up
     *
     * @param fromState {@link State}
     * @param toState   {@link State}
     */
    public void addTransition(String fromState, String toState) {
        storeTransition(fromState, toState, null);
    }

    /**
     * To set the transition up
     *
     * @param fromState {@link State}
     * @param toState   {@link State}
     * @param condition {@link Condition}
     */
    public void addTransition(String fromState, String toState, Condition<T, E> condition) {
        storeTransition(fromState, toState, condition);
    }

    /**
     * To set the transition up
     *
     * @param fromState {@link State}
     * @param toState   {@link State}
     */
    public void addTransition(String fromState, State<E> toState) {
        addState(toState);
        addTransition(fromState, toState.getName());
    }

    /**
     * To set the transition up
     *
     * @param fromState {@link State}
     * @param toState   {@link State}
     * @param condition {@link Condition}
     */
    public void addTransition(String fromState, State<E> toState, Condition<T, E> condition) {
        addState(toState);
        addTransition(fromState, toState.getName(), condition);
    }

    private void switchToNextState(E event) {
        if (!transitions.containsKey(currentState.getName())) {
            throw new TransitionMissedException(currentState.getName());
        }
        Map<String, Condition<T, E>> conditions = transitions.get(currentState.getName());
        List<String> nextStates = new ArrayList<>();
        for (String key : conditions.keySet()) {
            if (conditions.get(key) == null) {
                nextStates.add(key);
            } else if (conditions.get(key).check((T) this, event)) {
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

    private void nextState(State<E> state, E event) {
        previousState = currentState;
        currentState = state;
        currentState.beforeEvent();
    }

    private void checkStateExist(String name) throws StateAlreadyExistsException {
        if (this.states.containsKey(name)) {
            throw new StateAlreadyExistsException(name);
        }
    }

    private void storeTransition(String fromState, String toState, Condition<T, E> condition) {
        if (!transitions.containsKey(fromState)) {
            transitions.put(fromState, new HashMap<>());
        }
        if (transitions.get(fromState).containsKey(toState)) {
            throw new ConditionAlreadyExistsException(fromState, toState);
        }
        transitions.get(fromState).put(toState, condition);
    }

}
