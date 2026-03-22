# Indexinator

A Java tool for inspecting JPA/Hibernate models and databases to detect potential performance issues, particularly missing indexes.

## Overview

I built Indexinator to help developers identify common database performance issues rooted in the leaking abstraction that is an O/R Mapper.
 The tool analyzes your entity classes and the actual database schema to find discrepancies that can lead to slow queries and performance degradation:

- Missing indexes on foreign key columns
- Missing indexes on unique constraint columns
- Tables without primary keys
- Missing indexes on frequently queried columns
- Suboptimal composite indexes

## Project Structure

This is a multi-module Maven project:

```
jpa_helper/
├── indexinator-core/          # Core inspection library
│   └── Reusable library for JPA/database inspection
│
├── indexinator-hibernate/     # Hibernate provider extension
│   └── ServiceLoader-based mapping resolver for requirement-to-schema mapping
│
└── indexinator-demo/          # Demo application
    └── Spring Boot demo with Teachers/Students/Classes model
```

## Features

### Indexinator Core

The core library provides:

- **Entity Analysis**: Extracts metadata from JPA entities using reflection
- **Schema Inspection**: Uses JDBC metadata to inspect actual database schema
- **Issue Detection**: Compares entity metadata with database schema to find issues
- **Detailed Reporting**: Generates comprehensive reports with severity levels and recommendations

### Detection Capabilities

| Issue Type | Severity | Description |
|------------|----------|-------------|
| Missing FK Index | HIGH/MEDIUM | Foreign key columns without indexes (JPA `@ManyToOne`, owning `@OneToOne`) |
| Missing Unique Index | MEDIUM | `@Column(unique = true)` columns without indexes |
| Missing Declared Index | MEDIUM | `@Table(indexes=...)` declared but not present in schema |
| Missing Query Index | MEDIUM | Columns used in Spring Data derived queries without indexes |
| Potential Composite Index | LOW | Opportunity for composite indexes based on access patterns |

### Indexinator Hibernate Extension 

A ServiceLoader-based provider (`RequirementMappingResolverProvider`) that maps JPA entity/property requirements to actual table/column names using Hibernate's metamodel API, enabling accurate schema validation.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker (required for Testcontainers in integration tests)
- PostgreSQL (for running the demo application, or use Docker)

Building and running the tests is just plain Maven.
