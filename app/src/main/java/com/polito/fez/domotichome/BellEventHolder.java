package com.polito.fez.domotichome;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.polito.fez.domotichome.datastructure.BellEventData;
import com.polito.fez.domotichome.firebase.SingletonCallback;
import com.polito.fez.domotichome.firebase.SingletonManager;

public class BellEventHolder extends RecyclerView.ViewHolder {

    private TextView txtEvent;
    private TextView txtEventDate;
    private ImageView img;

    public BellEventHolder(View itemView) {
        super(itemView);
        txtEvent = (TextView)itemView.findViewById(R.id.txtEvent);
        txtEventDate = (TextView)itemView.findViewById(R.id.txtEventDate);
        img = (ImageView)itemView.findViewById(R.id.img);
    }

    public void setContents(BellEventData current) {
        String path = current.getPath();
        txtEventDate.setText(current.getTimestamp());

        if (path.equals("")) {
            txtEvent.setText("Evento preso");
            img.setVisibility(View.GONE);
        } else {
            txtEvent.setText("Evento perso");

            SingletonManager.getPathFromStorage(path, new SingletonCallback(){
                @Override
                public void doCallback(Object dataReturned) {
                    if(dataReturned!=null) {
                        byte[] decodedString = Base64.decode((String)dataReturned, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        img.setImageBitmap(bitmap);
                    }
                }
            });
        }
    }
}
