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
import com.omnihealthgroup.reshining.custom.dao.UserDataDAO;
import com.omnihealthgroup.reshining.custom.object.BioData;
import com.omnihealthgroup.reshining.custom.object.MeasureStandard;
import com.omnihealthgroup.reshining.custom.object.UserData;

import java.util.List;


public class ShowBodyHeightFragment extends DrawerFrameworkMainFragment {
    private static final String TAG = ShowBodyHeightFragment.class.getSimpleName();

    private Button newEventCancel, newEventCommit;
    private TextView bodyweight_time, updata_status, bodyheight, bodyweight, bodyheight_note, bodyweight_ideal, bodyweight_target, bodyheight_assess;
    private ImageView updata_status_icon, bodyheight_assess_icon;

    private BioData bioData;
    private List<UserData> dietBodyWeight;
    private List<MeasureStandard> Weight;

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
        String title = getString(R.string.fragment_bodyheight) + " - " + getString(R.string.show_bloodpressure_title);
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
        rootView = inflater.inflate(R.layout.fragment_showbodyheight, container, false);
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
                    Fragment fragment = new ListBodyHeightFragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_showbodyheight, fragment)
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

        bodyweight_time = (TextView) rootView.findViewById(R.id.bodyweight_time);
        updata_status = (TextView) rootView.findViewById(R.id.updata_status);
        bodyweight = (TextView) rootView.findViewById(R.id.bodyweight);
        bodyheight = (TextView) rootView.findViewById(R.id.bodyheight);
        bodyheight_note = (TextView) rootView.findViewById(R.id.bodyheight_note);
        bodyweight_ideal = (TextView) rootView.findViewById(R.id.bodyweight_ideal);
        bodyweight_target = (TextView) rootView.findViewById(R.id.bodyweight_target);
        bodyheight_assess = (TextView) rootView.findViewById(R.id.bodyheight_assess);

        updata_status_icon = (ImageView) rootView.findViewById(R.id.updata_status_icon);
        bodyheight_assess_icon = (ImageView) rootView.findViewById(R.id.bodyheight_assess_icon);

        UserDataDAO userDataDAO = new UserDataDAO(getContext());
        dietBodyWeight = userDataDAO.getuserdata();

        rootView.findViewById(R.id.event_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ListBodyHeightFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_showbodyheight, fragment)
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
                Fragment fragment = new ListBodyHeightFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_showbodyheight, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        if (getArguments() != null && getArguments().getSerializable("bioData") != null) {
            bioData = (BioData) getArguments().getSerializable("bioData");

            bodyweight_time.setText(bioData.getDeviceTime());
            bodyweight_time.setTextColor(getResources().getColor(R.color.defTextColor));

            if (dietBodyWeight.get(0).getDietidealbodyweight() != null) {
                bodyweight_ideal.setText(dietBodyWeight.get(0).getDietidealbodyweight().substring(0, dietBodyWeight.get(0).getDietidealbodyweight().length() - 2));
            } else {
                bodyweight_ideal.setText(getString(R.string.data_ideal_no));
            }

            if (dietBodyWeight.get(0).getDietintentbodyweight() != null) {
                bodyweight_target.setText(dietBodyWeight.get(0).getDietintentbodyweight().substring(0, dietBodyWeight.get(0).getDietintentbodyweight().length() - 2));
            } else {
                bodyweight_target.setText(getString(R.string.data_target_no));
            }

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

            bodyweight.setText(bioData.getBodyWeight());
            if (dietBodyWeight.get(0).getDietidealbodyweight() != null && dietBodyWeight.get(0).getDietintentbodyweight() != null) {
                if (Float.parseFloat(bodyweight.getText().toString()) == Float.parseFloat(dietBodyWeight.get(0).getDietintentbodyweight())) {
                    bodyweight.setTextColor(getResources().getColor(R.color.defTextColor));
                    bodyheight_assess.setText(getString(R.string.bodyweight_assess_good) + "\n" + getString(R.string.bloodpressure_assess_good_mark));
                    bodyheight_assess.setTextSize(16);
                    bodyheight_assess.setTextColor(getResources().getColor(R.color.event_type_gracegreen));
                    bodyheight_assess_icon.setImageResource(R.mipmap.weight_smile_very_icon);
                } else if ((Float.parseFloat(bodyweight.getText().toString()) > Float.parseFloat(dietBodyWeight.get(0).getDietintentbodyweight())
                        && Float.parseFloat(bodyweight.getText().toString()) <= Float.parseFloat(dietBodyWeight.get(0).getDietidealbodyweight()))
                        || (Float.parseFloat(bodyweight.getText().toString()) < Float.parseFloat(dietBodyWeight.get(0).getDietintentbodyweight())
                        && Float.parseFloat(bodyweight.getText().toString()) > Float.parseFloat(dietBodyWeight.get(0).getDietintentbodyweight()) - 10f)) {
                    bodyweight.setTextColor(getResources().getColor(R.color.F7AC27));
                    bodyheight_assess.setText(getString(R.string.bodyweight_assess_ok) + "\n" + getString(R.string.bloodpressure_assess_ok_mark));
                    bodyheight_assess.setTextSize(15);
                    bodyheight_assess.setTextColor(getResources().getColor(R.color.F7AC27));
                    bodyheight_assess_icon.setImageResource(R.mipmap.weight_smile_icon);
                } else {
                    bodyweight.setTextColor(getResources().getColor(R.color.F72C27));
                    bodyheight_assess.setText(getString(R.string.bodyweight_assess_no) + "\n" + getString(R.string.bloodpressure_assess_no_mark));
                    bodyheight_assess.setTextSize(14);
                    bodyheight_assess.setTextColor(getResources().getColor(R.color.F72C27));
                    bodyheight_assess_icon.setImageResource(R.mipmap.weight_smile_no_icon);
                }
            } else {
                if (Float.parseFloat(bodyweight.getText().toString()) < Float.parseFloat(Weight.get(0).getWarningMax())
                        && Float.parseFloat(bodyweight.getText().toString()) > Float.parseFloat(Weight.get(0).getWarningMin())) {
                    bodyweight.setTextColor(getResources().getColor(R.color.defTextColor));
                    bodyheight_assess.setText(getString(R.string.bodyweight_assess_good_preset) + "\n" + getString(R.string.bloodpressure_assess_good_mark));
                    bodyheight_assess.setTextSize(17);
                    bodyheight_assess.setTextColor(getResources().getColor(R.color.event_type_gracegreen));
                    bodyheight_assess_icon.setImageResource(R.mipmap.weight_smile_very_icon);
                } else if ((Float.parseFloat(bodyweight.getText().toString()) >= Float.parseFloat(Weight.get(0).getWarningMax())
                        && Float.parseFloat(bodyweight.getText().toString()) < Float.parseFloat(Weight.get(0).getDangerMax()))
                        || (Float.parseFloat(bodyweight.getText().toString()) <= Float.parseFloat(Weight.get(0).getWarningMin())
                        && Float.parseFloat(bodyweight.getText().toString()) > Float.parseFloat(Weight.get(0).getDangerMin()))) {
                    bodyweight.setTextColor(getResources().getColor(R.color.F7AC27));
                    bodyheight_assess.setText(getString(R.string.bodyweight_assess_ok_preset) + "\n" + getString(R.string.bloodpressure_assess_ok_mark));
                    bodyheight_assess.setTextSize(15);
                    bodyheight_assess.setTextColor(getResources().getColor(R.color.F7AC27));
                    bodyheight_assess_icon.setImageResource(R.mipmap.weight_smile_icon);
                } else {
                    bodyweight.setTextColor(getResources().getColor(R.color.F72C27));
                    bodyheight_assess.setText(getString(R.string.bodyweight_assess_no_preset) + "\n" + getString(R.string.bloodpressure_assess_no_mark));
                    bodyheight_assess.setTextSize(14);
                    bodyheight_assess.setTextColor(getResources().getColor(R.color.F72C27));
                    bodyheight_assess_icon.setImageResource(R.mipmap.weight_smile_no_icon);
                }
            }

            bodyheight.setText(bioData.getBodyHeight());
            bodyheight.setTextColor(getResources().getColor(R.color.defTextColor));

            if (bioData.getDescription() != null
                    && !bioData.getDescription().equals("null")
                    && !bioData.getDescription().equals("")) {
                bodyheight_note.setText(bioData.getDescription());
                bodyheight_note.setTextColor(getResources().getColor(R.color.defTextColor));
            } else {
                bodyheight_note.setText(getString(R.string.data_note_no));
                //                bodyheight_note.setTextColor(getResources().getColor(R.color.monthview_date_backgroundcolor_gray));
            }

        }
    }

}
