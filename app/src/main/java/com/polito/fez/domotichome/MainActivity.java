package com.polito.fez.domotichome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.polito.fez.domotichome.datastructure.StateData;
import com.polito.fez.domotichome.firebase.SingletonCallback;
import com.polito.fez.domotichome.firebase.SingletonManager;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Map<Integer, List<StateData>> statesMap;
    private View room1, room2, room3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer!=null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        this.room1 = findViewById(R.id.room1);
        this.room2 = findViewById(R.id.room2);
        assert room2 != null;
        room2.setOnClickListener(noSensorIntoRoom);

        this.room3 = findViewById(R.id.room3);
        assert room3 != null;
        room3.setOnClickListener(noSensorIntoRoom);

        SingletonManager.getStates(new SingletonCallback() {
            @Override
            public void doCallback(Object dataReturned) {
                statesMap = (Map<Integer, List<StateData>>) dataReturned;

                LinearLayout layout = (LinearLayout) findViewById(R.id.lin1);
                assert layout != null;
                layout.setVisibility(View.VISIBLE);

                ContentLoadingProgressBar progressBar = (ContentLoadingProgressBar) findViewById(R.id.progressBar);
                assert progressBar != null;
                progressBar.setVisibility(View.GONE);

                List<StateData> states = statesMap.entrySet().iterator().next().getValue();
                TextView txtRoomName = (TextView) room1.findViewById(R.id.txtRoomName);
                TextView txtTemperature = (TextView) room1.findViewById(R.id.txtTemperature);
                TextView txtHumidity = (TextView) room1.findViewById(R.id.txtHumidity);

                String roomName = String.format(getResources().getString(R.string.room_name), states.get(0).getRoom());
                txtRoomName.setText(roomName);

                for (StateData data: states) {
                    switch(data.getCodeEventType()) {
                        case temperature:
                            String temp = String.format(getResources().getString(R.string.temperature_label), data.getValueRead());
                            txtTemperature.setText(temp);
                            break;
                        case humidity:
                            String hum = String.format(getResources().getString(R.string.temperature_label), data.getValueRead());
                            txtHumidity.setText(hum);
                            break;
                    }
                }

                // FOR OTHERS ROOMS
                TextView txtRoomName2 = (TextView) room2.findViewById(R.id.txtRoomName);
                txtRoomName2.setText("Room 2");
                TextView txtTemperature2 = (TextView) room2.findViewById(R.id.txtTemperature);
                txtTemperature2.setText(" - ");
                TextView txtHumidity2 = (TextView) room2.findViewById(R.id.txtHumidity);
                txtHumidity2.setText(" - ");

                TextView txtRoomName3 = (TextView) room3.findViewById(R.id.txtRoomName);
                txtRoomName3.setText("Room 3");
                TextView txtTemperature3 = (TextView) room3.findViewById(R.id.txtTemperature);
                txtTemperature3.setText(" - ");
                TextView txtHumidity3 = (TextView) room3.findViewById(R.id.txtHumidity);
                txtHumidity3.setText(" - ");
            }
        });
    }

    public void clickRoom1(View view) {
        Intent intent = new Intent(this, RoomDetailsActivity.class);
        intent.putExtra("roomID",1);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_send:
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sharedPref), 0);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove("email");
                editor.remove("password");
                editor.apply();
                SingletonManager.logout(new SingletonCallback() {
                    @Override
                    public void doCallback(Object dataReturned) {
                        boolean isLogout = (boolean) dataReturned;
                        if(isLogout){
                            Toast.makeText(getApplicationContext(),"Logged Out", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                            finish();
                        }
                    }
                });

                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    View.OnClickListener noSensorIntoRoom = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Snackbar.make(v, getString(R.string.room_no_active), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    };
}
