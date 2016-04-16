package gr.gkortsaridis.heartbuddy;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class HeartReceiver extends WearableListenerService {

    SharedPreferences sharedpreferences;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.i("OnDataChanged","CALLED");
        for(DataEvent dataEvent : dataEvents){
            if(dataEvent.getType() == DataEvent.TYPE_CHANGED){
                DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                String path = dataEvent.getDataItem().getUri().getPath();
                if(path.equals("/heart-rate")){
                    int heart_rate = dataMap.getInt("heart-rate");
                    String timestamp = dataMap.getString("timestamp");
                    Log.i("Pira HearRate",heart_rate+"");
                    Log.i("Pira timestamp",timestamp);
                    saveData(heart_rate, timestamp);
                }
            }
        }
    }


    private void saveData(int hr,String ts){
        sharedpreferences = getSharedPreferences("heart-rate-history", Context.MODE_PRIVATE);

        String hrHistory = sharedpreferences.getString("heart-rates", "");
        String tsHistory = sharedpreferences.getString("timestamps","");

        if(hrHistory.equals("")){
            hrHistory += hr+"";
        }else{
            hrHistory += ","+hr;
        }

        if(tsHistory.equals("")){
            tsHistory += ts;
        }else{
            tsHistory += ","+ts;
        }

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("heart-rates", hrHistory);
        editor.putString("timestamps",tsHistory);
        editor.commit();
    }
}
