package com.polito.fez.domotichome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.polito.fez.domotichome.firebase.SingletonCallback;
import com.polito.fez.domotichome.firebase.SingletonManager;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sharedPref), 0);
        if(sharedPref.contains("email") && sharedPref.contains("password")) {
            if (!sharedPref.getString("email", "").equalsIgnoreCase("") && !sharedPref.getString("password", "").equalsIgnoreCase("")) {
                TextView loadingText = (TextView) findViewById(R.id.loadingText);
                assert loadingText != null;
                loadingText.setVisibility(View.VISIBLE);

                SingletonManager.login(sharedPref.getString("email", ""), sharedPref.getString("password", ""), new SingletonCallback() {
                    @Override
                    public void doCallback(Object dataReturned) {

                        if((Boolean)dataReturned) {
                            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sharedPref), 0);
                            final SharedPreferences.Editor editor = sharedPref.edit();
                            editor.apply();
                            getDataFromServer();
                        }else {
                            Toast.makeText(getApplicationContext(),getString(R.string.no_log),Toast.LENGTH_SHORT ).show();
                            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        }else {
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void getDataFromServer() {
        SingletonManager.getStates(new SingletonCallback() {
            @Override
            public void doCallback(Object dataReturned) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
