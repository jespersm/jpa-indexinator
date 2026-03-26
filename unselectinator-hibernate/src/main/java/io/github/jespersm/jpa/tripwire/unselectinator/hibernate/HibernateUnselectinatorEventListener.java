package io.github.jespersm.jpa.tripwire.unselectinator.hibernate;

import io.github.jespersm.jpa.tripwire.unselectinator.core.EntityLoadTracker;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.InitializeCollectionEvent;
import org.hibernate.event.spi.InitializeCollectionEventListener;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;

/**
 * Bridges Hibernate load events into the thread-local tracker.
 */
public class HibernateUnselectinatorEventListener implements PostLoadEventListener, InitializeCollectionEventListener {
    private final EntityLoadTracker tracker;

    /**
     * @param tracker tracker receiving load callbacks
     */
    public HibernateUnselectinatorEventListener(EntityLoadTracker tracker) {
        this.tracker = tracker;
    }

    /**
     * Record an entity post-load callback.
     *
     * @param event Hibernate post-load event
     */
    @Override
    public void onPostLoad(PostLoadEvent event) {
        tracker.recordEntityLoad(event.getEntity(), event.getId(), event.getPersister().getEntityName());
    }

    /**
     * Record a collection initialization callback.
     *
     * @param event Hibernate collection initialization event
     * @throws HibernateException propagated from Hibernate listener contract
     */
    @Override
    public void onInitializeCollection(InitializeCollectionEvent event) throws HibernateException {
        tracker.recordCollectionLoad(
                event.getAffectedOwnerOrNull(),
                event.getAffectedOwnerEntityName(),
                event.getAffectedOwnerIdOrNull(),
                event.getCollection().getRole()
        );
    }
}

