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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import feign.Capability;
import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.Target;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import org.jspecify.annotations.Nullable;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;

final class FeignClientFactoryBean implements FactoryBean<Object> {


	private final Class<?> type;

	private final String contextId;

	private final String name;

	private final String url;

	private @Nullable NamedContextFactory contexts;

	private @Nullable Object client;


	FeignClientFactoryBean(Class<?> type, String contextId, String name, String url) {
		this.type = type;
		this.contextId = contextId;
		this.name = name;
		this.url = url;
	}

	public void setContexts(NamedContextFactory contexts) {
		this.contexts = contexts;
	}

	@Override
	public synchronized Object getObject() {
		if (this.client == null) {
			Assert.state(this.contexts != null, "Named context factory is required");
			ApplicationContext context = this.contexts.getContext(this.contextId);
			Feign.Builder builder = getOrDefault(context, Feign.Builder.class, Feign.builder());
			builder.contract(getOrDefault(context, Contract.class, new SpringMvcContract(context.getEnvironment())));
			HttpMessageConverterCodec codec = new HttpMessageConverterCodec();
			builder.encoder(getOrDefault(context, Encoder.class, codec));
			builder.decoder(getOrDefault(context, Decoder.class, codec));
			builder.retryer(getOrDefault(context, Retryer.class, Retryer.NEVER_RETRY));
			apply(context, Client.class, builder::client);
			apply(context, Request.Options.class, builder::options);
			apply(context, ErrorDecoder.class, builder::errorDecoder);
			apply(context, Logger.class, builder::logger);
			apply(context, Logger.Level.class, builder::logLevel);
			Map<String, RequestInterceptor> interceptors = new LinkedHashMap<>(
					BeanFactoryUtils.beansOfTypeIncludingAncestors(context, RequestInterceptor.class));
			List<RequestInterceptor> ordered = new ArrayList<>(interceptors.values());
			AnnotationAwareOrderComparator.sort(ordered);
			builder.requestInterceptors(ordered);
			BeanFactoryUtils.beansOfTypeIncludingAncestors(context, Capability.class).values()
					.forEach(builder::addCapability);
			this.client = builder.target(new Target.HardCodedTarget<>(this.type, this.name, this.url));
		}
		return this.client;
	}

	private <T> T getOrDefault(ApplicationContext context, Class<T> type, T defaultValue) {
		T bean = find(context, type);
		return (bean != null ? bean : defaultValue);
	}

	private <T> void apply(ApplicationContext context, Class<T> type, Consumer<T> consumer) {
		T bean = find(context, type);
		if (bean != null) {
			consumer.accept(bean);
		}
	}

	private <T> @Nullable T find(ApplicationContext context, Class<T> type) {
		if (context.getBeanNamesForType(type).length > 0) {
			return context.getBean(type);
		}
		ApplicationContext parent = context.getParent();
		return (parent != null ? find(parent, type) : null);
	}

	@Override
	public Class<?> getObjectType() {
		return this.type;
	}

}
