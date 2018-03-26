package com.eyun.file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Properties specific to JHipster.
 * <p>
 * Properties are configured in the application.yml file.
 */
@Configuration
@ConfigurationProperties(prefix = "application.oss", ignoreUnknownFields = true)
public class OssProperties {
	final String endpoint;
	final String access_id;
	final String access_key;
	final String bucket_name;
	public OssProperties(@Value("${application.oss.endpoint}") String endpoint,
                         @Value("${application.oss.access_id}") String access_id,
                         @Value("${application.oss.access_key}") String access_key,
                         @Value("${application.oss.bucket_name}") String bucket_name) {
		this.endpoint = endpoint;
		this.access_id = access_id;
		this.access_key = access_key;
		this.bucket_name = bucket_name;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public String getAccess_id() {
		return access_id;
	}

	public String getAccess_key() {
		return access_key;
	}

	public String getBucket_name() {
		return bucket_name;
	}

}
