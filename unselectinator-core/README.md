# Unselectinator Core

`unselectinator-core` provides provider-agnostic lazy-select observation primitives.

## Main Concepts

- `EntityLoadTracker`: thread-local tracker for explicit fetch scopes and load/select events.
- `Unselectinator`: tiny facade to run code and capture a `LazySelectReport`.
- `FetchEndpoint`: caller-visible initiating operation (`REPOSITORY_METHOD` or `ENTITY_MANAGER`).
- `ObservedEntityManagerFactory`: wraps `EntityManager`/`Query` calls and marks explicit `EntityManager` fetch boundaries.

## Typical Usage

```java
ObservationResult<List<String>> result = unselectinator.observe(() -> {
    SchoolClass schoolClass = schoolClassRepository.findByCourseCode("CS101").orElseThrow();
    return schoolClass.getStudents().stream()
            .map(student -> student.getClasses().size())
            .map(String::valueOf)
            .toList();
});

LazySelectReport report = result.report();
```

`EntityLoadTracker` prefers repository endpoints when a repository call nests `EntityManager` operations.

