package gr.gkortsaridis.heartbuddy;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends Activity implements SensorEventListener {

    private TextView mTextView;
    int heart_rate;
    SensorManager mSensorManager;
    Sensor mHeartRateSensor;

    private boolean doneLoading;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        alarmMgr = (AlarmManager)getBaseContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getBaseContext(), HeartService.class);
        alarmIntent = PendingIntent.getBroadcast(getBaseContext(), 0, intent, 0);

        // Set the alarm to start at 00:00 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 52);

        Log.d("WEAR", "Start at : " + DateFormat.format("yyyy-MM-dd hh:mm:ss", calendar.getTimeInMillis()));
        // setRepeating() lets you specify a precise custom interval--in this case, 2 hours
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmMgr.INTERVAL_HOUR, alarmIntent);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                doneLoading = false;
                initializeSensor();
            }
        });
    }

    private void initializeSensor(){
        mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);

        if(mHeartRateSensor == null){
            mTextView.setText("Cannot access Sensor");
            Toast.makeText(getBaseContext(), "Cannot access Heart Rate sensor", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Update your data.
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            heart_rate = (int) event.values[0];
            //Log.i("Heart Rate", heart_rate + " Acc. : "+event.accuracy+" "+event.toString());

            if(heart_rate > 0 && !doneLoading){
                doneLoading = true;
            }

            if(doneLoading) {
                mTextView.setText(heart_rate + " BPM");
            }else{
                mTextView.setText("Initializing");
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if(accuracy > 2){
            Log.i("Found Heart Rate",heart_rate+"");
            mSensorManager.unregisterListener(this);
        }
    }

}
