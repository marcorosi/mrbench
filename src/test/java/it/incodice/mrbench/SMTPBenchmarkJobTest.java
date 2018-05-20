package it.incodice.mrbench;

import it.incodice.mrbench.config.BenchmarkConfig;
import it.incodice.mrbench.config.ConfigBuilder;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Integration test for SMTPBenchmarkJob
 *
 * Sends email message to various SMTP server with different configuration
 */
public class SMTPBenchmarkJobTest {

	/**
	 * Send to localhost without authentication
	 */
	@Test
	public void sendAnonymous(){
		SMTPBenchmarkJob job = fixtureSMTPBenchmarkJob("config/local.properties");
		assertTrue(job.send(new BenchmarkJobResult()));
	}

	/**
	 * Send to gmail with TLS auth
	 */
	@Test
	public void sendWithTLSAuth(){
		SMTPBenchmarkJob job = fixtureSMTPBenchmarkJob("config/gmail_tls.properties");
		assertTrue(job.send(new BenchmarkJobResult()));
	}

	/**
	 * Send to gmail with SSL auth
	 */
	@Test
	public void sendWithSSLAuth(){
		SMTPBenchmarkJob job = fixtureSMTPBenchmarkJob("config/gmail_ssl.properties");
		assertTrue(job.send(new BenchmarkJobResult()));
	}

	/**
	 * Send to gmail with TLS auth and connection reuse (check logs for details)
	 */
	@Test
	public void sendWithTLSAuthAndReuse(){
		SMTPBenchmarkJob job = fixtureSMTPBenchmarkJobWithReuse("config/gmail_tls.properties");
		assertTrue(job.send(new BenchmarkJobResult()));
		assertTrue(job.send(new BenchmarkJobResult()));
	}

	private SMTPBenchmarkJob fixtureSMTPBenchmarkJob(String configFile) {
		BenchmarkConfig benchmarkConfig = ConfigBuilder.buildBenchmarkConfig("unit_test_bench.properties",configFile);
		return SMTPBenchmarkJob.builder()
				.benchmarkConfig(benchmarkConfig)
				.sessionDebug(true)
				.build();
	}

	private SMTPBenchmarkJob fixtureSMTPBenchmarkJobWithReuse(String configFile) {
		BenchmarkConfig benchmarkConfig = ConfigBuilder.buildBenchmarkConfig("unit_test_bench.properties",configFile);
		benchmarkConfig.setReuseConnection(true);
		return SMTPBenchmarkJob.builder()
				.benchmarkConfig(benchmarkConfig)
				.sessionDebug(true)
				.build();
	}

}