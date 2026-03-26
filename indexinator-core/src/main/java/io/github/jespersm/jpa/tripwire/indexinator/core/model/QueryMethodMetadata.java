package io.github.jespersm.jpa.tripwire.indexinator.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Metadata about a repository query method.
 *
 * @param methodName repository method name
 * @param queriedFields fields referenced by the query derivation
 * @param queryType query operation type
 */
public record QueryMethodMetadata(String methodName, List<String> queriedFields, QueryType queryType) {
    /**
     * Canonical constructor with defensive copying.
     *
     * @param methodName repository method name
     * @param queriedFields queried field names
     * @param queryType query operation type
     */
    public QueryMethodMetadata(String methodName, List<String> queriedFields, QueryType queryType) {
        this.methodName = methodName;
        this.queriedFields = new ArrayList<>(queriedFields);
        this.queryType = queryType;
    }

    @Override
    public List<String> queriedFields() {
        return new ArrayList<>(queriedFields);
    }

    /**
     * Query operation category inferred from the method prefix.
     */
    public enum QueryType {
        FIND,
        COUNT,
        DELETE,
        EXISTS,
        CUSTOM
    }

    @Override
    public String toString() {
        return String.format("%s (queries: %s)", methodName, queriedFields);
    }
}
