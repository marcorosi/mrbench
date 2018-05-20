package it.incodice.mrbench;

import lombok.Data;

@Data
public class BenchmarkJobResult {

	long min;
	long max;
	long totalTime;
	int success;
	int errors;
	long byteSent;

	public BenchmarkJobResult() {
		min=Long.MAX_VALUE;
		max=0;
		totalTime=0;
		success=0;
		errors=0;
		byteSent=0;
	}

	public String format() {
		return String.join("\n"
				, "=== Job Result ==="
				, String.format("Samples successfully collected: %d",success)
				, String.format("Samples with error: %d",errors)
				, String.format("Min time: %d ms",min)
				, String.format("Max time: %d ms",max)
				, String.format("Avg time: %d ms",avg())
				//, String.format("Total byte sent: %d",byteSent)
				//, String.format("Total time: %d",totalTime)
				, String.format("Throughput: %.0f byte/sec",thr())
				, "=================="
		);
	}

	private double thr() {
		return byteSent/(totalTime/1000d);
	}

	private long avg() {
		return success > 0 ? totalTime/success : 0;
	}

	public void register(long time, int messageSize) {
		totalTime+=time;
		success++;
		if(time<min)
			min=time;
		if(time>max)
			max=time;
		byteSent+=messageSize;
	}

	public void registerError() {
		errors++;
	}
}
