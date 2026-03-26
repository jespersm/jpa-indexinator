package io.github.jespersm.jpa.tripwire.unselectinator.core;

/**
 * Observed return value together with the lazy-select report captured while computing it.
 *
 * @param value observed return value
 * @param report report captured during execution
 */
public record ObservationResult<T>(T value, LazySelectReport report) {
}

