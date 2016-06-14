package com.polito.fez.domotichome;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.polito.fez.domotichome.datastructure.StateData;
import com.polito.fez.domotichome.firebase.SingletonCallback;
import com.polito.fez.domotichome.firebase.SingletonManager;

import java.util.List;
import java.util.Map;

public class RoomDetailsActivity extends AppCompatActivity {

    private TextView txtEditTemp, txtHumidity, txtTemperature;
    private ImageButton btnPlus, btnMinus;
    private List<StateData> statesList;
    private float temperature;
    private float humidity;
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
            setInfoIntoView();
        }
    }

    private void setInfoIntoView() {

        this.txtEditTemp = (TextView) findViewById(R.id.txtEditTemperature);
        this.txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        this.txtTemperature = (TextView) findViewById(R.id.txtTemperature);
        this.btnPlus = (ImageButton) findViewById(R.id.btnPlusTemp);
        this.btnMinus = (ImageButton) findViewById(R.id.btnMinusTemp);

        SingletonManager.getStates(new SingletonCallback() {
            @Override
            public void doCallback(Object dataReturned) {
                Map<Integer, List<StateData>> statesMap = (Map<Integer, List<StateData>>) dataReturned;

                statesList = statesMap.entrySet().iterator().next().getValue();

                for (StateData state : statesList) {

                    switch (state.getCodeEventType()) {
                        case temperature:
                            temperature = state.getValueRead();
                            txtEditTemp.setText(String.format("%.2f", temperature));
                            txtEditTemp.setText(String.format("%.2f", temperature));
                            txtTemperature.setText(String.format("%.2f", temperature));
                            break;
                        case humidity:
                            humidity = state.getValueRead();
                            txtHumidity.setText(String.format("%.2f", humidity));
                            break;
                        case light:
                            if (state.getValueRead() == 0) {
                                lightOn = false;
                            } else {
                                lightOn = true;
                            }
                            break;
                        case warm:
                            if (state.getValueRead() == 0) {
                                warmOn = false;
                            } else {
                                warmOn = true;
                            }
                            break;
                    }
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
    }
}
