package io.github.jespersm.jpa.tripwire.indexinator.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Information about a database index.
 */
public class IndexInfo {
    private final String indexName;
    private final String tableName;
    private final List<String> columns;
    private final boolean isUnique;

    /**
     * @param indexName index name
     * @param tableName table owning the index
     * @param isUnique whether the index enforces uniqueness
     */
    public IndexInfo(String indexName, String tableName, boolean isUnique) {
        this.indexName = indexName;
        this.tableName = tableName;
        this.isUnique = isUnique;
        this.columns = new ArrayList<>();
    }

    /**
     * Add one indexed column in physical order.
     *
     * @param columnName indexed column name
     */
    public void addColumn(String columnName) {
        this.columns.add(columnName);
    }

    /** @return index name */
    public String getIndexName() {
        return indexName;
    }

    /** @return table name */
    public String getTableName() {
        return tableName;
    }

    /** @return defensive copy of indexed column names */
    public List<String> getColumns() {
        return new ArrayList<>(columns);
    }

    /** @return whether the index is unique */
    public boolean isUnique() {
        return isUnique;
    }

    /** @return whether the index contains more than one column */
    public boolean isComposite() {
        return columns.size() > 1;
    }
}
