package it.incodice.mrbench;

import it.incodice.mrbench.config.BenchmarkConfig;
import it.incodice.mrbench.config.ConfigBuilder;
import it.incodice.mrbench.config.SMTPConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Main launcher
 */
@Slf4j public class Application {

	public static void main(String[] args) {

		CommandLine commandLine = parseArguments(args);

		if(commandLine==null) {
			//bad arguments
			System.exit(1);
		}

		log.info("Setup...");
		BenchmarkConfig benchmarkConfig = buildBenchmarkConfig(commandLine);
		log.info("Setup completed");

		log.debug("Test configuration: \n{}", benchmarkConfig);

		BenchmarkRunner benchmarkRunner = new BenchmarkRunner(benchmarkConfig);
		try {
			log.info("Starting benchmark '{}'", benchmarkConfig.getDescription());
			BenchmarkResult result = benchmarkRunner.run();
			log.info("Benchmark completed");

			log.info("Printing result on standard output...");
			System.out.println(result.format());

			System.exit(0);
		} catch (Exception e) {
			log.error("error during test execution",e);

			System.exit(1);
		}
	}

	/**
	 * try to parse command line arguments and print help usage in case of problems
	 *
	 * @return CommandLine instance to access program arguments, null if something goes wrong
	 */
	private static CommandLine parseArguments(String[] args) {

		Options options = new Options();

		options.addOption(
				Option.builder("c")
						.longOpt("config")
						.desc("Configuration file to use")
						.hasArg()
						.required()
						.build()
		);

		options.addOption(
				Option.builder("n")
						.longOpt("requests")
						.desc("Number of requests to perform, default to "+ BenchmarkConfig.DEFAULT_REQUEST_NUMBER)
						.hasArg()
						.build()
		);

		options.addOption(
				Option.builder("p")
						.longOpt("concurrent")
						.desc("Number of concurrent request (total test requests will be n*p), default to "+ BenchmarkConfig.DEFAULT_CONCURRENT_VALUE)
						.hasArg()
						.build()
		);

		options.addOption(
				Option.builder("s")
						.longOpt("size")
						.desc("Requests character size, default to "+ BenchmarkConfig.DEFAULT_REQUEST_SIZE)
						.hasArg()
						.build()
		);

		options.addOption(
				Option.builder("r")
						.longOpt("reuse")
						.desc("Activate connection reuse")
						.build()
		);

		CommandLineParser commandLineParser = new DefaultParser();
		HelpFormatter helpFormatter = new HelpFormatter();

		try {
			return commandLineParser.parse(options, args);
		} catch (ParseException e) {
			log.error(e.getMessage());
			helpFormatter.printHelp("mrbench", options);
			return null;
		}
	}

	private static BenchmarkConfig buildBenchmarkConfig(CommandLine commandLine) {

		SMTPConfig smtpConfig = ConfigBuilder.buildSMTPConfig(commandLine.getOptionValue("config"));

		BenchmarkConfig benchmarkConfig = new BenchmarkConfig(smtpConfig);
		benchmarkConfig.setRequestsNumber(Integer.valueOf(commandLine.getOptionValue("n", BenchmarkConfig.DEFAULT_REQUEST_NUMBER+"")));
		benchmarkConfig.setRequestSize(Integer.valueOf(commandLine.getOptionValue("s", BenchmarkConfig.DEFAULT_REQUEST_SIZE+"")));
		benchmarkConfig.setConcurrent(Integer.valueOf(commandLine.getOptionValue("p", BenchmarkConfig.DEFAULT_CONCURRENT_VALUE+"")));
		benchmarkConfig.setReuseConnection(commandLine.hasOption("reuse"));

		return benchmarkConfig;
	}
}
