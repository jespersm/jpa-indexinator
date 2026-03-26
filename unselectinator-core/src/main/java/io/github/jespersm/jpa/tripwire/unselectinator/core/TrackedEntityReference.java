package io.github.jespersm.jpa.tripwire.unselectinator.core;

/**
 * Reference to a loaded entity instance (or synthetic owner reference when no Java object is available).
 *
 * @param entityName fully qualified entity class name
 * @param entityId entity identifier value, when known
 * @param identityHash identity hash of the loaded Java object, or {@code -1} for synthetic references
 */
public record TrackedEntityReference(String entityName, Object entityId, int identityHash) {

    /**
     * Create a tracked reference from a concrete entity instance.
     *
     * @param entityName fully qualified entity class name
     * @param entityId entity identifier value
     * @param entity loaded entity instance
     * @return tracked reference
     */
    public static TrackedEntityReference forInstance(String entityName, Object entityId, Object entity) {
        return new TrackedEntityReference(entityName, entityId, System.identityHashCode(entity));
    }

    /**
     * Create a synthetic reference when no Java owner instance is available.
     *
     * @param entityName fully qualified entity class name
     * @param entityId entity identifier value
     * @return synthetic tracked reference
     */
    public static TrackedEntityReference synthetic(String entityName, Object entityId) {
        return new TrackedEntityReference(entityName, entityId, -1);
    }

    /** @return display name as {@code SimpleEntity#id} or {@code SimpleEntity} */
    public String displayName() {
        int lastDot = entityName.lastIndexOf('.');
        String simpleName = lastDot >= 0 ? entityName.substring(lastDot + 1) : entityName;
        if (entityId == null) {
            return simpleName;
        }
        return simpleName + "#" + entityId;
    }

    @Override
    public String toString() {
        return displayName() + (identityHash >= 0 ? "@" + Integer.toHexString(identityHash) : "");
    }
}
