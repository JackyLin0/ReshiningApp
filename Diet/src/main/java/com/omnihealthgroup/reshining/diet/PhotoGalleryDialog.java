package com.omnihealthgroup.reshining.diet;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by lhm05 on 2016/08/19.
 */
public class PhotoGalleryDialog extends Dialog {
    private ImageView iv;
    private Button  btn;

    public PhotoGalleryDialog(Context context, int themeResId, Bitmap bm){
        super(context,themeResId);
        setContentView(R.layout.dialog_photo);
        iv=(ImageView)findViewById(R.id.dialog_pic);
        iv.setImageBitmap(bm);


        btn=(Button) findViewById(R.id.close_photo);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

       // Window window = getWindow();
       // WindowManager.LayoutParams params = window.getAttributes();
//        float density = getDensity(context);
//        params.width = (int) density*300 ;
//        params.height =(int) density*280;
//        params.gravity = Gravity.CENTER;
//        window.setAttributes(params);

        setCancelable(true);
        setCanceledOnTouchOutside(true);


    }

    private float getDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.density;
    }
}
