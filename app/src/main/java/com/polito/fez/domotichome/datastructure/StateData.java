package com.polito.fez.domotichome.datastructure;

import com.google.firebase.database.Exclude;

/**
 * Created by Nadia on 23/05/2016.
 */
public class StateData {

    public enum CodeEventType { temperature, humidity, bell, light, warm }


    private int CodeEvent;
    public int getCodeEvent() { return CodeEvent; }
    @Exclude
    public CodeEventType getCodeEventType() {
        return CodeEventType.values()[CodeEvent];
    }
    public void setCodeEvent(int _code) { this.CodeEvent = _code; }

    private String FirebaseId;
    @Exclude
    public String getFirebaseId() { return FirebaseId; }
    public void setFirebaseId(String _fbId) { this.FirebaseId = _fbId; }

    private int Room;
    public int getRoom() {  return Room; }
    public void setRoom(int _room) { this.Room = _room; }

    private String Timestamp;
    public String getTimestamp() { return Timestamp; }
    public void setTimestamp(String _timestamp) { this.Timestamp = _timestamp; }

    private float ValueRead;
    public float getValueRead() { return ValueRead; }
    public void setValueRead(float _value) { this.ValueRead = _value; }

    public StateData() {
        this.CodeEvent = CodeEventType.temperature.ordinal();
        this.FirebaseId = "";
        this.Room = 1;
        this.Timestamp = "";
        this.ValueRead = 0;
    }

    @Override
    @Exclude
    public String toString() {
        return "StateData (FB:" + FirebaseId + ") -> CodeEvent:" + CodeEvent + ", Timestamp:" +
                Timestamp + ", ValueRead:" + ValueRead;
    }
}
