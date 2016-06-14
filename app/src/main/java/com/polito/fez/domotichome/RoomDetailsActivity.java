package com.polito.fez.domotichome;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polito.fez.domotichome.datastructure.StateData;
import com.polito.fez.domotichome.firebase.SingletonCallback;
import com.polito.fez.domotichome.firebase.SingletonManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomDetailsActivity extends AppCompatActivity {

    private TextView txtEditTemp, txtHumidity, txtTemperature;
    private ImageButton btnPlus, btnMinus;
    private Button btnLightOn, btnLightOff, btnWarmOn, btnWarmOff, btnBell;
    private Map<com.polito.fez.domotichome.datastructure.StateData.CodeEventType, StateData> statesRoom;
    private float temperature;
    private float humidity;
    private boolean lightOn;
    private boolean warmOn;
    private ChildEventListener statesListener;

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
        statesListener = new ChildEventListener() {
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
                txtEditTemp.setText(String.format("%.2f", temperature));
                txtEditTemp.setText(String.format("%.2f", temperature));
                txtTemperature.setText(String.format("%.2f", temperature));
                break;
            case humidity:
                Log.d("debugGiulia","humidity: "+state.getValueRead());
                humidity = state.getValueRead();
                txtHumidity.setText(String.format("%.2f", humidity));
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
        this.btnPlus = (ImageButton) findViewById(R.id.btnPlusTemp);
        this.btnMinus = (ImageButton) findViewById(R.id.btnMinusTemp);
        this.btnLightOn = (Button) findViewById(R.id.btnLightOn);
        this.btnLightOff = (Button) findViewById(R.id.btnLightOff);
        this.btnWarmOn = (Button) findViewById(R.id.btnWarmOn);
        this.btnWarmOff = (Button) findViewById(R.id.btnWarmOff);
        this.btnBell = (Button) findViewById(R.id.btnBell);
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

        this.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temperature += 0.5f;
                txtEditTemp.setText(String.format("%.2f", temperature));
            }
        });

        this.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((temperature - 0.5f) >= 0) {
                    temperature -= 0.5f;
                    txtEditTemp.setText(String.format("%.2f", temperature));
                }
            }
        });

        this.btnLightOn.setOnClickListener(lightListener);
        this.btnLightOff.setOnClickListener(lightListener);

        this.btnWarmOn.setOnClickListener(warmListener);
        this.btnWarmOff.setOnClickListener(warmListener);

        this.btnBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BellEventsActivity.class);
                startActivity(intent);
            }
        });
    }

    View.OnClickListener warmListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int newValue = -1;
            if(warmOn) { // Voglio spegnere il riscaldamento
                newValue = 0;
            } else { // Voglio accendere il riscaldamento
                newValue = 1;
            }

            SingletonManager.sendWarmCommand(1, newValue, new SingletonCallback() {
                @Override
                public void doCallback(Object dataReturned) {
                    boolean result = (boolean) dataReturned;

                    if(result) {
                        warmOn = false;
                    } else {
                        warmOn = true;
                    }
                }
            });
        }
    };

    View.OnClickListener lightListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int newValue = -1;
            if(lightOn) { // Voglio spegnere la luce
                newValue = 0;
            } else { // Voglio accendere la luce
                newValue = 1;
            }

            SingletonManager.sendLightCommand(1, newValue, new SingletonCallback() {
                @Override
                public void doCallback(Object dataReturned) {
                    boolean result = (boolean) dataReturned;

                    if(result) {
                        lightOn = false;
                    } else {
                        lightOn = true;
                    }
                }
            });
        }
    };
}
