package io.github.jespersm.jpa.tripwire.indexinator.core.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Immutable report containing all findings from one inspection run.
 */
public class InspectionReport {
    private final LocalDateTime timestamp;
    private final List<Issue> issues;
    private final int tablesInspected;
    private final int entitiesAnalyzed;

    /**
     * Create a report snapshot.
     *
     * @param issues detected issues
     * @param tablesInspected number of inspected tables
     * @param entitiesAnalyzed number of analyzed entities
     */
    public InspectionReport(List<Issue> issues, int tablesInspected, int entitiesAnalyzed) {
        this.timestamp = LocalDateTime.now();
        this.issues = new ArrayList<>(issues);
        this.tablesInspected = tablesInspected;
        this.entitiesAnalyzed = entitiesAnalyzed;
    }

    /** @return timestamp when the report was created */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /** @return defensive copy of detected issues */
    public List<Issue> getIssues() {
        return new ArrayList<>(issues);
    }

    /** @return total number of detected issues */
    public int getIssueCount() {
        return issues.size();
    }

    /** @return number of inspected tables */
    public int getTablesInspected() {
        return tablesInspected;
    }

    /** @return number of analyzed entities */
    public int getEntitiesAnalyzed() {
        return entitiesAnalyzed;
    }

    /**
     * Filter issues by severity.
     *
     * @param severity severity to include
     * @return matching issues
     */
    public List<Issue> getIssuesBySeverity(IssueSeverity severity) {
        return issues.stream()
                .filter(issue -> issue.severity() == severity)
                .collect(Collectors.toList());
    }

    /** @return issue counts grouped by severity */
    public Map<IssueSeverity, Long> getIssueCountBySeverity() {
        return issues.stream()
                .collect(Collectors.groupingBy(Issue::severity, Collectors.counting()));
    }

    /** @return {@code true} when at least one issue is present */
    public boolean hasIssues() {
        return !issues.isEmpty();
    }

    /** @return {@code true} when at least one critical issue is present */
    public boolean hasCriticalIssues() {
        return issues.stream().anyMatch(issue -> issue.severity() == IssueSeverity.CRITICAL);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Indexinator Inspection Report ===\n");
        sb.append("Timestamp: ").append(timestamp).append("\n");
        sb.append("Tables Inspected: ").append(tablesInspected).append("\n");
        sb.append("Entities Analyzed: ").append(entitiesAnalyzed).append("\n");
        sb.append("Total Issues Found: ").append(issues.size()).append("\n\n");

        Map<IssueSeverity, Long> severityCounts = getIssueCountBySeverity();
        sb.append("Issues by Severity:\n");
        for (IssueSeverity severity : IssueSeverity.values()) {
            long count = severityCounts.getOrDefault(severity, 0L);
            if (count > 0) {
                sb.append("  ").append(severity).append(": ").append(count).append("\n");
            }
        }

        if (!issues.isEmpty()) {
            sb.append("\n=== Detailed Issues ===\n");
            for (Issue issue : issues) {
                sb.append(issue).append("\n");
            }
        }

        return sb.toString();
    }
}
