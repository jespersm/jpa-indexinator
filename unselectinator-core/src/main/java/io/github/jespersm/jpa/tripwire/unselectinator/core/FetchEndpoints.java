package io.github.jespersm.jpa.tripwire.unselectinator.core;

/**
 * Factory methods for explicit fetch endpoints with caller capture.
 */
public final class FetchEndpoints {
    private FetchEndpoints() {
    }

    /**
     * Create an endpoint for a repository method invocation.
     *
     * @param repositoryType repository interface type
     * @param methodName repository method name
     * @return endpoint descriptor with caller location
     */
    public static FetchEndpoint repositoryMethod(Class<?> repositoryType, String methodName) {
        return new FetchEndpoint(
                FetchEndpointKind.REPOSITORY_METHOD,
                repositoryType.getName(),
                methodName,
                StackFrameCapture.captureUserLocation()
        );
    }

    /**
     * Create an endpoint for a direct EntityManager API call.
     *
     * @param methodName EntityManager method name
     * @return endpoint descriptor with caller location
     */
    public static FetchEndpoint entityManagerMethod(String methodName) {
        return new FetchEndpoint(
                FetchEndpointKind.ENTITY_MANAGER,
                "jakarta.persistence.EntityManager",
                methodName,
                StackFrameCapture.captureUserLocation()
        );
    }
}

