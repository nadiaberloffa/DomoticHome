package com.polito.fez.domotichome;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.polito.fez.domotichome.datastructure.BellEventData;
import com.polito.fez.domotichome.firebase.SingletonCallback;
import com.polito.fez.domotichome.firebase.SingletonManager;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class BellEventsActivity extends AppCompatActivity {

    private List<BellEventData> bellList;
    private ContentLoadingProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bell_events);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarBack);
        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.bell_event_title));
            toolbar.setTitleTextColor(ContextCompat.getColor(this.getApplicationContext(), R.color.colorTitleToolbar));
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {  //se click su "indietro"
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        bellList = new LinkedList<>();
        progressBar = (ContentLoadingProgressBar)findViewById(R.id.progressBar);

        SingletonManager.getBellEvents(new SingletonCallback() {
            @Override
            public void doCallback(Object dataReturned) {
                if (dataReturned != null) {
                    bellList = (List<BellEventData>) dataReturned;
                    setRecyclerView();
                }else{
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setRecyclerView() {

        RecyclerView recyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);

        progressBar.setVisibility(View.GONE);

        //TODO: ordinare la lista -> bellList
        Collections.sort(bellList, new Comparator<BellEventData>() {
            @Override//< 0 if lhs is less than rhs, 0 if they are equal, and > 0 if lhs is greater than rhs.
            public int compare(BellEventData lhs, BellEventData rhs) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy HH:mm:ss");

                try {
                    Date lhsDate = dateFormat.parse(lhs.getTimestamp());
                    Date rhsDate = dateFormat.parse(rhs.getTimestamp());

                    Timestamp lhsTime = new java.sql.Timestamp(lhsDate.getTime());
                    Timestamp rhsTime = new java.sql.Timestamp(rhsDate.getTime());

                    if (lhsTime.before(rhsTime)) return -1;
                    else if (lhsTime.equals(rhsTime)) return 0;
                    else return 1;

                } catch (Exception e) {
                    e.printStackTrace();
                    return -1;
                }
            }
        });

        BellEventAdapter adapter = new BellEventAdapter(this,bellList);
        assert recyclerView != null;
        recyclerView.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}