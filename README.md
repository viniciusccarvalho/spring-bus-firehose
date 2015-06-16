# Spring Bus Firehose Sample

Sample [spring-bus](http://github.com/spring-projects/spring-bus) project that listens for [cloudfoundry doppler](https://github.com/cloudfoundry/loggregator) firehose events.

## Running

Run `git submodule init` to fetch the protobuf definitions. 

When importing this project into your favorite IDE you may need to run `gradle build` to make sure generated classes are created.

### Subprojects

* source-module: The source that drains events from doppler, filters messages based on `doppler-events` and sends them downstream.
* tap-module: Act as a sink that will log all messages to the console. 


### Dashboard

The `source-module` has a basic dashboard published on its root path (index.html). This simple dashboard can be used to check the throughput
 of messages being consumed and produced:
 
 ![Dashboard](https://cloud.githubusercontent.com/assets/803893/8193968/7d7845ec-1446-11e5-93ac-2d02f9aa0d0c.png)

## Redis

The only message bus supported is [redis](http://redis.io). You will need either to have a local installation or if you are running inside PCF, just bind a redis service to the application. 

## Running

Once you build with `gradle build` just start each project by calling `java -jar <module-name>/build/libs/<module-name>-1.0.0.BUILD-SNAPSHOT.jar`

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
    #should the output message contain POJOs or JSON strings
    output-json: <true | false>
    trust-self-certs: <true | false>
    #usually uaa.<pcfdomain>
    authentication-url:
    cf-domain:
    username:
    password:
```

After running you should be able to see the messages showing up on a queue in redis named `queue.firehose.0`

## Todo

* ~~Integrate with spring-bus-samples tap~~
* ~~Output JSON not binary protobuf~~
* Take over the world


