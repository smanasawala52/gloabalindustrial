package com.alpha.interview.wizard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Configuration
public class GcsConfiguration {

	@Bean
	public Storage storage() {
		Storage storage = StorageOptions.getDefaultInstance().getService();
		try {
			Page<Bucket> buckets = storage.list();
			for (Bucket bucket : buckets.iterateAll()) {
				System.out.println("Bucket name: " + bucket.getName());
			}
		} catch (Exception e) {

		}
		return storage;
	}
}
