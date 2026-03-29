package io.github.jespersm.jpa.tripwire.unselectinator.hibernate.spring;

import io.github.jespersm.jpa.tripwire.unselectinator.core.EntityLoadTracker;
import io.github.jespersm.jpa.tripwire.unselectinator.core.ObservedEntityManagerFactory;
import io.github.jespersm.jpa.tripwire.unselectinator.core.Unselectinator;
import io.github.jespersm.jpa.tripwire.unselectinator.hibernate.HibernateUnselectinator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

/**
 * One-stop Spring {@link Configuration} that wires Unselectinator into a Spring Boot /
 * Spring Data JPA application for integration testing.
 *
 * <p>This class is <em>not</em> activated automatically. Import it explicitly from each
 * test that needs Unselectinator observation, for example:
 *
 * <pre>{@code
 * @SpringBootTest(classes = MyApp.class)
 * @Import(UnselectinatorSpringConfiguration.class)
 * class MyIntegrationTest { … }
 * }</pre>
 *
 * <p>or as a shared base-class annotation:
 *
 * <pre>{@code
 * @SpringBootTest(classes = { MyApp.class, UnselectinatorSpringConfiguration.class })
 * abstract class AbstractMyTest { … }
 * }</pre>
 *
 * <p>Requires Spring ORM, Spring AOP, AspectJ weaver, and Hibernate on the classpath
 * (all provided as optional dependencies of {@code unselectinator-hibernate}).
 */
@Configuration
public class UnselectinatorSpringConfiguration {

    /**
     * Static factory for EntityLoadTracker so it is available during the BeanPostProcessor
     * phase without triggering the "not eligible for auto-proxying" warning.
     */
    @Bean
    public static EntityLoadTracker entityLoadTracker() {
        return new EntityLoadTracker();
    }

    @Bean
    public Unselectinator unselectinator(EntityLoadTracker tracker) {
        return new Unselectinator(tracker);
    }

    /**
     * Registers the repository-fetch aspect as an explicit bean rather than relying
     * on component scanning, keeping activation fully under caller control.
     */
    @Bean
    public RepositoryFetchObservationAspect repositoryFetchObservationAspect(EntityLoadTracker tracker) {
        return new RepositoryFetchObservationAspect(tracker);
    }

    /**
     * Declared static so Spring does not need to instantiate the {@code @Configuration}
     * class before all {@link BeanPostProcessor}s are registered. Uses
     * {@link ObjectProvider} for the {@link EntityLoadTracker} dependency so that Spring
     * resolves it lazily, preventing the "not eligible for auto-proxying"
     * {@code BeanPostProcessorChecker} warning.
     */
    @Bean
    public static BeanPostProcessor unselectinatorEntityManagerFactoryPostProcessor(
            ObjectProvider<EntityLoadTracker> trackerProvider) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof LocalContainerEntityManagerFactoryBean emfb) {
                    EntityLoadTracker tracker = trackerProvider.getObject();
                    emfb.getJpaPropertyMap().put(
                            AvailableSettings.STATEMENT_INSPECTOR,
                            HibernateUnselectinator.statementInspector(tracker)
                    );
                    emfb.getJpaPropertyMap().put(
                            "hibernate.integrator_provider",
                            HibernateUnselectinator.integratorProvider(tracker)
                    );
                }
                return bean;
            }
        };
    }

    @Bean
    public EntityManager observedEntityManager(EntityManagerFactory entityManagerFactory,
                                               EntityLoadTracker tracker) {
        EntityManager shared = SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
        return ObservedEntityManagerFactory.wrap(shared, tracker);
    }
}

