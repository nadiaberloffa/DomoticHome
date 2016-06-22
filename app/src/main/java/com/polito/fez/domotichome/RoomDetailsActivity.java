package com.polito.fez.domotichome;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.polito.fez.domotichome.datastructure.StateData;
import com.polito.fez.domotichome.firebase.SingletonCallback;
import com.polito.fez.domotichome.firebase.SingletonManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomDetailsActivity extends AppCompatActivity {

    private TextView txtEditTemp, txtHumidity, txtTemperature;
    private Button btnLightOn, btnLightOff, btnWarmOn, btnWarmOff;
    private Map<com.polito.fez.domotichome.datastructure.StateData.CodeEventType, StateData> statesRoom;
    private float temperature;
    private boolean lightOn;
    private boolean warmOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);
        
        int roomID = getIntent().getIntExtra("roomID",-1);
        if(roomID==-1){
            Toast.makeText(RoomDetailsActivity.this, getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
            finish();
        }else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarBack);
            if (toolbar != null) {
                toolbar.setTitle(String.format(getString(R.string.room_name), roomID));
                toolbar.setTitleTextColor(ContextCompat.getColor(this.getApplicationContext(), R.color.colorTitleToolbar));
                setSupportActionBar(toolbar);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {  //se click su "indietro"
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
            this.setInfoIntoView();
            this.setListenerToFirebase();
        }
    }

    private void setListenerToFirebase() {
        ChildEventListener statesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                StateData updState = dataSnapshot.getValue(StateData.class);
                statesRoom.put(updState.getCodeEventType(), updState);
                checkData(updState);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference("states").addChildEventListener(statesListener);
    }

    private void checkData(StateData state) {
        switch (state.getCodeEventType()) {
            case temperature:
                Log.d("debugGiulia","temperature: "+state.getValueRead());
                temperature = state.getValueRead();
                String temp = String.format(getResources().getString(R.string.temperature_label), temperature);
                txtEditTemp.setText(temp);
                txtTemperature.setText(temp);
                break;
            case humidity:
                Log.d("debugGiulia","humidity: "+state.getValueRead());
                float humidity = state.getValueRead();
                String hum = String.format(getResources().getString(R.string.humidity_label), humidity);
                txtHumidity.setText(hum);
                break;
            case light:
                if (state.getValueRead() == 0) {
                    lightOn = false;
                    btnLightOff.setVisibility(View.VISIBLE);
                    btnLightOn.setVisibility(View.GONE);
                } else {
                    lightOn = true;
                    btnLightOff.setVisibility(View.GONE);
                    btnLightOn.setVisibility(View.VISIBLE);
                }
                Log.d("debugGiulia","light: " + lightOn);
                break;
            case warm:
                if (state.getValueRead() == 0) {
                    warmOn = false;
                    btnWarmOff.setVisibility(View.VISIBLE);
                    btnWarmOn.setVisibility(View.GONE);
                } else {
                    warmOn = true;
                    btnWarmOff.setVisibility(View.GONE);
                    btnWarmOn.setVisibility(View.VISIBLE);
                }
                Log.d("debugGiulia","warmOn: " + warmOn);
                break;
        }
    }

    private void setInfoIntoView() {

        this.txtEditTemp = (TextView) findViewById(R.id.txtEditTemperature);
        this.txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        this.txtTemperature = (TextView) findViewById(R.id.txtTemperature);
        ImageButton btnPlus = (ImageButton) findViewById(R.id.btnPlusTemp);
        ImageButton btnMinus = (ImageButton) findViewById(R.id.btnMinusTemp);
        this.btnLightOn = (Button) findViewById(R.id.btnLightOn);
        this.btnLightOff = (Button) findViewById(R.id.btnLightOff);
        this.btnWarmOn = (Button) findViewById(R.id.btnWarmOn);
        this.btnWarmOff = (Button) findViewById(R.id.btnWarmOff);
        this.statesRoom = new HashMap<>();

        SingletonManager.getStates(new SingletonCallback() {
            @Override
            public void doCallback(Object dataReturned) {
                Map<Integer, List<StateData>> statesMap = (Map<Integer, List<StateData>>) dataReturned;

                List<StateData> statesList = statesMap.get(1); // Prendo gli stati di room 1

                for (StateData state : statesList) {
                    statesRoom.put(state.getCodeEventType(), state);

                    checkData(state);
                }
            }
        });

        assert btnPlus != null;
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temperature += 0.5f;
                txtEditTemp.setText(String.format(getResources().getString(R.string.temperature_label), temperature));
            }
        });

        assert btnMinus != null;
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((temperature - 0.5f) >= 0) {
                    temperature -= 0.5f;
                    txtEditTemp.setText(String.format(getResources().getString(R.string.temperature_label), temperature));
                }
            }
        });

        this.btnLightOn.setOnClickListener(lightListener);
        this.btnLightOff.setOnClickListener(lightListener);

        this.btnWarmOn.setOnClickListener(warmListener);
        this.btnWarmOff.setOnClickListener(warmListener);
    }

    View.OnClickListener warmListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int newValue;
            if(warmOn) { // Voglio spegnere il riscaldamento
                newValue = 0;
            } else { // Voglio accendere il riscaldamento
                newValue = 1;
            }

            SingletonManager.sendWarmCommand(1, newValue, new SingletonCallback() {
                @Override
                public void doCallback(Object dataReturned) {
                    boolean result = (boolean) dataReturned;
                    warmOn = !result;
                }
            });
        }
    };

    View.OnClickListener lightListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int newValue;
            if(lightOn) { // Voglio spegnere la luce
                newValue = 0;
            } else { // Voglio accendere la luce
                newValue = 1;
            }

            SingletonManager.sendLightCommand(1, newValue, new SingletonCallback() {
                @Override
                public void doCallback(Object dataReturned) {
                    boolean result = (boolean) dataReturned;
                    lightOn = !result;
                }
            });
        }
    };
}
