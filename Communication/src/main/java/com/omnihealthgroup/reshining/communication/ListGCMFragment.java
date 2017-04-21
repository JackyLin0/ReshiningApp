package com.omnihealthgroup.reshining.communication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.omnihealthgroup.drawerframework.DrawerFrameworkMainFragment;
import com.omnihealthgroup.reshining.communication.R;
import com.omnihealthgroup.reshining.communication.adapter.GCMViewAdapter;
import com.omnihealthgroup.reshining.custom.adapter.RecyclerItemClickListener;
import com.omnihealthgroup.reshining.custom.Util.ShowMEProgressDiaLog;
import com.omnihealthgroup.reshining.custom.Util.WebServiceConnection;
import com.omnihealthgroup.reshining.custom.dao.GCMDataDAO;
import com.omnihealthgroup.reshining.custom.object.GCMData;

import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;


public class ListGCMFragment extends DrawerFrameworkMainFragment {
    private static final String TAG = "ListGCMFragment";

    private RecyclerView cardView;
    private LinearLayoutManager linearLayoutManager;
    private GCMViewAdapter gcmViewAdapter;
    private AlphaInAnimationAdapter alphaAdapter;

    private List<GCMData> listData;

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
        String title = getString(R.string.fragment_list_gcm);
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
        rootView = inflater.inflate(R.layout.fragment_list_gcm, container, false);
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

        //        // 監聽返回鍵
        //        getView().setFocusableInTouchMode(true);
        //        getView().requestFocus();
        //        getView().setOnKeyListener(new View.OnKeyListener() {
        //            @Override
        //            public boolean onKey(View v, int keyCode, KeyEvent event) {
        //                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
        //                    // handle back button
        //                    //                    CancelAlertDialog();
        //                    Fragment fragment = new MyHealthCareFragment();
        //                    Bundle bundle = new Bundle();
        //                    bundle.putInt("fragmentType", 0);
        //                    getFragmentManager().beginTransaction()
        //                            .replace(R.id.fragment_listpbloodpressure, fragment)
        //                            .addToBackStack(null)
        //                            .commit();
        //                    return true;
        //                }
        //                return false;
        //            }
        //        });
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
        GCMDataDAO gcmDataDAO = new GCMDataDAO(getContext());
        listData = gcmDataDAO.getuserdata();

        if (getArguments() != null && getArguments().getString("title") != null) {
            Bundle bundle = new Bundle();
            if (listData.size() > 0) {
                bundle.putString("GCM_time", listData.get(0).getReceivetime());
                bundle.putString("GCM_title", listData.get(0).getTitle());
                bundle.putString("GCM_body", listData.get(0).getBody());
            }

            DialogFragment dialogFragment = new ShowGCMFragment();
            dialogFragment.setArguments(bundle);
            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PageTransparent);
            dialogFragment.show(getActivity().getSupportFragmentManager(), "ShowGCMFragment");

        }

        //////////////////////////////
        cardView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Bundle bundle = new Bundle();
                        if (listData.size() > 0) {
                            bundle.putString("GCM_time", listData.get(position).getReceivetime());
                            bundle.putString("GCM_title", listData.get(position).getTitle());
                            bundle.putString("GCM_body", listData.get(position).getBody());
                        }

                        DialogFragment dialogFragment = new ShowGCMFragment();
                        dialogFragment.setArguments(bundle);
                        dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PageTransparent);
                        dialogFragment.show(getActivity().getSupportFragmentManager(), "ShowGCMFragment");

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

        if (listData.size() > 0) {
            rootView.findViewById(R.id.linearLayout1).setVisibility(View.GONE);
            rootView.findViewById(R.id.linearLayout2).setVisibility(View.VISIBLE);
            pb.dismiss();

            initAdapter(listData);
        } else {
            rootView.findViewById(R.id.linearLayout1).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.linearLayout2).setVisibility(View.GONE);
            pb.dismiss();
        }
    }

    private void initAdapter(List<GCMData> listData) {
        //创建默认的线性LayoutManager
        linearLayoutManager = new LinearLayoutManager(getContext());
        cardView.setLayoutManager(linearLayoutManager);//这里用线性显示 类似于listview
        //        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));//这里用线性宫格显示 类似于grid view
        //        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));//这里用线性宫格显示 类似于瀑布流
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        cardView.setHasFixedSize(true);

        gcmViewAdapter = new GCMViewAdapter(getContext(), listData);
        alphaAdapter = new AlphaInAnimationAdapter(gcmViewAdapter);
        cardView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));
    }

}
