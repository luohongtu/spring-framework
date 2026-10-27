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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.Assert;

/**
 * Manage lazily created, named child contexts with isolated configurations.
 * <p>Common configuration lives in an intermediate context so local beans
 * override defaults by type as well as by name.
 * @author Spring Framework Contributors
 * @since 7.1
 */
public class NamedContextFactory implements ApplicationContextAware, DisposableBean {


	private final Map<String, Specification> specifications = new LinkedHashMap<>();

	private final Map<String, AnnotationConfigApplicationContext> contexts = new LinkedHashMap<>();

	private final Map<String, AnnotationConfigApplicationContext> defaults = new LinkedHashMap<>();

	private final Set<Class<?>> defaultConfiguration = new LinkedHashSet<>();

	private @Nullable ApplicationContext parent;

	private boolean closed;


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.parent = applicationContext;
	}

	/**
	 * Register common configuration before any client context is created.
	 * @since 7.1
	 */
	public synchronized void addDefaultConfiguration(Class<?>... configuration) {
		Assert.state(this.contexts.isEmpty() && !this.closed, "Contexts already created or factory closed");
		this.defaultConfiguration.addAll(List.of(configuration));
	}

	/**
	 * Register a client specification, rejecting conflicting registrations.
	 * @since 7.1
	 */
	public synchronized void register(String name, Class<?>[] configuration, boolean inheritParent) {
		Assert.hasText(name, "Context name must not be empty");
		Assert.state(!this.closed, "Factory is closed");
		Specification specification = new Specification(List.of(configuration), inheritParent);
		Specification previous = this.specifications.putIfAbsent(name, specification);
		Assert.state(previous == null || previous.equals(specification),
				"Conflicting configuration for contextId '" + name + "'");
	}

	/**
	 * Return the named context, creating it on first access.
	 * @since 7.1
	 */
	public synchronized AnnotationConfigApplicationContext getContext(String name) {
		Assert.state(!this.closed, "Factory is closed");
		Assert.state(this.specifications.containsKey(name), "Unknown contextId '" + name + "'");
		AnnotationConfigApplicationContext existing = this.contexts.get(name);
		if (existing != null) {
			return existing;
		}
		Specification specification = this.specifications.get(name);
		Assert.state(specification != null, "Client specification must exist");
		AnnotationConfigApplicationContext common = createContext(name + ".defaults",
				(specification.inheritParent() ? this.parent : null));
		if (this.parent != null && this.parent.getEnvironment() instanceof ConfigurableEnvironment environment) {
			common.getEnvironment().merge(environment);
		}
		AnnotationConfigApplicationContext context = createContext(name, common);
		try {
			if (!this.defaultConfiguration.isEmpty()) {
				common.register(this.defaultConfiguration.toArray(Class<?>[]::new));
			}
			common.refresh();
			if (!specification.configuration().isEmpty()) {
				context.register(specification.configuration().toArray(Class<?>[]::new));
			}
			context.refresh();
			this.defaults.put(name, common);
			this.contexts.put(name, context);
			return context;
		}
		catch (RuntimeException | Error ex) {
			context.close();
			common.close();
			throw ex;
		}
	}

	private AnnotationConfigApplicationContext createContext(String name, @Nullable ApplicationContext parent) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.setDisplayName("FeignClient-" + name);
		if (parent != null) {
			context.setParent(parent);
		}
		context.getEnvironment().getPropertySources().addFirst(
				new MapPropertySource("feignClient", Map.of("spring.openfeign.contextId", name)));
		return context;
	}

	/**
	 * Return the identifiers of contexts already created.
	 * @since 7.1
	 */
	public synchronized Set<String> getContextNames() {
		return Set.copyOf(this.contexts.keySet());
	}

	@Override
	public synchronized void destroy() {
		this.closed = true;
		try {
			this.contexts.values().forEach(AnnotationConfigApplicationContext::close);
		}
		finally {
			this.defaults.values().forEach(AnnotationConfigApplicationContext::close);
			this.contexts.clear();
			this.defaults.clear();
		}
	}


	private record Specification(List<Class<?>> configuration, boolean inheritParent) {
	}

}
