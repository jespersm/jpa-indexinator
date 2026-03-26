package io.github.jespersm.jpa.tripwire.unselectinator.core;

/**
 * User-visible explicit fetch entry points.
 */
public enum FetchEndpointKind {
    /** Endpoint initiated from a repository method invocation. */
    REPOSITORY_METHOD,
    /** Endpoint initiated directly through EntityManager API calls. */
    ENTITY_MANAGER
}

