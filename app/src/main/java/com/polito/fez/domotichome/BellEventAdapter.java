package com.polito.fez.domotichome;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.polito.fez.domotichome.datastructure.BellEventData;

import java.util.List;

public class BellEventAdapter extends RecyclerView.Adapter<BellEventHolder> {

    private List<BellEventData> bellList;
    private LayoutInflater inflater;

    public BellEventAdapter(Context context, List<BellEventData> _bellList) {
        this.bellList = _bellList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public BellEventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View card = this.inflater.inflate(R.layout.bell_card, parent, false);
        return new BellEventHolder(card);
    }

    @Override
    public void onBindViewHolder(BellEventHolder holder, int position) {
        BellEventData current = this.bellList.get(position);
        holder.setContents(current);
    }

    @Override
    public int getItemCount() {
        return bellList.size();
    }
}
