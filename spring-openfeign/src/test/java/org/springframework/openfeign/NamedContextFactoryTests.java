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

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

class NamedContextFactoryTests {

	@Test
	void contextsAreLazyIsolatedAndDestroyed() {
		NamedContextFactory factory = new NamedContextFactory();
		factory.register("one", new Class<?>[] {ClientConfig.class}, true);
		factory.register("two", new Class<?>[] {ClientConfig.class}, true);
		assertThat(factory.getContextNames()).isEmpty();
		var one = factory.getContext("one");
		assertThat(factory.getContext("one")).isSameAs(one);
		assertThat(one.getBean(Marker.class)).isNotSameAs(factory.getContext("two").getBean(Marker.class));
		Marker marker = one.getBean(Marker.class);
		factory.destroy();
		assertThat(marker.destroyed).isTrue();
		assertThat(one.isActive()).isFalse();
		assertThat(factory.getContextNames()).isEmpty();
		assertThatIllegalStateException().isThrownBy(() -> factory.getContext("one"));
	}

	@Test
	void localConfigurationOverridesDefaultsByTypeAndName() {
		try (var parent = new AnnotationConfigApplicationContext()) {
			parent.registerBean("parentValue", String.class, () -> "parent");
			parent.refresh();
			NamedContextFactory factory = new NamedContextFactory();
			factory.setApplicationContext(parent);
			factory.addDefaultConfiguration(DefaultConfig.class);
			factory.register("local", new Class<?>[] {LocalConfig.class}, true);
			assertThat(factory.getContext("local").getBean(String.class)).isEqualTo("local");
			assertThat(factory.getContext("local").getBean("shared")).isEqualTo(2);
			factory.register("default", new Class<?>[0], true);
			assertThat(factory.getContext("default").getBean(String.class)).isEqualTo("default");
			factory.destroy();
		}
	}

	@Test
	void disablingInheritanceRetainsEnvironmentAndDefaults() {
		try (var parent = new AnnotationConfigApplicationContext()) {
			parent.getEnvironment().getPropertySources().addFirst(
					new MapPropertySource("test", Map.of("key", "value")));
			parent.registerBean("parentValue", String.class, () -> "parent");
			parent.refresh();
			NamedContextFactory factory = new NamedContextFactory();
			factory.setApplicationContext(parent);
			factory.register("isolated", new Class<?>[0], false);
			var context = factory.getContext("isolated");
			assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
					.isThrownBy(() -> context.getBean(String.class));
			assertThat(context.getEnvironment().getProperty("key")).isEqualTo("value");
			assertThat(context.getEnvironment().getProperty("spring.openfeign.contextId")).isEqualTo("isolated");
			factory.destroy();
			assertThat(parent.isActive()).isTrue();
		}
	}

	@Test
	void conflictingContextSpecificationsAreRejected() {
		NamedContextFactory factory = new NamedContextFactory();
		factory.register("same", new Class<?>[] {ClientConfig.class}, true);
		factory.register("same", new Class<?>[] {ClientConfig.class}, true);
		assertThatIllegalStateException().isThrownBy(() -> factory.register("same", new Class<?>[0], true))
				.withMessageContaining("Conflicting configuration");
		factory.destroy();
	}


	@Configuration(proxyBeanMethods = false)
	static class ClientConfig {
		@Bean
		Marker marker() {
			return new Marker();
		}
	}

	@Configuration(proxyBeanMethods = false)
	static class DefaultConfig {
		@Bean
		String defaultValue() {
			return "default";
		}
		@Bean
		Integer shared() {
			return 1;
		}
	}

	@Configuration(proxyBeanMethods = false)
	static class LocalConfig {
		@Bean
		String localValue() {
			return "local";
		}
		@Bean
		Integer shared() {
			return 2;
		}
	}

	static class Marker implements DisposableBean {
		final AtomicBoolean destroyed = new AtomicBoolean();
		@Override
		public void destroy() {
			this.destroyed.set(true);
		}
	}

}
