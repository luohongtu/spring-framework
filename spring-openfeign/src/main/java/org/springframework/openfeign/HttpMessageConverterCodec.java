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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import feign.RequestTemplate;
import feign.Response;
import feign.codec.Decoder;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.jspecify.annotations.Nullable;

import org.springframework.core.ResolvableType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.SmartHttpMessageConverter;
import org.springframework.util.Assert;

/**
 * Encode and decode Feign messages using Spring HTTP message converters.
 * @author Spring Framework Contributors
 * @since 7.1
 */
public class HttpMessageConverterCodec implements Encoder, Decoder {


	private final List<HttpMessageConverter<?>> converters;


	/**
	 * Create a codec with the default client message converters.
	 * @since 7.1
	 */
	public HttpMessageConverterCodec() {
		this(defaultConverters());
	}

	/**
	 * Create a codec with an explicit ordered list of converters.
	 * @since 7.1
	 */
	public HttpMessageConverterCodec(List<HttpMessageConverter<?>> converters) {
		Assert.notEmpty(converters, "Message converters must not be empty");
		this.converters = List.copyOf(converters);
	}


	private static List<HttpMessageConverter<?>> defaultConverters() {
		List<HttpMessageConverter<?>> converters = new ArrayList<>();
		HttpMessageConverters.forClient().registerDefaults().build().forEach(converters::add);
		return converters;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void encode(Object value, Type bodyType, RequestTemplate template) {
		HttpHeaders headers = new HttpHeaders();
		template.headers().forEach((name, values) -> headers.put(name, List.copyOf(values)));
		MediaType contentType = headers.getContentType();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		HttpOutputMessage message = new HttpOutputMessage() {
			@Override
			public OutputStream getBody() {
				return bytes;
			}
			@Override
			public HttpHeaders getHeaders() {
				return headers;
			}
		};
		try {
			for (HttpMessageConverter<?> candidate : this.converters) {
				if (candidate instanceof GenericHttpMessageConverter<?> generic &&
						generic.canWrite(bodyType, value.getClass(), contentType)) {
					((GenericHttpMessageConverter<Object>) generic).write(value, bodyType, contentType, message);
				}
				else if (candidate instanceof SmartHttpMessageConverter<?> smart &&
						smart.canWrite(ResolvableType.forType(bodyType), value.getClass(), contentType)) {
					((SmartHttpMessageConverter<Object>) smart).write(
							value, ResolvableType.forType(bodyType), contentType, message, null);
				}
				else if (candidate.canWrite(value.getClass(), contentType)) {
					((HttpMessageConverter<Object>) candidate).write(value, contentType, message);
				}
				else {
					continue;
				}
				headers.forEach((name, values) -> {
					template.removeHeader(name);
					template.header(name, values);
				});
				template.body(bytes.toByteArray(), StandardCharsets.UTF_8);
				return;
			}
		}
		catch (IOException ex) {
			throw new EncodeException("Could not encode request body", ex);
		}
		throw new EncodeException("No message converter for " + bodyType);
	}

	@Override
	public @Nullable Object decode(Response response, Type type) throws IOException {
		if (response.body() == null || response.status() == 204 || response.status() == 205) {
			return null;
		}
		HttpHeaders headers = new HttpHeaders();
		response.headers().forEach((name, values) -> headers.put(name, List.copyOf(values)));
		MediaType contentType = headers.getContentType();
		if (contentType == null) {
			contentType = MediaType.APPLICATION_OCTET_STREAM;
		}
		PushbackInputStream body = new PushbackInputStream(response.body().asInputStream());
		int firstByte = body.read();
		if (firstByte == -1) {
			return null;
		}
		body.unread(firstByte);
		HttpInputMessage message = new HttpInputMessage() {
			@Override
			public InputStream getBody() {
				return body;
			}
			@Override
			public HttpHeaders getHeaders() {
				return headers;
			}
		};
		for (HttpMessageConverter<?> candidate : this.converters) {
			if (candidate instanceof GenericHttpMessageConverter<?> generic &&
					generic.canRead(type, null, contentType)) {
				return generic.read(type, null, message);
			}
			if (candidate instanceof SmartHttpMessageConverter<?> smart &&
					smart.canRead(ResolvableType.forType(type), contentType)) {
				return smart.read(ResolvableType.forType(type), message, null);
			}
			if (type instanceof Class<?> bodyClass && candidate.canRead(bodyClass, contentType)) {
				@SuppressWarnings("unchecked")
				HttpMessageConverter<Object> converter = (HttpMessageConverter<Object>) candidate;
				return converter.read(bodyClass, message);
			}
		}
		throw new IOException("No message converter for " + type);
	}

}
