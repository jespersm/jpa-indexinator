package io.github.jespersm.jpa.tripwire.unselectinator.hibernate.spring;

import io.github.jespersm.jpa.tripwire.unselectinator.core.EntityLoadTracker;
import io.github.jespersm.jpa.tripwire.unselectinator.core.FetchEndpoint;
import io.github.jespersm.jpa.tripwire.unselectinator.core.FetchEndpoints;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

import org.aspectj.lang.reflect.MethodSignature;

/**
 * Treats Spring Data repository method invocations as the user-visible explicit fetch
 * boundary for Unselectinator observation.
 *
 * <p>This aspect carries no {@code @Component} stereotype and is therefore never
 * picked up by component scanning. It is registered exclusively as a Spring bean by
 * {@link UnselectinatorSpringConfiguration#repositoryFetchObservationAspect}, ensuring
 * that activation is fully explicit and per-test-class.
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RepositoryFetchObservationAspect {

    private final EntityLoadTracker tracker;

    public RepositoryFetchObservationAspect(EntityLoadTracker tracker) {
        this.tracker = tracker;
    }

    @Around("this(org.springframework.data.repository.Repository) && execution(public * *(..))")
    public Object observeRepositoryFetch(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        if (method.getDeclaringClass() == Object.class) {
            return joinPoint.proceed();
        }

        FetchEndpoint endpoint = FetchEndpoints.repositoryMethod(method.getDeclaringClass(), method.getName());
        tracker.beginExplicitFetch(endpoint);
        try {
            return joinPoint.proceed();
        } finally {
            tracker.endExplicitFetch();
        }
    }
}

