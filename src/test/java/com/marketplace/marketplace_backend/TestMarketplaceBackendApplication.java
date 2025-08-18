package com.marketplace.marketplace_backend;

import org.springframework.boot.SpringApplication;

public class TestMarketplaceBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(MarketplaceBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
