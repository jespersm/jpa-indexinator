package io.github.jespersm.jpa.tripwire.unselectinator.core;

/**
 * A concrete entity or collection load event together with the number of selects since the previous load event.
 *
 * @param entity loaded entity or owner reference
 * @param relationName loaded relation name for collection loads, or {@code null} for entity loads
 * @param selectsSincePreviousLoad select count delta since prior load event
 * @param explicitFetchLoad whether this load happened inside an explicit fetch scope
 */
public record TrackedLoadEvent(
        TrackedEntityReference entity,
        String relationName,
        int selectsSincePreviousLoad,
        boolean explicitFetchLoad
) {
}

