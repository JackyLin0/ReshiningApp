package com.omnihealthgroup.reshining.diet;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.omnihealthgroup.reshining.diet.camera.CameraViewGroup;

/**
 * Created by lhm05 on 2016/08/18.
 */
public class CameraActivity extends Activity {

    private String dateString;
    private String dating_tpye;
    private String TAG="CameraActivity";
    private CameraViewGroup camera_group;
    LinearLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_camera);
        container=(LinearLayout)findViewById(R.id.container);
        dateString=getIntent().getStringExtra("dateview");
        dating_tpye=getIntent().getStringExtra("dating_tpye");
        camera_group =new CameraViewGroup(this,null,dateString,dating_tpye);


        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
        , ViewGroup.LayoutParams.MATCH_PARENT);
        camera_group.setLayoutParams(lp);
        container.addView(camera_group);


        super.onCreate(savedInstanceState);
    }
}
