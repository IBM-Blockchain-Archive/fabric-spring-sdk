package org.springframework.data.chaincode.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.chaincode.repository.Chaincode;

import java.lang.reflect.Method;

public class FabricEventsAnnotationsBeanPostProcessor implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(FabricEventsAnnotationsBeanPostProcessor.class);

    @Autowired
    private FabricEventsListenersRegistry registry;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        for (Method method : bean.getClass().getMethods()) {
            ChaincodeEventListener cel = method.getAnnotation(ChaincodeEventListener.class);
            BlockEventListener bel = method.getAnnotation(BlockEventListener.class);
            if (cel != null) {
                logger.debug("Found annotation ChaincodeEventListener in bean {} method {}", beanName, method.getName());
                Class<?> ccClass = cel.chaincode();
                Chaincode ccAnnotation = AnnotationUtils.findAnnotation(ccClass, Chaincode.class);
                if (ccAnnotation != null) {
                    String ccName = ccAnnotation.name();
                    String chName = ccAnnotation.channel();
                    String methodName = method.getName();
                    logger.debug("Connect chaincode events for channel {} and chaincode {} to bean {} and method {}", chName, ccName, beanName, methodName);
                    registry.registerChaincodeEventListener(chName, ccName, beanName, methodName);
                } else {
                    logger.warn("Class {} don't have correct annotations", ccClass.getName());
                }
            } else if (bel != null) {
                logger.debug("Found annotation BlockEventListener in bean {} method {}", beanName, method.getName());
                String chName = bel.channel();
                String methodName = method.getName();
                logger.debug("Connect chaincode events for channel {} to bean {} and method {}", chName, beanName, methodName);
                registry.registerBlockEventListener(chName, beanName, methodName);
            }
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
