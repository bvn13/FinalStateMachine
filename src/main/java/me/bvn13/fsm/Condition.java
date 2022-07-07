package me.bvn13.fsm;

/**
 * Condition of transitions
 */
@FunctionalInterface
public interface Condition<T extends Fsm, E> {
    boolean check(T fsm, E event);
}
