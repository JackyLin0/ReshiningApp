package com.omnihealthgroup.reshining.diet.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.omnihealthgroup.reshining.custom.IO.TokenGet;
import com.omnihealthgroup.reshining.diet.R;

/**
 * Created by lhm05 on 2016/08/03.
 */
public class CameraViewGroup extends RelativeLayout {
    private ImageButton camera_btn;
    private String TAG="camera";
    private CameraView cameraView;
    private String dateString;
    private String dating_tpye;

    public CameraViewGroup(Context context, AttributeSet attrs,String dateString,String dating_tpye) {
        super(context, attrs);
        cameraView=new CameraView(context,attrs, dateString, dating_tpye);
        addView(cameraView);

        this.dateString=dateString;
        this.dating_tpye=dating_tpye;

        camera_btn=new ImageButton(context, attrs);
        LayoutParams lp=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(ALIGN_PARENT_BOTTOM);
        lp.addRule(CENTER_HORIZONTAL);

        this.dateString=dateString;
        this.dating_tpye=dating_tpye;

        camera_btn.setLayoutParams(lp);
        Bitmap bm= BitmapFactory.decodeResource(getResources(), R.drawable.camera_btn);

        camera_btn.setImageBitmap(bm);
        addView(camera_btn);
        camera_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                cameraView.onClick(view);
            }
        });

    }


    public void setPamater(String dateString,String dating_tpye) {


    }
}
