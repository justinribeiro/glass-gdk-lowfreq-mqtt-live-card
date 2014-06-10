glass-gdk-lowfreq-mqtt-live-card
================================

Sample Glassware GDK application that shows the use of Eclipse Paho/MQTT for low frequency live card.

## Setup

1. This uses project uses Gradle.
2. You'll need the latest Google GDK add-on from Android SDK Tools
3. I've included a pre-built Eclipse Paho jar, but make sure it's in your build path

## Broker setup

If you have a look in LiveCardService.java you'll see the set of variables you'll need to change.

```java
  private static String MqttBrokerUri = "tcp://MY_BROKER:1883";
  private static String MqttClientId = "glass-randomid";
  private static String MqttBrokerTopic = "sensors/ard-04";
```

## I have no MQTT enabled Arduino or Pi!
You can still send messages to the app as long as you have something like [mosquitto_pub](http://mosquitto.org/documentation/):

```
mosquitto_pub -h MY_BROKER -t sensors/ard-04 -m 98.0
```

The command above just says "send 98.0 to the topic sensors/ard-04 at the host MY_BROKER".
