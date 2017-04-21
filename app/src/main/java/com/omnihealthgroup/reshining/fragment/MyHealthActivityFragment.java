package com.omnihealthgroup.reshining.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.omnihealthgroup.drawerframework.DrawerFrameworkMainFragment;
import com.omnihealthgroup.reshining.R;
import com.omnihealthgroup.reshining.custom.IO.TokenGet;
import com.omnihealthgroup.reshining.custom.Util.ShowMEProgressDiaLog;
import com.omnihealthgroup.reshining.custom.Util.WebServiceConnection;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Administrator on 2016/5/23.
 */
public class MyHealthActivityFragment extends DrawerFrameworkMainFragment {
    private static final String TAG = "MyHealthActivityFragment";
    private View rootView;
    private WebView myBrowser = null;
    private String authCode = null;

    private SharedPreferences prf;
    private WebServiceConnection webServiceConnection;

    /**
     * 載入共用ToolBar
     *
     * @param toolbar toolbar
     */
    @Override
    protected void onSetToolbar(Toolbar toolbar) {
        super.onSetToolbar(toolbar);
        toolbar.setTitle("我的健康管理");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        prf = getActivity().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_myhealthactivity, container, false);
        Log.v(TAG, "onCreateView");

        initView();
        //        checkView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

        checkView();

        // 監聽返回鍵
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button
                    new android.support.v7.app.AlertDialog.Builder(getContext())
                            .setTitle(getString(com.omnihealthgroup.reshining.badymeasurement.R.string.message_title))
                            .setMessage(getString(com.omnihealthgroup.reshining.badymeasurement.R.string.msg_leave_confirm))
                            .setPositiveButton(getString(com.omnihealthgroup.reshining.badymeasurement.R.string.msg_confirm), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    getActivity().finish();
                                }
                            })
                            .setNegativeButton(getString(com.omnihealthgroup.reshining.badymeasurement.R.string.msg_cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }).show();
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

        //        new TokenExp().cancel(true);
        //        new TokenGet().cancel(true);
        new com.omnihealthgroup.reshining.custom.IO.TokenExp(getContext()).cancel(true);
        new com.omnihealthgroup.reshining.custom.IO.TokenGet(getContext(), authCode).cancel(true);
    }


    private void initView() {
        //        rootView.findViewById(R.id.gcm_btn).setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                //                Fragment fragment = new PersonalProfilesFragment();
        //                //                getFragmentManager().beginTransaction()
        //                //                        .replace(R.id.fragment_mysystemsettings, fragment)
        //                //                        .addToBackStack(null)
        //                //                        .commit();
        //
        //                DialogFragment dialogFragment = new SendGCMFragment();
        //                //                dialogFragment.setArguments(bundle);
        //                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PageTransparent);
        //                dialogFragment.show(getActivity().getSupportFragmentManager(), "DatingShowFragment");
        //
        //            }
        //        });
    }

    private void checkView() {
        String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String checkLogin = webServiceConnection.DateCompare(timeStr, prf.getString("take_time", ""), prf.getString("expires_in", ""));

        if (webServiceConnection.isOnline(getContext())) {
            if (checkLogin.equals("true")) {
                loadPageView();
            } else if (checkLogin.equals("exToke")) {
                new com.omnihealthgroup.reshining.custom.IO.TokenExp(getContext()).execute();
            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(com.omnihealthgroup.reshining.badymeasurement.R.string.message_title))
                        .setMessage(getString(com.omnihealthgroup.reshining.badymeasurement.R.string.msg_retokenget))
                        .setPositiveButton(getString(com.omnihealthgroup.reshining.badymeasurement.R.string.msg_confirm),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        try {
                                            getAuthRequest(getContext(), com.omnihealthgroup.reshining.badymeasurement.R.layout.auth_dialog, com.omnihealthgroup.reshining.badymeasurement.R.id.webv);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).show();
            }
        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(com.omnihealthgroup.reshining.badymeasurement.R.string.msg_connect_faild_title))
                    .setMessage(getString(com.omnihealthgroup.reshining.badymeasurement.R.string.msg_connect_faild))
                    .setPositiveButton(getString(com.omnihealthgroup.reshining.badymeasurement.R.string.msg_confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                                    getContext().startActivity(intent);
                                }
                            }).show();
        }
    }

    private void loadPageView() {
        final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(getContext()
                , getString(R.string.webview_loading_title)
                , getString(R.string.msg_tokenget), false, true);
        pb.show();

        Log.v(TAG, TAG + "Start");

        pb.dismiss();
    }

    /**
     * GetAuthCode
     */
    protected void getAuthRequest(final Context context, int dialogid, int webid) {
        final Dialog auth_dialog = new Dialog(context);
        final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(context, getString(com.omnihealthgroup.reshining.badymeasurement.R.string.message_title), getString(com.omnihealthgroup.reshining.badymeasurement.R.string.msg_tokenget), true, false);
        pb.show();

        auth_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //去除Dialog標題
        auth_dialog.setContentView(dialogid);
        myBrowser = (WebView) auth_dialog.findViewById(webid);
        webServiceConnection.setBrowserProperty(myBrowser);
        webServiceConnection.setWebviewProperty(myBrowser, getContext());

        String webSiteStr = webServiceConnection.AuthServer + "/" + webServiceConnection.AuthRequest
                + "?response_type=code"
                + "&client_id=" + webServiceConnection.CLIENT_ID
                //                + "&secret_key=" + webServiceConnection.CLIENT_SECRET
                + "&redirect_url=" + webServiceConnection.REDIRECT_URI
                + "&display=page"
                + "&scope=user_profile"
                + "&scope=measure_data"
                + "&scope=health_report"
                + "&state=Oauth_Call"
                + "&access_type=online"
                + "&prompt=none"
                + "&login_hint=email";

        Log.v(TAG + "webSiteStr", webSiteStr);
        myBrowser.loadUrl(webSiteStr);

        myBrowser.setWebViewClient(new WebViewClient() {
            boolean authComplete = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pb.dismiss();
                if (url.contains("?code=") && authComplete != true) {
                    Uri uri = Uri.parse(url);
                    authCode = uri.getQueryParameter("code");
                    Log.i("", "CODE : " + authCode);
                    authComplete = true;
                    //                    Toast.makeText(context, "Authorization Code is: " + authCode, Toast.LENGTH_SHORT).show();
                    pb.dismiss();
                    auth_dialog.dismiss();
                    new TokenGet(context, authCode).execute();

                } else if (url.contains("error=access_denied")) {
                    Log.i("", "ACCESS_DENIED_HERE");
                    authComplete = true;
                    Toast.makeText(context, getString(com.omnihealthgroup.reshining.badymeasurement.R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    auth_dialog.dismiss();
                }
            }
        });
        auth_dialog.show();
        auth_dialog.setCancelable(true);
    }

}


