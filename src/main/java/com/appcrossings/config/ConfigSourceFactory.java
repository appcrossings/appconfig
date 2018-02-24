package com.appcrossings.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigSourceFactory {

	private final static Logger logger = LoggerFactory.getLogger(ConfigSourceFactory.class);

	public static final String FILE_SYSTEM = "file";
	public static final String AWS_S3 = "s3";
	public static final String HTTPS = "http";

	private final static Map<String, ConfigSource> sources = new HashMap<>();

	public static ConfigSource buildConfigSource(final String source) {

		if (sources.containsKey(source.toLowerCase()))
			return sources.get(source.toLowerCase());

		ConfigSource cs = null;

		Class clazz = null;

		try {
			switch (source) {
			case FILE_SYSTEM:

				clazz = Class.forName("com.appcrossings.config.file.FilesystemSource");
				break;

			case HTTPS:

				clazz = Class.forName("com.appcrossings.config.file.FilesystemSource");
				break;

			default:
				throw new IllegalArgumentException("No config source known for " + source);

			}

			cs = (ConfigSource) clazz.newInstance();

		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			throw new RuntimeException(e);
		}

		if (!sources.containsKey(source.toLowerCase()))
			sources.put(source.toLowerCase(), cs);

		return cs;

	}

}
