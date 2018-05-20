package it.incodice.mrbench.config;

import lombok.Data;
import lombok.NonNull;

/**
 * Config of a benchmark run
 */
@Data
public class BenchmarkConfig {

	public static final int DEFAULT_REQUEST_SIZE = 1000;
	public static final int DEFAULT_CONCURRENT_VALUE = 1;
	public static final int DEFAULT_REQUEST_NUMBER = 1;

	int concurrent;
	int requestsNumber;
	int requestSize;
	boolean reuseConnection;

	SMTPConfig smtpConfig;

	public BenchmarkConfig(@NonNull SMTPConfig smtpConfig){
		this.smtpConfig = smtpConfig;
		this.concurrent = DEFAULT_CONCURRENT_VALUE;
		this.requestsNumber = DEFAULT_REQUEST_NUMBER;
		this.requestSize = DEFAULT_REQUEST_SIZE;
		this.reuseConnection = false;
	}

	/**
	 * @return a description of this benchmark for the UI
	 */
	public String getDescription() {
		return String.format("SMTP Server %s", smtpConfig.getHost());
	}
}
