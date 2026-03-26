package io.github.jespersm.jpa.tripwire.indexinator.core.requirement;

/**
 * Resolves an index requirement from entity/property paths to relational names.
 */
public interface RequirementMappingResolver {
    /**
     * Resolve a normalized requirement into table/column names.
     *
     * @param requirement requirement to resolve
     * @return resolved mapping, or {@code null} when this resolver cannot resolve it
     */
    ResolvedRequirementMapping resolve(IndexRequirement requirement);
}

