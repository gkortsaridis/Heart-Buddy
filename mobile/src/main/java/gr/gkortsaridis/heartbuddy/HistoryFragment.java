package gr.gkortsaridis.heartbuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class HistoryFragment extends android.support.v4.app.Fragment {

    SharedPreferences sharedpreferences;
    Button send;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_history, container, false);

        sharedpreferences = getActivity().getSharedPreferences("heart-rate-history", Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedpreferences.edit();
        String hrHistory = sharedpreferences.getString("heart-rates","");
        String tmHistory = sharedpreferences.getString("timestamps", "");

        final String[] hrhistory = hrHistory.split(",");
        final String[] tmhistory = tmHistory.split(",");
        Log.i("hrHistory Length ",hrhistory.length+"");
        Log.i("tmHistory Length ",tmhistory.length+"");


        LineChart lineChart = (LineChart) view.findViewById(R.id.chart);
        // creating list of entry
        final ArrayList<Entry> entries = new ArrayList<>();
        final ArrayList<String> labels = new ArrayList<String>();
        if(hrhistory.length >1 ) {
            if (hrhistory.length < 24) {
                for (int i = 0; i < hrhistory.length; i++) {
                    if(!hrhistory[i].equals("")) {
                        entries.add(new Entry(Integer.parseInt(hrhistory[i]), i));
                        labels.add(tmhistory[i]);
                    }
                }
            } else {
                int j = 0;
                for (int i = hrhistory.length - 24; i < hrhistory.length; i++) {
                    if(!hrhistory[i].equals("")) {
                        entries.add(new Entry(Integer.parseInt(hrhistory[i]), j++));
                        labels.add(tmhistory[i]);
                    }
                }
            }

            LineDataSet dataset = new LineDataSet(entries, "BPM at Specific timestamp");
            LineData data = new LineData(labels, dataset);
            lineChart.setData(data); // set the data and list of lables into chart
            lineChart.setDescription("Description");  // set the description

            dataset.setDrawFilled(true);
            //dataset.setColors(ColorTemplate.COLORFUL_COLORS);
            lineChart.animateY(5000);
        }else{
            Toast.makeText(getContext(),"No BPM history found. Open your watch application to start monitoring",Toast.LENGTH_LONG).show();
        }

        send = (Button) view.findViewById(R.id.send_btn);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedpreferences = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
                if(sharedpreferences.getString("doc_mail","").equals("")){
                    Toast.makeText(getContext(),"You need to provide a doctor email first",Toast.LENGTH_SHORT).show();
                }else {
                    String subject =sharedpreferences.getString("prof_surn","")+" "+sharedpreferences.getString("prof_name","")+" Daily History";
                    String body = "";
                    if(hrhistory.length < 24){
                        for(int i=0; i<hrhistory.length; i++){
                            body += "Timestamp : "+tmhistory[i]+" BPM :"+hrhistory[i]+"\n";
                        }
                    }else {
                        for (int i = hrhistory.length-24; i < hrhistory.length; i++) {
                            body += "Timestamp : "+tmhistory[i]+" BPM :"+hrhistory[i]+"\n";
                        }
                    }


                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", sharedpreferences.getString("doc_mail",""), null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, body);
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            }
        });

        return view;
    }
}
