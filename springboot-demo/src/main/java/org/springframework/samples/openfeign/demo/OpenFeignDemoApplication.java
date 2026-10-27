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

package org.springframework.samples.openfeign.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.openfeign.EnableFeignClients;
import org.springframework.samples.openfeign.client.ReadInventoryClient;

/**
 * Run the self-contained OpenFeign demonstration.
 * @author Spring Framework Contributors
 * @since 7.1
 */
@SpringBootApplication(proxyBeanMethods = false)
@EnableFeignClients(basePackageClasses = ReadInventoryClient.class)
public final class OpenFeignDemoApplication {


	private OpenFeignDemoApplication() {
	}



	/**
	 * Start the application.
	 * @since 7.1
	 */
	public static void main(String[] args) {
		SpringApplication.run(OpenFeignDemoApplication.class, args);
	}

}
