package it.incodice.mrbench.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Helper class for creating configuration beans from Java properties file
 */
public class ConfigBuilder {

	public static SMTPConfig buildSMTPConfig(String filename) {
		try {
			Properties props = loadProperties(filename);
			SMTPConfig config = new SMTPConfig();
			config.setHost(props.getProperty("smtp.host"));
			config.setPort(props.getProperty("smtp.port"));
			config.setUsername(props.getProperty("smtp.username"));
			config.setPassword(props.getProperty("smtp.password"));
			config.setAuthenticationType(AuthenticationTypeEnum.valueOf(props.getProperty("smtp.auth")));
			config.setSubject(props.getProperty("message.subject"));
			config.setSender(props.getProperty("message.from"));
			config.setRecipient(props.getProperty("message.to"));
			return config;
		} catch (Exception e) {
			throw new RuntimeException("Unable to setup application config, please verify the configuration files",e);
		}
	}

	public static BenchmarkConfig buildBenchmarkConfig(String filename, String smtpConfigFile) {
		try {
			BenchmarkConfig config = new BenchmarkConfig(buildSMTPConfig(smtpConfigFile));
			Properties props = loadProperties(filename);
			config.setRequestsNumber(Integer.valueOf(props.getProperty("test.message.number")));
			config.setRequestSize(Integer.valueOf(props.getProperty("test.message.size")));
			config.setReuseConnection(Boolean.valueOf(props.getProperty("test.connection.reuse")));
			return config;
		} catch (Exception e) {
			throw new RuntimeException("Unable to setup benchmark config, please verify the configuration files",e);
		}
	}

	private static Properties loadProperties(String filename) throws IOException {
		Properties props = new Properties();

		try {
			//first option: file path relative to working dir
			props.load(new FileInputStream(filename));
			//props.load(ConfigBuilder.class.getClassLoader().getResourceAsStream(filename));
		} catch (Exception e) {
			try {
				//otherwise look in the classpath (useful for unit test)
				props.load(ConfigBuilder.class.getClassLoader().getResourceAsStream(filename));
			} catch (Exception e1) {
				throw new FileNotFoundException(filename);
			}
		}

		return props;
	}
}
