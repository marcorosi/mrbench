package it.incodice.mrbench;

import it.incodice.mrbench.config.BenchmarkConfig;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class coordinates the work of multiple benchmark job
 */
@Slf4j
public class BenchmarkRunner {

	BenchmarkConfig benchmarkConfig;

	public BenchmarkRunner(@NonNull	BenchmarkConfig benchmarkConfig) {
		this.benchmarkConfig=benchmarkConfig;
	}

	public BenchmarkResult run() throws InterruptedException {

		if(benchmarkConfig.getConcurrent()<1)
			return new BenchmarkResult();

		ExecutorService executor = Executors.newFixedThreadPool(benchmarkConfig.getConcurrent());

		List<Callable<BenchmarkJobResult>> jobs = new ArrayList<>();
		for (int i = 0; i < benchmarkConfig.getConcurrent(); i++) {
			jobs.add(buildBenchmarkJob(benchmarkConfig));
		}

		BenchmarkResult benchmarkResult = new BenchmarkResult();
		benchmarkResult.setStartTime(System.currentTimeMillis());
		List<Future<BenchmarkJobResult>> futures = executor.invokeAll(jobs);
		futures.forEach(f->{
			try {
				benchmarkResult.add(f.get());
			} catch (Exception e) {
				log.error("error during job execution",e);
			}
		});
		benchmarkResult.setFinishTime(System.currentTimeMillis());

		if(log.isDebugEnabled())
			benchmarkResult.getJobResults().forEach(r->System.out.println(r.format()));

		return benchmarkResult;
	}

	private static SMTPBenchmarkJob buildBenchmarkJob(BenchmarkConfig benchmarkConfig) {
		return SMTPBenchmarkJob.builder()
				.benchmarkConfig(benchmarkConfig)
				.sessionDebug(log.isTraceEnabled())
				.build();
	}

}
