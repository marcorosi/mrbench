package it.incodice.mrbench;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkResult {

	long startTime;
	long finishTime;

	long min;
	long max;
	long avg;
	double thr;
	long totalTime;

	int success;
	int errors;
	long byteSent;

	private List<BenchmarkJobResult> results;

	public BenchmarkResult() {
		results = new ArrayList<>();
		min=Long.MAX_VALUE;
		max=0;
		totalTime=0;
		startTime=0;
		finishTime=0;
		success=0;
		errors=0;
		byteSent=0;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}

	public void add(BenchmarkJobResult benchmarkResult) {
		results.add(benchmarkResult);
		updateStats(benchmarkResult);
	}

	private void updateStats(BenchmarkJobResult r) {

		success += r.success;
		errors += r.errors;

		if(success > 0) {
			totalTime += r.totalTime;

			byteSent += r.byteSent;
			if(r.getMin()<min)
				min=r.getMin();
			if(r.getMax()>max)
				max=r.getMax();

			avg = totalTime/success;
			thr = byteSent/(totalTime/1000d);
		}
	}

	public List<BenchmarkJobResult> getJobResults() {
		return results;
	}

	public String format() {
		return String.join("\n"
				, "\n======= MRBench Result ======="
				, String.format("\nConcurrency level: %d",results.size())
				, String.format("Time taken for tests: %.3f seconds",globalTimeInSeconds())

				, String.format("\nCompleted requests: %d",success)
				, String.format("Failed requests: %d",errors)

				, String.format("\nMin time: %d ms",min())
				, String.format("Max time: %d ms",max)
				, String.format("Avg time: %d ms",avg)
				//, String.format("Total byte sent: %d",byteSent)
				//, String.format("Total time: %d",totalTime)
				, String.format("\nServer throughput: %s",formattedThr())
				, String.format("Test throughput: %.3f req/sec",req())
				, "\n=============================="
		);
	}

	private long min() {
		return success > 0 ? min : 0;
	}

	private String formattedThr() {
		if(thr>1024*1024)
			return String.format("%.3f MB/sec",thr/(1024*1024));
		else if(thr>1024)
			return String.format("%.3f Kb/sec",thr/1024);
		else
			return String.format("%.0f byte/sec",thr);
	}

	private double req() {
		return success/globalTimeInSeconds();
	}

	private double globalTimeInSeconds() {
		return (finishTime-startTime)/1000d;
	}
}
