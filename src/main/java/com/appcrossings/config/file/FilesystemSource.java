package com.appcrossings.config.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appcrossings.config.ConfigSource;
import com.appcrossings.config.StringUtils;

public class FilesystemSource implements ConfigSource {

	public FilesystemSource() {

	}

	private final static Logger log = LoggerFactory.getLogger(FilesystemSource.class);

	private InputStream getFileStream(String fullPath) throws FileNotFoundException, IOException {

		InputStream stream = null;

		if (isURL(fullPath)) {

			stream = new URL(fullPath).openStream();

		} else if (isFile(fullPath)) {

			String trimmed = fullPath.replaceFirst("file:", "");
			stream = new FileInputStream(new File(trimmed));

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
		} else {
			log.info("Not found.");
			throw new FileNotFoundException(fullPath);
		}

		return stream;

	}

	protected Properties fetchProperties(String propertiesPath, String propertiesFileName) {

		Properties p = new Properties();

		String fullPath = "";

		if (!propertiesPath.endsWith(File.separator) && !propertiesFileName.startsWith(File.separator))
			fullPath = propertiesPath + "/" + propertiesFileName;
		else
			fullPath = propertiesPath + propertiesFileName;

		try (InputStream stream = getFileStream(fullPath);) {

			log.info("Attempting " + fullPath);
			p.load(stream);

		} catch (FileNotFoundException e) {

			log.info("Not found.");

		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}

		return p;
	}

	protected Properties downloadFile(URL resource) {

		Properties p = new Properties();
		Throwable ex = null;
		for (int retry = 0; retry < 3; retry++) {

			try (InputStream stream2 = resource.openStream()) {

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
			throw new RuntimeException(ex);
		}

		return p;

	}

	public Properties loadHosts(String hostsFile) throws IllegalArgumentException {

		log.info("Fetching hosts file from path: " + hostsFile);

		Properties hosts = new Properties();

		try (InputStream stream = getFileStream(hostsFile)) {

			hosts.load(stream);

		} catch (IOException e) {
			log.error("Can't load hosts file", e);
		} finally {

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

		for (Properties p : all) {
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
		URI uri = URI.create(path);
		uri.getScheme();
		
		return path.trim().startsWith(File.separator);
	}

}
