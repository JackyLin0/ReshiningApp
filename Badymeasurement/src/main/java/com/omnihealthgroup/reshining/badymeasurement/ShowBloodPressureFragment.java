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


public class ShowBloodPressureFragment extends DrawerFrameworkMainFragment {
    private static final String TAG = ShowBloodPressureFragment.class.getSimpleName();

    private Button newEventCancel, newEventCommit;
    private TextView bloodpressure_time, updata_status, bloodpressure_BHP, bloodpressure_BLP, heardpulse, bloodpressure_note, Text_limit_bloodpressureBHP, Text_limit_bloodpressureBHP_warning, Text_limit_bloodpressureBLP, Text_limit_bloodpressureBLP_warning, Text_limit_bloodpressureHR_warning, Text_limit_bloodpressureHR, bloodpressure_assess;
    private ImageView updata_status_icon, bloodpressure_assess_icon;

    private BioData bioData;
    private List<MeasureStandard> SBP, DBP, HR;

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
        String title = getString(R.string.fragment_bloopressure) + " - " + getString(R.string.show_bloodpressure_title);
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
        rootView = inflater.inflate(R.layout.fragment_showbloodpressure, container, false);
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
                    Fragment fragment = new ListBloodPressureFragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_showbloodpressure, fragment)
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

        bloodpressure_time = (TextView) rootView.findViewById(R.id.bloodpressure_time);
        updata_status = (TextView) rootView.findViewById(R.id.updata_status);
        bloodpressure_BHP = (TextView) rootView.findViewById(R.id.bloodpressure_BHP);
        bloodpressure_BLP = (TextView) rootView.findViewById(R.id.bloodpressure_BLP);
        heardpulse = (TextView) rootView.findViewById(R.id.heardpulse);
        bloodpressure_note = (TextView) rootView.findViewById(R.id.bloodpressure_note);
        Text_limit_bloodpressureBHP = (TextView) rootView.findViewById(R.id.Text_limit_bloodpressureBHP);
        Text_limit_bloodpressureBHP_warning = (TextView) rootView.findViewById(R.id.Text_limit_bloodpressureBHP_warning);
        Text_limit_bloodpressureBLP = (TextView) rootView.findViewById(R.id.Text_limit_bloodpressureBLP);
        Text_limit_bloodpressureBLP_warning = (TextView) rootView.findViewById(R.id.Text_limit_bloodpressureBLP_warning);
        Text_limit_bloodpressureHR = (TextView) rootView.findViewById(R.id.Text_limit_bloodpressureHR);
        Text_limit_bloodpressureHR_warning = (TextView) rootView.findViewById(R.id.Text_limit_bloodpressureHR_warning);
        bloodpressure_assess = (TextView) rootView.findViewById(R.id.bloodpressure_assess);

        updata_status_icon = (ImageView) rootView.findViewById(R.id.updata_status_icon);
        bloodpressure_assess_icon = (ImageView) rootView.findViewById(R.id.bloodpressure_assess_icon);

        MeasureStandardDAO measureStandardDAO = new MeasureStandardDAO(getContext());
        SBP = measureStandardDAO.getuserdata_item("SBP");
        DBP = measureStandardDAO.getuserdata_item("DBP");
        HR = measureStandardDAO.getuserdata_item("HR");

        rootView.findViewById(R.id.event_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ListBloodPressureFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_showbloodpressure, fragment)
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
                Fragment fragment = new ListBloodPressureFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_showbloodpressure, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        if (getArguments() != null && getArguments().getSerializable("bioData") != null) {
            bioData = (BioData) getArguments().getSerializable("bioData");

            bloodpressure_time.setText(bioData.getDeviceTime());
            bloodpressure_time.setTextColor(getResources().getColor(R.color.defTextColor));

            Text_limit_bloodpressureBHP_warning.setText(SBP.get(0).getWarningMax().substring(0, SBP.get(0).getWarningMax().length() - 2));
            Text_limit_bloodpressureBHP.setText(SBP.get(0).getDangerMax().substring(0, SBP.get(0).getDangerMax().length() - 2));
            Text_limit_bloodpressureBLP_warning.setText(DBP.get(0).getWarningMax().substring(0, DBP.get(0).getWarningMax().length() - 2));
            Text_limit_bloodpressureBLP.setText(DBP.get(0).getDangerMax().substring(0, DBP.get(0).getDangerMax().length() - 2));
            Text_limit_bloodpressureBHP.setText(SBP.get(0).getDangerMax().substring(0, SBP.get(0).getDangerMax().length() - 2));
            Text_limit_bloodpressureHR_warning.setText(HR.get(0).getWarningMax().substring(0, HR.get(0).getWarningMax().length() - 2));
            Text_limit_bloodpressureHR.setText(HR.get(0).getDangerMax().substring(0, HR.get(0).getDangerMax().length() - 2));

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

            //            bloodpressure.setText(bioData.getBhp() + " / " + bioData.getBlp());
            if ((Float.parseFloat(bioData.getBhp()) < Float.parseFloat(SBP.get(0).getWarningMax())
                    && Float.parseFloat(bioData.getBhp()) > Float.parseFloat(SBP.get(0).getWarningMin()))
                    && (Float.parseFloat(bioData.getBlp()) < Float.parseFloat(DBP.get(0).getWarningMax())
                    && Float.parseFloat(bioData.getBlp()) > Float.parseFloat(DBP.get(0).getWarningMin()))) {
                //                bloodpressure.setTextColor(getResources().getColor(R.color.defTextColor));
                bloodpressure_assess.setText(getString(R.string.bloodpressure_assess_good) + "\n" + getString(R.string.bloodpressure_assess_good_mark));
                bloodpressure_assess.setTextSize(17);
                bloodpressure_assess.setTextColor(getResources().getColor(R.color.event_type_gracegreen));
                bloodpressure_assess_icon.setImageResource(R.mipmap.weight_smile_very_icon);
            } else if (((Float.parseFloat(bioData.getBhp()) >= Float.parseFloat(SBP.get(0).getWarningMax())
                    && Float.parseFloat(bioData.getBhp()) < Float.parseFloat(SBP.get(0).getDangerMax()))
                    || (Float.parseFloat(bioData.getBhp()) <= Float.parseFloat(SBP.get(0).getWarningMin())
                    && Float.parseFloat(bioData.getBhp()) > Float.parseFloat(SBP.get(0).getDangerMin())))
                    && ((Float.parseFloat(bioData.getBlp()) >= Float.parseFloat(DBP.get(0).getWarningMax())
                    && Float.parseFloat(bioData.getBlp()) < Float.parseFloat(DBP.get(0).getDangerMax()))
                    || (Float.parseFloat(bioData.getBlp()) <= Float.parseFloat(DBP.get(0).getWarningMin())
                    && Float.parseFloat(bioData.getBlp()) > Float.parseFloat(DBP.get(0).getDangerMin())))) {
                //                bloodpressure.setTextColor(getResources().getColor(R.color.F7AC27));
                bloodpressure_assess.setText(getString(R.string.bloodpressure_assess_ok) + "\n" + getString(R.string.bloodpressure_assess_ok_mark));
                bloodpressure_assess.setTextSize(15);
                bloodpressure_assess.setTextColor(getResources().getColor(R.color.F7AC27));
                bloodpressure_assess_icon.setImageResource(R.mipmap.weight_smile_icon);
            } else {
                //                bloodpressure.setTextColor(getResources().getColor(R.color.F72C27));
                bloodpressure_assess.setText(getString(R.string.bloodpressure_assess_no) + "\n" + getString(R.string.bloodpressure_assess_ok_mark));
                bloodpressure_assess.setTextSize(14);
                bloodpressure_assess.setTextColor(getResources().getColor(R.color.F72C27));
                bloodpressure_assess_icon.setImageResource(R.mipmap.weight_smile_no_icon);
            }

            bloodpressure_BHP.setText(bioData.getBhp());
            if ((Float.parseFloat(bioData.getBhp()) < Float.parseFloat(SBP.get(0).getWarningMax())
                    && Float.parseFloat(bioData.getBhp()) > Float.parseFloat(SBP.get(0).getWarningMin()))) {
                bloodpressure_BHP.setTextColor(getResources().getColor(R.color.defTextColor));
            } else if ((Float.parseFloat(bioData.getBhp()) >= Float.parseFloat(SBP.get(0).getWarningMax())
                    && Float.parseFloat(bioData.getBhp()) < Float.parseFloat(SBP.get(0).getDangerMax()))
                    || (Float.parseFloat(bioData.getBhp()) <= Float.parseFloat(SBP.get(0).getWarningMin())
                    && Float.parseFloat(bioData.getBhp()) > Float.parseFloat(SBP.get(0).getDangerMin()))) {
                bloodpressure_BHP.setTextColor(getResources().getColor(R.color.F7AC27));
            } else {
                bloodpressure_BHP.setTextColor(getResources().getColor(R.color.F72C27));
            }

            bloodpressure_BLP.setText(bioData.getBlp());
            if ((Float.parseFloat(bioData.getBlp()) < Float.parseFloat(DBP.get(0).getWarningMax())
                    && Float.parseFloat(bioData.getBlp()) > Float.parseFloat(DBP.get(0).getWarningMin()))) {
                bloodpressure_BLP.setTextColor(getResources().getColor(R.color.defTextColor));
            } else if ((Float.parseFloat(bioData.getBlp()) >= Float.parseFloat(DBP.get(0).getWarningMax())
                    && Float.parseFloat(bioData.getBlp()) < Float.parseFloat(DBP.get(0).getDangerMax()))
                    || (Float.parseFloat(bioData.getBlp()) <= Float.parseFloat(DBP.get(0).getWarningMin())
                    && Float.parseFloat(bioData.getBlp()) > Float.parseFloat(DBP.get(0).getDangerMin()))) {
                bloodpressure_BLP.setTextColor(getResources().getColor(R.color.F7AC27));
            } else {
                bloodpressure_BLP.setTextColor(getResources().getColor(R.color.F72C27));
            }

            heardpulse.setText(bioData.getPulse());
            if ((Float.parseFloat(bioData.getPulse()) < Float.parseFloat(HR.get(0).getWarningMax())
                    && Float.parseFloat(bioData.getPulse()) > Float.parseFloat(HR.get(0).getWarningMin()))) {
                heardpulse.setTextColor(getResources().getColor(R.color.defTextColor));
            } else if ((Float.parseFloat(bioData.getPulse()) >= Float.parseFloat(HR.get(0).getWarningMax())
                    && Float.parseFloat(bioData.getPulse()) < Float.parseFloat(HR.get(0).getDangerMax()))
                    || (Float.parseFloat(bioData.getPulse()) <= Float.parseFloat(HR.get(0).getWarningMin())
                    && Float.parseFloat(bioData.getPulse()) > Float.parseFloat(HR.get(0).getDangerMin()))) {
                heardpulse.setTextColor(getResources().getColor(R.color.F7AC27));
            } else {
                heardpulse.setTextColor(getResources().getColor(R.color.F72C27));
            }

            if (bioData.getDescription() != null
                    && !bioData.getDescription().equals("null")
                    && !bioData.getDescription().equals("")) {
                bloodpressure_note.setText(bioData.getDescription());
                bloodpressure_note.setTextColor(getResources().getColor(R.color.defTextColor));
            } else {
                bloodpressure_note.setText(getString(R.string.data_note_no));
                //                bloodpressure_note.setTextColor(getResources().getColor(R.color.monthview_date_backgroundcolor_gray));
            }
        }
    }

}
