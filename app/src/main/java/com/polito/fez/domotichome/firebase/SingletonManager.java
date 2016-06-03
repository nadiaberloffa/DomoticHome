package com.polito.fez.domotichome.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polito.fez.domotichome.datastructure.StateData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nadia on 24/05/2016.
 */
public class SingletonManager {

    private static Map<Integer, List<StateData>> states;

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


}
