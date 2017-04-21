package com.omnihealthgroup.reshining;


import android.app.FragmentManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.omnihealthgroup.reshining.custom.Util.WebServiceConnection;
import com.omnihealthgroup.reshining.custom.dao.GCMDataDAO;
import com.omnihealthgroup.reshining.custom.object.GCMData;
import com.omnihealthgroup.reshining.fragment.LoginFragment;
import com.omnihealthgroup.reshining.fragment.MainFragment;
import com.omnihealthgroup.reshining.gcm.QuickstartPreferences;
import com.omnihealthgroup.reshining.gcm.RegistrationIntentService;
import com.omnihealthgroup.reshining.schedule.WeekEventDialogFragment;
import com.omnihealthgroup.reshining.schedule.dao.DialogEventData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //    private static final String TAG = "MainActivity";
    private String TAG = MainActivity.class.getSimpleName();
    public static DialogFragment weekEventDialogFragment;

    private WebServiceConnection webServiceConnection;
    private SharedPreferences prf;

    //GCM
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 10000;
    private BroadcastReceiver gcmRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "onCreate");

        prf = getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();


        // GCM Registration BroadcastReceiver
        gcmRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.v(TAG, "Token sended");
                } else {
                    Log.v(TAG, "Token not send");
                }
            }
        };

        // Registering BroadcastReceiver
        isReceiverRegistered = false;
        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        Fragment fragment = new LoginFragment();
        //        String data = getIntent().getStringExtra("title");
        GCMDataDAO gcmDataDAO = new GCMDataDAO(MainActivity.this);
        List<GCMData> gcmDatas = gcmDataDAO.getuserdata_status();
        if (gcmDatas.size() > 0) {
            Bundle bundle = new Bundle();
            bundle.putString("title", gcmDatas.get(0).getTitle());
            fragment.setArguments(bundle);
            for (int i = 0; i < gcmDatas.size(); i++) {
                gcmDatas.get(i).setStatus("1");
                gcmDataDAO.update_status(gcmDatas.get(i));
            }
            //取消以前顯示的所有通知
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        } else {
            Log.v(TAG + "getIntent()", "NOOOOOOOOOOOOOOOOOOOOOOOOOO");
        }

        //        if (getIntent().getExtras() != null) {
        //            Bundle bundle = getIntent().getExtras();
        //            fragment.setArguments(bundle);
        //        }
        // 載入登入頁面碎片
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

        checkPlayServices();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");

        if (weekEventDialogFragment != null) {
            weekEventDialogFragment.dismiss();
        }
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop");

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy");

        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v(TAG, "onNewIntent");
        Fragment fragment = new MainFragment();
        GCMDataDAO gcmDataDAO = new GCMDataDAO(MainActivity.this);
        List<GCMData> gcmDatas = gcmDataDAO.getuserdata_status();
        if (gcmDatas.size() > 0) {
            Bundle bundle = new Bundle();
            bundle.putString("title_onNewIntent", gcmDatas.get(0).getTitle());
            fragment.setArguments(bundle);
            for (int i = 0; i < gcmDatas.size(); i++) {
                gcmDatas.get(i).setStatus("1");
                gcmDataDAO.update_status(gcmDatas.get(i));
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        } else {
            Log.v(TAG + "onNewIntent", "NOOOOOOOOOOOOOOOOOOOOOOOOOO");
        }
    }

    @Override
    public void onBackPressed() {
        //        super.onBackPressed();
        Log.v(TAG, "onBackPressed");

        FragmentManager fm = this.getFragmentManager();
        if (fm.getBackStackEntryCount() != 0) {
            fm.popBackStack();
        } else {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.message_title))
                    .setMessage(getString(R.string.msg_leave_confirm))
                    .setPositiveButton(getString(R.string.msg_confirm), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.msg_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    }).show();
        }
        //        /*MyPatient用*/
        //        FragmentManager fm = getSupportFragmentManager();
        //        int count = fm.getBackStackEntryCount();
        //
        //        Log.i("MY_PATIENT", "count" + count);
        //        for (int i = 0; i < count; i++) {
        //            if (fm.getBackStackEntryAt(i).getName() != null)
        //                Log.i("MY_PATIENT", i + ";" + fm.getBackStackEntryAt(i).getName());
        //
        //        }
        //
        //        Log.v("TAG", TAG);
        //
        //        //        if (!MyPatientsFragment.isLock) {
        //        //            if (fm.getBackStackEntryCount() >= 1) {
        //        //                for (int i = 0; i < count; i++) {
        //        //                    String backStackEntryName = fm.getBackStackEntryAt(i).getName();
        //        //                    if (backStackEntryName != null && backStackEntryName.equals("myPatient")) {
        //        //                        fm.popBackStack();
        //        //                        break;
        //        //                    }
        //        //                }
        //        //            }
        //        //        }
        //        //        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
        //
        //        finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void insertGCMevent(Intent intent) {
        Log.v(TAG + "onNewIntent", intent.getStringExtra("title"));
        String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        GCMDataDAO gcmDataDAO = new GCMDataDAO(MainActivity.this);
        GCMData gcmData = new GCMData();

        gcmData.setReceivetime(timeStr);
        gcmData.setTitle(intent.getStringExtra("title"));
        gcmData.setBody(intent.getStringExtra("body"));
        gcmData.setStatus("1");
        gcmDataDAO.insert(gcmData);

    }

    //    private void showGCMevent(Intent intent) {
    //        Log.v(TAG + "onNewIntent", intent.getStringExtra("title"));
    //        String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    //
    //        Bundle bundle = new Bundle();
    //        bundle.putString("GCM_time", timeStr);
    //        bundle.putString("GCM_title", intent.getStringExtra("title"));
    //        bundle.putString("GCM_body", intent.getStringExtra("body"));
    //
    //        DialogFragment dialogFragment = new ShowGCMFragment();
    //        dialogFragment.setArguments(bundle);
    //        dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PageTransparent);
    //        dialogFragment.show(getSupportFragmentManager(), "ShowGCMFragment");
    //
    //    }
    //

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 月檢視頁面點擊單日事件監聽
     *
     * @param view touch view
     */

    public void showWeekEvent(View view) {
        int yearTag = (Integer) view.getTag(R.id.synchro_year);
        int instanceYear = Calendar.getInstance().get(Calendar.YEAR);
        if (yearTag == instanceYear
                || yearTag == (instanceYear - 1)
                || yearTag == (instanceYear + 1)
                || yearTag == (instanceYear + 2)
                || yearTag == (instanceYear + 3)) {
            //  取得第一個顯示的fragment編號
            DialogEventData dialogEventData = DialogEventData.showDialogEventData;
            dialogEventData.setDialogDayOfWeek((Integer) view.getTag(R.id.synchro_day_in_week));
            dialogEventData.setStartDialogYear(yearTag);
            dialogEventData.setStartDialogMonth((Integer) view.getTag(R.id.synchro_month));
            dialogEventData.setStartDialogDate((Integer) view.getTag(R.id.synchro_date));

            weekEventDialogFragment = new WeekEventDialogFragment();
            weekEventDialogFragment.setStyle(
                    DialogFragment.STYLE_NORMAL,
                    R.style.PageTransparent
            );
            weekEventDialogFragment.show(MainActivity.this
                    .getSupportFragmentManager(), "WeekEventDialog");
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * GCM Receiver Registration
     */
    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager
                    .getInstance(this)
                    .registerReceiver(
                            gcmRegistrationBroadcastReceiver,
                            new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE)
                    );
            isReceiverRegistered = true;
            Log.d(TAG, "GCM Receiver Registed");
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.v(TAG, "This device is not supported.");

                finish();
            }
            webServiceConnection.initToast(MainActivity.this, getString(R.string.gcm_note));
            return false;
        }
        return true;
    }

    //    // When you change direction of phone, this method will be called.
    //    // It store the state of video (Current position)
    //    @Override
    //    public void onSaveInstanceState(Bundle savedInstanceState) {
    //        super.onSaveInstanceState(savedInstanceState);
    //
    //        // Store current position.
    //        savedInstanceState.putInt("CurrentPosition", videoView.getCurrentPosition());
    //        videoView.pause();
    //    }
    //
    //
    //    // After rotating the phone. This method is called.
    //    @Override
    //    public void onRestoreInstanceState(Bundle savedInstanceState) {
    //        super.onRestoreInstanceState(savedInstanceState);
    //
    //        // Get saved position.
    //        position = savedInstanceState.getInt("CurrentPosition");
    //        videoView.seekTo(position);
    //    }

}
