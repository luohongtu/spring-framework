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

package org.springframework.samples.openfeign.client;

import org.springframework.openfeign.FeignClient;
import org.springframework.samples.openfeign.configuration.WriteClientConfiguration;
import org.springframework.samples.openfeign.model.Greeting;
import org.springframework.samples.openfeign.model.MessageReceipt;
import org.springframework.samples.openfeign.model.MessageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Demonstrate the write configuration for the shared inventory service.
 * @author Spring Framework Contributors
 * @since 7.1
 */
@FeignClient(name = "inventory", contextId = "inventoryWrite", url = "${demo.downstream-url}",
		path = "/api", configuration = WriteClientConfiguration.class)
public interface WriteInventoryClient {

	/**
	 * Retrieve a greeting with path and query parameters.
	 * @since 7.1
	 */
	@GetMapping("/greetings/{name}")
	Greeting greeting(@PathVariable("name") String name, @RequestParam("salutation") String salutation);

	/**
	 * Send and receive a JSON payload.
	 * @since 7.1
	 */
	@PostMapping(path = "/messages", consumes = "application/json")
	MessageReceipt send(@RequestBody MessageRequest request);

}
