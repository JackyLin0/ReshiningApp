package com.omnihealthgroup.reshining.badymeasurement;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnihealthgroup.drawerframework.DrawerFrameworkMainFragment;
import com.omnihealthgroup.reshining.custom.Util.WebServiceConnection;
import com.omnihealthgroup.reshining.custom.dao.MeasureStandardDAO;
import com.omnihealthgroup.reshining.custom.object.BioData;
import com.omnihealthgroup.reshining.custom.object.MeasureStandard;

import java.util.List;


public class ShowBloodGlucoseFragment extends DrawerFrameworkMainFragment {
    private static final String TAG = ShowBloodGlucoseFragment.class.getSimpleName();

    private Button newEventCancel, newEventCommit;
    private TextView bloodglucose_time, updata_status, bloodglucose, bloodglucose_type, bloodglucose_note, Text_limit_bloodglucoseAC, Text_limit_bloodglucoseAC_warning, Text_limit_bloodglucosePC, Text_limit_bloodglucosePC_warning, bloodglucose_assess;
    private ImageView updata_status_icon, bloodglucose_type_icon, bloodglucose_assess_icon;

    private BioData bioData;
    private List<MeasureStandard> GluBeforeMeal, GluAfterMeal;

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
        String title = getString(R.string.fragment_glucose) + " - " + getString(R.string.show_bloodpressure_title);
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
        rootView = inflater.inflate(R.layout.fragment_showbloodglucose, container, false);
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
                    Fragment fragment = new ListBloodGlucoseFragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_showbloodglucose, fragment)
                            .addToBackStack(null)
                            .commit();
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

    }

    private void initView() {
        newEventCommit = (Button) rootView.findViewById(R.id.new_event_btn_commit);
        newEventCommit.setText(getString(R.string.check_string));
        newEventCommit.setTextColor(getResources().getColor(R.color.white_color));

        bloodglucose_time = (TextView) rootView.findViewById(R.id.bloodglucose_time);
        updata_status = (TextView) rootView.findViewById(R.id.updata_status);
        bloodglucose = (TextView) rootView.findViewById(R.id.bloodglucose);
        bloodglucose_type = (TextView) rootView.findViewById(R.id.bloodglucose_type);
        bloodglucose_note = (TextView) rootView.findViewById(R.id.bloodglucose_note);
        Text_limit_bloodglucoseAC = (TextView) rootView.findViewById(R.id.Text_limit_bloodglucoseAC);
        Text_limit_bloodglucoseAC_warning = (TextView) rootView.findViewById(R.id.Text_limit_bloodglucoseAC_warning);
        Text_limit_bloodglucosePC = (TextView) rootView.findViewById(R.id.Text_limit_bloodglucosePC);
        Text_limit_bloodglucosePC_warning = (TextView) rootView.findViewById(R.id.Text_limit_bloodglucosePC_warning);
        bloodglucose_assess = (TextView) rootView.findViewById(R.id.bloodglucose_assess);

        updata_status_icon = (ImageView) rootView.findViewById(R.id.updata_status_icon);
        bloodglucose_type_icon = (ImageView) rootView.findViewById(R.id.bloodglucose_type_icon);
        bloodglucose_assess_icon = (ImageView) rootView.findViewById(R.id.bloodglucose_assess_icon);

        MeasureStandardDAO measureStandardDAO = new MeasureStandardDAO(getContext());
        GluBeforeMeal = measureStandardDAO.getuserdata_item("GluBeforeMeal");
        GluAfterMeal = measureStandardDAO.getuserdata_item("GluAfterMeal");

        rootView.findViewById(R.id.event_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ListBloodGlucoseFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_showbloodglucose, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    /**
     * 初始化完成按鈕與取消按鈕的監聽器
     */
    private void loadPageView() {
        //完成按鈕
        newEventCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ListBloodGlucoseFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_showbloodglucose, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        if (getArguments() != null && getArguments().getSerializable("bioData") != null) {
            bioData = (BioData) getArguments().getSerializable("bioData");

            bloodglucose_time.setText(bioData.getDeviceTime());
            bloodglucose_time.setTextColor(getResources().getColor(R.color.defTextColor));

            Text_limit_bloodglucoseAC_warning.setText(GluBeforeMeal.get(0).getWarningMax().substring(0, GluBeforeMeal.get(0).getWarningMax().length() - 2));
            Text_limit_bloodglucoseAC.setText(GluBeforeMeal.get(0).getDangerMax().substring(0, GluBeforeMeal.get(0).getDangerMax().length() - 2));
            Text_limit_bloodglucosePC_warning.setText(GluAfterMeal.get(0).getWarningMax().substring(0, GluAfterMeal.get(0).getWarningMax().length() - 2));
            Text_limit_bloodglucosePC.setText(GluAfterMeal.get(0).getDangerMax().substring(0, GluAfterMeal.get(0).getDangerMax().length() - 2));

            if (bioData.getUploaded() == 1) {
                updata_status_icon.setImageResource(R.mipmap.bloodglucose_updata_success);
                updata_status.setText(getString(R.string.user_upload_blood_glucose));
                updata_status.setTextSize(18);
                updata_status.setTextColor(getResources().getColor(R.color.event_type_gracegreen));
            } else {
                updata_status_icon.setImageResource(R.mipmap.bloodglucose_updata_failure);
                updata_status.setText(getString(R.string.user_notupload_blood_glucose));
                updata_status.setTextSize(16);
                updata_status.setTextColor(getResources().getColor(R.color.event_type_yellow));
            }

            try {
                if (bioData.getAc() != null) {
                    bloodglucose.setText(bioData.getAc());
                    if (Float.parseFloat(bloodglucose.getText().toString()) < Float.parseFloat(GluBeforeMeal.get(0).getWarningMax())
                            && Float.parseFloat(bloodglucose.getText().toString()) > Float.parseFloat(GluBeforeMeal.get(0).getWarningMin())) {
                        bloodglucose.setTextColor(getResources().getColor(R.color.defTextColor));
                        bloodglucose_assess.setText(getString(R.string.bloodglucose_assess_good) + "\n" + getString(R.string.bloodpressure_assess_good_mark));
                        bloodglucose_assess.setTextSize(17);
                        bloodglucose_assess.setTextColor(getResources().getColor(R.color.event_type_gracegreen));
                        bloodglucose_assess_icon.setImageResource(R.mipmap.weight_smile_very_icon);
                    } else if ((Float.parseFloat(bloodglucose.getText().toString()) >= Float.parseFloat(GluBeforeMeal.get(0).getWarningMax())
                            && Float.parseFloat(bloodglucose.getText().toString()) < Float.parseFloat(GluBeforeMeal.get(0).getDangerMax()))
                            || (Float.parseFloat(bloodglucose.getText().toString()) <= Float.parseFloat(GluBeforeMeal.get(0).getWarningMin())
                            && Float.parseFloat(bloodglucose.getText().toString()) > Float.parseFloat(GluBeforeMeal.get(0).getDangerMin()))) {
                        bloodglucose.setTextColor(getResources().getColor(R.color.F7AC27));
                        bloodglucose_assess.setText(getString(R.string.bloodglucose_assess_ok) + "\n" + getString(R.string.bloodpressure_assess_ok_mark));
                        bloodglucose_assess.setTextSize(15);
                        bloodglucose_assess.setTextColor(getResources().getColor(R.color.F7AC27));
                        bloodglucose_assess_icon.setImageResource(R.mipmap.weight_smile_icon);
                    } else {
                        bloodglucose.setTextColor(getResources().getColor(R.color.F72C27));
                        bloodglucose_assess.setText(getString(R.string.bloodglucose_assess_no) + "\n" + getString(R.string.bloodpressure_assess_no_mark));
                        bloodglucose_assess.setTextSize(14);
                        bloodglucose_assess.setTextColor(getResources().getColor(R.color.F72C27));
                        bloodglucose_assess_icon.setImageResource(R.mipmap.weight_smile_no_icon);
                    }

                    bloodglucose_type_icon.setImageResource(R.mipmap.bloodglucose_ac);
                    bloodglucose_type.setText(getString(R.string.before) + getString(R.string.blood_glucose_string));
                    bloodglucose_type.setTextColor(getResources().getColor(R.color.C8A152));
                }

                if (bioData.getPc() != null) {
                    bloodglucose.setText(bioData.getPc());
                    if (Float.parseFloat(bloodglucose.getText().toString()) < Float.parseFloat(GluAfterMeal.get(0).getWarningMax())
                            && Float.parseFloat(bloodglucose.getText().toString()) > Float.parseFloat(GluAfterMeal.get(0).getWarningMin())) {
                        bloodglucose.setTextColor(getResources().getColor(R.color.defTextColor));
                        bloodglucose_assess.setText(getString(R.string.bloodglucose_assess_good) + "\n" + getString(R.string.bloodpressure_assess_good_mark));
                        bloodglucose_assess.setTextSize(17);
                        bloodglucose_assess.setTextColor(getResources().getColor(R.color.event_type_gracegreen));
                        bloodglucose_assess_icon.setImageResource(R.mipmap.weight_smile_very_icon);
                    } else if ((Float.parseFloat(bloodglucose.getText().toString()) >= Float.parseFloat(GluAfterMeal.get(0).getWarningMax())
                            && Float.parseFloat(bloodglucose.getText().toString()) < Float.parseFloat(GluAfterMeal.get(0).getDangerMax()))
                            || (Float.parseFloat(bloodglucose.getText().toString()) <= Float.parseFloat(GluAfterMeal.get(0).getWarningMin())
                            && Float.parseFloat(bloodglucose.getText().toString()) > Float.parseFloat(GluAfterMeal.get(0).getDangerMin()))) {
                        bloodglucose.setTextColor(getResources().getColor(R.color.F7AC27));
                        bloodglucose_assess.setText(getString(R.string.bloodglucose_assess_ok) + "\n" + getString(R.string.bloodpressure_assess_ok_mark));
                        bloodglucose_assess.setTextColor(getResources().getColor(R.color.F7AC27));
                        bloodglucose_assess_icon.setImageResource(R.mipmap.weight_smile_icon);
                    } else {
                        bloodglucose.setTextColor(getResources().getColor(R.color.F72C27));
                        bloodglucose_assess.setText(getString(R.string.bloodglucose_assess_no) + "\n" + getString(R.string.bloodpressure_assess_no_mark));
                        bloodglucose_assess.setTextColor(getResources().getColor(R.color.F72C27));
                        bloodglucose_assess_icon.setImageResource(R.mipmap.weight_smile_no_icon);
                    }

                    bloodglucose_type_icon.setImageResource(R.mipmap.bloodglucose_pc);
                    bloodglucose_type.setText(getString(R.string.after) + getString(R.string.blood_glucose_string));
                    bloodglucose_type.setTextColor(getResources().getColor(R.color.A74A3D));
                }

                if (bioData.getNm() != null) {
                    bloodglucose.setText(bioData.getNm());
                    if (Float.parseFloat(bloodglucose.getText().toString()) < Float.parseFloat(GluAfterMeal.get(0).getWarningMax())
                            && Float.parseFloat(bloodglucose.getText().toString()) > Float.parseFloat(GluAfterMeal.get(0).getWarningMin())) {
                        bloodglucose.setTextColor(getResources().getColor(R.color.defTextColor));
                        bloodglucose_assess.setText(getString(R.string.bloodglucose_assess_good) + "\n" + getString(R.string.bloodpressure_assess_good_mark));
                        bloodglucose_assess.setTextSize(17);
                        bloodglucose_assess.setTextColor(getResources().getColor(R.color.event_type_gracegreen));
                        bloodglucose_assess_icon.setImageResource(R.mipmap.weight_smile_very_icon);
                    } else if ((Float.parseFloat(bloodglucose.getText().toString()) >= Float.parseFloat(GluAfterMeal.get(0).getWarningMax())
                            && Float.parseFloat(bloodglucose.getText().toString()) < Float.parseFloat(GluAfterMeal.get(0).getDangerMax()))
                            || (Float.parseFloat(bloodglucose.getText().toString()) <= Float.parseFloat(GluAfterMeal.get(0).getWarningMin())
                            && Float.parseFloat(bloodglucose.getText().toString()) > Float.parseFloat(GluAfterMeal.get(0).getDangerMin()))) {
                        bloodglucose.setTextColor(getResources().getColor(R.color.F7AC27));
                        bloodglucose_assess.setText(getString(R.string.bloodglucose_assess_ok) + "\n" + getString(R.string.bloodpressure_assess_ok_mark));
                        bloodglucose_assess.setTextColor(getResources().getColor(R.color.F7AC27));
                        bloodglucose_assess_icon.setImageResource(R.mipmap.weight_smile_icon);
                    } else {
                        bloodglucose.setTextColor(getResources().getColor(R.color.F72C27));
                        bloodglucose_assess.setText(getString(R.string.bloodglucose_assess_no) + "\n" + getString(R.string.bloodpressure_assess_no_mark));
                        bloodglucose_assess.setTextColor(getResources().getColor(R.color.F72C27));
                        bloodglucose_assess_icon.setImageResource(R.mipmap.weight_smile_no_icon);
                    }

                    bloodglucose_type_icon.setImageResource(R.mipmap.bloodglucose_nm);
                    bloodglucose_type.setText(getString(R.string.usually) + getString(R.string.blood_glucose_string));
                    bloodglucose_type.setTextColor(getResources().getColor(R.color.b799AB3));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (bioData.getDescription() != null
                    && !bioData.getDescription().equals("null")
                    && !bioData.getDescription().equals("")) {
                bloodglucose_note.setText(bioData.getDescription());
                bloodglucose_note.setTextColor(getResources().getColor(R.color.defTextColor));
            } else {
                bloodglucose_note.setText(getString(R.string.data_note_no));
                //                bloodglucose_note.setTextColor(getResources().getColor(R.color.monthview_date_backgroundcolor_gray));
            }
        }
    }

}
