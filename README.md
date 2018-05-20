# MR Bench

A simple Java tool for SMTP server benchmarking.

Main features:

- supports 3 authentication mode: none, TLS and SSL
- can execute concurrent request
- can reuse SMTP connection

## Usage

1. Compile with:

```
 mvn package -DskipTests
```

2. Prepare a configuration file: some examples are available in src/main/resources/config/ folder.

- gmail_tls.properties: GMail server with TLS authentication
- gmail_ssl.properties: GMail server with SSL authentication
- local.properties: localhost without authentication

For Gmail or other SMTP server with authentication **you need to specify this properties:**

```
smtp.username=<smtp server username>
smtp.password=<smtp server password>
message.from=<email address for the sender of the test messages>
message.to=<email address for the recipient of the test messages>
```

3. Launch with:

```
 java -jar target/mrb.jar -c <path_to/configuration_file> -n <number of request>
```

### Options

```
 -c,--config <arg>       Configuration file to use
 -n,--requests <arg>     Number of requests to perform
 -p,--concurrent <arg>   Number of concurrent request (default to 1, total test requests will be n*p)
 -r,--reuse              Activate connection reuse
 -s,--size <arg>         Requests size (default is 1000)
```

You need to specify at least -c and -n parameters. 

Example:

```
java -jar target/mrb.jar -c src/main/resources/config/gmail_tls.properties -n 100 -s 10000 -p 2 --reuse
```

## Test

The only test (SMTPBenchmarkJobTest) is an integration test and needs a working setup to run properly.
To run it you need to:

- set username/password in gmail_tls.properties and gmail_ssl.properties
- start a local SMTP server on port 25

The file unit_test_bench.properties can be used to customize the test behaviour.
 
Have fun

M.