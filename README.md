Spring XD Firehose
==================

An example of a [spring-bus](https://github.com/spring-projects/spring-bus) source that connects to Cloudfoundry firehose.

Using it
------------------

* Configuration

Supports the following configuration options (can be set as application.yml or environment variables, thanks to spring-boot):

```
 firehose:
    doppler-url: ws://doppler.10.1.1.11.xip.io
    doppler-events: "HttpStart,LogMessage"
    #only needed if you are deploying to CF
    authentication-url:
    cf-domain:
    username:
    password:

```
The options should be self-explanatory.

The doppler-events is a comma separated list of events you'd like to filter, use this to control what doppler messages do you want to be pushed downstream.

* Requirements

It currently only needs a rabbitmq server started on localhost, if you are deploying to PCF you can just bind a rabbit instance from there.


