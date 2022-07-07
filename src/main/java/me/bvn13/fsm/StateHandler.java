package me.bvn13.fsm;

/**
 * State handler
 * @param <T>
 */
@FunctionalInterface
public interface StateHandler<T extends Fsm> {

    void handle(T fms);

}
