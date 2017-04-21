package com.omnihealthgroup.reshining.badymeasurement;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omnihealthgroup.drawerframework.DrawerFrameworkMainFragment;
import com.omnihealthgroup.reshining.badymeasurement.adapter.BloodPressureViewAdapter;
import com.omnihealthgroup.reshining.custom.adapter.RecyclerItemClickListener;
import com.omnihealthgroup.reshining.custom.Util.ShowMEProgressDiaLog;
import com.omnihealthgroup.reshining.custom.Util.WebServiceConnection;
import com.omnihealthgroup.reshining.custom.dao.BioDataAdapter;
import com.omnihealthgroup.reshining.custom.object.BioData;

import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;


public class ListBloodPressureFragment extends DrawerFrameworkMainFragment {
    private static final String TAG = ListBloodPressureFragment.class.getSimpleName();

    private RecyclerView cardView;
    private LinearLayoutManager linearLayoutManager;
    private BloodPressureViewAdapter bloodPressureViewAdapter;
    private AlphaInAnimationAdapter alphaAdapter;

    private List<BioData> listData;

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
        String title = getString(R.string.fragment_bloopressure) + " - " + getString(R.string.list_bloodpressure_title);
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
        rootView = inflater.inflate(R.layout.fragment_listpbloodpressure, container, false);
        Log.v(TAG, "onCreateView");

        initView();
        initHandler();
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
                    //                    CancelAlertDialog();
                    Fragment fragment = new MyHealthCareFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("fragmentType", 0);
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_listpbloodpressure, fragment)
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
        cardView = (RecyclerView) rootView.findViewById(R.id.cardView);

    }

    private void initHandler() {
        LinearLayout lay_trend = (LinearLayout) rootView.findViewById(R.id.lay_trend);
        LinearLayout lay_datalist = (LinearLayout) rootView.findViewById(R.id.lay_datalist);
        LinearLayout lay_datainput = (LinearLayout) rootView.findViewById(R.id.lay_datainput);

        ImageView btn_trend = (ImageView) rootView.findViewById(R.id.btn_trend);
        ImageView btn_datalist = (ImageView) rootView.findViewById(R.id.btn_datalist);
        ImageView btn_datainput = (ImageView) rootView.findViewById(R.id.btn_datainput);

        TextView text_trend = (TextView) rootView.findViewById(R.id.text_trend);
        TextView text_datalist = (TextView) rootView.findViewById(R.id.text_datalist);
        TextView text_datainput = (TextView) rootView.findViewById(R.id.text_datainput);

        lay_datainput.setVisibility(View.GONE);

        lay_trend.setOrientation(LinearLayout.HORIZONTAL);
        lay_trend.setBackgroundResource(R.mipmap.lay_icon_off);
        lay_datalist.setOrientation(LinearLayout.HORIZONTAL);
        lay_datalist.setBackgroundResource(R.mipmap.lay_icon_on);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(50, 0, 0, 0);

        btn_trend.setImageResource(R.mipmap.btn_trend_off);
        btn_trend.setLayoutParams(lp);

        text_trend.setGravity(Gravity.CENTER | Gravity.RIGHT);
        text_trend.setPadding(0, 10, 80, 0);
        text_trend.setTextSize(18);

        btn_datalist.setImageResource(R.mipmap.btn_datalist_on);
        btn_datalist.setLayoutParams(lp);

        text_datalist.setGravity(Gravity.CENTER | Gravity.RIGHT);
        text_datalist.setPadding(0, 10, 80, 0);
        text_datalist.setTextSize(18);

        rootView.findViewById(R.id.lay_trend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new MyHealthCareFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("fragmentType", 0);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_listpbloodpressure, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        rootView.findViewById(R.id.lay_datalist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, TAG + "btn_datalist");
            }
        });

        rootView.findViewById(R.id.lay_datainput).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AddBloodPressureFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_listpbloodpressure, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        //////////////////////////////
        cardView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d(TAG, "onItemClick : postion " + position);
                        BioData bioData = new BioData();
                        bioData = listData.get(position);
                        Log.v(TAG, bioData.get_id());

                        //                        Bundle bundle = new Bundle();
                        //                        bundle.putSerializable("bioData", bioData);
                        //                        Intent intent = new Intent();
                        //                        intent.setClass(getActivity(), AddNewDatingActivity.class);
                        //                        intent.putExtras(bundle);
                        //                        startActivity(intent);

                        Fragment fragment = new ShowBloodPressureFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("bioData", bioData);
                        fragment.setArguments(bundle);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.fragment_listpbloodpressure, fragment)
                                .addToBackStack(null)
                                .commit();
                    }

                    @Override
                    public void onLongClick(View view, int posotion) {
                        Log.d(TAG, "onLongClick position : " + posotion);
                    }
                }));
    }

    /**
     * 初始化完成按鈕與取消按鈕的監聽器
     */
    private void loadPageView() {
        ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(getContext()
                , getString(R.string.webview_loading_title)
                , getString(R.string.msg_tokenget), false, true);
        pb.show();

        BioDataAdapter bioDataAdapter = new BioDataAdapter(getContext());
        listData = bioDataAdapter.getBloodPressure();
        if (listData.size() > 0) {

            pb.dismiss();

            initAdapter(listData);
        }
    }

    private void initAdapter(List<BioData> listData) {
        //创建默认的线性LayoutManager
        linearLayoutManager = new LinearLayoutManager(getContext());
        cardView.setLayoutManager(linearLayoutManager);//这里用线性显示 类似于listview
        //        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));//这里用线性宫格显示 类似于grid view
        //        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));//这里用线性宫格显示 类似于瀑布流
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        cardView.setHasFixedSize(true);

        bloodPressureViewAdapter = new BloodPressureViewAdapter(getContext(), listData);
        alphaAdapter = new AlphaInAnimationAdapter(bloodPressureViewAdapter);
        cardView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));
    }

}
