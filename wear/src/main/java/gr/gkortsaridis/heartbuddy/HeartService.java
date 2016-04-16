package gr.gkortsaridis.heartbuddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yoko on 26/02/16.
 */
public class HeartService extends BroadcastReceiver implements SensorEventListener, GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener{

    SensorManager mSensorManager;
    Sensor mHeartRateSensor;
    int sensorCounter,heart_rate,max_tries=1000;
    Context mContext;
    GoogleApiClient mGoogleApiClient;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        // For our recurring task, we'll just display a message
        /*Vibrator vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        long[] vibrationPattern = {0, 500, 50, 300};
        //-1 - don't repeat
        final int indexInPatternToRepeat = -1;
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);*/
        Log.d("Testing", "Service got started at " + DateFormat.format("h:mm:ss aa ", System.currentTimeMillis()));

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        mSensorManager = ((SensorManager) context.getSystemService(context.SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);

        if(mHeartRateSensor == null){
            Log.i("Heart Rate","NULL");
            Toast.makeText(context, "(Service) Cannot access Heart Rate sensor", Toast.LENGTH_SHORT).show();
        }

        sensorCounter = 0;
        mSensorManager.registerListener(HeartService.this, mHeartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorCounter++;
        heart_rate = (int) event.values[0];
        //Log.i("Heart Rate : " + heart_rate, "Counter : " + sensorCounter);
        if(sensorCounter > max_tries){
            Log.i("Didnt get heart rate", "Stopping");
            sendHeartRateToDevice(heart_rate);
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if(accuracy > 2){
            Log.i("Found Heart Rate",heart_rate+"");
            sendHeartRateToDevice(heart_rate);
            mSensorManager.unregisterListener(this);
        }
    }


    private void sendHeartRateToDevice(int hr){


        Toast.makeText(mContext, "Your Heart Rate is : " + hr, Toast.LENGTH_SHORT).show();
        SimpleDateFormat s = new SimpleDateFormat("dd:MM:yyyy:hh:mm:ss");
        String timestamp = s.format(new Date());


        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/heart-rate");
        putDataMapRequest.getDataMap().putInt("heart-rate",hr);
        putDataMapRequest.getDataMap().putString("timestamp",timestamp);

        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient,request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        if(!dataItemResult.getStatus().isSuccess()){
                            Log.i("Failed to send","Data to Phone");
                        }else{
                            Log.i("Success","Data Sent");
                            Vibrator vibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
                            long[] vibrationPattern = {0, 500, 50, 300};
                            //-1 - don't repeat
                            final int indexInPatternToRepeat = -1;
                            vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);

                        }
                    }
                });
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
