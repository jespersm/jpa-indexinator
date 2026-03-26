package io.github.jespersm.jpa.tripwire.indexinator.core.model;

/**
 * Information about a foreign key constraint.
 *
 * @param constraintName foreign key constraint name
 * @param tableName source table name
 * @param columnName source column name
 * @param referencedTableName target table name
 * @param referencedColumnName target column name
 */
public record ForeignKeyInfo(String constraintName, String tableName, String columnName, String referencedTableName,
                             String referencedColumnName) {
}
