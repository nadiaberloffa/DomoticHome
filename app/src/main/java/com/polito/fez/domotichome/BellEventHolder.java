package com.polito.fez.domotichome;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.polito.fez.domotichome.datastructure.BellEventData;
import com.polito.fez.domotichome.firebase.SingletonCallback;
import com.polito.fez.domotichome.firebase.SingletonManager;

public class BellEventHolder extends RecyclerView.ViewHolder {

    private TextView txtEvent;
    private TextView txtEventDate;
    private ImageView img;
    private View itemView;
    private Bitmap bitmap1;
    private String path1, path2, path3;

    public BellEventHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        txtEvent = (TextView)itemView.findViewById(R.id.txtEvent);
        txtEventDate = (TextView)itemView.findViewById(R.id.txtEventDate);
        img = (ImageView)itemView.findViewById(R.id.img);
    }

    public void setContents(BellEventData current) {
        path1 = current.getPath1();
        path2 = current.getPath2();
        path3 = current.getPath3();
        txtEventDate.setText(current.getTimestamp());

        if (path1.equals("")) {
            txtEvent.setText(R.string.receive_call);
            img.setVisibility(View.GONE);
        } else {
            txtEvent.setText(R.string.lost_call);

            byte[] decodedString = Base64.decode(path1, Base64.DEFAULT);
            bitmap1 = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            img.setImageBitmap(bitmap1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Dialog dialog = new Dialog(v.getContext());

                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                    dialog.setContentView(R.layout.show_photo);
                    dialog.setTitle(R.string.frames_title);

                    ImageView photo1 = (ImageView) dialog.findViewById(R.id.photo1);
                    if (photo1 != null)
                        photo1.setImageBitmap(bitmap1);

                    byte[] decodedString = Base64.decode(path2, Base64.DEFAULT);
                    Bitmap bitmap2 = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    ImageView photo2 = (ImageView) dialog.findViewById(R.id.photo2);
                    if (photo1 != null)
                        photo2.setImageBitmap(bitmap2);

                    decodedString = Base64.decode(path3, Base64.DEFAULT);
                    Bitmap bitmap3 = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    img.setImageBitmap(bitmap1);

                    ImageView photo3 = (ImageView) dialog.findViewById(R.id.photo3);
                    if (photo3 != null)
                        photo3.setImageBitmap(bitmap3);

                    Button buttonOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
                    buttonOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
        }
    }
}
