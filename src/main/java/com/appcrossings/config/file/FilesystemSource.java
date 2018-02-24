package com.appcrossings.config.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import com.appcrossings.config.ConfigSource;
import com.google.common.base.Throwables;

public class FilesystemSource implements ConfigSource {

	public FilesystemSource() {

	}

	private final static Logger log = LoggerFactory.getLogger(FilesystemSource.class);

	protected Properties fetchProperties(String propertiesPath, String propertiesFileName) {

		Properties p = new Properties();
		final String fullPath = propertiesPath + "/" + propertiesFileName;

		InputStream stream = null;
		try {

			log.info("Attempting " + fullPath);
			if (isURL(fullPath)) {

				stream = new URL(fullPath).openStream();

			} else if (isFile(fullPath)) {

				stream = new FileInputStream(new File(fullPath));

			} else if (isClasspath(fullPath)) {

				String trimmed = fullPath.replaceFirst("classpath:", "");
				stream = this.getClass().getResourceAsStream(trimmed);

			} else if (isPath(fullPath)) {

				// could be relative to classpath or filesystem root (i.e. linux)

				stream = this.getClass().getResourceAsStream(fullPath);

				if (stream == null)
					stream = new FileInputStream(new File(fullPath));

			}

			if (stream != null) {
				log.info("Found " + fullPath);
				p.load(stream);
			} else {
				log.info("Not found. Retrying...");
				throw new FileNotFoundException(fullPath);
			}

		} catch (IOException e) {

			log.info("Not found. Retrying...");

		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (Exception e) {
				// nothing
			}
		}

		return p;
	}

	protected Properties downloadFile(Resource resource) {

		Properties p = new Properties();
		Throwable ex = null;
		for (int retry = 0; retry < 3; retry++) {

			try (InputStream stream2 = resource.getInputStream()) {

				p.load(stream2);
				break;

			} catch (IOException e) {
				ex = e;
				continue;

			}
		}

		// we've retried, file not found...no issue, keep going
		if (ex != null && StringUtils.hasText(ex.getMessage())
				&& (ex.getMessage().contains("code: 403") || ex.getMessage().contains("code: 404"))) {

			// Do nothing here since we'll just keep looping with parent
			// path anyway

		} else if (ex != null) {
			Throwables.propagate(ex);
		}

		return p;

	}

	public Properties loadHosts(String hostsFile) throws IllegalArgumentException {

		log.info("Fetching hosts file from path: " + hostsFile);

		Resource resource = new DefaultResourceLoader().getResource(hostsFile);

		boolean exists = false;
		for (int retry = 0; retry < 3; retry++) {
			if (resource.exists()) {
				exists = true;
				break;
			}

			log.info("Not found. Retrying...");
		}

		if (!exists) {
			throw new IllegalArgumentException(
					"Properties file " + hostsFile + " couldn't be found at location " + hostsFile);
		}

		Properties hosts = new Properties();

		try (InputStream stream = resource.getInputStream()) {

			hosts.load(stream);

		} catch (IOException e) {
			log.error("Can't load hosts file", e);
		}

		return hosts;

	}

	public Properties loadProperties(String propertiesPath, String propertiesFileName) {

		List<Properties> all = new ArrayList<>();

		if (StringUtils.hasText(propertiesPath)) {

			do {

				all.add(fetchProperties(propertiesPath, propertiesFileName));
				propertiesPath = stripDir(propertiesPath);

			} while (new File(propertiesPath).getParent() != null);
		}

		// Finally, check classpath
		// if (searchClasspath) {
		all.add(fetchProperties("classpath:/config/", propertiesFileName));
		all.add(fetchProperties("classpath:", propertiesFileName));
		// }

		Collections.reverse(all); // sort from root to highest

		Properties ps = new Properties();
		
		for(Properties p : all) {
			ps.putAll(p);
		}
		
		return ps;

	}

	protected String stripDir(String path) {

		int i = path.lastIndexOf("/");

		if (i > 0)
			return path.substring(0, i);

		return "";

	}

	protected boolean isURL(String path) {
		return path.trim().startsWith("http");
	}

	protected boolean isClasspath(String path) {
		return path.trim().startsWith("classpath:");
	}

	protected boolean isFile(String path) {
		return path.trim().startsWith("file:");
	}

	protected boolean isPath(String path) {
		return path.trim().startsWith(File.pathSeparator);
	}

}
