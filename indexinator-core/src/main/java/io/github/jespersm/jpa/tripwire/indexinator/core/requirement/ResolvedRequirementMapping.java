package io.github.jespersm.jpa.tripwire.indexinator.core.requirement;

import java.util.List;

/**
 * Resolved relational mapping for an entity/property index requirement.
 *
 * @param tableName resolved table name
 * @param columnNames resolved column names in declaration order
 */
public record ResolvedRequirementMapping(String tableName, List<String> columnNames) {
}

