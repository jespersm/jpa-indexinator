package io.github.jespersm.jpa.tripwire.indexinator.core.requirement;

import io.github.jespersm.jpa.tripwire.indexinator.core.model.IssueSeverity;
import io.github.jespersm.jpa.tripwire.indexinator.core.model.IssueType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Normalized index requirement that can originate from multiple metadata sources.
 */
public class IndexRequirement {
	private final Class<?> entityClass;
	private final List<String> propertyPaths;
	private final boolean unique;
	private final RequirementSource source;
	private final IssueType issueType;
	private final IssueSeverity severity;
	private final String context;

	private IndexRequirement(Class<?> entityClass,
							 List<String> propertyPaths,
							 boolean unique,
							 RequirementSource source,
							 IssueType issueType,
							 IssueSeverity severity,
							 String context) {
		this.entityClass = entityClass;
		this.propertyPaths = new ArrayList<>(propertyPaths);
		this.unique = unique;
		this.source = source;
		this.issueType = issueType;
		this.severity = severity;
		this.context = context;
	}

	/**
	 * Create an index requirement from one or more property paths.
	 *
	 * @param entityClass entity declaring the properties
	 * @param propertyPaths property paths (for example {@code schoolClass.teacher.id})
	 * @param unique whether a unique index is required
	 * @param source requirement origin
	 * @param issueType issue type to emit when requirement is unmet
	 * @param severity issue severity when requirement is unmet
	 * @param context human-readable context for diagnostics
	 * @return normalized requirement instance
	 */
	public static IndexRequirement forProperties(Class<?> entityClass,
												 List<String> propertyPaths,
												 boolean unique,
												 RequirementSource source,
												 IssueType issueType,
												 IssueSeverity severity,
												 String context) {
		return new IndexRequirement(entityClass, propertyPaths, unique, source, issueType, severity, context);
	}

	/** @return entity class declaring the indexed property path(s) */
	public Class<?> getEntityClass() {
		return entityClass;
	}

	/** @return immutable property paths that require an index */
	public List<String> getPropertyPaths() {
		return Collections.unmodifiableList(propertyPaths);
	}


	/** @return {@code true} when a unique index is required */
	public boolean isUnique() {
		return unique;
	}

	/** @return source that produced this requirement */
	public RequirementSource getSource() {
		return source;
	}

	/** @return issue type associated with unmet requirement */
	public IssueType getIssueType() {
		return issueType;
	}

	/** @return issue severity associated with unmet requirement */
	public IssueSeverity getSeverity() {
		return severity;
	}

	/** @return human-readable diagnostic context for this requirement */
	public String getContext() {
		return context;
	}
}

