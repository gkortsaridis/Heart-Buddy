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

public class DoctorFragment extends android.support.v4.app.Fragment {

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
        View view = inflater.inflate(R.layout.fragment_doctor, container, false);


        sharedpreferences = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();

        name = (EditText) view.findViewById(R.id.doctor_name);
        surname = (EditText) view.findViewById(R.id.doctor_surname);
        mail = (EditText) view.findViewById(R.id.doctor_mail);
        phone = (EditText) view.findViewById(R.id.doctor_phone);

        name.setText(sharedpreferences.getString("doc_name",""));
        surname.setText(sharedpreferences.getString("doc_surn",""));
        mail.setText(sharedpreferences.getString("doc_mail",""));
        phone.setText(sharedpreferences.getString("doc_phone",""));

        save = (Button) view.findViewById(R.id.save_doc);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("doc_name",name.getText().toString());
                editor.putString("doc_surn",surname.getText().toString());
                editor.putString("doc_mail",mail.getText().toString());
                editor.putString("doc_phone",phone.getText().toString());
                editor.commit();
                Toast.makeText(getContext(),"Doctor data saved",Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}
