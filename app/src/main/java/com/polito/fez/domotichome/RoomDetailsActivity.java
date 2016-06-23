package com.polito.fez.domotichome;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private double temperature;
    private boolean lightOn;
    ImageButton btnPlus, btnMinus;
    RadioButton auto, manual;
    private boolean warmOn;
    CountDownTimer tempTimer = null, modeTimer = null;
    int secondiTimer = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        int roomID = getIntent().getIntExtra("roomID", -1);
        if (roomID == -1) {
            Toast.makeText(RoomDetailsActivity.this, getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
            finish();
        } else {
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
            this.setRadioButton();
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
                Log.d("debugGiulia", "temperature: " + state.getValueRead());
                temperature = state.getValueRead();
                String temp = String.format(getResources().getString(R.string.temperature_label), temperature);
                txtEditTemp.setText(temp);
                txtTemperature.setText(temp);
                break;
            case humidity:
                Log.d("debugGiulia", "humidity: " + state.getValueRead());
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
                Log.d("debugGiulia", "light: " + lightOn);
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
                Log.d("debugGiulia", "warmOn: " + warmOn);
                break;
            case autoWarm:
                if (state.getValueRead() == 1) {
                    auto.setChecked(true);
                    btnPlus.setClickable(true);
                    btnPlus.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_gray_48dp));

                    assert btnMinus != null;
                    btnMinus.setClickable(true);
                    btnMinus.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_gray_48dp));
                } else {
                    manual.setChecked(true);
                    btnPlus.setClickable(false);
                    btnMinus.setClickable(false);
                }
                Log.d("debugGiulia", "warmOn: " + warmOn);
                break;
        }
    }

    private void setRadioButton() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);

        assert radioGroup != null;
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.auto) {

                    modeTimer = null;

                    assert btnPlus != null;
                    btnPlus.setOnClickListener(buttonPlus);
                    btnPlus.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_orange_48dp));

                    assert btnMinus != null;
                    btnMinus.setOnClickListener(buttonMin);
                    btnMinus.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_orange_48dp));
                } else if (checkedId == R.id.manual) {

                    setModeTimer();

                    assert btnPlus != null;
                    btnPlus.setClickable(false);
                    btnPlus.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_gray_48dp));

                    assert btnMinus != null;
                    btnMinus.setClickable(false);
                    btnMinus.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_gray_48dp));
                }
            }
        });

        auto = (RadioButton) findViewById(R.id.auto);
        manual = (RadioButton) findViewById(R.id.manual);
    }

    private void setInfoIntoView() {

        this.txtEditTemp = (TextView) findViewById(R.id.txtEditTemperature);
        this.txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        this.txtTemperature = (TextView) findViewById(R.id.txtTemperature);
        btnPlus = (ImageButton) findViewById(R.id.btnPlusTemp);
        btnMinus = (ImageButton) findViewById(R.id.btnMinusTemp);
        this.btnLightOn = (Button) findViewById(R.id.btnLightOn);
        this.btnLightOff = (Button) findViewById(R.id.btnLightOff);
        this.btnWarmOn = (Button) findViewById(R.id.btnWarmOn);
        this.btnWarmOff = (Button) findViewById(R.id.btnWarmOff);
        this.statesRoom = new HashMap<>();

        assert btnPlus != null;
        btnPlus.setOnClickListener(buttonPlus);

        assert btnMinus != null;
        btnMinus.setOnClickListener(buttonMin);

        this.btnLightOn.setOnClickListener(lightListener);
        this.btnLightOff.setOnClickListener(lightListener);

        this.btnWarmOn.setOnClickListener(warmListener);
        this.btnWarmOff.setOnClickListener(warmListener);

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


    }

    View.OnClickListener buttonPlus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            temperature += 0.5f;
            txtEditTemp.setText(String.format(getResources().getString(R.string.temperature_label), temperature));
            setTempTimer();
        }
    };

    private void setTempTimer() {

        tempTimer = new CountDownTimer(60*secondiTimer, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                tempTimer.cancel();
                tempTimer = null;

                SingletonManager.sendManualCommand(1, temperature, new SingletonCallback() {
                    @Override
                    public void doCallback(Object dataReturned) {
                        Toast.makeText(RoomDetailsActivity.this, getString(R.string.new_temp, temperature), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }

    private void setModeTimer() {
        modeTimer = new CountDownTimer(60*secondiTimer, 1) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                modeTimer.cancel();
                modeTimer = null;

                SingletonManager.sendManualCommand(1, -1, new SingletonCallback() {
                    @Override
                    public void doCallback(Object dataReturned) {
                        Toast.makeText(RoomDetailsActivity.this, getString(R.string.man_mode), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }

    View.OnClickListener buttonMin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if ((temperature - 0.5f) >= 0) {
                temperature -= 0.5f;
                txtEditTemp.setText(String.format(getResources().getString(R.string.temperature_label), temperature));
                setTempTimer();
            }
        }
    };

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
