package com.example.shreyanshjain.ekta;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.shreyanshjain.ekta.models.Contacts;
import com.example.shreyanshjain.ekta.utils.ObjectSerializer;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EmergencyContacts extends AppCompatActivity {

    @BindView(R.id.name1)
    TextInputEditText name1;

    @BindView(R.id.phone1)
    TextInputEditText phone1;

    @BindView(R.id.name2)
    TextInputEditText name2;

    @BindView(R.id.phone2)
    TextInputEditText phone2;

    @BindView(R.id.submit)
    Button submit;

    Contacts cont1,cont2;
    ArrayList<String> contacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);
        ButterKnife.bind(this);

        cont1 = new Contacts();
        cont2 = new Contacts();

        contacts = new ArrayList<>();
    }

    @OnClick(R.id.submit)
    void save_Contacts()
    {
        cont1.setName(name1.getText().toString());
        cont1.setPhone(phone1.getText().toString());
        cont2.setPhone(phone2.getText().toString());
        cont2.setName(name2.getText().toString());

        contacts.add(phone1.getText().toString());
        contacts.add(phone2.getText().toString());

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.shreyanshjain.ekta",Context.MODE_PRIVATE);

        try
        {
            sharedPreferences.edit().putString("Contacts",ObjectSerializer.serialize(contacts)).apply();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e("Shared Preferences",e.getMessage());
        }

        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
