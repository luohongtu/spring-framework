/*
 * Copyright 2002-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.openfeign;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * Declare an interface as an OpenFeign client.
 * @author Spring Framework Contributors
 * @since 7.1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignClient {

	/**
	 * Alias for {@link #name()}.
	 * @since 7.1
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * Service name, used as the context identifier when none is specified.
	 * @since 7.1
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * Identifier of the isolated client context.
	 * @since 7.1
	 */
	String contextId() default "";

	/**
	 * Absolute HTTP or HTTPS target URL, with support for placeholders.
	 * @since 7.1
	 */
	String url();

	/**
	 * Path prefix for all requests.
	 * @since 7.1
	 */
	String path() default "";

	/**
	 * Configuration classes registered only in this client's context.
	 * @since 7.1
	 */
	Class<?>[] configuration() default {};

	/**
	 * Additional qualifiers for injection.
	 * @since 7.1
	 */
	String[] qualifiers() default {};

	/**
	 * Whether the client bean is primary.
	 * @since 7.1
	 */
	boolean primary() default true;

	/**
	 * Whether client configuration may inherit beans from the parent context.
	 * @since 7.1
	 */
	boolean inheritParentConfiguration() default true;
}
