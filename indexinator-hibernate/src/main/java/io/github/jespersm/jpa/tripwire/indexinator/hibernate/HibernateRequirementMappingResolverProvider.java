package io.github.jespersm.jpa.tripwire.indexinator.hibernate;

import io.github.jespersm.jpa.tripwire.indexinator.core.requirement.RequirementMappingResolver;
import io.github.jespersm.jpa.tripwire.indexinator.core.requirement.RequirementMappingResolverProvider;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import java.util.List;

/**
 * ServiceLoader provider that contributes Hibernate-backed mapping resolvers.
 * <p>
 * This provider activates only when the supplied {@link EntityManagerFactory}
 * can be unwrapped to Hibernate's {@link SessionFactoryImplementor}.
 */
public class HibernateRequirementMappingResolverProvider implements RequirementMappingResolverProvider {

    /**
     * Hibernate provider should run before generic/fallback providers.
     *
     * @return provider priority
     */
    @Override
    public int priority() {
        return 100;
    }

    /**
     * @param entityManagerFactory runtime entity manager factory
     * @return {@code true} when Hibernate internals are available
     */
    @Override
    public boolean supports(EntityManagerFactory entityManagerFactory) {
        try {
            return entityManagerFactory.unwrap(SessionFactoryImplementor.class) != null;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * @param entityManagerFactory runtime entity manager factory
     * @return resolver list backed by Hibernate SQL mapping metadata
     */
    @Override
    public List<RequirementMappingResolver> createResolvers(EntityManagerFactory entityManagerFactory) {
        SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
        return List.of(new HibernateSqlMappingResolver(sessionFactory));
    }
}

