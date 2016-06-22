package com.polito.fez.domotichome.datastructure;

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

    private String Path1;
    public String getPath1() { return Path1; }
    public void setPath1(String _path1) { this.Path1 = _path1; }

    private String Path2;
    public String getPath2() { return Path2; }
    public void setPath2(String _path2) { this.Path2 = _path2; }

    private String Path3;
    public String getPath3() { return Path3; }
    public void setPath3(String _path3) { this.Path1 = _path3; }

    @Override
    public String toString() {
        return "BellEventData (SQL:" + MySqlId + ") -> timestamp:" + Timestamp + ", room:" + Room + ", path:" + Path1;
    }
}
