package org.springframework.data.chaincode.events;

import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation mark {@link Component} method as listener for specific chaincode events, defined by chaincode repository
 * @author Gennady Laventman
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ChaincodeEventListener {
    /**
     * Chaincode interface class, see {@link org.springframework.data.chaincode.repository.Chaincode} annotation and {@link org.springframework.data.chaincode.repository.ChaincodeRepository} interface
     */
    Class<?> chaincode();
}
