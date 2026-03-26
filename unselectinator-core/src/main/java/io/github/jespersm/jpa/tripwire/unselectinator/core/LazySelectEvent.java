package io.github.jespersm.jpa.tripwire.unselectinator.core;

/**
 * Lazy load detected outside an explicit fetch boundary.
 *
 * @param kind lazy load category
 * @param initiatingEndpoint explicit fetch endpoint that previously initiated the traversal
 * @param owner owning entity reference
 * @param relationName relationship field/role name
 * @param loadedEntity lazily loaded entity reference, when available
 * @param selectsSincePreviousLoad number of selects since the previous tracked load event
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

