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

package org.springframework.samples.openfeign.tests;

import java.util.Map;

import feign.FeignException;
import feign.RequestInterceptor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.openfeign.NamedContextFactory;
import org.springframework.samples.openfeign.client.ReadInventoryClient;
import org.springframework.samples.openfeign.demo.OpenFeignDemoApplication;
import org.springframework.samples.openfeign.demo.web.InventoryController;
import org.springframework.samples.openfeign.model.Greeting;
import org.springframework.samples.openfeign.model.MessageReceipt;
import org.springframework.samples.openfeign.model.MessageRequest;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class OpenFeignDemoTests {


	private static ConfigurableApplicationContext backend;

	private static ConfigurableApplicationContext application;

	private static RestClient http;


	@BeforeAll
	static void startApplications() {
		// Start the real downstream controller first, using an OS-assigned port.
		backend = new SpringApplicationBuilder(BackendApplication.class).run("--server.port=0");
		int backendPort = ((WebServerApplicationContext) backend).getWebServer().getPort();
		try {
			// Feign resolves URLs during registration, before local.server.port is available.
			application = new SpringApplicationBuilder(OpenFeignDemoApplication.class).run(
					"--server.port=0", "--demo.downstream-url=http://127.0.0.1:" + backendPort);
			int port = ((WebServerApplicationContext) application).getWebServer().getPort();
			http = RestClient.create("http://127.0.0.1:" + port);
		}
		catch (RuntimeException | Error ex) {
			backend.close();
			throw ex;
		}
	}

	@AfterAll
	static void stopApplications() {
		try {
			if (application != null) {
				var child = application.getBean(NamedContextFactory.class).getContext("inventoryRead");
				application.close();
				assertThat(child.isActive()).isFalse();
			}
		}
		finally {
			if (backend != null) {
				backend.close();
			}
		}
	}

	@Test
	void getPassesPathAndQueryParameters() {
		Greeting greeting = http.get().uri("/demo/greetings/{name}?salutation={salutation}", "Spring", "Good morning")
				.retrieve().body(Greeting.class);
		assertThat(greeting).isEqualTo(new Greeting("Spring", "Good morning, Spring!", "read"));
	}

	@Test
	void postConvertsJsonRequestAndResponse() {
		MessageReceipt receipt = http.post().uri("/demo/messages").body(new MessageRequest("你好，Spring"))
				.retrieve().body(MessageReceipt.class);
		assertThat(receipt).isEqualTo(new MessageReceipt("你好，Spring", "write"));
	}

	@Test
	void sameServiceNameHasIsolatedContextConfigurations() {
		Map<String, Greeting> greetings = http.get().uri("/demo/isolation").retrieve()
				.body(new ParameterizedTypeReference<Map<String, Greeting>>() {});
		assertThat(greetings).containsEntry("read", new Greeting("Spring", "Hello, Spring!", "read"))
				.containsEntry("write", new Greeting("Spring", "Hello, Spring!", "write"));
		assertThat(application.getBean(NamedContextFactory.class).getContextNames())
				.containsExactlyInAnyOrder("inventoryRead", "inventoryWrite");
	}

	@Test
	void clientConfigurationDoesNotLeakIntoMainContext() {
		assertThat(application.getBeansOfType(RequestInterceptor.class)).isEmpty();
	}

	@Test
	void downstreamNotFoundBecomesFeignException() {
		ReadInventoryClient client = application.getBean(ReadInventoryClient.class);
		assertThatExceptionOfType(FeignException.NotFound.class).isThrownBy(client::failure)
				.satisfies(ex -> assertThat(ex.status()).isEqualTo(404));
	}

	@Test
	void demoTranslatesDownstreamErrorToBadGateway() {
		assertThatExceptionOfType(RestClientResponseException.class).isThrownBy(() ->
				http.get().uri("/demo/error").retrieve().body(String.class))
				.satisfies(ex -> {
					assertThat(ex.getStatusCode().value()).isEqualTo(502);
					assertThat(ex.getResponseBodyAsString()).contains("\"upstreamStatus\":404");
				});
	}


	// Explicit sources avoid scanning the demo clients into the downstream server.
	@EnableAutoConfiguration
	@Import(InventoryController.class)
	static class BackendApplication {
	}

}
