# Unselectinator Hibernate Integration

`unselectinator-hibernate` wires Hibernate SQL and load events into `unselectinator-core`.

## Components

- `HibernateUnselectinatorStatementInspector`: counts `SELECT` SQL statements.
- `HibernateUnselectinatorIntegrator`: registers Hibernate event listeners.
- `HibernateUnselectinatorEventListener`: forwards `POST_LOAD` and `INIT_COLLECTION` events to `EntityLoadTracker`.
- `HibernateUnselectinator`: helper factory for statement inspector and integrator provider.

## Spring Integration in This Repository

The shared test fixture (`indexinator-test-parent`) demonstrates one-stop setup in:

- `indexinator-test-parent/src/main/java/io/github/jespersm/indexinator/test/unselectinator/UnselectinatorDemoConfiguration.java`
- `indexinator-test-parent/src/main/java/io/github/jespersm/indexinator/test/unselectinator/RepositoryFetchObservationAspect.java`

See integration tests in:

- `indexinator-test-parent/src/test/java/io/github/jespersm/indexinator/test/IndexinatorIntegrationTest.java`

