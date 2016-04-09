package com.mechatronase.mqtt;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttConnect;

public class MainActivity extends AppCompatActivity {

    private static final int CONNECT_TIMEOUT = 2000;
    private static final String CLIENT_ID = "android_user";
    private static final String BROKER = "tcp://m10.cloudmqtt.com:14344";
    String status = "";

    private MqttConnectOptions getConnectOptions() {
        MqttConnectOptions connOpts = new MqttConnectOptions();

        connOpts.setCleanSession(true);
        connOpts.setConnectionTimeout(3);
        connOpts.setKeepAliveInterval(60);
        connOpts.setUserName("light");
        connOpts.setPassword("light".toCharArray());
        connOpts.setServerURIs(new String[]{});
        return connOpts;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Switch hlight = (Switch) findViewById(R.id.hlight);
        Switch hfan = (Switch) findViewById(R.id.hfan);
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);*/

//end of modification
        try {
            final MqttAsyncClient client = new MqttAsyncClient(BROKER, CLIENT_ID, null);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("Mqtt", "connection lost");
                    cause.printStackTrace();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    Log.d("Mqtt message", message.toString());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) { /* not used */ }
            });

            final IMqttToken connectToken = client.connect(getConnectOptions());
            connectToken.waitForCompletion(CONNECT_TIMEOUT);

            Log.d("Mqtt", "client_connected=" + client.isConnected());

            client.publish("/light", new MqttMessage("hola dinu".getBytes()));
            client.subscribe("/light", 1);
            hlight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
            boolean isChecked) {

            if(isChecked){
                try{
                  client.publish("/light", new MqttMessage("on".getBytes()));
                  Snackbar.make(buttonView, "light on", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                catch(MqttException e) {
                    Log.d("Mqtt publish", "could not publish");
                    e.printStackTrace();
                }
            }else{
                try{
                  client.publish("/light", new MqttMessage("off".getBytes()));
                  Snackbar.make(buttonView, "light off", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                catch(MqttException e) {
                    Log.d("Mqtt publish", "could not publish");
                    e.printStackTrace();
                }
            }

            }
            });
            hfan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
            boolean isChecked) {

            if(isChecked){
                try{
                  client.publish("/fan", new MqttMessage("on".getBytes()));
                  Snackbar.make(buttonView, "Fan on", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                catch(MqttException e) {
                    Log.d("Mqtt publish", "could not publish");
                    e.printStackTrace();
                }
            }else{
                try{
                  client.publish("/fan", new MqttMessage("off".getBytes()));
                  Snackbar.make(buttonView, "Fan off", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                catch(MqttException e) {
                    Log.d("Mqtt publish", "could not publish");
                    e.printStackTrace();
                }
            }

            }
            });
            /*fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ;
                    try {
                        if (status == "off" || status ==""){
                            client.publish("/light", new MqttMessage("on".getBytes()));
                            status = "on";
                            Snackbar.make(view, "light on", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        else if (status=="on"){
                            client.publish("/light off", new MqttMessage("off".getBytes()));
                            status = "off";
                            Snackbar.make(view, "light off", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                    catch (MqttException e) {
                        Log.d("Mqtt publish", "could not publish");
                        e.printStackTrace();
                    }

                }
            });*/
        } catch (MqttException e) {
            Log.d("Mqtt", "could not create mqtt client");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
