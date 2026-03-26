package io.github.jespersm.jpa.tripwire.indexinator.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Metadata extracted from a JPA entity.
 */
public class EntityMetadata {
    private final String entityName;
    private final String tableName;
    private final Class<?> entityClass;
    private final List<ColumnMetadata> columns;
    private final List<RelationshipMetadata> relationships;

    /**
     * @param entityName entity name from JPA metadata
     * @param tableName mapped table name
     * @param entityClass Java entity type
     */
    public EntityMetadata(String entityName, String tableName, Class<?> entityClass) {
        this.entityName = entityName;
        this.tableName = tableName;
        this.entityClass = entityClass;
        this.columns = new ArrayList<>();
        this.relationships = new ArrayList<>();
    }

    /** @param column mapped column metadata */
    public void addColumn(ColumnMetadata column) {
        this.columns.add(column);
    }

    /** @param relationship mapped relationship metadata */
    public void addRelationship(RelationshipMetadata relationship) {
        this.relationships.add(relationship);
    }

    /** @return JPA entity name */
    public String getEntityName() {
        return entityName;
    }

    /** @return physical table name */
    public String getTableName() {
        return tableName;
    }

    /** @return Java entity type */
    public Class<?> getEntityClass() {
        return entityClass;
    }

    /** @return defensive copy of mapped columns */
    public List<ColumnMetadata> getColumns() {
        return new ArrayList<>(columns);
    }

    /** @return defensive copy of mapped relationships */
    public List<RelationshipMetadata> getRelationships() {
        return new ArrayList<>(relationships);
    }
}
