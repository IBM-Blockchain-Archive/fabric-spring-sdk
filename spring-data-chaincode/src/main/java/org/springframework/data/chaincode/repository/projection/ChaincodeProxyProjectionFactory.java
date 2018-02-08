package org.springframework.data.chaincode.repository.projection;

import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.projection.MethodInterceptorFactory;
import org.springframework.data.projection.ProjectionInformation;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

public class ChaincodeProxyProjectionFactory extends SpelAwareProxyProjectionFactory {
    private static final Logger logger = LoggerFactory.getLogger(ChaincodeProxyProjectionFactory.class);

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        super.setBeanFactory(beanFactory);
    }

    @Override
    protected ProjectionInformation createProjectionInformation(Class<?> projectionType) {
        logger.debug("Creating project information for {}", projectionType);
        return super.createProjectionInformation(projectionType);
    }

    @Override
    protected MethodInterceptor postProcessAccessorInterceptor(MethodInterceptor interceptor, Object source, Class<?> projectionType) {
        return super.postProcessAccessorInterceptor(interceptor, source, projectionType);
    }

    @Override
    public void registerMethodInvokerFactory(MethodInterceptorFactory factory) {
        super.registerMethodInvokerFactory(factory);
    }

    @Override
    public <T> T createProjection(Class<T> projectionType, Object source) {
        logger.debug("Creating projection for type {}, source {}", projectionType, source);
        return super.createProjection(projectionType, source);
    }

    @Override
    public <T> T createProjection(Class<T> projectionType) {
        logger.debug("Creating projection for type {}, source {}", projectionType);
        return super.createProjection(projectionType);
    }

    @Override
    public <T> T createNullableProjection(Class<T> projectionType, Object source) {
        return super.createNullableProjection(projectionType, source);
    }
}
