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
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.sun.net.httpserver.HttpServer;
import feign.RequestInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.openfeign.scan.ScannedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

class FeignClientsIntegrationTests {

	private HttpServer server;

	private AnnotationConfigApplicationContext context;

	@BeforeEach
	void startServer() throws IOException {
		this.server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		this.server.createContext("/api", exchange -> {
			String response = exchange.getRequestURI() + "|" + exchange.getRequestHeaders().getFirst("X-Client") +
					"|" + exchange.getRequestHeaders().getFirst("X-Request") + "|" +
					new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
			byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().add("Content-Type", "text/plain;charset=UTF-8");
			exchange.sendResponseHeaders(200, bytes.length);
			try (var body = exchange.getResponseBody()) {
				body.write(bytes);
			}
		});
		this.server.start();
		this.context = new AnnotationConfigApplicationContext();
		this.context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", Map.of(
				"target", "http://127.0.0.1:" + this.server.getAddress().getPort())));
	}

	@AfterEach
	void stopServer() {
		this.context.close();
		this.server.stop(0);
	}

	@Test
	void clientsWithSameServiceNameUseIndependentConfigurations() {
		this.context.register(ClientsConfig.class);
		this.context.refresh();
		FirstClient first = this.context.getBean(FirstClient.class);
		SecondClient second = this.context.getBean(SecondClient.class);
		assertThat(first.get("42", "hello world", "header"))
				.isEqualTo("/api/items/42?q=hello%20world|first|header|");
		assertThat(second.get()).isEqualTo("/api/ping|second|null|");
		assertThat(this.context.getBean("firstFeignClient")).isSameAs(first);
		assertThat(this.context.getBean("secondAlias")).isSameAs(second);
		assertThat(this.context.getBeansOfType(RequestInterceptor.class)).isEmpty();
		NamedContextFactory factory = this.context.getBean(NamedContextFactory.class);
		var child = factory.getContext("first");
		this.context.close();
		assertThat(child.isActive()).isFalse();
	}

	@Test
	void bodyUsesSpringMessageConverters() {
		this.context.register(ClientsConfig.class);
		this.context.refresh();
		assertThat(this.context.getBean(FirstClient.class).post("你好"))
				.isEqualTo("/api/echo|first|null|你好");
	}

	@Test
	void conflictingContextIdFailsAtStartup() {
		this.context.register(ConflictConfig.class);
		assertThatIllegalStateException().isThrownBy(this.context::refresh)
				.withMessageContaining("Conflicting configuration");
	}

	@Test
	void packageScanningRegistersClientsAndFallsBackToName() {
		this.context.register(ScanningConfig.class);
		this.context.refresh();
		assertThat(this.context.getBean(ScannedClient.class).get()).isEqualTo("/api/ping|null|null|");
		assertThat(this.context.getBean("scannedFeignClient")).isInstanceOf(ScannedClient.class);
		assertThat(this.context.getBean(NamedContextFactory.class).getContextNames()).contains("scanned");
	}

	@Test
	void parentInterceptorsCanBeExcludedPerClient() {
		this.context.registerBean("parentInterceptor", RequestInterceptor.class,
				() -> template -> template.header("X-Client", "parent"));
		this.context.register(InheritanceConfig.class);
		this.context.refresh();
		assertThat(this.context.getBean(InheritedClient.class).get()).isEqualTo("/api/ping|parent|null|");
		assertThat(this.context.getBean(IsolatedClient.class).get()).isEqualTo("/api/ping|null|null|");
	}



	@Configuration(proxyBeanMethods = false)
	@EnableFeignClients(clients = {FirstClient.class, SecondClient.class})
	static class ClientsConfig {
	}

	@Configuration(proxyBeanMethods = false)
	@EnableFeignClients(clients = {FirstClient.class, ConflictingClient.class})
	static class ConflictConfig {
	}

	@Configuration(proxyBeanMethods = false)
	@EnableFeignClients(basePackageClasses = ScannedClient.class)
	static class ScanningConfig {
	}

	@Configuration(proxyBeanMethods = false)
	@EnableFeignClients(clients = {InheritedClient.class, IsolatedClient.class})
	static class InheritanceConfig {
	}

	@FeignClient(name = "inherited", url = "${target}", path = "/api")
	interface InheritedClient {
		@GetMapping("/ping")
		String get();
	}

	@FeignClient(name = "isolated", url = "${target}", path = "/api", inheritParentConfiguration = false)
	interface IsolatedClient {
		@GetMapping("/ping")
		String get();
	}


	@FeignClient(name = "service", contextId = "first", url = "${target}", path = "/api",
			configuration = FirstConfig.class)
	interface FirstClient {
		@GetMapping("/items/{id}")
		String get(@PathVariable("id") String id, @RequestParam("q") String query,
				@RequestHeader("X-Request") String header);

		@PostMapping(path = "/echo", consumes = "text/plain;charset=UTF-8")
		String post(@RequestBody String body);
	}

	@FeignClient(name = "service", contextId = "second", url = "${target}", path = "/api",
			configuration = SecondConfig.class, qualifiers = "secondAlias", primary = false)
	interface SecondClient {
		@GetMapping("/ping")
		String get();
	}

	@FeignClient(name = "service", contextId = "first", url = "${target}")
	interface ConflictingClient {
		@GetMapping("/ping")
		String get();
	}

	@Configuration(proxyBeanMethods = false)
	static class FirstConfig {
		@Bean
		RequestInterceptor firstInterceptor() {
			return template -> template.header("X-Client", "first");
		}
	}

	@Configuration(proxyBeanMethods = false)
	static class SecondConfig {
		@Bean
		RequestInterceptor secondInterceptor() {
			return template -> template.header("X-Client", "second");
		}
	}

}
