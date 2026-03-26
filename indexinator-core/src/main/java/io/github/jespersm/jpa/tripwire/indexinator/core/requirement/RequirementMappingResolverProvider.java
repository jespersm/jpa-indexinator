package io.github.jespersm.jpa.tripwire.indexinator.core.requirement;

import jakarta.persistence.EntityManagerFactory;

import java.util.List;

/**
 * Classpath-discoverable provider for mapping resolvers.
 * Implementations are typically loaded through {@link java.util.ServiceLoader}.
 */
public interface RequirementMappingResolverProvider {

    /**
     * Provider priority (higher runs first).
     *
     * @return ordering priority
     */
    default int priority() {
        return 0;
    }

    /**
     * Whether this provider can work with the supplied entity manager factory.
     *
     * @param entityManagerFactory target entity manager factory
     * @return {@code true} when this provider supports the runtime
     */
    boolean supports(EntityManagerFactory entityManagerFactory);

    /**
     * Create resolver instances for this provider.
     *
     * @param entityManagerFactory target entity manager factory
     * @return resolver instances to try in order
     */
    List<RequirementMappingResolver> createResolvers(EntityManagerFactory entityManagerFactory);
}

