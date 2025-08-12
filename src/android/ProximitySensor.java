package com.example.proximitysensor;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ProximitySensor extends CordovaPlugin implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("start")) {
            this.callbackContext = callbackContext;
            startListening();
            return true;
        } else if (action.equals("stop")) {
            stopListening();
            callbackContext.success("Stopped");
            return true;
        }
        return false;
    }

    private void startListening() {
        sensorManager = (SensorManager) cordova.getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            if (proximitySensor != null) {
                sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                callbackContext.error("Proximity sensor not available");
            }
        }
    }

    private void stopListening() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        boolean isNear = event.values[0] < proximitySensor.getMaximumRange();
        try {
            JSONObject result = new JSONObject();
            result.put("isNear", isNear);
            result.put("distance", event.values[0]);
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        
    }
}
