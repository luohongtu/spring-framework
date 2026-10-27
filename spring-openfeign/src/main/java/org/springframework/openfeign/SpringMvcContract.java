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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import feign.Contract;
import feign.MethodMetadata;
import feign.Request;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Adapt Spring MVC request mappings to OpenFeign method metadata.
 * @author Spring Framework Contributors
 * @since 7.1
 */
public class SpringMvcContract extends Contract.BaseContract {


	private final Environment environment;


	/**
	 * Create a contract using the standard environment.
	 * @since 7.1
	 */
	public SpringMvcContract() {
		this(new StandardEnvironment());
	}

	/**
	 * Create a contract with placeholder resolution against the given environment.
	 * @since 7.1
	 */
	public SpringMvcContract(Environment environment) {
		Assert.notNull(environment, "Environment must not be null");
		this.environment = environment;
	}


	@Override
	protected void processAnnotationOnClass(MethodMetadata data, Class<?> type) {
		RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(type, RequestMapping.class);
		if (mapping != null) {
			applyMapping(data, mapping);
		}
	}

	@Override
	protected void processAnnotationOnMethod(MethodMetadata data, Annotation annotation, Method method) {
		if (annotation instanceof RequestMapping ||
				AnnotatedElementUtils.hasAnnotation(annotation.annotationType(), RequestMapping.class)) {
			RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
			Assert.state(mapping != null, "Request mapping must exist");
			applyMapping(data, mapping);
			if (mapping.method().length == 0 && data.template().method() == null) {
				data.template().method(Request.HttpMethod.GET);
			}
		}
	}

	private void applyMapping(MethodMetadata data, RequestMapping mapping) {
		Assert.isTrue(mapping.method().length <= 1, "Only one HTTP method is supported");
		Assert.isTrue(mapping.path().length <= 1, "Only one request path is supported");
		Assert.isTrue(mapping.consumes().length <= 1, "Only one consumes media type is supported");
		if (mapping.method().length == 1) {
			data.template().method(Request.HttpMethod.valueOf(mapping.method()[0].name()));
		}
		if (mapping.path().length == 1) {
			String path = resolve(mapping.path()[0]);
			data.template().uri((path.startsWith("/") ? path : "/" + path), true);
		}
		if (mapping.consumes().length == 1) {
			data.template().header("Content-Type", resolve(mapping.consumes()[0]));
		}
		if (mapping.produces().length > 0) {
			data.template().header("Accept", mapping.produces());
		}
		for (String header : mapping.headers()) {
			String[] pair = StringUtils.split(resolve(header), "=");
			Assert.isTrue(pair != null && !pair[0].endsWith("!"), "Headers must use name=value syntax");
			data.template().header(pair[0], pair[1]);
		}
		for (String parameter : mapping.params()) {
			String[] pair = StringUtils.split(resolve(parameter), "=");
			Assert.isTrue(pair != null && !pair[0].endsWith("!"), "Parameters must use name=value syntax");
			data.template().query(pair[0], pair[1]);
		}
	}

	@Override
	protected boolean processAnnotationsOnParameter(MethodMetadata data, Annotation[] annotations, int index) {
		for (Annotation candidate : annotations) {
			Annotation annotation = AnnotationUtils.synthesizeAnnotation(candidate, data.method());
			String name;
			if (annotation instanceof PathVariable variable) {
				name = parameterName(variable.name(), data, index);
				Assert.isTrue(data.template().url().contains("{" + name + "}"),
						"Path variable must occur in the request path: " + name);
			}
			else if (annotation instanceof RequestParam parameter) {
				if (Map.class.isAssignableFrom(data.method().getParameterTypes()[index])) {
					Assert.state(data.queryMapIndex() == null, "Only one query map is supported");
					data.queryMapIndex(index);
					return true;
				}
				name = parameterName(parameter.name(), data, index);
				data.template().query(name, "{" + name + "}");
			}
			else if (annotation instanceof RequestHeader header) {
				if (Map.class.isAssignableFrom(data.method().getParameterTypes()[index])) {
					Assert.state(data.headerMapIndex() == null, "Only one header map is supported");
					data.headerMapIndex(index);
					return true;
				}
				name = parameterName(header.name(), data, index);
				data.template().header(name, "{" + name + "}");
			}
			else if (annotation instanceof CookieValue cookie) {
				name = parameterName(cookie.name(), data, index);
				String value = name + "={" + name + "}";
				Collection<String> cookies = data.template().headers().get("Cookie");
				if (cookies != null) {
					value = String.join("; ", cookies) + "; " + value;
				}
				data.template().removeHeader("Cookie");
				data.template().header("Cookie", value);
			}
			else {
				Assert.isTrue(!annotation.annotationType().getPackageName().equals(
						"org.springframework.web.bind.annotation") ||
						annotation instanceof RequestBody,
						"Unsupported request parameter annotation: " + annotation.annotationType().getSimpleName());
				continue;
			}
			nameParam(data, name, index);
			return true;
		}
		return false;
	}

	private String parameterName(String declaredName, MethodMetadata data, int index) {
		String name = resolve(declaredName);
		if (!StringUtils.hasText(name)) {
			MethodParameter parameter = new MethodParameter(data.method(), index);
			parameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
			String discovered = parameter.getParameterName();
			Assert.hasText(discovered, "Specify a request parameter name or compile with -parameters");
			name = discovered;
		}
		return name;
	}

	private String resolve(String value) {
		return this.environment.resolveRequiredPlaceholders(value);
	}

}
