package com.polito.fez.domotichome.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polito.fez.domotichome.datastructure.BellEventData;
import com.polito.fez.domotichome.datastructure.StateData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SingletonManager {

    private static Map<Integer, List<StateData>> states = null;
    private static List<BellEventData> bellEvents = null;
    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private SingletonManager() {}

    public static void getStates(final SingletonCallback callback) {
        if(states == null) {
            states = new HashMap<>();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference statesRef = database.getReference("states");

            statesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap: dataSnapshot.getChildren()) {
                        StateData state = snap.getValue(StateData.class);

                        if(state != null) {
                            state.setFirebaseId(snap.getKey());

                            if(states.containsKey(state.getRoom())) {
                                List<StateData> statesList = states.get(state.getRoom());
                                statesList.add(state);
                                states.put(state.getRoom(), statesList);
                            } else {
                                List<StateData> newStates = new ArrayList<>();
                                newStates.add(state);
                                states.put(state.getRoom(), newStates);
                            }
                        }
                    }
                    callback.doCallback(states);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //statesRef.addChildEventListener(statesListener);
        } else {
            callback.doCallback(states);
        }
    }

    public static void getBellEvents(final SingletonCallback callback) {
        bellEvents = new LinkedList<>();

        final Date today = new Date();
        final Date sevenDayBefore = new Date(today.getTime() - 7 * 24 * 3600 * 1000 ); //Subtract n days = today.
        //Log.d("debugGiulia","date: "+dateFormat.format(today));
        //Log.d("debugGiulia","date7: "+dateFormat.format(sevenDayBefore));
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference statesRef = database.getReference("bell_events");

        statesRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    BellEventData bellEventData = snap.getValue(BellEventData.class);

                    if (bellEventData != null) {

                        try {
                            Date date = dateFormat.parse(bellEventData.getTimestamp());
                            if(date.after(sevenDayBefore))
                                bellEvents.add(bellEventData);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                callback.doCallback(bellEvents);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.doCallback(bellEvents);
            }
        });
    }

    public static void getPathFromStorage(String mySqlId, String path, final SingletonCallback callback) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference statesRef = database.getReference("bell_events");
        statesRef.child(mySqlId).child(path).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String file = "";
                if(dataSnapshot!=null) {
                    file=dataSnapshot.getValue(String.class);
                }
                callback.doCallback(file);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.doCallback("");
            }
        });
    }

    public static void login(final String email, final String password, final SingletonCallback callback) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        final FirebaseUser userFirebase = firebaseAuth.getCurrentUser();

                        if (userFirebase != null) {
                            if (!task.isSuccessful())
                                callback.doCallback(false);
                            else
                                callback.doCallback(true);
                        } else
                            callback.doCallback(false);
                    }
                });
    }

    public static void logout(final SingletonCallback callback){
        firebaseAuth.signOut();
        callback.doCallback(true);
    }

    public static void sendLightCommand(int room, int newVal, final SingletonCallback callback) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference statesRef = database.getReference("commands").child("3").child("valueRead"); // CodeEvent per Light on/off
        statesRef.setValue(newVal, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null) {
                    callback.doCallback(true);
                } else {
                    callback.doCallback(false);
                }
            }
        });
    }

    public static void sendManualCommand(int room, double newVal, final SingletonCallback callback) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference statesRef = database.getReference("commands").child("5").child("valueRead");
        statesRef.setValue(newVal, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null) {
                    callback.doCallback(true);
                } else {
                    callback.doCallback(false);
                }
            }
        });
    }

    public static void sendWarmCommand(int room, int newVal, final SingletonCallback callback) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference statesRef = database.getReference("commands").child("4").child("valueRead"); // CodeEvent per Light on/off
        statesRef.setValue(newVal, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null) {
                    callback.doCallback(true);
                } else {
                    callback.doCallback(false);
                }
            }
        });
    }
}
