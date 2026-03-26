package io.github.jespersm.jpa.tripwire.indexinator.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Metadata extracted from a Spring Data repository interface.
 */
public class RepositoryMetadata {
    private final Class<?> repositoryInterface;
    private final Class<?> entityClass;
    private final String entityTableName;
    private final List<QueryMethodMetadata> queryMethods;

    /**
     * @param repositoryInterface repository interface type
     * @param entityClass managed entity type
     * @param entityTableName managed entity table name
     */
    public RepositoryMetadata(Class<?> repositoryInterface, Class<?> entityClass, String entityTableName) {
        this.repositoryInterface = repositoryInterface;
        this.entityClass = entityClass;
        this.entityTableName = entityTableName;
        this.queryMethods = new ArrayList<>();
    }

    /** @param queryMethod repository query method metadata */
    public void addQueryMethod(QueryMethodMetadata queryMethod) {
        this.queryMethods.add(queryMethod);
    }

    /** @return repository interface type */
    public Class<?> getRepositoryInterface() {
        return repositoryInterface;
    }

    /** @return managed entity type */
    public Class<?> getEntityClass() {
        return entityClass;
    }

    /** @return managed entity table name */
    public String getEntityTableName() {
        return entityTableName;
    }

    /** @return defensive copy of discovered query methods */
    public List<QueryMethodMetadata> getQueryMethods() {
        return new ArrayList<>(queryMethods);
    }

    @Override
    public String toString() {
        return String.format("Repository: %s (entity: %s, methods: %d)",
                repositoryInterface.getSimpleName(),
                entityClass.getSimpleName(),
                queryMethods.size());
    }
}
