package org.cofomo.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages ="org.cofomo.commons")
public class CofomoProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(CofomoProviderApplication.class, args);
	}

}
