package me.bvn13.fsm;

/**
 * State behavior
 */
public interface StateBehaviour<E> {

    default void beforeEvent() {

    }

    default void afterEvent() {

    }

    default void process(E event) {

    }

}
