package com.omnihealthgroup.reshining.badymeasurement;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class BloodPressureFragment extends DrawerFrameworkMainFragment {
    private static final String TAG = BloodPressureFragment.class.getSimpleName();

    private LineChart chart;
    private TextView bloodpressure_title, bloodpressure_time, bloodpressure_BHP, bloodpressure_BLP, bloodpressure_HP;
    private ImageView bloodpressure_warning;

    private ArrayList<String> recordtime = new ArrayList<>();
    private ArrayList<String> recordvalues_BHP = new ArrayList<>();
    private ArrayList<String> recordvalues_BLP = new ArrayList<>();
    private ArrayList<String> recordvalues_HP = new ArrayList<>();
    private List<MeasureStandard> SBP, DBP, HR;

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
        rootView = inflater.inflate(R.layout.fragment_tabpage_bloodpressure, container, false);
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
        bloodpressure_title = (TextView) rootView.findViewById(R.id.bloodpressure_title);
        bloodpressure_time = (TextView) rootView.findViewById(R.id.bloodpressure_time);
        bloodpressure_BHP = (TextView) rootView.findViewById(R.id.bloodpressure_BHP);
        bloodpressure_BLP = (TextView) rootView.findViewById(R.id.bloodpressure_BLP);
        bloodpressure_HP = (TextView) rootView.findViewById(R.id.bloodpressure_HP);

        bloodpressure_warning = (ImageView) rootView.findViewById(R.id.bloodpressure_warning);
        bloodpressure_warning.setVisibility(View.GONE);

        chart = (LineChart) rootView.findViewById(R.id.chart);

        MeasureStandardDAO measureStandardDAO = new MeasureStandardDAO(getContext());
        SBP = measureStandardDAO.getuserdata_item("SBP");
        DBP = measureStandardDAO.getuserdata_item("DBP");
        HR = measureStandardDAO.getuserdata_item("HR");

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
        List<BioData> listData = bioDataAdapter.getBloodPressure_withTime(timeStr, formatter.format(new Date()));
        if (listData.size() > 0) {
            recordtime.clear();
            recordvalues_BHP.clear();
            recordvalues_BLP.clear();
            recordvalues_HP.clear();

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

                    recordvalues_BHP.add(bioData.getBhp());
                    recordvalues_BLP.add(bioData.getBlp());
                    recordvalues_HP.add(bioData.getPulse());
                }
            }

            pb.dismiss();
            drawchart(recordtime, recordvalues_BHP, recordvalues_BLP, recordvalues_HP);
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
            , final ArrayList<String> recordvalues_BHP
            , final ArrayList<String> recordvalues_BLP
            , final ArrayList<String> recordvalues_HP) {
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

            String dataset_label1 = getString(R.string.systolic_blood_pressure);
            ArrayList<Entry> yVals1 = new ArrayList<>();

            int cont = 0;
            for (int i = 0; i < recordvalues_BHP.size(); i++) {
                if (Float.parseFloat(recordvalues_BHP.get(i)) > Float.parseFloat(SBP.get(0).getDangerMax()) + 10f) {
                    chart.getAxisLeft().resetAxisMaxValue();
                } else if (Float.parseFloat(recordvalues_BHP.get(i)) < Float.parseFloat(DBP.get(0).getWarningMin()) - 10f) {
                    chart.getAxisLeft().resetAxisMinValue();
                }
                yVals1.add(new Entry(Float.parseFloat(recordvalues_BHP.get(i)), cont));
                cont++;
            }

            String dataset_label2 = getString(R.string.diastolic_blood_pressure);
            ArrayList<Entry> yVals2 = new ArrayList<>();

            int cont2 = 0;
            for (int i = 0; i < recordvalues_BLP.size(); i++) {
                if (Float.parseFloat(recordvalues_BLP.get(i)) > Float.parseFloat(SBP.get(0).getDangerMax()) + 10f) {
                    chart.getAxisLeft().resetAxisMaxValue();
                } else if (Float.parseFloat(recordvalues_BLP.get(i)) < Float.parseFloat(DBP.get(0).getWarningMin()) - 10f) {
                    chart.getAxisLeft().resetAxisMinValue();
                }
                yVals2.add(new Entry(Float.parseFloat(recordvalues_BLP.get(i)), cont2));
                cont2++;
            }

            String dataset_label3 = getString(R.string.pulse);
            ArrayList<Entry> yVals3 = new ArrayList<>();

            int cont3 = 0;
            for (int i = 0; i < recordvalues_HP.size(); i++) {
                if (Float.parseFloat(recordvalues_HP.get(i)) > Float.parseFloat(SBP.get(0).getDangerMax()) + 10f) {
                    chart.getAxisLeft().resetAxisMaxValue();
                } else if (Float.parseFloat(recordvalues_HP.get(i)) < Float.parseFloat(DBP.get(0).getWarningMin()) - 10f) {
                    chart.getAxisLeft().resetAxisMinValue();
                }
                yVals3.add(new Entry(Float.parseFloat(recordvalues_HP.get(i)), cont3));
                cont3++;
            }

            LineDataSet dataSet1 = new LineDataSet(yVals1, dataset_label1);
            dataSet1.setColors(new int[]{R.color.D74844}, getContext());
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
            dataSet2.setColors(new int[]{R.color.b4D88C4}, getContext());
            dataSet2.setLineWidth(5f);
            dataSet2.setCircleSize(5f);
            dataSet2.setValueTextSize(12f);
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
            dataSet3.setColors(new int[]{R.color.A4D031}, getContext());
            dataSet3.setLineWidth(5f);
            dataSet3.setCircleSize(5f);
            dataSet3.setValueTextSize(12f);
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

            List<LineDataSet> dataSetList = new ArrayList<>();
            dataSetList.add(dataSet1);
            dataSetList.add(dataSet2);
            dataSetList.add(dataSet3);

            LineData data = new LineData(xVals, dataSetList);
            chart.setData(data);
            chart.invalidate();

            bloodpressure_title.setText(getString(R.string.bloodpressure_title));
            bloodpressure_title.setTextColor(getResources().getColor(R.color.defTextColor));
            bloodpressure_time.setText(recordtime.get(recordtime.size() - 1));
            bloodpressure_time.setTextColor(getResources().getColor(R.color.defTextColor));
            bloodpressure_BHP.setText(recordvalues_BHP.get(recordvalues_BHP.size() - 1));
            bloodpressure_BLP.setText(recordvalues_BLP.get(recordvalues_BLP.size() - 1));
            bloodpressure_HP.setText(recordvalues_HP.get(recordvalues_HP.size() - 1));

            if ((Float.parseFloat(recordvalues_BHP.get(recordvalues_BHP.size() - 1)) >= Float.parseFloat(SBP.get(0).getDangerMax())
                    || Float.parseFloat(recordvalues_BHP.get(recordvalues_BHP.size() - 1)) <= Float.parseFloat(SBP.get(0).getDangerMin()))
                    || (Float.parseFloat(recordvalues_BLP.get(recordvalues_BLP.size() - 1)) >= Float.parseFloat(DBP.get(0).getDangerMax())
                    || Float.parseFloat(recordvalues_BLP.get(recordvalues_BLP.size() - 1)) < Float.parseFloat(DBP.get(0).getDangerMin()))) {
                //                bloodpressure_BHP.setTextColor(getResources().getColor(R.color.F72C27));
                //                bloodpressure_BLP.setTextColor(getResources().getColor(R.color.F72C27));

                bloodpressure_warning.setVisibility(View.VISIBLE);
            } else if ((Float.parseFloat(recordvalues_BHP.get(recordvalues_BHP.size() - 1)) >= Float.parseFloat(SBP.get(0).getWarningMax())
                    && Float.parseFloat(recordvalues_BHP.get(recordvalues_BHP.size() - 1)) < Float.parseFloat(SBP.get(0).getDangerMax()))
                    || (Float.parseFloat(recordvalues_BHP.get(recordvalues_BHP.size() - 1)) <= Float.parseFloat(SBP.get(0).getWarningMin())
                    && Float.parseFloat(recordvalues_BHP.get(recordvalues_BHP.size() - 1)) > Float.parseFloat(SBP.get(0).getDangerMin()))
                    || (Float.parseFloat(recordvalues_BLP.get(recordvalues_BLP.size() - 1)) >= Float.parseFloat(DBP.get(0).getWarningMax())
                    && Float.parseFloat(recordvalues_BLP.get(recordvalues_BLP.size() - 1)) < Float.parseFloat(DBP.get(0).getDangerMax()))
                    || (Float.parseFloat(recordvalues_BLP.get(recordvalues_BLP.size() - 1)) <= Float.parseFloat(DBP.get(0).getWarningMin())
                    && Float.parseFloat(recordvalues_BLP.get(recordvalues_BLP.size() - 1)) > Float.parseFloat(DBP.get(0).getDangerMin()))) {
                //                bloodpressure_BHP.setTextColor(getResources().getColor(R.color.F7AC27));
                //                bloodpressure_BLP.setTextColor(getResources().getColor(R.color.F7AC27));

                bloodpressure_warning.setVisibility(View.VISIBLE);
            } else {
                //                bloodpressure_BLP.setTextColor(getResources().getColor(R.color.defTextColor));

                bloodpressure_warning.setVisibility(View.GONE);
            }

            if (Float.parseFloat(recordvalues_BHP.get(recordvalues_BHP.size() - 1)) >= Float.parseFloat(SBP.get(0).getDangerMax())
                    || Float.parseFloat(recordvalues_BHP.get(recordvalues_BHP.size() - 1)) <= Float.parseFloat(SBP.get(0).getDangerMin())) {
                bloodpressure_BHP.setTextColor(getResources().getColor(R.color.F72C27));
            } else if ((Float.parseFloat(recordvalues_BHP.get(recordvalues_BHP.size() - 1)) >= Float.parseFloat(SBP.get(0).getWarningMax())
                    && Float.parseFloat(recordvalues_BHP.get(recordvalues_BHP.size() - 1)) < Float.parseFloat(SBP.get(0).getDangerMax()))
                    || (Float.parseFloat(recordvalues_BHP.get(recordvalues_BHP.size() - 1)) <= Float.parseFloat(SBP.get(0).getWarningMin())
                    && Float.parseFloat(recordvalues_BHP.get(recordvalues_BHP.size() - 1)) > Float.parseFloat(SBP.get(0).getDangerMin()))) {
                bloodpressure_BHP.setTextColor(getResources().getColor(R.color.F7AC27));

            } else {
                bloodpressure_BHP.setTextColor(getResources().getColor(R.color.defTextColor));
            }

            if (Float.parseFloat(recordvalues_BLP.get(recordvalues_BLP.size() - 1)) >= Float.parseFloat(DBP.get(0).getDangerMax())
                    || Float.parseFloat(recordvalues_BLP.get(recordvalues_BLP.size() - 1)) <= Float.parseFloat(DBP.get(0).getDangerMin())) {
                bloodpressure_BLP.setTextColor(getResources().getColor(R.color.F72C27));
            } else if ((Float.parseFloat(recordvalues_BLP.get(recordvalues_BLP.size() - 1)) >= Float.parseFloat(DBP.get(0).getWarningMax())
                    && Float.parseFloat(recordvalues_BLP.get(recordvalues_BLP.size() - 1)) < Float.parseFloat(DBP.get(0).getDangerMax()))
                    || (Float.parseFloat(recordvalues_BLP.get(recordvalues_BLP.size() - 1)) <= Float.parseFloat(DBP.get(0).getWarningMin())
                    && Float.parseFloat(recordvalues_BLP.get(recordvalues_BLP.size() - 1)) > Float.parseFloat(DBP.get(0).getDangerMin()))) {
                bloodpressure_BLP.setTextColor(getResources().getColor(R.color.F7AC27));

            } else {
                bloodpressure_BLP.setTextColor(getResources().getColor(R.color.defTextColor));
            }

            if (Float.parseFloat(recordvalues_HP.get(recordvalues_HP.size() - 1)) >= Float.parseFloat(HR.get(0).getDangerMax())
                    || Float.parseFloat(recordvalues_HP.get(recordvalues_HP.size() - 1)) <= Float.parseFloat(HR.get(0).getDangerMin())) {
                bloodpressure_HP.setTextColor(getResources().getColor(R.color.F72C27));
            } else if ((Float.parseFloat(recordvalues_HP.get(recordvalues_HP.size() - 1)) >= Float.parseFloat(HR.get(0).getWarningMax())
                    && Float.parseFloat(recordvalues_HP.get(recordvalues_HP.size() - 1)) < Float.parseFloat(HR.get(0).getDangerMax()))
                    || (Float.parseFloat(recordvalues_HP.get(recordvalues_HP.size() - 1)) <= Float.parseFloat(HR.get(0).getWarningMin())
                    && Float.parseFloat(recordvalues_HP.get(recordvalues_HP.size() - 1)) > Float.parseFloat(HR.get(0).getDangerMin()))) {
                bloodpressure_HP.setTextColor(getResources().getColor(R.color.F7AC27));
            } else {
                bloodpressure_HP.setTextColor(getResources().getColor(R.color.defTextColor));
            }

            chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                    Log.v(TAG + "Entry", String.valueOf(e));
                    bloodpressure_title.setText(getString(R.string.bloodpressure_title_edit));
                    bloodpressure_title.setTextColor(getResources().getColor(R.color.defTextColor));
                    bloodpressure_time.setText(recordtime.get(e.getXIndex()));
                    bloodpressure_time.setTextColor(getResources().getColor(R.color.defTextColor));
                    bloodpressure_BHP.setText(recordvalues_BHP.get(e.getXIndex()));
                    bloodpressure_BLP.setText(recordvalues_BLP.get(e.getXIndex()));
                    bloodpressure_HP.setText(recordvalues_HP.get(e.getXIndex()));

                    if ((Float.parseFloat(recordvalues_BHP.get(e.getXIndex())) >= Float.parseFloat(SBP.get(0).getDangerMax())
                            || Float.parseFloat(recordvalues_BHP.get(e.getXIndex())) <= Float.parseFloat(SBP.get(0).getDangerMin()))
                            || (Float.parseFloat(recordvalues_BLP.get(e.getXIndex())) >= Float.parseFloat(DBP.get(0).getDangerMax())
                            || Float.parseFloat(recordvalues_BLP.get(e.getXIndex())) < Float.parseFloat(DBP.get(0).getDangerMin()))) {
                        //                bloodpressure_BHP.setTextColor(getResources().getColor(R.color.F72C27));
                        //                bloodpressure_BLP.setTextColor(getResources().getColor(R.color.F72C27));

                        bloodpressure_warning.setVisibility(View.VISIBLE);
                    } else if ((Float.parseFloat(recordvalues_BHP.get(e.getXIndex())) >= Float.parseFloat(SBP.get(0).getWarningMax())
                            && Float.parseFloat(recordvalues_BHP.get(e.getXIndex())) < Float.parseFloat(SBP.get(0).getDangerMax()))
                            || (Float.parseFloat(recordvalues_BHP.get(e.getXIndex())) <= Float.parseFloat(SBP.get(0).getWarningMin())
                            && Float.parseFloat(recordvalues_BHP.get(e.getXIndex())) > Float.parseFloat(SBP.get(0).getDangerMin()))
                            || (Float.parseFloat(recordvalues_BLP.get(e.getXIndex())) >= Float.parseFloat(DBP.get(0).getWarningMax())
                            && Float.parseFloat(recordvalues_BLP.get(e.getXIndex())) < Float.parseFloat(DBP.get(0).getDangerMax()))
                            || (Float.parseFloat(recordvalues_BLP.get(e.getXIndex())) <= Float.parseFloat(DBP.get(0).getWarningMin())
                            && Float.parseFloat(recordvalues_BLP.get(e.getXIndex())) > Float.parseFloat(DBP.get(0).getDangerMin()))) {
                        //                bloodpressure_BHP.setTextColor(getResources().getColor(R.color.F7AC27));
                        //                bloodpressure_BLP.setTextColor(getResources().getColor(R.color.F7AC27));

                        bloodpressure_warning.setVisibility(View.VISIBLE);
                    } else {
                        //                bloodpressure_BLP.setTextColor(getResources().getColor(R.color.defTextColor));

                        bloodpressure_warning.setVisibility(View.GONE);
                    }

                    if (Float.parseFloat(recordvalues_BHP.get(e.getXIndex())) >= Float.parseFloat(SBP.get(0).getDangerMax())
                            || Float.parseFloat(recordvalues_BHP.get(e.getXIndex())) <= Float.parseFloat(SBP.get(0).getDangerMin())) {
                        bloodpressure_BHP.setTextColor(getResources().getColor(R.color.F72C27));
                    } else if ((Float.parseFloat(recordvalues_BHP.get(e.getXIndex())) >= Float.parseFloat(SBP.get(0).getWarningMax())
                            && Float.parseFloat(recordvalues_BHP.get(e.getXIndex())) < Float.parseFloat(SBP.get(0).getDangerMax()))
                            || (Float.parseFloat(recordvalues_BHP.get(e.getXIndex())) <= Float.parseFloat(SBP.get(0).getWarningMin())
                            && Float.parseFloat(recordvalues_BHP.get(e.getXIndex())) > Float.parseFloat(SBP.get(0).getDangerMin()))) {
                        bloodpressure_BHP.setTextColor(getResources().getColor(R.color.F7AC27));

                    } else {
                        bloodpressure_BHP.setTextColor(getResources().getColor(R.color.defTextColor));
                    }

                    if (Float.parseFloat(recordvalues_BLP.get(e.getXIndex())) >= Float.parseFloat(DBP.get(0).getDangerMax())
                            || Float.parseFloat(recordvalues_BLP.get(e.getXIndex())) <= Float.parseFloat(DBP.get(0).getDangerMin())) {
                        bloodpressure_BLP.setTextColor(getResources().getColor(R.color.F72C27));
                    } else if ((Float.parseFloat(recordvalues_BLP.get(e.getXIndex())) >= Float.parseFloat(DBP.get(0).getWarningMax())
                            && Float.parseFloat(recordvalues_BLP.get(e.getXIndex())) < Float.parseFloat(DBP.get(0).getDangerMax()))
                            || (Float.parseFloat(recordvalues_BLP.get(e.getXIndex())) <= Float.parseFloat(DBP.get(0).getWarningMin())
                            && Float.parseFloat(recordvalues_BLP.get(e.getXIndex())) > Float.parseFloat(DBP.get(0).getDangerMin()))) {
                        bloodpressure_BLP.setTextColor(getResources().getColor(R.color.F7AC27));
                    } else {
                        bloodpressure_BLP.setTextColor(getResources().getColor(R.color.defTextColor));
                    }

                    if (Float.parseFloat(recordvalues_HP.get(e.getXIndex())) >= Float.parseFloat(HR.get(0).getDangerMax())
                            || Float.parseFloat(recordvalues_HP.get(e.getXIndex())) <= Float.parseFloat(HR.get(0).getDangerMin())) {
                        bloodpressure_HP.setTextColor(getResources().getColor(R.color.F72C27));
                    } else if ((Float.parseFloat(recordvalues_HP.get(e.getXIndex())) >= Float.parseFloat(HR.get(0).getWarningMax())
                            && Float.parseFloat(recordvalues_HP.get(e.getXIndex())) < Float.parseFloat(HR.get(0).getDangerMax()))
                            || (Float.parseFloat(recordvalues_HP.get(e.getXIndex())) <= Float.parseFloat(HR.get(0).getWarningMin())
                            && Float.parseFloat(recordvalues_HP.get(e.getXIndex())) > Float.parseFloat(HR.get(0).getDangerMin()))) {
                        bloodpressure_HP.setTextColor(getResources().getColor(R.color.F7AC27));
                    } else {
                        bloodpressure_HP.setTextColor(getResources().getColor(R.color.defTextColor));
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
            //        LimitLine ll1 = new LimitLine(160f, "標準收縮壓數值上限");
            LimitLine ll1 = new LimitLine(Float.parseFloat(SBP.get(0).getDangerMax()), "");
            ll1.setLineWidth(2f);
            ll1.setLineColor(0xFFF1B7B0);
            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);

            //        LimitLine ll2 = new LimitLine(90f, "標準收縮壓數值下限");
            //        ll2.setLineWidth(2f);
            //        ll2.setLineColor(0xFFF1B7B0);
            //        ll2.enableDashedLine(10f, 10f, 0f);
            //        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            //        ll2.setTextSize(10f);

            //        LimitLine ll3 = new LimitLine(110f, "標準舒張壓數值上限");
            LimitLine ll3 = new LimitLine(Float.parseFloat(DBP.get(0).getDangerMax()), "");
            ll3.setLineWidth(2f);
            ll3.setLineColor(0xFFB6CEE7);
            ll3.enableDashedLine(10f, 10f, 0f);
            ll3.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll3.setTextSize(10f);

            //        LimitLine ll4 = new LimitLine(60f, "標準舒張壓數值下限");
            //        ll4.setLineWidth(2f);
            //        ll4.setLineColor(0xFFB6CEE7);
            //        ll4.enableDashedLine(10f, 10f, 0f);
            //        ll4.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            //        ll4.setTextSize(10f);

            LimitLine ll5 = new LimitLine(Float.parseFloat(HR.get(0).getDangerMax()), "");
            ll5.setLineWidth(2f);
            ll5.setLineColor(0xFFE8D7B5);
            ll5.enableDashedLine(10f, 10f, 0f);
            ll5.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll5.setTextSize(10f);

            leftAxis.addLimitLine(ll1);
            //        leftAxis.addLimitLine(ll2);
            leftAxis.addLimitLine(ll3);
            //        leftAxis.addLimitLine(ll4);
            leftAxis.addLimitLine(ll5);

            chart.getAxisLeft().setAxisMaxValue(Float.parseFloat(SBP.get(0).getDangerMax()) + 10f);
            chart.getAxisLeft().setAxisMinValue(Float.parseFloat(DBP.get(0).getWarningMin()) - 10f);
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
                    response = webServiceConnection.queryMeasureResource(prf.getString("access_token", ""), "BP", timeStr, formatter.format(new Date()));
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
                            //                            recordvalues_BHP.clear();
                            //                            recordvalues_BLP.clear();
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
                            bioDataAdapter.deleteBioData_device_type(3);

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
                                String[] bpData = recordvaluesstr.split(",");

                                BioData bioData = new BioData();

                                //                            bioData.set_id(timeStr + gcmUtil.getDeviceSerail(getContext()));
                                bioData.setUserId(UserName);
                                bioData.setDeviceTime(timeStr);
                                bioData.setDescription(recordnotestr);
                                bioData.setDeviceType(webServiceConnection.BIODATA_DEVICE_TYPE_BLOOD_PRESSURE);
                                bioData.setBhp(bpData[0]);
                                bioData.setBlp(bpData[1]);
                                bioData.setPulse(bpData[2]);
                                if (recorddeviceIdstr.length() > 10) {
                                    bioData.setDeviceMac(recorddeviceIdstr);
                                    bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_DEVICE);
                                    bioData.setDeviceId(DeviceID);
                                } else {
                                    bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_MANUAL);
                                }

                                bioData.setUploaded(webServiceConnection.DATA_ALREADY_UPLOAD);
                                bioDataAdapter.createBloodPressure(bioData);


                                //                            recordtime.add(timeStr);
                                //                            recordvalues_BHP.add(bpData[0]);
                                //                            recordvalues_BLP.add(bpData[1]);
                            }

                            pb.dismiss();
                            initchart();
                            //                        drawchart(recordtime, recordvalues_BHP, recordvalues_BLP);

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