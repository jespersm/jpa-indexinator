package io.github.jespersm.jpa.tripwire.unselectinator.hibernate;

import io.github.jespersm.jpa.tripwire.unselectinator.core.EntityLoadTracker;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.util.List;

/**
 * Small helper factory for registering Unselectinator with Hibernate.
 */
public final class HibernateUnselectinator {
    private HibernateUnselectinator() {
    }

    /**
     * Create a statement inspector that records SELECT statements.
     *
     * @param tracker tracker receiving SQL observations
     * @return Hibernate statement inspector
     */
    public static StatementInspector statementInspector(EntityLoadTracker tracker) {
        return new HibernateUnselectinatorStatementInspector(tracker);
    }

    /**
     * Create an integrator provider that installs Unselectinator listeners.
     *
     * @param tracker tracker receiving load events
     * @return integrator provider for Hibernate bootstrap properties
     */
    public static IntegratorProvider integratorProvider(EntityLoadTracker tracker) {
        return () -> List.of(new HibernateUnselectinatorIntegrator(tracker));
    }
}

