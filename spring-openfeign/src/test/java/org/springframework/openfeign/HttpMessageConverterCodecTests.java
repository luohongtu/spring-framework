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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import org.junit.jupiter.api.Test;

import org.springframework.core.ParameterizedTypeReference;

import static org.assertj.core.api.Assertions.assertThat;

class HttpMessageConverterCodecTests {

	private final HttpMessageConverterCodec codec = new HttpMessageConverterCodec();

	@Test
	void stringBodyPreservesCharsetAndBytes() {
		RequestTemplate template = new RequestTemplate();
		template.header("Content-Type", "text/plain;charset=UTF-8");
		this.codec.encode("你好", String.class, template);
		assertThat(new String(template.body(), StandardCharsets.UTF_8)).isEqualTo("你好");
		assertThat(template.headers()).containsKey("Content-Type");
	}

	@Test
	void genericJsonResponseUsesMessageConverters() throws IOException {
		try (Response response = response("[{\"name\":\"Spring\"}]", "application/json")) {
			Object result = this.codec.decode(response,
					new ParameterizedTypeReference<List<Map<String, String>>>() {}.getType());
			assertThat(result).isEqualTo(List.of(Map.of("name", "Spring")));
		}
	}

	@Test
	void jsonRequestUsesMessageConverters() {
		RequestTemplate template = new RequestTemplate();
		template.header("Content-Type", "application/json");
		this.codec.encode(Map.of("name", "Spring"), Map.class, template);
		assertThat(new String(template.body(), StandardCharsets.UTF_8)).isEqualTo("{\"name\":\"Spring\"}");
	}

	@Test
	void noContentHasNoDecodedBody() throws IOException {
		try (Response response = response("", "text/plain").toBuilder().status(204).build()) {
			assertThat(this.codec.decode(response, String.class)).isNull();
		}
	}

	@Test
	void emptyJsonResponseHasNoDecodedBody() throws IOException {
		try (Response response = response("", "application/json")) {
			assertThat(this.codec.decode(response, Map.class)).isNull();
		}
	}

	private Response response(String body, String contentType) {
		return Response.builder().status(200).reason("OK")
				.headers(Map.of("Content-Type", List.of(contentType)))
				.request(Request.create(Request.HttpMethod.GET, "http://localhost/", Map.of(),
						(byte[]) null, StandardCharsets.UTF_8, new RequestTemplate()))
				.body(body, StandardCharsets.UTF_8).build();
	}

}
