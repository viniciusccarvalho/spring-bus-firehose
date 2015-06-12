# Spring Bus Firehose Sample

Sample [spring-bus](http://github.com/spring-projects/spring-bus) source that listens for [cloudfoundry doppler](https://github.com/cloudfoundry/loggregator) firehose metrics.

## Running

Run `git submodule init` to fetch the protobuf definitions. 

When importing this project into your favorite IDE you may need to run `gradle build` to make sure generated classes are created.

## RabbitMQ

For now, the project uses [rabbitmq](http://rabbitmq.org) as a message broker, so you will need either to have a local installation or if you are running inside PCF, just bind a rabbitmq service to the application. 

## Running with lattice

If you have a local [lattice](http://lattice.cf) install, just modify your `src/main/resources/application.yml`

```
 firehose:
    doppler-url: ws://doppler.192.168.11.11.xip.io
    #List of dropsonde events you want to subscribe to
    doppler-events: "LogMessage,HttpStartStop"
```

## Running with cloudfoundry

If you want to run on cloudfoundry, first make sure your user has the proper rights to access [the doppler firehose](www.cloudcredo.com/cloud-foundry-firehose-and-friends/).

Modify the `application.yml` to have the following properties:

```
 firehose:
    doppler-url: wss://doppler.<your_cf_domain>
    #List of dropsonde events you want to subscribe to
    doppler-events: "HttpStart,LogMessage"
    #usually uaa.<pcfdomain>
    authentication-url:
    cf-domain:
    username:
    password:
```

After running you should be able to see the messages showing up on a queue in rabbit named `xdbus.firehose.0`

## Todo

* Integrate with spring-bus-samples tap
* Output JSON not binary protobuf
* Take over the world


