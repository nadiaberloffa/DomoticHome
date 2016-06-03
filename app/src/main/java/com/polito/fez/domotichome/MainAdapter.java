package com.polito.fez.domotichome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.polito.fez.domotichome.datastructure.StateData;

import java.util.List;

/**
 * Created by Nadia on 24/05/2016.
 */
public class MainAdapter extends ArrayAdapter<StateData> {

    private Context context;

    public MainAdapter(Context context, int resourceId, List<StateData> states) {
        super(context, resourceId, states);
        this.context = context;
    }

    @Override
    public View getView(int position,  View convertView, ViewGroup paren) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.row_room_data, null);

        StateData state = getItem(position);

        TextView txtRoomName = (TextView) convertView.findViewById(R.id.txtRoomName);
        String roomName = context.getResources().getString(R.string.room_name, state.getRoom());
        txtRoomName.setText(roomName);

        TextView txtTemperature = (TextView) convertView.findViewById(R.id.txtTemperature);
        //String temp = context.getResources().getString(R.string.temperature_label, state.get)

        return convertView;
    }
}
