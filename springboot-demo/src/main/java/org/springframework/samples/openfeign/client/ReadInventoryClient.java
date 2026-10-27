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
import org.springframework.samples.openfeign.configuration.ReadClientConfiguration;
import org.springframework.samples.openfeign.model.Greeting;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Demonstrate the read configuration for the shared inventory service.
 * @author Spring Framework Contributors
 * @since 7.1
 */
@FeignClient(name = "inventory", contextId = "inventoryRead", url = "${demo.downstream-url}",
		path = "/api", configuration = ReadClientConfiguration.class)
public interface ReadInventoryClient {

	/**
	 * Retrieve a greeting with path and query parameters.
	 * @since 7.1
	 */
	@GetMapping("/greetings/{name}")
	Greeting greeting(@PathVariable("name") String name, @RequestParam("salutation") String salutation);

	/**
	 * Trigger a downstream HTTP error.
	 * @since 7.1
	 */
	@GetMapping("/failure")
	String failure();

}
