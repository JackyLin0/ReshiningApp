package com.omnihealthgroup.reshining.badymeasurement;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.omnihealthgroup.drawerframework.DrawerFrameworkMainFragment;
import com.omnihealthgroup.reshining.custom.Util.ShowMEProgressDiaLog;
import com.omnihealthgroup.reshining.custom.Util.WebServiceConnection;
import com.omnihealthgroup.reshining.custom.dao.BioDataAdapter;
import com.omnihealthgroup.reshining.custom.dao.DeviceMappingAdapter;
import com.omnihealthgroup.reshining.custom.dao.MeasureStandardDAO;
import com.omnihealthgroup.reshining.custom.dao.UserDataDAO;
import com.omnihealthgroup.reshining.custom.object.BioData;
import com.omnihealthgroup.reshining.custom.object.DeviceMapping;
import com.omnihealthgroup.reshining.custom.object.MeasureStandard;
import com.omnihealthgroup.reshining.custom.object.UserData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BodyHeightFragment extends DrawerFrameworkMainFragment {
    private static final String TAG = "BodyHeightFragment";
    private ArrayList<String> recordtime = new ArrayList<>();
    private ArrayList<String> recordvalues_BodyHeight = new ArrayList<>();
    private ArrayList<String> recordvalues_BodyWeight = new ArrayList<>();
    private ArrayList<String> recordvalues_BodyBMI = new ArrayList<>();

    private List<MeasureStandard> Weight;
    private List<UserData> dietBodyWeight;

    private JSONArray recordArray_HG = null;

    private LineChart chart;
    private TextView weight_title, weight_time, weight_number, weight_number_unit, weight_bmi;
    private ImageView weight_smile;
    private String idealbodyWeight = null, targetbodyWeight = null, bodyHeight = null, bodyWeight = null;

    private View rootView;
    private SharedPreferences prf;
    private WebServiceConnection webServiceConnection;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        prf = getActivity().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tabpage_bodyheight, container, false);
        Log.v(TAG, "onCreateView");

        initView();
        checkView();

        initchart();
        webServiceConnection.setLineChart(chart);
        setleftAxis(chart);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");

        new queryMeasureResource_HG().cancel(true);
        new queryMeasureResource_WG().cancel(true);

    }

    private void initView() {
        weight_title = (TextView) rootView.findViewById(R.id.weight_title);
        weight_time = (TextView) rootView.findViewById(R.id.weight_time);
        weight_number = (TextView) rootView.findViewById(R.id.weight_number);
        weight_number_unit = (TextView) rootView.findViewById(R.id.weight_number_unit);
        weight_bmi = (TextView) rootView.findViewById(R.id.weight_bmi);
        weight_smile = (ImageView) rootView.findViewById(R.id.weight_smile);

        chart = (LineChart) rootView.findViewById(R.id.chart);

        MeasureStandardDAO measureStandardDAO = new MeasureStandardDAO(getContext());
        Weight = measureStandardDAO.getuserdata_item("Weight");

        UserDataDAO userDataDAO = new UserDataDAO(getContext());
        dietBodyWeight = userDataDAO.getuserdata();

        if (dietBodyWeight.get(0).getDietidealbodyweight() != null) {
            idealbodyWeight = dietBodyWeight.get(0).getDietidealbodyweight();
        } else {
            idealbodyWeight = Weight.get(0).getDangerMax();
        }

        if (dietBodyWeight.get(0).getDietintentbodyweight() != null) {
            targetbodyWeight = dietBodyWeight.get(0).getDietintentbodyweight();
        } else {
            targetbodyWeight = Weight.get(0).getDangerMin();
        }

        if (dietBodyWeight.get(0).getHeight() != null) {
            bodyHeight = dietBodyWeight.get(0).getHeight().substring(0, dietBodyWeight.get(0).getHeight().length() - 2);
        } else {
            bodyHeight = "170";
        }

        if (dietBodyWeight.get(0).getWeight() != null) {
            bodyWeight = dietBodyWeight.get(0).getWeight().substring(0, dietBodyWeight.get(0).getWeight().length() - 2);
        } else {
            bodyHeight = "65";
        }

        rootView.findViewById(R.id.tab_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, TAG);
            }
        });
    }

    private void checkView() {
        new queryMeasureResource_HG().execute();

    }

    private void initchart() {
        ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(getContext()
                , getString(R.string.webview_loading_title)
                , getString(R.string.msg_tokenget), false, true);
        pb.show();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH) - (prf.getInt("day_count", 0));
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        int millisecond = c.get(Calendar.MILLISECOND);

        String timeStr = null;
        try {
            Date date = formatter.parse(String.valueOf(year + "-" + (month + 1) + "-" + day + " " + hour + ":" + minute + ":" + second + "." + millisecond));
            timeStr = formatter.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        BioDataAdapter bioDataAdapter = new BioDataAdapter(getContext());
        List<BioData> listData = bioDataAdapter.getBody_withTime(timeStr, formatter.format(new Date()));
        if (listData.size() > 0) {
            recordtime.clear();
            recordvalues_BodyHeight.clear();
            recordvalues_BodyWeight.clear();
            recordvalues_BodyBMI.clear();

            for (BioData bioData : listData) {
                if (!bioData.getDeviceTime().equals("2000-00-00 00:00:00")) {
                    try {
                        SimpleDateFormat formatter_chat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String getDeviceTime = bioData.getDeviceTime().toString().replaceAll("/", "-");
                        Date date_chat = formatter_chat.parse(getDeviceTime);
                        String timeStr_chat = formatter_chat.format(date_chat);
                        recordtime.add(timeStr_chat);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    recordvalues_BodyHeight.add(bioData.getBodyHeight());
                    recordvalues_BodyWeight.add(bioData.getBodyWeight());
                    recordvalues_BodyBMI.add(bioData.getBmi());
                }
            }

            pb.dismiss();
            drawchart(recordtime, recordvalues_BodyHeight, recordvalues_BodyWeight, recordvalues_BodyBMI);
        } else {
            ArrayList<String> defArray = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                defArray.add("0");
            }

            pb.dismiss();
            drawchart(defArray, defArray, defArray, defArray);
        }
    }

    private void drawchart(final ArrayList<String> recordtime
            , ArrayList<String> recordvalues_BodyHeight
            , final ArrayList<String> recordvalues_BodyWeight
            , final ArrayList<String> recordvalues_BodyBMI) {
        ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(getContext()
                , getString(R.string.webview_loading_title)
                , getString(R.string.msg_tokenget), false, true);
        pb.show();

        try {
            List<String> xVals = new ArrayList<>();
            Log.v(TAG + "recordtime.size()", String.valueOf(recordtime.size()));
            for (int i = 0; i < recordtime.size(); i++) {
                if (recordtime.get(i).getBytes().length > 1) {
                    xVals.add(recordtime.get(i).substring(0, 9));
                } else {
                    xVals.add(recordtime.get(i));
                }
            }

            String dataset_label1 = getString(R.string.bodyHeight) + getString(R.string.body_number);
            ArrayList<Entry> yVals1 = new ArrayList<>();

            int cont = 0;
            for (int i = 0; i < recordvalues_BodyHeight.size(); i++) {
                if (Float.parseFloat(recordvalues_BodyHeight.get(i)) > 200f) {
                    chart.getAxisLeft().resetAxisMaxValue();
                } else if (Float.parseFloat(recordvalues_BodyHeight.get(i)) < 40f) {
                    chart.getAxisLeft().resetAxisMinValue();
                }
                yVals1.add(new Entry(Float.parseFloat(recordvalues_BodyHeight.get(i)), cont));
                cont++;
            }

            String dataset_label2 = getString(R.string.bodyWeight) + getString(R.string.body_number);
            ArrayList<Entry> yVals2 = new ArrayList<>();

            int cont2 = 0;
            for (int i = 0; i < recordvalues_BodyWeight.size(); i++) {
                if (Float.parseFloat(recordvalues_BodyWeight.get(i)) > Float.parseFloat(idealbodyWeight) + 30f) {
                    chart.getAxisLeft().resetAxisMaxValue();
                } else if (Float.parseFloat(recordvalues_BodyWeight.get(i)) < Float.parseFloat(targetbodyWeight) - 10f) {
                    chart.getAxisLeft().resetAxisMinValue();
                }
                yVals2.add(new Entry(Float.parseFloat(recordvalues_BodyWeight.get(i)), cont2));
                cont2++;
            }

            LineDataSet dataSet1 = new LineDataSet(yVals1, dataset_label1);
            dataSet1.setColors(new int[]{R.color.b65cec6}, getContext());
            dataSet1.setLineWidth(5f);
            dataSet1.setCircleSize(5f);
            dataSet1.setValueTextSize(12f);
            // 设置折线上显示数据的格式。如果不设置，将默认显示float数据格式。
            dataSet1.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    String s = String.valueOf(value);
                    if (Integer.valueOf(s.substring(s.length() - 1, s.length())) > 0) {
                        return String.valueOf(value);
                    } else {
                        int n = (int) value;
                        //                String str = n + "mmHg";
                        return String.valueOf(n);
                    }
                }
            });

            LineDataSet dataSet2 = new LineDataSet(yVals2, dataset_label2);
            //        dataSet2.setColors(new int[]{R.color.d478ae}, getContext());
            //            dataSet2.setColors(new int[]{R.color.D74844}, getContext());
            dataSet2.setColors(new int[]{R.color.A4D031}, getContext());
            dataSet2.setCircleColor(0xFFC8A152);      //设置结点圆圈的颜色
            //            dataSet1.setCircleColorHole(0xFFC8A152);// 填充折线上数据点、圆球里面包裹的中心空白处的颜色。
            dataSet2.setLineWidth(5f);
            dataSet2.setCircleSize(5f);
            dataSet2.setValueTextSize(12f);
            // 设置折线上显示数据的格式。如果不设置，将默认显示float数据格式。
            dataSet2.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    String s = String.valueOf(value);
                    if (Integer.valueOf(s.substring(s.length() - 1, s.length())) > 0) {
                        return String.valueOf(value);
                    } else {
                        int n = (int) value;
                        //                String str = n + "mmHg";
                        return String.valueOf(n);
                    }
                }
            });

            List<LineDataSet> dataSetList = new ArrayList<>();
            //        dataSetList.add(dataSet1);
            dataSetList.add(dataSet2);

            LineData data = new LineData(xVals, dataSetList);
            chart.setData(data);
            chart.invalidate();

            weight_title.setText(getString(R.string.weight_title));
            weight_title.setTextColor(getResources().getColor(R.color.defTextColor));
            weight_time.setText(recordtime.get(recordtime.size() - 1));
            weight_time.setTextColor(getResources().getColor(R.color.defTextColor));
            weight_number.setText(recordvalues_BodyWeight.get(recordvalues_BodyWeight.size() - 1));
            weight_number.setTextColor(getResources().getColor(R.color.defTextColor));
            if (weight_number.getText().toString().length() > 4) {
                weight_number_unit.setTextSize(12);
            }else {
                weight_number_unit.setTextSize(16);
            }

            if (Float.parseFloat(weight_number.getText().toString()) == Float.parseFloat(targetbodyWeight)) {
                weight_smile.setImageResource(R.mipmap.weight_smile_very_icon);
            } else if ((Float.parseFloat(weight_number.getText().toString()) > Float.parseFloat(targetbodyWeight)
                    && Float.parseFloat(weight_number.getText().toString()) <= Float.parseFloat(idealbodyWeight))
                    || (Float.parseFloat(weight_number.getText().toString()) < Float.parseFloat(targetbodyWeight)
                    && Float.parseFloat(weight_number.getText().toString()) >= Float.parseFloat(targetbodyWeight) - 10f)) {
                weight_smile.setImageResource(R.mipmap.weight_smile_icon);
            } else {
                weight_smile.setImageResource(R.mipmap.weight_smile_no_icon);
            }

            weight_bmi.setText("BMI：" + recordvalues_BodyBMI.get(recordvalues_BodyWeight.size() - 1));
            if (Float.parseFloat(recordvalues_BodyBMI.get(recordvalues_BodyWeight.size() - 1)) > 24f) {
                weight_bmi.setTextColor(getResources().getColor(R.color.F72C27));
            } else if (Float.parseFloat(recordvalues_BodyBMI.get(recordvalues_BodyWeight.size() - 1)) <= 18.5f) {
                weight_bmi.setTextColor(getResources().getColor(R.color.F7AC27));
            } else {
                weight_bmi.setTextColor(getResources().getColor(R.color.defTextColor));
            }

            chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                    Log.v(TAG + "Entry", String.valueOf(e));
                    weight_title.setText(getString(R.string.weight_title_edit));
                    weight_title.setTextColor(getResources().getColor(R.color.defTextColor));
                    weight_time.setText(recordtime.get(e.getXIndex()));
                    weight_time.setTextColor(getResources().getColor(R.color.defTextColor));
                    weight_number.setText(recordvalues_BodyWeight.get(e.getXIndex()));
                    weight_number.setTextColor(getResources().getColor(R.color.defTextColor));
                    if (weight_number.getText().toString().length() > 4) {
                        weight_number_unit.setTextSize(12);
                    }else {
                        weight_number_unit.setTextSize(16);
                    }

                    if (Float.parseFloat(weight_number.getText().toString()) == Float.parseFloat(targetbodyWeight)) {
                        weight_smile.setImageResource(R.mipmap.weight_smile_very_icon);
                    } else if ((Float.parseFloat(weight_number.getText().toString()) > Float.parseFloat(targetbodyWeight)
                            && Float.parseFloat(weight_number.getText().toString()) <= Float.parseFloat(idealbodyWeight))
                            || (Float.parseFloat(weight_number.getText().toString()) < Float.parseFloat(targetbodyWeight)
                            && Float.parseFloat(weight_number.getText().toString()) >= Float.parseFloat(targetbodyWeight) - 10f)) {
                        weight_smile.setImageResource(R.mipmap.weight_smile_icon);
                    } else {
                        weight_smile.setImageResource(R.mipmap.weight_smile_no_icon);
                    }

                    weight_bmi.setText("BMI：" + recordvalues_BodyBMI.get(e.getXIndex()));
                    if (Float.parseFloat(recordvalues_BodyBMI.get(e.getXIndex())) > 24f) {
                        weight_bmi.setTextColor(getResources().getColor(R.color.F72C27));
                    } else if (Float.parseFloat(recordvalues_BodyBMI.get(e.getXIndex())) <= 18.5f) {
                        weight_bmi.setTextColor(getResources().getColor(R.color.F7AC27));
                    } else {
                        weight_bmi.setTextColor(getResources().getColor(R.color.defTextColor));
                    }
                }

                @Override
                public void onNothingSelected() {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pb.dismiss();
        }

    }

    private void setleftAxis(LineChart chart) {
        try {
            // 获得左侧侧坐标轴
            YAxis leftAxis = chart.getAxisLeft();

            // 设置左侧的LimitLine
            LimitLine ll1 = new LimitLine(Float.parseFloat(targetbodyWeight), ""); //目標體重
            ll1.setLineWidth(2f);
            ll1.setLineColor(0xFFF1B7B0);
            //            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);

            //        LimitLine ll2 = new LimitLine(50f, "標準身高數值下限");
            //        ll2.setLineWidth(2f);
            //        ll2.setLineColor(0xFFF1B7B0);
            //        ll2.enableDashedLine(10f, 10f, 0f);
            //        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            //        ll2.setTextSize(10f);

            LimitLine ll3 = new LimitLine(Float.parseFloat(idealbodyWeight), ""); //理想體重
            ll3.setLineWidth(2f);
            ll3.setLineColor(0xFFB6CEE7);
            //            ll3.enableDashedLine(10f, 10f, 0f);
            ll3.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll3.setTextSize(10f);

            //        LimitLine ll4 = new LimitLine(40f, "標準體重數值下限");
            //        ll4.setLineWidth(2f);
            //        ll4.setLineColor(0xFFB6CEE7);
            //        ll4.enableDashedLine(10f, 10f, 0f);
            //        ll4.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            //        ll4.setTextSize(10f);

            leftAxis.addLimitLine(ll1);
            //        leftAxis.addLimitLine(ll2);
            leftAxis.addLimitLine(ll3);
            //        leftAxis.addLimitLine(ll4);

            chart.getAxisLeft().setAxisMaxValue(Float.parseFloat(idealbodyWeight) + 30f);
            chart.getAxisLeft().setAxisMinValue(Float.parseFloat(targetbodyWeight) - 10f);

            if (Float.parseFloat(targetbodyWeight) > 0f && Float.parseFloat(idealbodyWeight) > 0f) {
                Legend legend = chart.getLegend();
                //        legend.setForm(Legend.LegendForm.LINE);
                legend.setCustom(new int[]{Color.rgb(241, 183, 176), Color.rgb(182, 206, 231)}
                        , new String[]{getString(R.string.body_targetWeight) + getString(R.string.bodyWeight) + "：" + targetbodyWeight.substring(0, targetbodyWeight.length() - 2) + " " + getString(R.string.weight_unit_ch).substring(3, 5), getString(R.string.body_idealWeight) + getString(R.string.bodyWeight) + "：" + idealbodyWeight.substring(0, idealbodyWeight.length() - 2) + " " + getString(R.string.weight_unit_ch).substring(3, 5)});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected class queryMeasureResource_HG extends AsyncTask<String, String, JSONObject> {
        ShowMEProgressDiaLog pb;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH) - (prf.getInt("day_count", 0));
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        int millisecond = c.get(Calendar.MILLISECOND);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = new ShowMEProgressDiaLog(getContext(), getString(R.string.webview_loading_title), getString(R.string.msg_tokenget), false, true);
            //            pDialog = new ProgressDialog(getContext());
            //            pDialog.setMessage("連接資料庫中，請稍後...");
            //            pDialog.setIndeterminate(false);
            //            pDialog.setCancelable(true);
            //            Code = pref.getString("Code", "");
            pb.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String timeStr = null;
            JSONObject response = null;
            try {
                Date date = formatter.parse(String.valueOf(year + "-" + (month + 1) + "-" + day + " " + hour + ":" + minute + ":" + second + "." + millisecond));
                timeStr = formatter.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!isCancelled()) {
                try {
                    response = webServiceConnection.queryMeasureResource(prf.getString("access_token", ""), "HG", timeStr, formatter.format(new Date()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            if (isAdded()) {
                try {
                    if (response != null) {
                        Log.v(TAG + "response", response.toString());
                        if (response.getString("message").equals("Success.")) {
                            recordArray_HG = response.getJSONArray("result");
                            Log.v(TAG + "recordArray_HG", recordArray_HG.toString());

                            //                        if (recordtime.size() > 0) {
                            //                            recordtime.clear();
                            //                            recordvalues_HP.clear();
                            //                        }

                            //                        UserAdapter userAdapter = new UserAdapter(getContext());
                            //                        List<User> userList = userAdapter.getAllUser();
                            //                        String UserName = null;
                            //                        for (User user : userList) {
                            //                            Log.v("userName", user.getName().toString());
                            //                            UserName = user.getName();
                            //                        }
                            //
                            //                        DeviceMappingAdapter deviceMappingAdapter = new DeviceMappingAdapter(getContext());
                            //                        ArrayList<DeviceMapping> listDevice = deviceMappingAdapter.getAllDeviceData();
                            //                        String DeviceID = null;
                            //                        for (DeviceMapping deviceMapping : listDevice) {
                            //                            Log.v("DeviceID", deviceMapping.getDeviceId().toString());
                            //                            DeviceID = deviceMapping.getDeviceId();
                            //                        }
                            //
                            //                        GcmUtil gcmUtil = new GcmUtil();
                            //
                            //                        BioDataAdapter bioDataAdapter = new BioDataAdapter(getContext());
                            //                        bioDataAdapter.deleteBioData_device_type(1);
                            //
                            //                        for (int i = 0; i < recordArray.length(); i++) {
                            //                            String recordtypestr = recordArray.getJSONObject(i).get("type").toString();
                            //                            String recorddeviceIdstr = recordArray.getJSONObject(i).get("deviceId").toString();
                            //                            String recordtimestr = recordArray.getJSONObject(i).get("time").toString();
                            //                            String recordmarkstr = recordArray.getJSONObject(i).get("mark").toString();
                            //                            String recordvaluesstr = recordArray.getJSONObject(i).get("values").toString();
                            //
                            //                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            //                            Date date = formatter.parse(recordtimestr);
                            //                            String timeStr = formatter.format(date);
                            //
                            //                            recordvaluesstr = recordvaluesstr.replace("[", "");
                            //                            recordvaluesstr = recordvaluesstr.replace("]", "");
                            //                            //                            String[] bpData = recordvaluesstr.split(",");
                            //
                            //                            BioData bioData = new BioData();
                            //
                            //                            bioData.set_id(timeStr + gcmUtil.getDeviceSerail(getContext()));
                            //                            bioData.setUserId(UserName);
                            //                            bioData.setDeviceTime(timeStr);
                            //                            bioData.setDeviceType(webServiceConnection.BIODATA_DEVICE_TYPE_WEIGHT);
                            //                            //                            bioData.setBhp(bpData[0]);
                            //                            //                            bioData.setBlp(bpData[1]);
                            //                            //                            bioData.setPulse(bpData[2]);
                            //                            if (recorddeviceIdstr.length() > 10) {
                            //                                bioData.setDeviceMac(recorddeviceIdstr);
                            //                                bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_DEVICE);
                            //                                bioData.setDeviceId(DeviceID);
                            //                            } else {
                            //                                bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_MANUAL);
                            //                            }
                            //
                            //                            bioData.setUploaded(webServiceConnection.DATA_ALREADY_UPLOAD);
                            //                            bioData.setBodyHeight(recordvaluesstr);
                            //
                            //                            bioDataAdapter.createBioData(bioData);
                            //
                            //                            //                            recordtime.add(timeStr);
                            //                            //                            recordvalues_HP.add(bpData[2]);
                            //                        }

                            pb.dismiss();
                            new queryMeasureResource_WG().execute();
                            //                        initchart();
                            //                        drawchart(recordtime, recordvalues_HP);

                        } else {
                            Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                } finally {
                    pb.dismiss();
                }
            }
        }
    }

    protected class queryMeasureResource_WG extends AsyncTask<String, String, JSONObject> {
        ShowMEProgressDiaLog pb;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH) - (prf.getInt("day_count", 0));
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        int millisecond = c.get(Calendar.MILLISECOND);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = new ShowMEProgressDiaLog(getContext(), getString(R.string.webview_loading_title), getString(R.string.msg_tokenget), false, true);
            //            pDialog = new ProgressDialog(getContext());
            //            pDialog.setMessage("連接資料庫中，請稍後...");
            //            pDialog.setIndeterminate(false);
            //            pDialog.setCancelable(true);
            //            Code = pref.getString("Code", "");
            pb.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String timeStr = null;
            JSONObject response = null;
            try {
                Date date = formatter.parse(String.valueOf(year + "-" + (month + 1) + "-" + day + " " + hour + ":" + minute + ":" + second + "." + millisecond));
                timeStr = formatter.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!isCancelled()) {
                try {
                    response = webServiceConnection.queryMeasureResource(prf.getString("access_token", ""), "WG", timeStr, formatter.format(new Date()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            if (isAdded()) {
                try {
                    if (response != null) {
                        Log.v(TAG + "response", response.toString());
                        if (response.getString("message").equals("Success.")) {
                            JSONArray recordArray_WG = response.getJSONArray("result");
                            Log.v(TAG + "recordArray_WG", recordArray_WG.toString());

                            //                        if (recordtime.size() > 0) {
                            //                            recordtime.clear();
                            //                            recordvalues_HP.clear();
                            //                        }

                            UserDataDAO userDataDAO = new UserDataDAO(getContext());
                            List<UserData> userList = userDataDAO.getuserdata();
                            String UserName = null;
                            for (UserData user : userList) {
                                Log.v(TAG + "userName", user.getName().toString());
                                UserName = user.getName();
                            }

                            DeviceMappingAdapter deviceMappingAdapter = new DeviceMappingAdapter(getContext());
                            ArrayList<DeviceMapping> listDevice = deviceMappingAdapter.getAllDeviceData();
                            String DeviceID = null;
                            for (DeviceMapping deviceMapping : listDevice) {
                                Log.v(TAG + "DeviceID", deviceMapping.getDeviceId().toString());
                                DeviceID = deviceMapping.getDeviceId();
                            }

                            //                        GcmUtil gcmUtil = new GcmUtil();

                            BioDataAdapter bioDataAdapter = new BioDataAdapter(getContext());
                            bioDataAdapter.deleteBioData_device_type(1);

                            for (int i = 0; i < recordArray_WG.length(); i++) {
                                String recordtypestr = recordArray_WG.getJSONObject(i).get("type").toString();
                                String recorddeviceIdstr = recordArray_WG.getJSONObject(i).get("deviceId").toString();
                                String recordtimestr = recordArray_WG.getJSONObject(i).get("time").toString();
                                String recordmarkstr = recordArray_WG.getJSONObject(i).get("mark").toString();
                                String recordvaluesstr_WG = recordArray_WG.getJSONObject(i).get("values").toString();
                                String recordvaluesstr_HG = null;
                                if (i <= recordArray_HG.length() - 1) {
                                    recordvaluesstr_HG = recordArray_HG.getJSONObject(i).get("values").toString();
                                } else {
                                    recordvaluesstr_HG = recordArray_HG.getJSONObject(recordArray_HG.length() - 1).get("values").toString();
                                }

                                String recordnotestr_WG = recordArray_WG.getJSONObject(i).get("description").toString();

                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = formatter.parse(recordtimestr);
                                String timeStr = formatter.format(date);

                                recordvaluesstr_HG = recordvaluesstr_HG.replace("[", "");
                                recordvaluesstr_HG = recordvaluesstr_HG.replace("]", "");

                                recordvaluesstr_WG = recordvaluesstr_WG.replace("[", "");
                                recordvaluesstr_WG = recordvaluesstr_WG.replace("]", "");
                                //                            String[] bpData = recordvaluesstr.split(",");

                                BioData bioData = new BioData();

                                //                            bioData.set_id(timeStr + gcmUtil.getDeviceSerail(getContext()));
                                bioData.setUserId(UserName);
                                bioData.setDeviceTime(timeStr);
                                bioData.setDescription(recordnotestr_WG);
                                bioData.setDeviceType(webServiceConnection.BIODATA_DEVICE_TYPE_WEIGHT);
                                //                            bioData.setBhp(bpData[0]);
                                //                            bioData.setBlp(bpData[1]);
                                //                            bioData.setPulse(bpData[2]);
                                if (recorddeviceIdstr.length() > 10) {
                                    bioData.setDeviceMac(recorddeviceIdstr);
                                    bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_DEVICE);
                                    bioData.setDeviceId(DeviceID);
                                } else {
                                    bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_MANUAL);
                                }

                                if (recordvaluesstr_HG.length() > 5 || recordvaluesstr_WG.length() > 5) {
                                    DecimalFormat nf = new DecimalFormat("0.0");
                                    double e001i = Double.parseDouble(recordvaluesstr_HG);
                                    double e002i = Double.parseDouble(recordvaluesstr_WG);
                                    bioData.setBodyHeight(nf.format(e001i));
                                    bioData.setBodyWeight(nf.format(e002i));
                                } else {
                                    bioData.setBodyHeight(recordvaluesstr_HG);
                                    bioData.setBodyWeight(recordvaluesstr_WG);
                                }

                                DecimalFormat nf = new DecimalFormat("0.00");
                                double e001i = Double.parseDouble(recordvaluesstr_HG) / 100;
                                double e002i = Double.parseDouble(recordvaluesstr_WG);
                                double BMI = e002i / (e001i * e001i);
                                Log.v(TAG + "BMI", nf.format(BMI));

                                bioData.setBmi(nf.format(BMI));

                                bioData.setUploaded(webServiceConnection.DATA_ALREADY_UPLOAD);
                                bioDataAdapter.createBodyHeight(bioData);

                                //                            recordtime.add(timeStr);
                                //                            recordvalues_HP.add(bpData[2]);
                            }

                            pb.dismiss();
                            initchart();
                            //                        drawchart(recordtime, recordvalues_HP);

                        } else {
                            Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                } finally {
                    pb.dismiss();
                }
            }
        }
    }

}