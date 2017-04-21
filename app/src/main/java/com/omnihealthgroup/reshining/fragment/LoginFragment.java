package com.omnihealthgroup.reshining.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.omnihealthgroup.reshining.R;
import com.omnihealthgroup.reshining.custom.Util.GSONUtil;
import com.omnihealthgroup.reshining.custom.Util.GcmUtil;
import com.omnihealthgroup.reshining.custom.Util.ShowMEProgressDiaLog;
import com.omnihealthgroup.reshining.custom.Util.WebServiceConnection;
import com.omnihealthgroup.reshining.custom.dao.MeasureStandardDAO;
import com.omnihealthgroup.reshining.custom.dao.UserDataDAO;
import com.omnihealthgroup.reshining.custom.object.MeasureStandard;
import com.omnihealthgroup.reshining.custom.object.UserData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by lhm05 on 2016/08/08.
 */
public class LoginFragment extends Fragment {
    //    public static final String TAG = "LoginFragment";
    private String TAG = LoginFragment.class.getSimpleName();

    private View rootView;
    private Button loginBtn;
    private WebView myBrowser = null;
    private String authCode = null, pushId = null, SHA_encrypt = null;

    private SharedPreferences prf;
    private WebServiceConnection webServiceConnection;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        prf = getActivity().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login_health, container, false);
        Log.v(TAG, "onCreateView");

        initView();
        checkView();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");

        new queryUserinfo().cancel(true);
        new physiology_standard().cancel(true);
        new pushdeviceReg().cancel(true);
        new pushdeviceReg_valid().cancel(true);

        //        new TokenExp(getContext()).cancel(true);
        //        new TokenGet(getContext(), authCode).cancel(true);
        new TokenExp().cancel(true);
        new TokenGet().cancel(true);
    }

    private void initView() {
        loginBtn = (Button) rootView.findViewById(R.id.main_btnSubmit);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Todo 待增加Token 授權方式
                getAuthRequest(getContext(), R.layout.auth_dialog, R.id.webv);


                //                Fragment fragment = new MainFragment();
                //                getFragmentManager()
                //                        .beginTransaction()
                //                        .replace(R.id.container, fragment)
                //                        //                                .commit();
                //                        .commitAllowingStateLoss();

            }
        });
    }

    private void checkView() {
        String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        if (webServiceConnection.isOnline(getContext())) {
            if (prf.getString("access_token", "").getBytes().length > 0
                    && prf.getString("take_time", "").getBytes().length > 0
                    && prf.getString("expires_in", "").getBytes().length > 0) {
                Log.v(TAG + "access_token", String.valueOf(prf.getString("access_token", "").getBytes().length));
                String checkLogin = webServiceConnection.DateCompare(timeStr, prf.getString("take_time", ""), prf.getString("expires_in", ""));
                if (checkLogin.equals("true")) {
                    new queryUserinfo().execute();
                } else if (checkLogin.equals("exToke")) {
                    new TokenExp().execute();
                } else {
                    getAuthRequest(getContext(), R.layout.auth_dialog, R.id.webv);
                }
            } else {
                getAuthRequest(getContext(), R.layout.auth_dialog, R.id.webv);
                //                Log.v("access_token", String.valueOf(prf.getString("access_token", "").getBytes().length));
                //                initView();
            }
        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.msg_connect_faild_title))
                    .setMessage(getString(R.string.msg_connect_faild))
                    .setPositiveButton(getString(R.string.msg_confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    getActivity().finish();
                                }
                            }).show();
        }
    }

    protected class queryUserinfo extends AsyncTask<String, String, JSONObject> {
        ShowMEProgressDiaLog pb;

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
            JSONObject response = null;
            if (!isCancelled()) {
                try {
                    response = webServiceConnection.queryUserinfo(prf.getString("access_token", ""));
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

                            Map<String, String> responsemap = new HashMap<>();
                            responsemap = GSONUtil.GsonToMaps(response.getJSONObject("result").toString());

                            UserDataDAO userDataDAO = new UserDataDAO(getContext());
                            userDataDAO.deleteAll();
                            UserData userData = new UserData();

                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = formatter.parse(responsemap.get("userBirthDay"));
                            String timeStr = formatter.format(date);

                            userData.setUserUniqueId(responsemap.get("userUniqueId"));
                            userData.setRfid(responsemap.get("rfid"));
                            userData.setUserIDNO(responsemap.get("userIDNO"));
                            userData.setName_sur(responsemap.get("userSurname"));
                            userData.setName_first(responsemap.get("userFirstName"));
                            userData.setName(responsemap.get("userName"));
                            userData.setNickname(responsemap.get("userEngName"));
                            userData.setGender(responsemap.get("userSex"));
                            userData.setBirthday(timeStr);
                            userData.setUserNationality(responsemap.get("userNationality"));
                            userData.setPhone(responsemap.get("userHomeTEL"));
                            userData.setMobile(responsemap.get("userMobile"));
                            userData.setEmail(responsemap.get("userEMail"));
                            userData.setUserBlood(responsemap.get("userBlood"));
                            userData.setUserMarried(responsemap.get("userMarried"));
                            userData.setUserRhType(responsemap.get("userRhType"));
                            userData.setHeight(String.valueOf(responsemap.get("userBodyHeight")));
                            userData.setWeight(String.valueOf(responsemap.get("userBodyWeight")));

                            userData.setPhysician(responsemap.get("physician"));
                            userData.setNurse(responsemap.get("nurse"));
                            userData.setDietitian(responsemap.get("dietitian"));

                            Map<String, String> responsemap_sec = new HashMap<>();
                            responsemap_sec = GSONUtil.GsonToMaps(response.getJSONObject("result").getJSONObject("userCareSetting").toString());

                            userData.setBloodpressureenabled(String.valueOf(responsemap_sec.get("bloodPressureEnabled")));
                            userData.setBloodglucoseenabled(String.valueOf(responsemap_sec.get("bloodGlucoseEnabled")));
                            userData.setBodyweightenabled(String.valueOf(responsemap_sec.get("bodyWeightEnabled")));
                            userData.setDietenabled(String.valueOf(responsemap_sec.get("dietEnabled")));
                            userData.setExerciseenabled(String.valueOf(responsemap_sec.get("exerciseEnabled")));
                            userData.setSlumberenabled(String.valueOf(responsemap_sec.get("slumberEnabled")));
                            userData.setLesionsenabled(String.valueOf(responsemap_sec.get("lesionsEnabled")));
                            userData.setDietsuggestcalories(String.valueOf(responsemap_sec.get("dietSuggestCalories")));
                            userData.setDietidealbodyweight(String.valueOf(responsemap_sec.get("dietIdealBodyWeight")));
                            userData.setDietintentbodyweight(String.valueOf(responsemap_sec.get("dietIntentBodyWeight")));

                            userDataDAO.insert(userData);

                            //                        new Thread(new Runnable() {
                            //                            @Override
                            //                            public void run() {
                            //                                JSONObject response = null;
                            //                                if (!isCancelled()) {
                            //                                    try {
                            //                                        response = webServiceConnection.physiology_standard(prf.getString("access_token", ""));
                            //                                    } catch (Exception e) {
                            //                                        e.printStackTrace();
                            //                                    } finally {
                            //                                        Log.v(TAG, response.toString());
                            //                                    }
                            //                                }
                            //                            }
                            //                        }).start();

                            pb.dismiss();
                            new physiology_standard().execute();

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

    protected class physiology_standard extends AsyncTask<String, String, JSONObject> {
        ShowMEProgressDiaLog pb;

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
            JSONObject response = null;
            if (!isCancelled()) {
                try {
                    response = webServiceConnection.physiology_standard(prf.getString("access_token", ""));
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
                            MeasureStandardDAO measureStandardDAO = new MeasureStandardDAO(getContext());
                            measureStandardDAO.deleteAll();

                            JSONArray jsonArray = response.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                MeasureStandard measureStandard = new MeasureStandard();
                                measureStandard.setItemKind(String.valueOf(jsonArray.getJSONObject(i).getString("itemKind")));
                                measureStandard.setWarningMax(String.valueOf(jsonArray.getJSONObject(i).getString("warningMax")));
                                measureStandard.setWarningMin(String.valueOf(jsonArray.getJSONObject(i).getString("warningMin")));
                                measureStandard.setDangerMax(String.valueOf(jsonArray.getJSONObject(i).getString("dangerMax")));
                                measureStandard.setDangerMin(String.valueOf(jsonArray.getJSONObject(i).getString("dangerMin")));

                                measureStandardDAO.insert(measureStandard);
                            }

                            pb.dismiss();

                            /////////////////////////// 模擬器 /////////////
                            GcmUtil gcmUtil = new GcmUtil();
                            if (prf.getString("GCMRegistration_token", "").getBytes().length < 1
                                    && gcmUtil.getToken() != prf.getString("GCMRegistration_token", "")) {
                                new pushdeviceReg().execute();
                            } else {
                                //進入側選單
                                Fragment fragment = new MainFragment();
                                //                            Bundle bundle = new Bundle();
                                //                            bundle.putString("userName", responsemap.get("userName"));
                                if (getArguments() != null) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("title", getArguments().getString("title"));
                                    fragment.setArguments(bundle);
                                }

                                getFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.container, fragment)
                                                //                                .commit();
                                        .commitAllowingStateLoss();
                            }
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

    protected class pushdeviceReg extends AsyncTask<String, String, JSONObject> {
        ShowMEProgressDiaLog pb;

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
            JSONObject response = null;
            if (!isCancelled()) {
                try {
                    GcmUtil gcmUtil = new GcmUtil();
                    String ModelNumber = Build.DEVICE + "_" + Build.MODEL + "_" + "SDK" + Build.VERSION.SDK;

                    UserDataDAO userDataDAO = new UserDataDAO(getContext());
                    List<UserData> userList = userDataDAO.getuserdata();
                    String UserUniqueId = null;
                    for (UserData userData : userList) {
                        Log.v(TAG + "UserUniqueId", userData.getUserUniqueId().toString());
                        UserUniqueId = userData.getUserUniqueId();
                    }

                    response = webServiceConnection.pushdeviceReg(gcmUtil.getToken(), UserUniqueId, gcmUtil.getDeviceSerail(), ModelNumber);
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
                        Log.v("response", response.toString());
                        if (response.getString("u") != null
                                && response.getString("v") != null) {
                            Log.v(TAG, response.getString("u"));
                            Log.v(TAG, response.getString("v"));
                            pushId = response.getString("u");

                            String salt = webServiceConnection.Salt_push;
                            String password = webServiceConnection.Password_push;
                            String AES_decrypt = webServiceConnection.decrypt(response.getString("v"), salt.getBytes(), password.getBytes());
                            Log.v("AES_decrypt", AES_decrypt);

                            SHA_encrypt = webServiceConnection.getSHA(AES_decrypt);
                            Log.v("SHA_encrypt", SHA_encrypt);

                            pb.dismiss();
                            new pushdeviceReg_valid().execute();
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

    protected class pushdeviceReg_valid extends AsyncTask<String, String, String> {
        ShowMEProgressDiaLog pb;

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
        protected String doInBackground(String... args) {
            String response = null;
            if (!isCancelled() && pushId != null && SHA_encrypt != null) {
                try {
                    response = webServiceConnection.pushdeviceReg_valid(pushId, SHA_encrypt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (isAdded()) {
                try {
                    if (response != null) {
                        Log.v(TAG + "response", response.toString());
                        if (response.equals("success")) {

                            GcmUtil gcmUtil = new GcmUtil();
                            prf.edit().putString("GCMRegistration_token", gcmUtil.getToken())
                                    .putInt("pushdeviceReg", 88)
                                    .commit();
                            Log.v(TAG + "GCMRegistration_token", prf.getString("GCMRegistration_token", ""));
                            //                        Map<String, String> responsemap = new HashMap<>();
                            //                        responsemap = GSONUtil.GsonToMaps(response.getJSONObject("result").toString());
                            //                        Log.v("responsemap", responsemap.toString());
                            //
                            //                        UserAdapter userAdapter = new UserAdapter(getContext());
                            //                        userAdapter.delAllUser();
                            //                        User user = new User();
                            //
                            //                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            //                        Date date = formatter.parse(responsemap.get("userBirthDay"));
                            //                        String timeStr = formatter.format(date);
                            //
                            //                        user.setUserUniqueId(responsemap.get("userUniqueId"));
                            //                        user.setRfid(responsemap.get("rfid"));
                            //                        user.setUserIDNO(responsemap.get("userIDNO"));
                            //                        user.setName(responsemap.get("userName"));
                            //                        user.setNickname(responsemap.get("userEngName"));
                            //                        user.setGender(responsemap.get("userSex"));
                            //                        user.setBirthday(timeStr);
                            //                        user.setUserNationality(responsemap.get("userNationality"));
                            //                        user.setPhone(responsemap.get("userHomeTEL"));
                            //                        user.setMobile(responsemap.get("userMobile"));
                            //                        user.setEmail(responsemap.get("userEMail"));
                            //                        user.setUserBlood(responsemap.get("userBlood"));
                            //                        user.setUserRhType(responsemap.get("userRhType"));
                            //                        user.setUserMarried(responsemap.get("userMarried"));
                            //
                            //                        userAdapter.createtUser(user);

                            pb.dismiss();
                            //進入側選單
                            Fragment fragment = new MainFragment();
                            //                            Bundle bundle = new Bundle();
                            //                            bundle.putString("userName", responsemap.get("userName"));
                            if (getArguments() != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("title", getArguments().getString("title"));
                                fragment.setArguments(bundle);
                            }

                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, fragment)
                                            //                                .commit();
                                    .commitAllowingStateLoss();
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

    /**
     * GetAuthCode
     */
    protected void getAuthRequest(final Context context, int dialogid, int webid) {
        final Dialog auth_dialog = new Dialog(context);
        final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(context, getString(R.string.message_title), getString(R.string.msg_tokenget), true, false);
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
                    new TokenGet().execute();

                } else if (url.contains("error=access_denied")) {
                    Log.i("", "ACCESS_DENIED_HERE");
                    authComplete = true;
                    Toast.makeText(context, getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    auth_dialog.dismiss();
                }
            }
        });
        auth_dialog.show();
        auth_dialog.setCancelable(true);
    }

    protected class TokenGet extends AsyncTask<String, String, JSONObject> {
        ShowMEProgressDiaLog pb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = new ShowMEProgressDiaLog(getContext(), getString(R.string.webview_loading_title), getString(R.string.msg_tokenget), true, false);
            //            pDialog = new ProgressDialog(getContext());
            //            pDialog.setMessage("連接資料庫中，請稍後...");
            //            pDialog.setIndeterminate(false);
            //            pDialog.setCancelable(true);
            //            Code = pref.getString("Code", "");
            pb.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject response = null;
            if (!isCancelled()) {
                try {
                    response = webServiceConnection.getTokenRequest(authCode, webServiceConnection.CLIENT_ID, webServiceConnection.CLIENT_SECRET);
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
                        if (response.getString("message").equals("Success")) {
                            String access_token = response.getJSONObject("result").getString("access_token");
                            String refresh_token = response.getJSONObject("result").getString("refresh_token");
                            String expires_in = response.getJSONObject("result").getString("expires_in");
                            String take_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                            Log.d(TAG + "access_token", access_token);
                            Log.d(TAG + "refresh_token", refresh_token);
                            Log.d(TAG + "expires_in", expires_in);
                            Log.d(TAG + "take_time", take_time);

                            prf.edit().putString("access_token", access_token)
                                    .putString("refresh_token", refresh_token)
                                    .putString("expires_in", expires_in)
                                    .putString("take_time", take_time)
                                    .putInt("day_count", 3) //初始筆數
                                    .commit();

                            pb.dismiss();
                            new queryUserinfo().execute();

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

    protected class TokenExp extends AsyncTask<String, String, JSONObject> {
        ShowMEProgressDiaLog pb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = new ShowMEProgressDiaLog(getContext(), getString(R.string.webview_loading_title), getString(R.string.msg_tokenexp), true, false);
            //            pDialog = new ProgressDialog(getContext());
            //            pDialog.setMessage("連接資料庫中，請稍後...");
            //            pDialog.setIndeterminate(false);
            //            pDialog.setCancelable(true);
            //            Code = pref.getString("Code", "");
            pb.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject response = null;
            if (!isCancelled()) {
                try {
                    response = webServiceConnection.getTokenExRequest(prf.getString("access_token", ""), prf.getString("refresh_token", ""), prf.getString("expires_in", ""));
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
                        if (response.getString("message").equals("Success")) {
                            String access_token = response.getJSONObject("result").getString("access_token");
                            String refresh_token = response.getJSONObject("result").getString("refresh_token");
                            String expires_in = response.getJSONObject("result").getString("expires_in");
                            String take_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                            Log.d(TAG + "access_token", access_token);
                            Log.d(TAG + "refresh_token", refresh_token);
                            Log.d(TAG + "expires_in", expires_in);
                            Log.d(TAG + "take_time", take_time);

                            prf.edit().putString("access_token", access_token)
                                    .putString("refresh_token", refresh_token)
                                    .putString("expires_in", expires_in)
                                    .putString("take_time", take_time)
                                    .commit();

                            pb.dismiss();
                            new queryUserinfo().execute();

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
