package com.polito.fez.domotichome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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
    private boolean light;
    private boolean warm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

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

                for (StateData state: statesList) {
                    switch(state.getCodeEventType()) {
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
                            if(state.getValueRead() == 0) {
                                light = false;
                            } else {
                                light = true;
                            }
                            break;
                        case warm:
                            if(state.getValueRead() == 0) {
                                warm = false;
                            } else {
                                warm = true;
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
                if((temperature - 0.5f) >= 0) {
                    temperature -= 0.5f;
                    txtEditTemp.setText(String.format("%.2f", temperature));
                }
            }
        });
    }
}
