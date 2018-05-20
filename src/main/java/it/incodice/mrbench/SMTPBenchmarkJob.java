package it.incodice.mrbench;

import it.incodice.mrbench.config.BenchmarkConfig;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.concurrent.Callable;

/**
 * A service for calling an SMTP server
 */
@Slf4j
public class SMTPBenchmarkJob implements Callable<BenchmarkJobResult> {

	private BenchmarkConfig benchmarkConfig;

	private Session session;
	private Transport transport;

	//this are reused for optimization
	private String messageBody;
	private int messageSize;

	@Builder
	public SMTPBenchmarkJob(BenchmarkConfig benchmarkConfig, boolean sessionDebug) {
		this.benchmarkConfig = benchmarkConfig;
		this.session = initSession();
		this.session.setDebug(sessionDebug);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < benchmarkConfig.getMessageSize(); i++) {
			sb.append("a");
		}
		this.messageBody = sb.toString();
		messageSize = -1;
	}

	/**
	 * @return true if send succeeded, false if any error occurs
	 */
	public boolean send(BenchmarkJobResult benchmarkResult) {
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(benchmarkConfig.getSmtpConfig().getSender()));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(benchmarkConfig.getSmtpConfig().getRecipient()));
			message.setSubject(benchmarkConfig.getSmtpConfig().getSubject());
			message.setText(messageBody);

			log.debug("sending message...");
			long start = System.currentTimeMillis();
			getTransport().send(message);
			long end = System.currentTimeMillis();

			if(messageSize<0) {
				//first call: manually calculate message size (because message.getSize() doesn't work)
				ByteArrayOutputStream bais = new ByteArrayOutputStream();
				message.writeTo(bais);
				messageSize = bais.size();
				bais.close();
			}

			//log.info("msg size {}",message.getSize());
			//log.info("bais size {}",bais.size());
			//log.info("sb size {}",sb.toString().getBytes().length);

			benchmarkResult.register(end-start,messageSize);

			return true;
		} catch (Exception e) {
			if(log.isDebugEnabled())
				log.debug("request failure", e);
			else
				log.error("request failure: {}", e.getMessage());

			benchmarkResult.registerError();
			return false;
		}
	}

	/**
	 * runs the job
	 *
	 * @return the benchmark result for this job
	 */
	@Override public BenchmarkJobResult call() throws Exception {
		BenchmarkJobResult benchmarkResult = new BenchmarkJobResult();
		for (int i = 0; i < benchmarkConfig.getNumberOfMessage(); i++) {
			send(benchmarkResult);
		}
		return benchmarkResult;
	}

	private Transport getTransport() throws MessagingException {
		if (this.transport != null)
			return this.transport;
		else
			return initTransport();
	}

	private Transport initTransport() throws MessagingException {
		Transport transport;
		try {
			transport = session.getTransport("smtp");
			if (this.benchmarkConfig.isReuseConnection())
				this.transport = transport;

			transport.connect();

			return transport;
		} catch (NoSuchProviderException e) {
			throw new RuntimeException("unable to setup SMTP service", e);
		}
	}

	private Session initSession() {
		switch (benchmarkConfig.getSmtpConfig().getAuthenticationType()) {
		case SSL:
			return initSSL();
		case TLS:
			return initTLS();
		default:
			return initAnonymous();
		}
	}

	private Session initAnonymous() {
		Properties props = System.getProperties();
		props.put("mail.smtp.host", benchmarkConfig.getSmtpConfig().getHost());
		props.put("mail.smtp.port", benchmarkConfig.getSmtpConfig().getPort());
		return Session.getInstance(props, null);
	}

	private Session initSSL() {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", benchmarkConfig.getSmtpConfig().getHost());
		props.put("mail.smtp.port", benchmarkConfig.getSmtpConfig().getPort());

		props.put("mail.smtp.socketFactory.port", benchmarkConfig.getSmtpConfig().getPort());
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(benchmarkConfig.getSmtpConfig().getUsername(), benchmarkConfig.getSmtpConfig().getPassword());
			}
		});

		return session;
	}

	private Session initTLS() {
		Properties props = new Properties();

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", benchmarkConfig.getSmtpConfig().getHost());
		props.put("mail.smtp.port", benchmarkConfig.getSmtpConfig().getPort());

		props.put("mail.smtp.starttls.enable", "true");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(benchmarkConfig.getSmtpConfig().getUsername(), benchmarkConfig.getSmtpConfig().getPassword());
			}
		});

		return session;
	}
}
