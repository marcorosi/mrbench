package it.incodice.mrbench.config;

import lombok.Data;
import lombok.NonNull;

import javax.mail.Session;

/**
 * Config of a benchmark run
 */
@Data
public class BenchmarkConfig {

	int concurrent;
	int numberOfMessage;
	int messageSize;
	boolean reuseConnection;

	SMTPConfig smtpConfig;

	public BenchmarkConfig(@NonNull SMTPConfig smtpConfig){
		this.smtpConfig = smtpConfig;
	}

	/**
	 * @return a description of this benchmark for the UI
	 */
	public String getDescription() {
		return String.format("SMTP Server %s", smtpConfig.getHost());
	}
}
