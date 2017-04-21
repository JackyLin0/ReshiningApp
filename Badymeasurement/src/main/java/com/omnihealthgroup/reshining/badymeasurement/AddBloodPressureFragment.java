package com.omnihealthgroup.reshining.badymeasurement;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.omnihealthgroup.drawerframework.DrawerFrameworkMainFragment;
import com.omnihealthgroup.reshining.custom.Util.ShowMEProgressDiaLog;
import com.omnihealthgroup.reshining.custom.Util.WebServiceConnection;
import com.omnihealthgroup.reshining.custom.dao.BioDataAdapter;
import com.omnihealthgroup.reshining.custom.dao.MeasureStandardDAO;
import com.omnihealthgroup.reshining.custom.dao.UserDataDAO;
import com.omnihealthgroup.reshining.custom.object.BioData;
import com.omnihealthgroup.reshining.custom.object.MeasureStandard;
import com.omnihealthgroup.reshining.custom.object.UserData;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class AddBloodPressureFragment extends DrawerFrameworkMainFragment {
    private static final String TAG = "AddBloodPressureFragment";

    private static EditText editTextRecordDateTime;
    private Button newEventCancel, newEventCommit;
    private NumberPicker numPicker_sbp, numPicker_dbp, numPicker_hr;

    private List<MeasureStandard> SBP, DBP, HR;
    private String num_sbp = null, num_dbp = null, num_hr = null;

    private View rootView;
    private WebServiceConnection webServiceConnection;
    private SharedPreferences prf;

    /**
     * 載入共用ToolBar
     *
     * @param toolbar toolbar
     */
    @Override
    protected void onSetToolbar(Toolbar toolbar) {
        super.onSetToolbar(toolbar);
        String title = getString(R.string.fragment_bloopressure) + " - " + getString(R.string.add_bloodpressure_title);
        toolbar.setTitle(title);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //editview 不會自動跳出鍵盤
        prf = getActivity().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_addbloodpressure, container, false);
        Log.v(TAG, "onCreateView");

        initView();
        loadPageView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

        // 監聽返回鍵
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button
                    CancelAlertDialog();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");

        //        boolean isStartMainService = webServiceConnection.isServiceRunning(getActivity(), MainService.class.getName());
        //        if (isStartMainService) {
        //            //  關閉BT3.0的Service
        //            Intent btServiceIntent = new Intent(getActivity(), GetBlueToothDeviceDataService.class);
        //            getActivity().stopService(btServiceIntent);
        //            //  關閉解析BT3.0的Service
        //            Intent MainServiceIntent = new Intent(getActivity(), MainService.class);
        //            getActivity().stopService(MainServiceIntent);
        //        }
    }

    private void initView() {
        LinearLayout lay_trend = (LinearLayout) rootView.findViewById(R.id.lay_trend);
        LinearLayout lay_datalist = (LinearLayout) rootView.findViewById(R.id.lay_datalist);
        LinearLayout lay_datainput = (LinearLayout) rootView.findViewById(R.id.lay_datainput);

        ImageView btn_trend = (ImageView) rootView.findViewById(R.id.btn_trend);
        ImageView btn_datalist = (ImageView) rootView.findViewById(R.id.btn_datalist);
        ImageView btn_datainput = (ImageView) rootView.findViewById(R.id.btn_datainput);

        TextView text_trend = (TextView) rootView.findViewById(R.id.text_trend);
        TextView text_datalist = (TextView) rootView.findViewById(R.id.text_datalist);
        TextView text_datainput = (TextView) rootView.findViewById(R.id.text_datainput);

        lay_trend.setOrientation(LinearLayout.VERTICAL);
        lay_trend.setBackgroundResource(R.mipmap.lay_icon_off);
        lay_datalist.setOrientation(LinearLayout.VERTICAL);
        lay_datalist.setBackgroundResource(R.mipmap.lay_icon_off);
        lay_datainput.setOrientation(LinearLayout.VERTICAL);
        lay_datainput.setBackgroundResource(R.mipmap.lay_icon_on);

        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp3.setMargins(0, 10, 0, 0);

        btn_trend.setImageResource(R.mipmap.btn_trend_off);
        btn_trend.setLayoutParams(lp3);

        text_trend.setGravity(Gravity.CENTER);
        text_trend.setPadding(0, 0, 5, 0);
        text_trend.setTextSize(16);

        btn_datalist.setImageResource(R.mipmap.btn_datalist_off);
        btn_datalist.setLayoutParams(lp3);

        text_datalist.setGravity(Gravity.CENTER);
        text_datalist.setPadding(0, 0, 5, 0);
        text_datalist.setTextSize(16);

        btn_datainput.setImageResource(R.mipmap.btn_datainput_on);
        btn_datainput.setLayoutParams(lp3);

        text_datainput.setGravity(Gravity.CENTER);
        text_datainput.setPadding(0, 0, 5, 0);
        text_datainput.setTextSize(16);

        numPicker_sbp = (NumberPicker) rootView.findViewById(R.id.numPicker_sbp);
        numPicker_dbp = (NumberPicker) rootView.findViewById(R.id.numPicker_dbp);
        numPicker_hr = (NumberPicker) rootView.findViewById(R.id.numPicker_hr);

        newEventCancel = (Button) rootView.findViewById(R.id.new_event_btn_cancel);
        newEventCancel.setTextColor(getResources().getColor(R.color.white_color));
        newEventCommit = (Button) rootView.findViewById(R.id.new_event_btn_commit);
        newEventCommit.setTextColor(getResources().getColor(R.color.white_color));

        editTextRecordDateTime = (EditText) rootView.findViewById(R.id.editTextRecordDateTime);
        //        editTextDiastolicBloodPressure = (EditText) rootView.findViewById(R.id.editTextDiastolicBloodPressure);
        //        editTextSystolicBloodPressure = (EditText) rootView.findViewById(R.id.editTextSystolicBloodPressure);
        //        editTextPulse = (EditText) rootView.findViewById(R.id.editTextPulse);

        MeasureStandardDAO measureStandardDAO = new MeasureStandardDAO(getContext());
        SBP = measureStandardDAO.getuserdata_item("SBP");
        DBP = measureStandardDAO.getuserdata_item("DBP");
        HR = measureStandardDAO.getuserdata_item("HR");

        ///////////////////////////
        rootView.findViewById(R.id.lay_trend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new MyHealthCareFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("fragmentType", 0);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_addbloodpressure, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        rootView.findViewById(R.id.lay_datalist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ListBloodPressureFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_addbloodpressure, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        rootView.findViewById(R.id.lay_datainput).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, TAG + "btn_datainput");

            }
        });

    }

    /**
     * 初始化完成按鈕與取消按鈕的監聽器
     */
    private void loadPageView() {
        //取消按鈕
        newEventCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelAlertDialog();
            }
        });

        //完成按鈕
        newEventCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveKeyInData();
            }
        });

        numPicker_sbp.setMaxValue(Integer.parseInt(SBP.get(0).getDangerMax().substring(0, SBP.get(0).getDangerMax().length() - 2)) + 60);
        numPicker_sbp.setMinValue(0);
        numPicker_sbp.setValue(Integer.parseInt(SBP.get(0).getWarningMin().substring(0, SBP.get(0).getWarningMin().length() - 2)) + 10);
        numPicker_sbp.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldNum, int newNum) {
                num_sbp = String.valueOf(newNum);
            }
        });

        if (num_sbp == null) {
            num_sbp = String.valueOf(numPicker_sbp.getValue());
        }

        numPicker_dbp.setMaxValue(Integer.parseInt(DBP.get(0).getDangerMax().substring(0, DBP.get(0).getDangerMax().length() - 2)) + 60);
        numPicker_dbp.setMinValue(0);
        numPicker_dbp.setValue(Integer.parseInt(DBP.get(0).getWarningMin().substring(0, DBP.get(0).getWarningMin().length() - 2)) + 10);
        numPicker_dbp.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldNum, int newNum) {
                //newNum=oldNum;
                num_dbp = String.valueOf(newNum);
            }
        });

        if (num_dbp == null) {
            num_dbp = String.valueOf(numPicker_dbp.getValue());
        }

        numPicker_hr.setMaxValue(Integer.parseInt(HR.get(0).getDangerMax().substring(0, HR.get(0).getDangerMax().length() - 2)) + 60);
        numPicker_hr.setMinValue(0);
        numPicker_hr.setValue(Integer.parseInt(HR.get(0).getWarningMin().substring(0, HR.get(0).getWarningMin().length() - 2)) + 10);
        numPicker_hr.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldNum, int newNum) {
                num_hr = String.valueOf(newNum);
            }
        });

        if (num_hr == null) {
            num_hr = String.valueOf(numPicker_hr.getValue());
        }


        //輸入量測日期 先帶入現在時間
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        editTextRecordDateTime.setText(sdf.format(new Date()));
        //        editTextRecordDateTime.setInputType(InputType.TYPE_NULL); // 關閉軟鍵盤

        editTextRecordDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 設定初始日期
                showTruitonTimePickerDialog(v);
                showTruitonDatePickerDialog(v);
            }
        });
    }

    public void showTruitonDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getChildFragmentManager(), "datePicker");
    }

    public void showTruitonTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getChildFragmentManager(), "timePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user(顯示在EditView中Month要加一)
            editTextRecordDateTime.setText(year + "-" + (month + 1) + "-" + day);
        }
    }

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Calendar c = Calendar.getInstance();

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute
                    , DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            int second = c.get(Calendar.SECOND);
            int millisecond = c.get(Calendar.MILLISECOND);

            // Do something with the time chosen by the user
            editTextRecordDateTime.setText(editTextRecordDateTime.getText() + " " + hourOfDay + ":" + minute + ":" + second + "." + millisecond);
            try {
                Log.v(TAG + "editTextRecordDateTime", editTextRecordDateTime.getText().toString());
                Date date = formatter.parse(editTextRecordDateTime.getText().toString());
                String timeStr = formatter.format(date);
                editTextRecordDateTime.setText(timeStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveKeyInData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String recorderDateTime = editTextRecordDateTime.getText().toString();
        BioData bioData = new BioData();
        bioData.setDeviceTime(recorderDateTime);
        bioData.setDeviceType(webServiceConnection.BIODATA_DEVICE_TYPE_BLOOD_PRESSURE);
        bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_MANUAL);

        Date recordDate;
        try {
            recordDate = sdf.parse(recorderDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            webServiceConnection.editTextAlertDialog(getContext(), getString(R.string.user_alert_message5));
            return;
        }

        try {
            if (num_sbp.equals("")) {
                webServiceConnection.editTextAlertDialog(getContext(), getString(R.string.user_enter_blood_pressure_sbp));
                return;
            } else if (Float.parseFloat(num_sbp) > Float.parseFloat(SBP.get(0).getDangerMax()) + 60f
                    || Float.parseFloat(num_sbp) < Float.parseFloat(SBP.get(0).getDangerMin()) - 30f) {
                webServiceConnection.editTextAlertDialog(getContext(), getString(R.string.user_enter_blood_pressure_sbp2));
                return;

            } else if (num_dbp.equals("")) {
                webServiceConnection.editTextAlertDialog(getContext(), getString(R.string.user_enter_blood_pressure_dbp));
                return;
            } else if (Float.parseFloat(num_dbp) > Float.parseFloat(DBP.get(0).getDangerMax()) + 60f
                    || Float.parseFloat(num_dbp) < Float.parseFloat(DBP.get(0).getDangerMin()) - 30f) {
                return;

            } else if (num_hr.equals("")) {
                webServiceConnection.editTextAlertDialog(getContext(), getString(R.string.user_enter_pulse));
                return;
            } else if (Float.parseFloat(num_hr) > Float.parseFloat(HR.get(0).getDangerMax()) + 60f
                    || Float.parseFloat(num_hr) < Float.parseFloat(HR.get(0).getDangerMin()) - 30f) {
                webServiceConnection.editTextAlertDialog(getContext(), getString(R.string.user_enter_pulse2));
                return;

            } else if (editTextRecordDateTime.getText().toString().trim().equals("")) {
                webServiceConnection.editTextAlertDialog(getContext(), getString(R.string.user_enter_blood_glucose_time));
                return;
            } else if (recordDate.after(new Date())) {
                webServiceConnection.editTextAlertDialog(getContext(), getString(R.string.user_alert_message6));
                editTextRecordDateTime.setText(sdf.format(new Date()));
                return;
            } else {
                bioData.setBhp(num_sbp);
                bioData.setBlp(num_dbp);
                bioData.setPulse(num_hr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //UserData
        //        UserAdapter userAdapter = new UserAdapter(AddMeasurePressureActivity.this);
        //        final User user = userAdapter.getUserUIdAndPassword();
        UserDataDAO userDataDAO = new UserDataDAO(getContext());
        List<UserData> userList = userDataDAO.getuserdata();
        String UserName = null;
        for (UserData user : userList) {
            Log.v(TAG + "userName", user.getName().toString());
            UserName = user.getName();
        }

        final BioDataAdapter bioDataAdapter = new BioDataAdapter(getContext());
        //        bioData.set_id(recorderDateTime + user.getUid());
        //        bioData.setUserId(user.getUid());
//        GcmUtil gcmUtil = new GcmUtil();
        //        bioData.set_id(recorderDateTime + gcmUtil.getDeviceSerail(AddMeasurePressureActivity.this));
        bioData.setUserId(UserName);
        //        bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_MANUAL);
        bioData.setUploaded(WebServiceConnection.DATA_IS_NOT_UPLOAD);
        bioDataAdapter.createBloodPressure(bioData);
        //        webServiceConnection.getMessageDialog(getString(R.string.message_title), getString(R.string.user_save_blood_pressure), getContext()).show();
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.message_title))
                .setMessage(getString(R.string.user_save_blood_pressure))
                .setPositiveButton(getString(R.string.msg_confirm),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(getContext()
                                        , getString(R.string.webview_loading_title)
                                        , getString(R.string.msg_tokenget), false, true);
                                pb.show();

                                //        final NetService netService = new NetService();
                                final ArrayList<BioData> listBioData = bioDataAdapter.getUploaded();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            while (true) {
                                                Log.i(TAG, "manual upload data");
                                                //                    String reUpdateResponse = netService.CallUploadVitalSign(user, listBioData, false);
                                                JSONObject reUpdateResponse = webServiceConnection.addMeasureResource(prf.getString("access_token", ""), listBioData, false);
                                                //                    if (reUpdateResponse != null && reUpdateResponse.equals("{\"Message\" : \"A01\"}")) {
                                                if (reUpdateResponse != null && reUpdateResponse.getString("message").toString().equals("Success.")) {
                                                    // 更新sql lite資料庫
                                                    bioDataAdapter.updataUploaded(listBioData);
                                                    //                                                    Log.i(TAG, "資料重新上傳成功");
                                                    Looper.prepare();
                                                    Toast.makeText(getContext(), getString(R.string.msg_net_reUpdate), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                    break;
                                                } else {
                                                    Looper.prepare();
                                                    Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                                //返回行事曆檢視畫面
                                pb.dismiss();
                                //                                finish();
                                Fragment fragment = new MyHealthCareFragment();
                                Bundle bundle = new Bundle();
                                bundle.putInt("fragmentType", 0);
                                fragment.setArguments(bundle);
                                getFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_addbloodpressure, fragment)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        }).show();
        //清除edittext
        //        editTextDiastolicBloodPressure.setText("");
        //        editTextSystolicBloodPressure.setText("");
        //        editTextPulse.setText("");
        editTextRecordDateTime.setText(sdf.format(new Date()));
    }

    private void CancelAlertDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder
                .setTitle(getResources().getString(R.string.new_measure_cancel_title))
                .setMessage(getResources().getString(R.string.new_event_cancel_message))
                .setPositiveButton(
                        getResources().getString(R.string.msg_confirm),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //取消新增私人行程回到行事曆檢視畫面
                                //                                finish();
                                Fragment fragment = new MyHealthCareFragment();
                                Bundle bundle = new Bundle();
                                bundle.putInt("fragmentType", 0);
                                fragment.setArguments(bundle);
                                getFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_addbloodpressure, fragment)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        })
                .setNeutralButton(
                        getResources().getString(R.string.msg_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //關閉提醒視窗回到新增私人行程
                            }
                        });
        AlertDialog alertDialog = dialogBuilder.show();
        TextView alertMessageText = (TextView) alertDialog.findViewById(android.R.id.message);
        alertMessageText.setGravity(Gravity.CENTER);
        alertDialog.show();
    }

}
