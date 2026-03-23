package io.github.jespersm.unselectinator.hibernate;

import io.github.jespersm.unselectinator.core.EntityLoadTracker;
import org.hibernate.resource.jdbc.spi.StatementInspector;

/**
 * Records every Hibernate SELECT statement in the current observation.
 */
public class HibernateUnselectinatorStatementInspector implements StatementInspector {
    private final EntityLoadTracker tracker;

    public HibernateUnselectinatorStatementInspector(EntityLoadTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public String inspect(String sql) {
        tracker.recordSelect(sql);
        return sql;
    }
}

