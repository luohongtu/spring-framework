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

import org.springframework.http.ResponseEntity;
import org.springframework.samples.openfeign.model.Greeting;
import org.springframework.samples.openfeign.model.MessageReceipt;
import org.springframework.samples.openfeign.model.MessageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provide local endpoints called by the Feign clients.
 * @author Spring Framework Contributors
 * @since 7.1
 */
@RestController
@RequestMapping("/api")
public class InventoryController {

	/**
	 * Return a greeting and the invoking client's identity.
	 * @since 7.1
	 */
	@GetMapping("/greetings/{name}")
	public Greeting greeting(@PathVariable("name") String name,
			@RequestParam(name = "salutation", defaultValue = "Hello") String salutation,
			@RequestHeader(name = "X-Client-Role", defaultValue = "direct") String role) {

		return new Greeting(name, salutation + ", " + name + "!", role);
	}

	/**
	 * Echo a JSON message and the invoking client's identity.
	 * @since 7.1
	 */
	@PostMapping("/messages")
	public MessageReceipt send(@RequestBody MessageRequest request,
			@RequestHeader(name = "X-Client-Role", defaultValue = "direct") String role) {

		return new MessageReceipt(request.message(), role);
	}

	/**
	 * Return a deliberate downstream error.
	 * @since 7.1
	 */
	@GetMapping("/failure")
	public ResponseEntity<Map<String, String>> failure() {
		return ResponseEntity.status(404).body(Map.of("message", "Inventory item not found"));
	}

}
