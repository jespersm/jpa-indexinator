package io.github.jespersm.jpa.tripwire.unselectinator.core;

/**
 * Best-effort entity relationship edge observed during lazy loading.
 *
 * @param owner owner entity reference
 * @param relationName relation field/role name
 * @param target target entity reference
 */
public record TrackedRelationEdge(
        TrackedEntityReference owner,
        String relationName,
        TrackedEntityReference target
) {
}

