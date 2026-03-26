package io.github.jespersm.jpa.tripwire.unselectinator.core;

/**
 * Describes the explicit fetch operation visible to user code.
 *
 * @param kind endpoint category
 * @param ownerType fully qualified owner type name
 * @param methodName method name that starts the explicit fetch
 * @param callerLocation source location where the endpoint was created
 */
public record FetchEndpoint(
        FetchEndpointKind kind,
        String ownerType,
        String methodName,
        SourceLocation callerLocation
) {

    /** @return stable endpoint signature as {@code ownerType#methodName} */
    public String signature() {
        return ownerType + "#" + methodName;
    }

    /** @return simple display name as {@code SimpleType#methodName} */
    public String displayName() {
        int lastDot = ownerType.lastIndexOf('.');
        String simpleType = lastDot >= 0 ? ownerType.substring(lastDot + 1) : ownerType;
        return simpleType + "#" + methodName;
    }

    @Override
    public String toString() {
        return displayName() + " via " + callerLocation;
    }
}

