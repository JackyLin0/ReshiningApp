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

public class BloodGlucoseFragment extends DrawerFrameworkMainFragment {
    private static final String TAG = "BloodGlucoseFragment";

    private LineChart chart;
    private TextView bloodglucose_title, bloodglucose_time, bloodglucose_type, bloodglucose_number;
    private ImageView bloodglucose_warning, bloodglucose_type_icon;

    private ArrayList<String> recordtime = new ArrayList<>();
    private ArrayList<String> recordvalues_BS = new ArrayList<>();
    private ArrayList<String> recordvalues_BSAC = new ArrayList<>();
    private ArrayList<String> recordvalues_BSPC = new ArrayList<>();
    private ArrayList<String> recordvalues_BSNM = new ArrayList<>();

    private ArrayList<String> time_bsac = new ArrayList<>();
    private ArrayList<String> time_bspc = new ArrayList<>();
    private ArrayList<String> time_bsnm = new ArrayList<>();

    private List<MeasureStandard> GluBeforeMeal, GluAfterMeal;


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
        rootView = inflater.inflate(R.layout.fragment_tabpage_bloodglucose, container, false);
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

        new queryMeasureResource().cancel(true);

    }

    private void initView() {
        bloodglucose_title = (TextView) rootView.findViewById(R.id.bloodglucose_title);
        bloodglucose_time = (TextView) rootView.findViewById(R.id.bloodglucose_time);
        bloodglucose_type = (TextView) rootView.findViewById(R.id.bloodglucose_type);
        bloodglucose_number = (TextView) rootView.findViewById(R.id.bloodglucose_number);

        bloodglucose_type_icon = (ImageView) rootView.findViewById(R.id.bloodglucose_type_icon);
        bloodglucose_warning = (ImageView) rootView.findViewById(R.id.bloodglucose_warning);
        bloodglucose_warning.setVisibility(View.GONE);

        chart = (LineChart) rootView.findViewById(R.id.chart);

        MeasureStandardDAO measureStandardDAO = new MeasureStandardDAO(getContext());
        GluBeforeMeal = measureStandardDAO.getuserdata_item("GluBeforeMeal");
        GluAfterMeal = measureStandardDAO.getuserdata_item("GluAfterMeal");

        rootView.findViewById(R.id.tab_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, TAG);
            }
        });
    }

    private void checkView() {
        new queryMeasureResource().execute();

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
        List<BioData> listData = bioDataAdapter.getBloodGlucose_withTime(timeStr, formatter.format(new Date()));
        if (listData.size() > 0) {
            recordtime.clear();
            recordvalues_BS.clear();
            recordvalues_BSAC.clear();
            recordvalues_BSPC.clear();
            recordvalues_BSNM.clear();

            time_bsac.clear();
            time_bspc.clear();
            time_bsnm.clear();

            int count = 0;
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

                    if (bioData.getAc() != null) {
                        recordvalues_BS.add(bioData.getAc());
                        recordvalues_BSAC.add(bioData.getAc());
                        time_bsac.add(String.valueOf(count));
                    } else if (bioData.getPc() != null) {
                        recordvalues_BS.add(bioData.getPc());
                        recordvalues_BSPC.add(bioData.getPc());
                        time_bspc.add(String.valueOf(count));
                    } else if (bioData.getNm() != null) {
                        recordvalues_BS.add(bioData.getNm());
                        recordvalues_BSNM.add(bioData.getNm());
                        time_bsnm.add(String.valueOf(count));
                    }
                    count++;
                }
            }

            pb.dismiss();
            drawchart(listData, recordtime, recordvalues_BS, recordvalues_BSAC, recordvalues_BSPC, recordvalues_BSNM, time_bsac, time_bspc, time_bsnm);
        } else {
            ArrayList<String> defArray = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                defArray.add("0");
            }
            pb.dismiss();
            drawchart(null, defArray, defArray, defArray, defArray, defArray, defArray, defArray, defArray);
        }
    }

    private void drawchart(List<BioData> listData, final ArrayList<String> recordtime, final ArrayList<String> recordvalues_BS
            , final ArrayList<String> recordvalues_BSAC, ArrayList<String> recordvalues_BSPC, ArrayList<String> recordvalues_BSNM
            , ArrayList<String> time_bsac, ArrayList<String> time_bspc, ArrayList<String> time_bsnm) {
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

            String dataset_label1 = getString(R.string.before) + getString(R.string.blood_glucose_string);
            ArrayList<Entry> yVals1 = new ArrayList<>();

            for (int i = 0; i < recordvalues_BSAC.size(); i++) {
                if (Float.parseFloat(recordvalues_BSAC.get(i)) > Float.parseFloat(GluAfterMeal.get(0).getDangerMax()) + 10f) {
                    chart.getAxisLeft().resetAxisMaxValue();
                } else if (Float.parseFloat(recordvalues_BSAC.get(i)) < Float.parseFloat(GluBeforeMeal.get(0).getWarningMin()) - 10f) {
                    chart.getAxisLeft().resetAxisMinValue();
                }
                yVals1.add(new Entry(Float.parseFloat(recordvalues_BSAC.get(i)), Integer.parseInt(time_bsac.get(i))));
            }

            String dataset_label2 = getString(R.string.after) + getString(R.string.blood_glucose_string);
            ArrayList<Entry> yVals2 = new ArrayList<>();

            for (int i = 0; i < recordvalues_BSPC.size(); i++) {
                if (Float.parseFloat(recordvalues_BSPC.get(i)) > Float.parseFloat(GluAfterMeal.get(0).getDangerMax()) + 10f) {
                    chart.getAxisLeft().resetAxisMaxValue();
                } else if (Float.parseFloat(recordvalues_BSPC.get(i)) < Float.parseFloat(GluBeforeMeal.get(0).getWarningMin()) - 10f) {
                    chart.getAxisLeft().resetAxisMinValue();
                }
                yVals2.add(new Entry(Float.parseFloat(recordvalues_BSPC.get(i)), Integer.parseInt(time_bspc.get(i))));
            }

            String dataset_label3 = getString(R.string.usually) + getString(R.string.blood_glucose_string);
            ArrayList<Entry> yVals3 = new ArrayList<>();

            for (int i = 0; i < recordvalues_BSNM.size(); i++) {
                if (Float.parseFloat(recordvalues_BSNM.get(i)) > Float.parseFloat(GluAfterMeal.get(0).getDangerMax()) + 10f) {
                    chart.getAxisLeft().resetAxisMaxValue();
                } else if (Float.parseFloat(recordvalues_BSNM.get(i)) < Float.parseFloat(GluBeforeMeal.get(0).getWarningMin()) - 10f) {
                    chart.getAxisLeft().resetAxisMinValue();
                }
                yVals3.add(new Entry(Float.parseFloat(recordvalues_BSNM.get(i)), Integer.parseInt(time_bsnm.get(i))));
            }

            String dataset_label4 = getString(R.string.blood_glucose_string);
            ArrayList<Entry> yVals4 = new ArrayList<>();

            int cont = 0;
            for (int i = 0; i < recordvalues_BS.size(); i++) {
                if (Float.parseFloat(recordvalues_BS.get(i)) > Float.parseFloat(GluAfterMeal.get(0).getDangerMax()) + 10f) {
                    chart.getAxisLeft().resetAxisMaxValue();
                } else if (Float.parseFloat(recordvalues_BS.get(i)) < Float.parseFloat(GluBeforeMeal.get(0).getWarningMin()) - 10f) {
                    chart.getAxisLeft().resetAxisMinValue();
                }
                yVals4.add(new Entry(Float.parseFloat(recordvalues_BS.get(i)), cont));
                cont++;
            }

            LineDataSet dataSet1 = new LineDataSet(yVals1, dataset_label1);
            dataSet1.setLineWidth(-10000000f);
            dataSet1.setColors(new int[]{R.color.white_color}, getContext());//设置 坐标提示线的颜色

            //        dataSet1.setColors(new int[]{R.color.C8A152}, getContext());//设置 坐标提示线的颜色
            dataSet1.setCircleColor(0xFFC8A152);      //设置结点圆圈的颜色
            dataSet1.setCircleColorHole(0xFFC8A152);// 填充折线上数据点、圆球里面包裹的中心空白处的颜色。
            //        dataSet1.setLineWidth(5f);
            dataSet1.setCircleSize(5f);
            dataSet1.setValueTextSize(12f);//设置坐标提示文字的大小
            //        dataSet1.setValueTextColor(Color.rgb(0, 0, 255));  //设置坐标提示文字的颜色
            //        dataSet1.setDrawCubic(true);// 改变折线样式，用曲线 - 默认是直线
            //        dataSet1.setCubicIntensity(0.2f); // 曲线的平滑度，值越大越平滑。
            // 设置折线上显示数据的格式。如果不设置，将默认显示float数据格式。
            dataSet1.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    //                    DecimalFormat df = new DecimalFormat("#.#");
                    //                    String s = df.format(value);
                    //                    Log.v(TAG,  s);

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
            dataSet2.setLineWidth(-10000000f);
            dataSet2.setColors(new int[]{R.color.white_color}, getContext());//设置 坐标提示线的颜色

            //            dataSet2.setColors(new int[]{R.color.A74A3D}, getContext());
            dataSet2.setCircleColor(0xFFA74A3D);      //设置结点圆圈的颜色
            dataSet2.setCircleColorHole(0xFFA74A3D);// 填充折线上数据点、圆球里面包裹的中心空白处的颜色。
            //            dataSet2.setLineWidth(5f);
            dataSet2.setCircleSize(5f);
            dataSet2.setValueTextSize(12f);//设置坐标提示文字的大小
            //        dataSet2.setValueTextColor(Color.rgb(0, 0, 255));  //设置坐标提示文字的颜色
            //        dataSet2.setDrawCubic(true);// 改变折线样式，用曲线 - 默认是直线
            //        dataSet2.setCubicIntensity(0.2f); // 曲线的平滑度，值越大越平滑。
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

            LineDataSet dataSet3 = new LineDataSet(yVals3, dataset_label3);
            dataSet3.setLineWidth(-10000000f);
            dataSet3.setColors(new int[]{R.color.white_color}, getContext());//设置 坐标提示线的颜色

            //            dataSet3.setColors(new int[]{R.color.b799AB3}, getContext());
            dataSet3.setCircleColor(0xFF799AB3);      //设置结点圆圈的颜色
            dataSet3.setCircleColorHole(0xFF799AB3);// 填充折线上数据点、圆球里面包裹的中心空白处的颜色。
            //            dataSet3.setLineWidth(5f);
            dataSet3.setCircleSize(5f);
            dataSet3.setValueTextSize(12f);//设置坐标提示文字的大小
            //        dataSet3.setValueTextColor(Color.rgb(0, 0, 255));  //设置坐标提示文字的颜色
            //        dataSet3.setDrawCubic(true);// 改变折线样式，用曲线 - 默认是直线
            //        dataSet3.setCubicIntensity(0.2f); // 曲线的平滑度，值越大越平滑。
            // 设置折线上显示数据的格式。如果不设置，将默认显示float数据格式。
            dataSet3.setValueFormatter(new ValueFormatter() {
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

            LineDataSet dataSet4 = new LineDataSet(yVals4, dataset_label4);
            dataSet4.setDrawValues(false);
            dataSet4.setDrawCircles(false);

            dataSet4.setColors(new int[]{R.color.fc8736}, getContext());
            //            dataSet4.setCircleColor(0xFF799AB3);      //设置结点圆圈的颜色
            //            dataSet1.setCircleColorHole(0xFF799AB3);// 填充折线上数据点、圆球里面包裹的中心空白处的颜色。
            dataSet4.setLineWidth(5f);
            //            dataSet4.setCircleSize(5f);
            //            dataSet4.setValueTextSize(12f);//设置坐标提示文字的大小
            //            dataSet4.setValueTextColor(Color.rgb(0, 0, 255));  //设置坐标提示文字的颜色
            //            dataSet4.setDrawCubic(true);// 改变折线样式，用曲线 - 默认是直线
            //            dataSet4.setCubicIntensity(0.2f); // 曲线的平滑度，值越大越平滑。
            //            // 设置折线上显示数据的格式。如果不设置，将默认显示float数据格式。
            //            dataSet4.setValueFormatter(new ValueFormatter() {
            //                @Override
            //                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            //                    String s = String.valueOf(value);
            //                    if (Integer.valueOf(s.substring(s.length() - 1, s.length())) > 0) {
            //                        return String.valueOf(value);
            //                    } else {
            //                        int n = (int) value;
            //                        //                String str = n + "mmHg";
            //                        return String.valueOf(n);
            //                    }
            //                }
            //            });

            List<LineDataSet> dataSetList = new ArrayList<>();
            dataSetList.add(dataSet1);
            dataSetList.add(dataSet2);
            dataSetList.add(dataSet3);
            dataSetList.add(dataSet4);

            LineData data = new LineData(xVals, dataSetList);
            chart.setData(data);
            chart.invalidate();

            bloodglucose_title.setText(getString(R.string.bloodglucose_title));
            bloodglucose_title.setTextColor(getResources().getColor(R.color.defTextColor));

            if (listData != null && listData.size() > 0) {
                bloodglucose_time.setText(listData.get(listData.size() - 1).getDeviceTime());
                bloodglucose_time.setTextColor(getResources().getColor(R.color.defTextColor));

                if (listData.get(listData.size() - 1).getAc() != null) {
                    bloodglucose_type.setText("（" + getString(R.string.before) + getString(R.string.blood_glucose_string) + "）");
                    bloodglucose_type.setTextColor(getResources().getColor(R.color.C8A152));
                    bloodglucose_type_icon.setImageResource(R.mipmap.bloodglucose_ac);
                    bloodglucose_number.setText(listData.get(listData.size() - 1).getAc());
                    if (Float.parseFloat(bloodglucose_number.getText().toString()) >= Float.parseFloat(GluBeforeMeal.get(0).getDangerMax())
                            || Float.parseFloat(bloodglucose_number.getText().toString()) < Float.parseFloat(GluBeforeMeal.get(0).getDangerMin())) {
                        bloodglucose_number.setTextColor(getResources().getColor(R.color.F72C27));
                        bloodglucose_warning.setVisibility(View.VISIBLE);
                    } else if ((Float.parseFloat(bloodglucose_number.getText().toString()) >= Float.parseFloat(GluBeforeMeal.get(0).getWarningMax())
                            && Float.parseFloat(bloodglucose_number.getText().toString()) < Float.parseFloat(GluBeforeMeal.get(0).getDangerMax()))
                            || (Float.parseFloat(bloodglucose_number.getText().toString()) <= Float.parseFloat(GluBeforeMeal.get(0).getWarningMin())
                            && Float.parseFloat(bloodglucose_number.getText().toString()) > Float.parseFloat(GluBeforeMeal.get(0).getDangerMin()))) {
                        bloodglucose_number.setTextColor(getResources().getColor(R.color.F7AC27));
                        bloodglucose_warning.setVisibility(View.VISIBLE);
                    } else {
                        bloodglucose_number.setTextColor(getResources().getColor(R.color.defTextColor));
                        bloodglucose_warning.setVisibility(View.GONE);
                    }
                } else if (listData.get(listData.size() - 1).getPc() != null) {
                    bloodglucose_type.setText("（" + getString(R.string.after) + getString(R.string.blood_glucose_string) + "）");
                    bloodglucose_type.setTextColor(getResources().getColor(R.color.A74A3D));
                    bloodglucose_type_icon.setImageResource(R.mipmap.bloodglucose_pc);
                    bloodglucose_number.setText(listData.get(listData.size() - 1).getPc());
                    if (Float.parseFloat(bloodglucose_number.getText().toString()) >= Float.parseFloat(GluAfterMeal.get(0).getDangerMax())
                            || Float.parseFloat(bloodglucose_number.getText().toString()) < Float.parseFloat(GluAfterMeal.get(0).getDangerMin())) {
                        bloodglucose_number.setTextColor(getResources().getColor(R.color.F72C27));
                        bloodglucose_warning.setVisibility(View.VISIBLE);
                    } else if ((Float.parseFloat(bloodglucose_number.getText().toString()) >= Float.parseFloat(GluAfterMeal.get(0).getWarningMax())
                            && Float.parseFloat(bloodglucose_number.getText().toString()) < Float.parseFloat(GluAfterMeal.get(0).getDangerMax()))
                            || (Float.parseFloat(bloodglucose_number.getText().toString()) <= Float.parseFloat(GluAfterMeal.get(0).getWarningMin())
                            && Float.parseFloat(bloodglucose_number.getText().toString()) > Float.parseFloat(GluAfterMeal.get(0).getDangerMin()))) {
                        bloodglucose_number.setTextColor(getResources().getColor(R.color.F7AC27));
                        bloodglucose_warning.setVisibility(View.VISIBLE);
                    } else {
                        bloodglucose_number.setTextColor(getResources().getColor(R.color.defTextColor));
                        bloodglucose_warning.setVisibility(View.GONE);
                    }
                } else if (listData.get(listData.size() - 1).getNm() != null) {
                    bloodglucose_type.setText("（" + getString(R.string.usually) + "）");
                    bloodglucose_type.setTextColor(getResources().getColor(R.color.b799AB3));
                    bloodglucose_type_icon.setImageResource(R.mipmap.bloodglucose_nm);
                    bloodglucose_number.setText(listData.get(listData.size() - 1).getNm());
                    if (Float.parseFloat(bloodglucose_number.getText().toString()) >= Float.parseFloat(GluAfterMeal.get(0).getDangerMax())
                            || Float.parseFloat(bloodglucose_number.getText().toString()) < Float.parseFloat(GluAfterMeal.get(0).getDangerMin())) {
                        bloodglucose_number.setTextColor(getResources().getColor(R.color.F72C27));
                        bloodglucose_warning.setVisibility(View.VISIBLE);
                    } else if ((Float.parseFloat(bloodglucose_number.getText().toString()) >= Float.parseFloat(GluAfterMeal.get(0).getWarningMax())
                            && Float.parseFloat(bloodglucose_number.getText().toString()) < Float.parseFloat(GluAfterMeal.get(0).getDangerMax()))
                            || (Float.parseFloat(bloodglucose_number.getText().toString()) <= Float.parseFloat(GluAfterMeal.get(0).getWarningMin())
                            && Float.parseFloat(bloodglucose_number.getText().toString()) > Float.parseFloat(GluAfterMeal.get(0).getDangerMin()))) {
                        bloodglucose_number.setTextColor(getResources().getColor(R.color.F7AC27));
                        bloodglucose_warning.setVisibility(View.VISIBLE);
                    } else {
                        bloodglucose_number.setTextColor(getResources().getColor(R.color.defTextColor));
                        bloodglucose_warning.setVisibility(View.GONE);
                    }
                }
            }

            chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                    Log.v(TAG + "Entry", String.valueOf(e));
                    Log.v(TAG + "Entry", String.valueOf(dataSetIndex));
                    Log.v(TAG + "Entry", String.valueOf(h));

                    bloodglucose_title.setText(getString(R.string.bloodglucose_title_edit));
                    bloodglucose_title.setTextColor(getResources().getColor(R.color.defTextColor));
                    bloodglucose_time.setText(recordtime.get(e.getXIndex()));
                    bloodglucose_time.setTextColor(getResources().getColor(R.color.defTextColor));

                    switch (dataSetIndex) {
                        case 0:
                            bloodglucose_type.setText("（" + getString(R.string.before) + getString(R.string.blood_glucose_string) + "）");
                            bloodglucose_type.setTextColor(getResources().getColor(R.color.C8A152));
                            bloodglucose_type_icon.setImageResource(R.mipmap.bloodglucose_ac);
                            String s_ac = String.valueOf(e.getVal());
                            if (Integer.valueOf(s_ac.substring(s_ac.length() - 1, s_ac.length())) > 0) {
                                bloodglucose_number.setText(s_ac);
                            } else {
                                bloodglucose_number.setText(String.valueOf((int) e.getVal()));
                            }
                            if (Float.parseFloat(bloodglucose_number.getText().toString()) >= Float.parseFloat(GluBeforeMeal.get(0).getDangerMax())
                                    || Float.parseFloat(bloodglucose_number.getText().toString()) <= Float.parseFloat(GluBeforeMeal.get(0).getDangerMin())) {
                                bloodglucose_number.setTextColor(getResources().getColor(R.color.F72C27));
                                bloodglucose_warning.setVisibility(View.VISIBLE);
                            } else if ((Float.parseFloat(bloodglucose_number.getText().toString()) >= Float.parseFloat(GluBeforeMeal.get(0).getWarningMax())
                                    && Float.parseFloat(bloodglucose_number.getText().toString()) < Float.parseFloat(GluBeforeMeal.get(0).getDangerMax()))
                                    || (Float.parseFloat(bloodglucose_number.getText().toString()) <= Float.parseFloat(GluBeforeMeal.get(0).getWarningMin())
                                    && Float.parseFloat(bloodglucose_number.getText().toString()) > Float.parseFloat(GluBeforeMeal.get(0).getDangerMin()))) {
                                bloodglucose_number.setTextColor(getResources().getColor(R.color.F7AC27));
                                bloodglucose_warning.setVisibility(View.VISIBLE);
                            } else {
                                bloodglucose_number.setTextColor(getResources().getColor(R.color.defTextColor));
                                bloodglucose_warning.setVisibility(View.GONE);
                            }
                            break;
                        case 1:
                            bloodglucose_type.setText("（" + getString(R.string.after) + getString(R.string.blood_glucose_string) + "）");
                            bloodglucose_type.setTextColor(getResources().getColor(R.color.A74A3D));
                            bloodglucose_type_icon.setImageResource(R.mipmap.bloodglucose_pc);
                            String s_pc = String.valueOf(e.getVal());
                            if (Integer.valueOf(s_pc.substring(s_pc.length() - 1, s_pc.length())) > 0) {
                                bloodglucose_number.setText(s_pc);
                            } else {
                                bloodglucose_number.setText(String.valueOf((int) e.getVal()));
                            }
                            if (Float.parseFloat(bloodglucose_number.getText().toString()) >= Float.parseFloat(GluAfterMeal.get(0).getDangerMax())
                                    || Float.parseFloat(bloodglucose_number.getText().toString()) < Float.parseFloat(GluAfterMeal.get(0).getDangerMin())) {
                                bloodglucose_number.setTextColor(getResources().getColor(R.color.F72C27));
                                bloodglucose_warning.setVisibility(View.VISIBLE);
                            } else if ((Float.parseFloat(bloodglucose_number.getText().toString()) >= Float.parseFloat(GluAfterMeal.get(0).getWarningMax())
                                    && Float.parseFloat(bloodglucose_number.getText().toString()) < Float.parseFloat(GluAfterMeal.get(0).getDangerMax()))
                                    || (Float.parseFloat(bloodglucose_number.getText().toString()) <= Float.parseFloat(GluAfterMeal.get(0).getWarningMin())
                                    && Float.parseFloat(bloodglucose_number.getText().toString()) > Float.parseFloat(GluAfterMeal.get(0).getDangerMin()))) {
                                bloodglucose_number.setTextColor(getResources().getColor(R.color.F7AC27));
                                bloodglucose_warning.setVisibility(View.VISIBLE);
                            } else {
                                bloodglucose_number.setTextColor(getResources().getColor(R.color.defTextColor));
                                bloodglucose_warning.setVisibility(View.GONE);
                            }
                            break;
                        case 2:
                            bloodglucose_type.setText("（" + getString(R.string.usually) + "）");
                            bloodglucose_type.setTextColor(getResources().getColor(R.color.b799AB3));
                            bloodglucose_type_icon.setImageResource(R.mipmap.bloodglucose_nm);
                            String s_nm = String.valueOf(e.getVal());
                            if (Integer.valueOf(s_nm.substring(s_nm.length() - 1, s_nm.length())) > 0) {
                                bloodglucose_number.setText(s_nm);
                            } else {
                                bloodglucose_number.setText(String.valueOf((int) e.getVal()));
                            }
                            if (Float.parseFloat(bloodglucose_number.getText().toString()) >= Float.parseFloat(GluAfterMeal.get(0).getDangerMax())
                                    || Float.parseFloat(bloodglucose_number.getText().toString()) < Float.parseFloat(GluAfterMeal.get(0).getDangerMin())) {
                                bloodglucose_number.setTextColor(getResources().getColor(R.color.F72C27));
                                bloodglucose_warning.setVisibility(View.VISIBLE);
                            } else if ((Float.parseFloat(bloodglucose_number.getText().toString()) >= Float.parseFloat(GluAfterMeal.get(0).getWarningMax())
                                    && Float.parseFloat(bloodglucose_number.getText().toString()) < Float.parseFloat(GluAfterMeal.get(0).getDangerMax()))
                                    || (Float.parseFloat(bloodglucose_number.getText().toString()) <= Float.parseFloat(GluAfterMeal.get(0).getWarningMin())
                                    && Float.parseFloat(bloodglucose_number.getText().toString()) > Float.parseFloat(GluAfterMeal.get(0).getDangerMin()))) {
                                bloodglucose_number.setTextColor(getResources().getColor(R.color.F7AC27));
                                bloodglucose_warning.setVisibility(View.VISIBLE);
                            } else {
                                bloodglucose_number.setTextColor(getResources().getColor(R.color.defTextColor));
                                bloodglucose_warning.setVisibility(View.GONE);
                            }
                            break;
                    }
                    //
                    //                bloodglucose_title.setText(getString(R.string.bloodglucose_title_edit));
                    //                bloodglucose_title.setTextColor(getResources().getColor(R.color.defTextColor));
                    //                bloodglucose_time.setText(recordtime.get(e.getXIndex()));
                    //                bloodglucose_time.setTextColor(getResources().getColor(R.color.defTextColor));
                    //
                    //
                    //
                    //                bloodglucose_type.setText(recordvalues_BSAC.get(e.getXIndex()));
                    //                bloodglucose_type.setTextColor(getResources().getColor(R.color.defTextColor));
                    //                bloodglucose_number.setText(recordvalues_BSAC.get(e.getXIndex()));
                    //                bloodglucose_number.setTextColor(getResources().getColor(R.color.defTextColor));

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
            LimitLine ll1 = new LimitLine(Float.parseFloat(GluBeforeMeal.get(0).getDangerMax()), "");
            ll1.setLineWidth(2f);
            ll1.setLineColor(0xFFE8D7B5);
            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);

            //        LimitLine ll2 = new LimitLine(70f, "標準飯前血糖數值下限");
            //        ll2.setLineWidth(2f);
            //        ll2.setLineColor(0xFFE8D7B5);
            //        ll2.enableDashedLine(10f, 10f, 0f);
            //        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            //        ll2.setTextSize(10f);

            LimitLine ll3 = new LimitLine(Float.parseFloat(GluAfterMeal.get(0).getDangerMax()), "");
            ll3.setLineWidth(2f);
            ll3.setLineColor(0xFFE2B8B1);
            ll3.enableDashedLine(10f, 10f, 0f);
            ll3.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll3.setTextSize(10f);

            //        LimitLine ll4 = new LimitLine(90f, "飯後血糖數值下限");
            //        ll4.setLineWidth(2f);
            //        ll4.setLineColor(0xFFE2B8B1);
            //        ll4.enableDashedLine(10f, 10f, 0f);
            //        ll4.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            //        ll4.setTextSize(10f);

            leftAxis.addLimitLine(ll1);
            //        leftAxis.addLimitLine(ll2);
            leftAxis.addLimitLine(ll3);
            //        leftAxis.addLimitLine(ll4);

            chart.getAxisLeft().setAxisMaxValue(Float.parseFloat(GluAfterMeal.get(0).getDangerMax()) + 10f);
            chart.getAxisLeft().setAxisMinValue(Float.parseFloat(GluBeforeMeal.get(0).getWarningMin()) - 10f);

            Legend legend = chart.getLegend();
            legend.setForm(Legend.LegendForm.CIRCLE);
            legend.setCustom(new int[]{Color.rgb(200, 161, 82), Color.rgb(167, 74, 61), Color.rgb(121, 154, 179)}
                    , new String[]{getString(R.string.before) + getString(R.string.blood_glucose_string), getString(R.string.after) + getString(R.string.blood_glucose_string), getString(R.string.usually)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected class queryMeasureResource extends AsyncTask<String, String, JSONObject> {
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
                    response = webServiceConnection.queryMeasureResource(prf.getString("access_token", ""), "BS", timeStr, formatter.format(new Date()));
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
                            JSONArray recordArray = response.getJSONArray("result");
                            Log.v(TAG + "jsonArray", recordArray.toString());

                            //                        if (recordtime.size() > 0) {
                            //                            recordtime.clear();
                            //                            recordvalues_BSAC.clear();
                            //                            recordvalues_BSPC.clear();
                            //                            recordvalues_BSNM.clear();
                            //
                            //                            time_bsac.clear();
                            //                            time_bspc.clear();
                            //                            time_bsnm.clear();
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
                            bioDataAdapter.deleteBioData_device_type(2);

                            //                        int count = 0;
                            for (int i = 0; i < recordArray.length(); i++) {
                                String recordtypestr = recordArray.getJSONObject(i).get("type").toString();
                                String recorddeviceIdstr = recordArray.getJSONObject(i).get("deviceId").toString();
                                String recordtimestr = recordArray.getJSONObject(i).get("time").toString();
                                String recordmarkstr = recordArray.getJSONObject(i).get("mark").toString();
                                String recordvaluesstr = recordArray.getJSONObject(i).get("values").toString();
                                String recordnotestr = recordArray.getJSONObject(i).get("description").toString();

                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = formatter.parse(recordtimestr);
                                String timeStr = formatter.format(date);

                                recordvaluesstr = recordvaluesstr.replace("[", "");
                                recordvaluesstr = recordvaluesstr.replace("]", "");

                                BioData bioData = new BioData();

                                //                            bioData.set_id(timeStr + gcmUtil.getDeviceSerail(getContext()));
                                bioData.setUserId(UserName);
                                bioData.setDeviceTime(timeStr);
                                bioData.setDescription(recordnotestr);
                                bioData.setDeviceType(webServiceConnection.BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE);

                                if (recorddeviceIdstr.length() > 10) {
                                    bioData.setDeviceMac(recorddeviceIdstr);
                                    bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_DEVICE);
                                    bioData.setDeviceId(DeviceID);
                                } else {
                                    bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_MANUAL);
                                }

                                if (recordvaluesstr.length() > 5) {
                                    DecimalFormat nf = new DecimalFormat("0.0");
                                    double e001i = Double.parseDouble(recordvaluesstr);
                                    recordvaluesstr = nf.format(e001i);

                                    //                                e001i = Math.round(e001i * 10.0) / 10.0;
                                }

                                if (recordmarkstr.equals("AC")) {
                                    bioData.setAc(recordvaluesstr);
                                } else if (recordmarkstr.equals("PC")) {
                                    bioData.setPc(recordvaluesstr);
                                } else if (recordmarkstr.equals("NM")) {
                                    bioData.setNm(recordvaluesstr);
                                }

                                bioData.setUploaded(webServiceConnection.DATA_ALREADY_UPLOAD);
                                bioDataAdapter.createGlucose(bioData);

                                //                            recordtime.add(timeStr);

                                //                            if (recordmarkstr.equals("AC")) {
                                //                                recordvalues_BSAC.add(recordvaluesstr);
                                //                                time_bsac.add(String.valueOf(count));
                                //                            } else if (recordmarkstr.equals("PC")) {
                                //                                recordvalues_BSPC.add(recordvaluesstr);
                                //                                time_bspc.add(String.valueOf(count));
                                //                            } else if (recordmarkstr.equals("NM")) {
                                //                                recordvalues_BSNM.add(recordvaluesstr);
                                //                                time_bsnm.add(String.valueOf(count));
                                //                            }
                                //                            count++;
                            }

                            pb.dismiss();
                            initchart();
                            //                        drawchart(recordtime, recordvalues_BSAC, recordvalues_BSPC, recordvalues_BSNM, time_bsac, time_bspc, time_bsnm);

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