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
@ConfigurationProperties(prefix = "application.image", ignoreUnknownFields = true)
public class ImageProperties {
	@Value("${application.image.nail.path}")
	private String imageNailPath;
	@Value("${application.image.nail.width}")
	private Integer imageNailWidth = 240;
	@Value("${application.image.nail.height}")
	private Integer imageNailHeight = 160;

	@Value("${application.image.small.path}")
	private String imageSmallPath;
	@Value("${application.image.small.width}")
	private Integer imageSmallWidth = 480;
	@Value("${application.image.small.height}")
	private Integer imageSmallHeight = 320;

	@Value("${application.image.origin.width}")
	private Integer imageOriginWidth = 0;
	@Value("${application.image.origin.height}")
	private Integer imageOriginHeight = 0;

	public String getImageNailPath() {
		return imageNailPath;
	}
	public Integer getImageNailWidth() {
		return imageNailWidth;
	}
	public Integer getImageNailHeight() {
		return imageNailHeight;
	}
	public String getImageSmallPath() {
		return imageSmallPath;
	}
	public Integer getImageSmallWidth() {
		return imageSmallWidth;
	}
	public Integer getImageSmallHeight() {
		return imageSmallHeight;
	}

	public Integer getImageOriginWidth() {
		return imageOriginWidth;
	}
	public Integer getImageOriginHeight() {
		return imageOriginHeight;
	}
}
