package com.justinribeiro.example.glass_lowfreq_mqtt_sensor.app;

/**
 * Created by justin.ribeiro on 6/9/2014.
 */
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class LiveCardService extends Service implements MqttCallback {

    private static final String LIVE_CARD_TAG = "LiveCardMqttDemo";

    private LiveCard mLiveCard;
    private RemoteViews mLiveCardView;

    private static MqttClient MqttClient = null;
    private static String MqttBrokerUri = "tcp://MY_BROKER:1883";
    private static String MqttClientId = "glass-randomid";
    private static String MqttBrokerTopic = "sensors/ard-04";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLiveCard == null) {

            // Get an instance of a live card
            mLiveCard = new LiveCard(this, LIVE_CARD_TAG);

            // Inflate a layout into a remote view
            mLiveCardView = new RemoteViews(getPackageName(), R.layout.sensor_output);

            // Set up the live card's action with a pending intent
            // to show a menu when tapped
            Intent menuIntent = new Intent(this, MenuActivity.class);
            menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));

            // Publish the live card
            mLiveCard.publish(PublishMode.REVEAL);

            if (MqttClient == null) {
                try {
                    // File persist is working funky for me on Glass
                    // For now, we'll switch to use memory for the demo
                    MemoryPersistence persistence = new MemoryPersistence();
                    MqttClient = new MqttClient(MqttBrokerUri, MqttClientId, persistence);

                } catch (MqttException e1) {
                    Log.e(LIVE_CARD_TAG, e1.getMessage());
                }

                // Connect to the broker
                try {
                    MqttClient.connect();
                    MqttClient.setCallback(this);
                    MqttClient.subscribe(MqttBrokerTopic,0);
                } catch (MqttSecurityException e) {
                    Log.e(LIVE_CARD_TAG, e.getMessage());
                } catch (MqttException e) {
                    Log.e(LIVE_CARD_TAG, e.getMessage());
                }
            }

            // Tell the user we're waiting for a report
            mLiveCardView.setTextViewText(R.id.sensor_temp, "Waiting...");
            mLiveCard.setViews(mLiveCardView);

        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mLiveCard != null && mLiveCard.isPublished()) {

            // This disconnects from the broker
            try {
                MqttClient.disconnect();
            } catch (MqttException e) {
                Log.e(LIVE_CARD_TAG, e.getMessage());
            }

            // This cleans up and release resources...but you have to disconnect first!
            try {
                MqttClient.close();
            } catch (MqttException e) {
                Log.e(LIVE_CARD_TAG, e.getMessage());
            }

            mLiveCard.unpublish();
            mLiveCard = null;
        }
        super.onDestroy();
    }

    public void connectionLost(Throwable cause) {
        Log.e(LIVE_CARD_TAG, "Mqtt Broker connection lost");

        // try to reconnect
        try {
            MqttClient.connect();
        } catch (MqttSecurityException e) {
            Log.e(LIVE_CARD_TAG, e.getMessage());
        } catch (MqttException e) {
            Log.e(LIVE_CARD_TAG, e.getMessage());
        }
    }

    public void deliveryComplete(IMqttDeliveryToken token) {
       Log.d(LIVE_CARD_TAG, "Mqtt message delivery complete");
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d(LIVE_CARD_TAG, topic.toString());
        Log.d(LIVE_CARD_TAG, message.toString());

        mLiveCardView.setTextViewText(R.id.sensor_temp, message.toString() + "°");
        mLiveCard.setViews(mLiveCardView);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
