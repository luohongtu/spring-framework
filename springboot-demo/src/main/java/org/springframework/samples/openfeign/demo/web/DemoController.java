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

package org.springframework.samples.openfeign.demo.web;

import java.util.Map;

import feign.FeignException;

import org.springframework.http.ResponseEntity;
import org.springframework.samples.openfeign.client.ReadInventoryClient;
import org.springframework.samples.openfeign.client.WriteInventoryClient;
import org.springframework.samples.openfeign.model.Greeting;
import org.springframework.samples.openfeign.model.MessageReceipt;
import org.springframework.samples.openfeign.model.MessageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Trigger Feign calls from HTTP endpoints for interactive demonstrations.
 * @author Spring Framework Contributors
 * @since 7.1
 */
@RestController
@RequestMapping("/demo")
public class DemoController {


	private final ReadInventoryClient readClient;

	private final WriteInventoryClient writeClient;


	/**
	 * Create the demonstration endpoints.
	 * @since 7.1
	 */
	public DemoController(ReadInventoryClient readClient, WriteInventoryClient writeClient) {
		this.readClient = readClient;
		this.writeClient = writeClient;
	}


	/**
	 * Invoke the read client with path and query parameters.
	 * @since 7.1
	 */
	@GetMapping("/greetings/{name}")
	public Greeting greeting(@PathVariable("name") String name,
			@RequestParam(name = "salutation", defaultValue = "Hello") String salutation) {

		return this.readClient.greeting(name, salutation);
	}

	/**
	 * Invoke the write client with a JSON request body.
	 * @since 7.1
	 */
	@PostMapping("/messages")
	public MessageReceipt send(@RequestBody MessageRequest request) {
		return this.writeClient.send(request);
	}

	/**
	 * Compare isolated configurations for clients with the same service name.
	 * @since 7.1
	 */
	@GetMapping("/isolation")
	public Map<String, Greeting> isolation() {
		return Map.of("read", this.readClient.greeting("Spring", "Hello"),
				"write", this.writeClient.greeting("Spring", "Hello"));
	}

	/**
	 * Translate a downstream not-found response to a gateway error.
	 * @since 7.1
	 */
	@GetMapping("/error")
	public ResponseEntity<Map<String, Object>> error() {
		try {
			this.readClient.failure();
			return ResponseEntity.ok(Map.of("message", "Unexpected downstream success"));
		}
		catch (FeignException.NotFound ex) {
			return ResponseEntity.status(502).body(Map.of(
					"message", "Downstream request failed", "upstreamStatus", ex.status()));
		}
	}

}
