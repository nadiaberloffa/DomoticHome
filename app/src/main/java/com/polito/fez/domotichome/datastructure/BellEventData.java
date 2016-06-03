package com.polito.fez.domotichome.datastructure;

/**
 * Created by Nadia on 23/05/2016.
 */
public class BellEventData {

    private int mySqlId;
    public int getMySqlId() { return mySqlId; }
    public void setMySqlId(int _id) { this.mySqlId = _id; }

    private int room;
    public int getRoom() { return room; }
    public void setRoom(int _room) { this.room = _room; }

    private String timestamp;
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String _timestamp) { this.timestamp = _timestamp; }

    private String path;
    public String getPath() { return path; }
    public void setPath(String _path) { this.path = _path; }

    @Override
    public String toString() {
        return "BellEventData (SQL:" + mySqlId + ") -> timestamp:" + timestamp + ", room:" + room + ", path:" + path;
    }
}
