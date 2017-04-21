package com.omnihealthgroup.reshining.gcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmReceiver;
import com.omnihealthgroup.reshining.custom.dao.GCMDBHelper;
import com.omnihealthgroup.reshining.custom.dao.GCMDataDAO;
import com.omnihealthgroup.reshining.custom.object.GCMData;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/24.
 */
public class MyGcmReceiver extends GcmReceiver {
    private static String TAG = MyGcmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        try {
            Bundle bundle = intent.getExtras();
            Log.v(TAG, bundle.toString());

            String title = bundle.getString("gcm.notification.title");
            Log.v(TAG, title);
            String body = bundle.getString("gcm.notification.body");
            Log.v(TAG, body);

            insertGCMevent(context, title, body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertGCMevent(Context context, String title, String body) {
        try {
            String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            GCMDataDAO gcmDataDAO = new GCMDataDAO(context);
            GCMData gcmData = new GCMData();
            if (!gcmDataDAO.dbIsOpen()) {
                GCMDBHelper.getDatabase(context);
            }

            gcmData.setReceivetime(timeStr);
            gcmData.setTitle(title);
            gcmData.setBody(body);
            gcmData.setStatus("0");
            gcmDataDAO.insert(gcmData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
