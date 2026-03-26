package io.github.jespersm.jpa.tripwire.unselectinator.core;

import java.util.function.Supplier;

/**
 * Small test-facing facade for running code while capturing lazy select activity.
 *
 * @param tracker tracker used for the current observation lifecycle
 */
public record Unselectinator(EntityLoadTracker tracker) {

    /**
     * Observe a void operation and return the captured report.
     *
     * @param runnable operation to execute
     * @return lazy-select observation report
     */
    public LazySelectReport observe(Runnable runnable) {
        tracker.startObservation();
        try {
            runnable.run();
            return tracker.finishObservation();
        } catch (RuntimeException ex) {
            tracker.finishObservation();
            throw ex;
        }
    }

    /**
     * Observe a value-producing operation and return both value and report.
     *
     * @param supplier operation to execute
     * @param <T> supplied value type
     * @return supplied value with its lazy-select report
     */
    public <T> ObservationResult<T> observe(Supplier<T> supplier) {
        tracker.startObservation();
        try {
            T value = supplier.get();
            return new ObservationResult<>(value, tracker.finishObservation());
        } catch (RuntimeException ex) {
            tracker.finishObservation();
            throw ex;
        }
    }
}

