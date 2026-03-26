package io.github.jespersm.jpa.tripwire.indexinator.core.model;

/**
 * Represents a database issue detected by Indexinator.
 *
 * @param severity issue severity
 * @param type issue type category
 * @param tableName affected table name
 * @param columnName affected column name, when applicable
 * @param description human-readable issue description
 * @param recommendation remediation recommendation
 */
public record Issue(IssueSeverity severity, IssueType type, String tableName, String columnName, String description,
                    String recommendation) {

    @Override
    public String toString() {
        return String.format("[%s] %s - %s.%s: %s (Recommendation: %s)",
                severity, type, tableName, columnName != null ? columnName : "N/A",
                description, recommendation);
    }
}
