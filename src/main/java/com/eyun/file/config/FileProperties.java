package com.eyun.file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Properties specific to upload files.
 * <p>
 * Properties are configured in the application.yml file.
 */
@Configuration
@ConfigurationProperties(prefix = "application.file", ignoreUnknownFields = true)
public class FileProperties {

	@Value("${application.file.location}")
	private String location;
	@Value("${application.file.output}")
	private String pathOutput;

	public String getLocation() {
		return location;
	}

	public String getPathOutput() {
		return pathOutput;
	}
}
