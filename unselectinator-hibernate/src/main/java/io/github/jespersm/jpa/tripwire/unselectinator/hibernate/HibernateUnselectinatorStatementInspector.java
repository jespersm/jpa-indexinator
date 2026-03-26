package io.github.jespersm.jpa.tripwire.unselectinator.hibernate;

import io.github.jespersm.jpa.tripwire.unselectinator.core.EntityLoadTracker;
import org.hibernate.resource.jdbc.spi.StatementInspector;

/**
 * Records every Hibernate SELECT statement in the current observation.
 */
public class HibernateUnselectinatorStatementInspector implements StatementInspector {
    private final EntityLoadTracker tracker;

    /**
     * @param tracker tracker receiving SQL observations
     */
    public HibernateUnselectinatorStatementInspector(EntityLoadTracker tracker) {
        this.tracker = tracker;
    }

    /**
     * @param sql SQL statement issued by Hibernate
     * @return unmodified SQL statement
     */
    @Override
    public String inspect(String sql) {
        tracker.recordSelect(sql);
        return sql;
    }
}

