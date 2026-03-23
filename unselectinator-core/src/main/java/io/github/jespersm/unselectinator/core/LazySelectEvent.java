package io.github.jespersm.unselectinator.core;

/**
 * Lazy load detected outside an explicit fetch boundary.
 */
public record LazySelectEvent(
        LazyLoadKind kind,
        FetchEndpoint initiatingEndpoint,
        TrackedEntityReference owner,
        String relationName,
        TrackedEntityReference loadedEntity,
        int selectsSincePreviousLoad
) {
}

