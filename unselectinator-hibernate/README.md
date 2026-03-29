# Unselectinator Hibernate Integration

`unselectinator-hibernate` wires Hibernate SQL and load events into `unselectinator-core`.

## Components

- `HibernateUnselectinatorStatementInspector`: counts `SELECT` SQL statements.
- `HibernateUnselectinatorIntegrator`: registers Hibernate event listeners.
- `HibernateUnselectinatorEventListener`: forwards `POST_LOAD` and `INIT_COLLECTION` events to `EntityLoadTracker`.
- `HibernateUnselectinator`: helper factory for statement inspector and integrator provider.

## Spring / Spring Boot Integration

`unselectinator-hibernate` also ships Spring wiring support in the `...hibernate.spring` package:

- `UnselectinatorSpringConfiguration`
- `RepositoryFetchObservationAspect`

For usage and explicit per-test opt-in examples, see the top-level `README.md`.

