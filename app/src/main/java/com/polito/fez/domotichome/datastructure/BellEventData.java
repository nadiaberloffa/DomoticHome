package com.polito.fez.domotichome.datastructure;

/**
 * Created by Nadia on 23/05/2016.
 */
public class BellEventData {

    private int MySqlId;
    public int getMySqlId() { return MySqlId; }
    public void setMySqlId(int _id) { this.MySqlId = _id; }

    private int Room;
    public int getRoom() { return Room; }
    public void setRoom(int _room) { this.Room = _room; }

    private String Timestamp;
    public String getTimestamp() { return Timestamp; }
    public void setTimestamp(String _timestamp) { this.Timestamp = _timestamp; }

    private String Path;
    public String getPath() { return Path; }
    public void setPath(String _path) { this.Path = _path; }

    @Override
    public String toString() {
        return "BellEventData (SQL:" + MySqlId + ") -> timestamp:" + Timestamp + ", room:" + Room + ", path:" + Path;
    }
}
