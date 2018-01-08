package org.springframework.data.chaincode.repository;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface Chaincode {
	/**
	 * Chaincode version 
	 */
	String version();

	/**
	 * Chaincode name
	 */
	String name();

	/**
	 * Chaincode code location in local file system
	 */
	String location() default "";

}
