package it.incodice.mrbench.config;

import lombok.Data;

/**
 * SMTP configuration
 */
@Data
public class SMTPConfig {

	String host;
	String port;
	String username;
	String password;
	AuthenticationTypeEnum authenticationType;
	String sender;
	String recipient;
	String subject;

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("SMTPConfig{");
		sb.append("host='").append(host).append('\'');
		sb.append(", port='").append(port).append('\'');
		sb.append(", username='").append(username).append('\'');
		sb.append(", password='").append("********").append('\'');
		sb.append(", authenticationType=").append(authenticationType);
		sb.append(", sender='").append(sender).append('\'');
		sb.append(", recipient='").append(recipient).append('\'');
		sb.append(", subject='").append(subject).append('\'');
		sb.append('}');
		return sb.toString();
	}

}
