package me.bvn13.fsm;

/**
 * State processor
 * @param <T> the type of FSM inherited class
 * @param <E> the type of Events
 */
@FunctionalInterface
public interface StateProcessor<T extends Fsm, E> {

    void process(T fms, E event);

}
