package io.github.jespersm.jpa.tripwire.indexinator.core.model;

/**
 * Metadata about a column in a JPA entity.
 *
 * @param fieldName Java field name on the entity
 * @param columnName physical database column name
 * @param isPrimaryKey whether the column is part of the primary key
 * @param isUnique whether the column has a unique constraint
 * @param isNullable whether the column accepts null values
 * @param fieldType Java type of the mapped field
 */
public record ColumnMetadata(
        String fieldName,
        String columnName,
        boolean isPrimaryKey,
        boolean isUnique,
        boolean isNullable,
        Class<?> fieldType) {
}
