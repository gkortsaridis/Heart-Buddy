package gr.gkortsaridis.heartbuddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ProfileFragment extends android.support.v4.app.Fragment {

    SharedPreferences sharedpreferences;
    Button save;
    EditText name,surname,mail,phone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        sharedpreferences = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();

        name = (EditText) view.findViewById(R.id.profile_name);
        surname = (EditText) view.findViewById(R.id.profile_surname);
        mail = (EditText) view.findViewById(R.id.profile_mail);
        phone = (EditText) view.findViewById(R.id.profile_phone);

        name.setText(sharedpreferences.getString("prof_name",""));
        surname.setText(sharedpreferences.getString("prof_surn",""));
        mail.setText(sharedpreferences.getString("prof_mail",""));
        phone.setText(sharedpreferences.getString("prof_phone",""));

        save = (Button) view.findViewById(R.id.save_prof);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("prof_name",name.getText().toString());
                editor.putString("prof_surn",surname.getText().toString());
                editor.putString("prof_mail",mail.getText().toString());
                editor.putString("prof_phone",phone.getText().toString());
                editor.commit();
                Toast.makeText(getContext(),"Profile data saved",Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}
