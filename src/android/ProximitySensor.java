package com.example.proximitysensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProximitySensor extends CordovaPlugin implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private CallbackContext callbackContext;
    private boolean isListening = false;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if (action.equals("start")) {
            this.callbackContext = callbackContext;
            startSensor();
            return true;
        } else if (action.equals("stop")) {
            stopSensor();
            callbackContext.success("Stopped");
            return true;
        }
        return false;
    }

    private void startSensor() {
        if (!isListening) {
            sensorManager = (SensorManager) cordova.getActivity().getSystemService(Context.SENSOR_SERVICE);
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

            if (proximitySensor != null) {
                sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
                isListening = true;
            } else {
                callbackContext.error("Proximity sensor not available");
            }
        }
    }

    private void stopSensor() {
        if (isListening && sensorManager != null) {
            sensorManager.unregisterListener(this);
            isListening = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float distance = event.values[0];
        boolean isClose = distance < proximitySensor.getMaximumRange();

        JSONObject data = new JSONObject();
        try {
            data.put("distance", distance);
            data.put("isClose", isClose);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (callbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, data);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Não usado
    }
}
