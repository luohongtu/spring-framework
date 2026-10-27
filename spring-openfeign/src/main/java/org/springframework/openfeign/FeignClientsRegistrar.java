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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

final class FeignClientsRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware {


	private static final String FACTORY_BEAN_NAME = NamedContextFactory.class.getName();

	private Environment environment = new StandardEnvironment();

	private ResourceLoader resourceLoader = new DefaultResourceLoader();


	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		EnableFeignClients attributes = metadata.getAnnotations().get(EnableFeignClients.class).synthesize();
		NamedContextFactory factory;
		if (registry.containsBeanDefinition(FACTORY_BEAN_NAME)) {
			Object existing = registry.getBeanDefinition(FACTORY_BEAN_NAME).getAttribute("factory");
			Assert.state(existing instanceof NamedContextFactory, "Conflicting named context factory");
			factory = (NamedContextFactory) existing;
		}
		else {
			factory = new NamedContextFactory();
			RootBeanDefinition definition = new RootBeanDefinition(NamedContextFactory.class, () -> factory);
			definition.setAttribute("factory", factory);
			registry.registerBeanDefinition(FACTORY_BEAN_NAME, definition);
		}
		factory.addDefaultConfiguration(attributes.defaultConfiguration());
		Set<Class<?>> clients = new LinkedHashSet<>(List.of(attributes.clients()));
		if (clients.isEmpty()) {
			Set<String> packages = new LinkedHashSet<>();
			for (String basePackage : attributes.basePackages()) {
				packages.add(this.environment.resolveRequiredPlaceholders(basePackage));
			}
			for (Class<?> baseClass : attributes.basePackageClasses()) {
				packages.add(ClassUtils.getPackageName(baseClass));
			}
			if (packages.isEmpty()) {
				packages.add(ClassUtils.getPackageName(metadata.getClassName()));
			}
			ClassPathScanningCandidateComponentProvider scanner =
					new ClassPathScanningCandidateComponentProvider(false, this.environment) {
						@Override
						protected boolean isCandidateComponent(AnnotatedBeanDefinition definition) {
							return definition.getMetadata().isIndependent() && definition.getMetadata().isInterface();
						}
					};
			scanner.setResourceLoader(this.resourceLoader);
			scanner.addIncludeFilter(new AnnotationTypeFilter(FeignClient.class));
			for (String basePackage : packages) {
				for (BeanDefinition candidate :
						scanner.findCandidateComponents(basePackage)) {
					String className = candidate.getBeanClassName();
					Assert.state(className != null, "Client class name must exist");
					clients.add(ClassUtils.resolveClassName(className, this.resourceLoader.getClassLoader()));
				}
			}
		}
		for (Class<?> client : clients) {
			registerClient(client, registry, factory);
		}
	}

	private void registerClient(Class<?> client, BeanDefinitionRegistry registry, NamedContextFactory factory) {
		Assert.isTrue(client.isInterface(), "Feign client must be an interface");
		FeignClient annotation = AnnotatedElementUtils.findMergedAnnotation(client, FeignClient.class);
		Assert.notNull(annotation, "Client must declare @FeignClient");
		String name = resolve(annotation.name());
		Assert.hasText(name, "Feign client name must not be empty");
		String contextId = resolve(annotation.contextId());
		if (!StringUtils.hasText(contextId)) {
			contextId = name;
		}
		factory.register(contextId, annotation.configuration(), annotation.inheritParentConfiguration());
		String url = resolve(annotation.url());
		Assert.isTrue(url.startsWith("http://") || url.startsWith("https://"),
				"Feign client URL must use HTTP or HTTPS");
		String path = resolve(annotation.path());
		if (StringUtils.hasText(path)) {
			url = (url.endsWith("/") ? url.substring(0, url.length() - 1) : url) +
					(path.startsWith("/") ? path : "/" + path);
		}
		RootBeanDefinition definition = new RootBeanDefinition(FeignClientFactoryBean.class);
		definition.getConstructorArgumentValues().addIndexedArgumentValue(0, client);
		definition.getConstructorArgumentValues().addIndexedArgumentValue(1, contextId);
		definition.getConstructorArgumentValues().addIndexedArgumentValue(2, name);
		definition.getConstructorArgumentValues().addIndexedArgumentValue(3, url);
		definition.getPropertyValues().add("contexts", factory);
		definition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, client);
		definition.setPrimary(annotation.primary());
		definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
		Assert.state(!registry.containsBeanDefinition(client.getName()), "Feign client already registered: " + client);
		registry.registerBeanDefinition(client.getName(), definition);
		String[] qualifiers = annotation.qualifiers();
		if (qualifiers.length == 0) {
			qualifiers = new String[] {contextId + "FeignClient"};
		}
		for (String qualifier : qualifiers) {
			String alias = resolve(qualifier);
			Assert.hasText(alias, "Feign client qualifier must not be empty");
			Assert.state(!registry.isBeanNameInUse(alias), "Feign client qualifier already in use: " + alias);
			registry.registerAlias(client.getName(), alias);
		}
	}

	private String resolve(String value) {
		return this.environment.resolveRequiredPlaceholders(value);
	}

}
