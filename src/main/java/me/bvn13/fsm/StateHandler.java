package me.bvn13.fsm;

/**
 * State handler
 * @param <T> class inherited from {@link Fsm}
 */
@FunctionalInterface
public interface StateHandler<T extends Fsm> {

    void handle(T fms);

}
