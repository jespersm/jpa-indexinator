package io.github.jespersm.jpa.tripwire.unselectinator.core;

/**
 * Distinguishes lazy entity loads from lazy collection initializations.
 */
public enum LazyLoadKind {
    /** A single entity instance is lazily loaded. */
    ENTITY_LOAD,
    /** A lazy collection association is initialized. */
    COLLECTION_LOAD
}

