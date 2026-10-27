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

import java.util.List;
import java.util.Map;

import feign.MethodMetadata;
import org.junit.jupiter.api.Test;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class SpringMvcContractTests {

	@Test
	void mergedMappingsResolvePlaceholdersAndStaticValues() {
		StandardEnvironment environment = new StandardEnvironment();
		environment.getPropertySources().addFirst(new MapPropertySource("test", Map.of("root", "/api")));
		List<MethodMetadata> methods = new SpringMvcContract(environment).parseAndValidateMetadata(MappedClient.class);
		MethodMetadata method = methods.get(0);
		assertThat(method.template().method()).isEqualTo("GET");
		assertThat(method.template().url()).isEqualTo("/api/items/{id}?mode=full&query={query}");
		assertThat(method.template().headers()).containsKeys("X-Fixed", "X-Token", "Cookie", "Accept");
		assertThat(method.indexToName()).containsEntry(0, List.of("id")).containsEntry(1, List.of("query"));
	}

	@Test
	void mapsBecomeQueryAndHeaderMaps() {
		MethodMetadata method = new SpringMvcContract().parseAndValidateMetadata(MapClient.class).get(0);
		assertThat(method.queryMapIndex()).isZero();
		assertThat(method.headerMapIndex()).isEqualTo(1);
		assertThat(method.bodyIndex()).isNull();
	}

	@Test
	void ambiguousPathsAreRejected() {
		assertThatIllegalArgumentException().isThrownBy(() ->
				new SpringMvcContract().parseAndValidateMetadata(AmbiguousClient.class))
				.withMessageContaining("Only one request path");
	}

	@Test
	void missingPathVariableIsRejected() {
		assertThatIllegalArgumentException().isThrownBy(() ->
				new SpringMvcContract().parseAndValidateMetadata(MissingVariableClient.class))
				.withMessageContaining("Path variable must occur");
	}


	@RequestMapping("${root}")
	interface MappedClient {
		@GetMapping(path = "/items/{id}", params = "mode=full", headers = "X-Fixed=yes", produces = "text/plain")
		String get(@PathVariable("id") String id, @RequestParam("query") String query,
				@RequestHeader("X-Token") String token, @CookieValue("session") String session);
	}

	interface MapClient {
		@GetMapping("/map")
		String get(@RequestParam Map<String, String> query, @RequestHeader Map<String, String> headers);
	}

	interface AmbiguousClient {
		@GetMapping({"/one", "/two"})
		String get();
	}

	interface MissingVariableClient {
		@GetMapping("/one")
		String get(@PathVariable("id") String id);
	}

}
