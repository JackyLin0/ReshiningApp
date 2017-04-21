package com.omnihealthgroup.reshining.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.omnihealthgroup.drawerframework.DrawerFrameworkLayoutFragment;
import com.omnihealthgroup.reshining.R;
import com.omnihealthgroup.reshining.badymeasurement.MyHealthCareFragment;
import com.omnihealthgroup.reshining.communication.ListGCMFragment;
import com.omnihealthgroup.reshining.custom.Util.GcmUtil;
import com.omnihealthgroup.reshining.custom.Util.ShowMEProgressDiaLog;
import com.omnihealthgroup.reshining.custom.Util.WebServiceConnection;
import com.omnihealthgroup.reshining.custom.dao.UserDataDAO;
import com.omnihealthgroup.reshining.custom.object.UserData;
import com.omnihealthgroup.reshining.diet.fragment.DietFragment;
import com.omnihealthgroup.reshining.persionalinformation.PersonalProfilesFragment;
import com.omnihealthgroup.reshining.schedule.MyHealthCalendarFragment;
import com.omnihealthgroup.reshining.setting.MySystemSettingsFragment;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by lhm05 on 2016/08/08.
 */
public class MainFragment extends DrawerFrameworkLayoutFragment {
    private static final String TAG = MainFragment.class.getSimpleName();

    private String pushId = null, SHA_encrypt = null, UserName = null;

    private WebServiceConnection webServiceConnection;
    private SharedPreferences prf;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        prf = getContext().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        new pushdeviceReg().cancel(true);
        new pushdeviceReg_valid().cancel(true);

        new pushdeviceout().cancel(true);
        new pushdeviceout_valid().cancel(true);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.v(TAG, "onViewStateRestored");

    }

    @Override
    protected void onSetDrawerMenu(NavigationView menuView) {
        super.onSetDrawerMenu(menuView);
        Log.v(TAG, "onSetDrawerMenu");
        if ((getArguments() != null && getArguments().getString("title") != null)
                || (getArguments() != null && getArguments().getString("title_onNewIntent") != null)) {
            menuView.setCheckedItem(R.id.information);
        } else {
            menuView.setCheckedItem(R.id.bloodpress);
        }
    }

    @Override
    protected void onSetDrawerMain(ViewGroup mainView) {
        super.onSetDrawerMain(mainView);
        Log.v(TAG, "onSetDrawerMain ");


        // TODO: 2016/08/08 預設身體量測
        Fragment defultFragment;
        if ((getArguments() != null && getArguments().getString("title") != null)
                || (getArguments() != null && getArguments().getString("title_onNewIntent") != null)) {
            defultFragment = new ListGCMFragment();
            Bundle bundle = new Bundle();


            if (getArguments().getString("title") != null) {
                bundle.putString("title", getArguments().getString("title"));
            } else {
                bundle.putString("title", getArguments().getString("title_onNewIntent"));

            }
            defultFragment.setArguments(bundle);
        } else {
            defultFragment = new MyHealthCareFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("fragmentType", 0);
            defultFragment.setArguments(bundle);
        }
        getChildFragmentManager()
                .beginTransaction()
                .replace(getDrawerMainResId(), defultFragment)
                .commit();
    }

    @Override
    protected void onSetMenuHeader(View headerView) {
        super.onSetMenuHeader(headerView);
        Log.v(TAG, "onSetMenuHeader");

        //        CharSequence acc = MyAccountManager.GetAccountName();
        //        CharSequence name = MyAccountManager.GetHumanName();
        //        Employee employee = MyAccountManager.GetInfo();
        String time;

        UserDataDAO userDataDAO = new UserDataDAO(getContext());
        List<UserData> userList = userDataDAO.getuserdata();
        Log.v(TAG + "userList", userList.toString());
        for (UserData user : userList) {
            Log.v(TAG + "userName", user.getName().toString());
            UserName = user.getName();
        }

        try {
            String timeStr = new SimpleDateFormat("HH").format(new Date());

            int timeInt = Integer.parseInt(timeStr);
            if (timeInt >= 5 && timeInt < 12) {
                time = "早安";
            } else if (timeInt >= 12 && timeInt < 17) {
                time = "午安";
            } else {
                time = "晚安";
            }

            View v;

            //            if (employee != null && employee.empNo != null && employee.hasPic) {
            //                ((OvalButton) headerView.findViewById(android.R.id.icon))
            //                        .setImageURI(ShowContactWebReference.EMPLOYEE_PIC_LARGE_URL + employee.empNo);
            //
            //                if ((v = headerView.findViewById(android.R.id.icon)) instanceof TextView) {
            //                    ((TextView) v).setText(name);
            //                }
            //
            //                if ((v = headerView.findViewById(android.R.id.title)) instanceof TextView) {
            //                    ((TextView) v).setText(Html.fromHtml(
            //                            // getString(R.string.nv_header_title, time, name, employee == null ? acc : employee.getEmpJobTitleString())));
            //                            getString(R.string.nv_header_title, time, name, getResources().getString(R.string.message))));
            //
            //                    ((TextView) v).setTextSize(20);
            //                }
            //            } else {

            if ((v = headerView.findViewById(android.R.id.icon)) instanceof ImageView) {
                v.setVisibility(View.GONE);
            }

            if ((v = headerView.findViewById(android.R.id.title)) instanceof TextView) {
                ((TextView) v).setText(Html.fromHtml(
                        // getString(R.string.nv_header_title, time, name, employee == null ? acc : employee.getEmpJobTitleString())));
                        getString(R.string.nv_header_title, time, UserName, getResources().getString(R.string.message))));
                ((TextView) v).setTextSize(20);
                v.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }

            //            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Log.v(TAG, "onNavigationItemSelected");
        Fragment fragment = null;
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.bloodpress:
                fragment = new MyHealthCareFragment();
                Bundle bloodpress_bundle = new Bundle();
                bloodpress_bundle.putInt("fragmentType", 0);
                fragment.setArguments(bloodpress_bundle);
                break;
            case R.id.bloodsugar:
                fragment = new MyHealthCareFragment();
                Bundle bloodsugar_bundle = new Bundle();
                bloodsugar_bundle.putInt("fragmentType", 1);
                fragment.setArguments(bloodsugar_bundle);
                break;
            case R.id.weight:
                fragment = new MyHealthCareFragment();
                Bundle weight_bundle = new Bundle();
                weight_bundle.putInt("fragmentType", 2);
                fragment.setArguments(weight_bundle);
                break;
//            case R.id.sport:
//                fragment = new MyHealthActivityFragment();
                //                Bundle sport_bundle = new Bundle();
                //                sport_bundle.putInt("fragmentType", 2);
                //                fragment.setArguments(sport_bundle);
//                break;
//            case R.id.sleep:
//                fragment = new MyHealthActivityFragment();
//                break;
//            case R.id.diet:
//                fragment = new DietFragment();
//                break;
//            case R.id.report:
//                fragment = new MyHealthActivityFragment();
//                break;
//            case R.id.lesion_care:
//                fragment = new MyHealthActivityFragment();
//                break;

//            case R.id.schedule:
//                fragment = new MyHealthCalendarFragment();
//                break;
            case R.id.information:
                fragment = new ListGCMFragment();
                break;
//            case R.id.health_education:
//                fragment = new MyHealthActivityFragment();
//                break;

            case R.id.persional_information:
                fragment = new PersonalProfilesFragment();
                break;
            case R.id.setting:
                fragment = new MySystemSettingsFragment();
                break;
            case R.id.logout:
                new pushdeviceout().execute();
                //                CancelAlertDialog();
                break;
        }

        if (fragment != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(getDrawerMainResId(), fragment)
                    .commit();
        }
        return super.onNavigationItemSelected(menuItem);
    }

    private void CancelAlertDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.message_title))
                .setMessage(getString(R.string.msg_logout_confirm))
                .setPositiveButton(getString(R.string.msg_confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getContext().getSharedPreferences("AuthServer", Context.MODE_PRIVATE).edit().clear().commit();
                        Fragment fragment = new LoginFragment();
                        if (fragment != null) {
                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, fragment)
                                    .commit();
                        }

                    }
                })
                .setNegativeButton(getString(R.string.msg_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (prf.getInt("pushdeviceReg", 0) == 88) {
                            new pushdeviceReg().execute();
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

    protected class pushdeviceReg extends AsyncTask<String, String, JSONObject> {
        ShowMEProgressDiaLog pb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = new ShowMEProgressDiaLog(getContext(), getString(com.omnihealthgroup.reshining.setting.R.string.webview_loading_title), getString(com.omnihealthgroup.reshining.setting.R.string.msg_tokenget), false, true);
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
                    for (UserData user : userList) {
                        Log.v(TAG + "UserUniqueId", user.getUserUniqueId().toString());
                        UserUniqueId = user.getUserUniqueId();
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
                        Log.v(TAG + "response", response.toString());
                        if (response.getString("u") != null
                                && response.getString("v") != null) {
                            pushId = response.getString("u");

                            String salt = webServiceConnection.Salt_push;
                            String password = webServiceConnection.Password_push;
                            String AES_decrypt = webServiceConnection.decrypt(response.getString("v"), salt.getBytes(), password.getBytes());
                            Log.v(TAG + "AES_decrypt", AES_decrypt);

                            SHA_encrypt = webServiceConnection.getSHA(AES_decrypt);
                            Log.v(TAG + "SHA_encrypt", SHA_encrypt);

                            pb.dismiss();
                            new pushdeviceReg_valid().execute();

                            //                            new Thread(new Runnable() {
                            //                                @Override
                            //                                public void run() {
                            //                                    String response = null;
                            //                                    if (!isCancelled() && pushId != null && SHA_encrypt != null) {
                            //                                        try {
                            //                                            response = webServiceConnection.pushdeviceReg_valid(pushId, SHA_encrypt);
                            //                                        } catch (Exception e) {
                            //                                            e.printStackTrace();
                            //                                        }
                            //                                    }
                            //                                    Log.v(TAG, response);
                            //                                }
                            //                            }).start();

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
            pb = new ShowMEProgressDiaLog(getContext(), getString(com.omnihealthgroup.reshining.setting.R.string.webview_loading_title), getString(com.omnihealthgroup.reshining.setting.R.string.msg_tokenget), false, true);
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


                        } else {
                            Toast.makeText(getContext(), getString(com.omnihealthgroup.reshining.setting.R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), getString(com.omnihealthgroup.reshining.setting.R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(getContext(), getString(com.omnihealthgroup.reshining.setting.R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                } finally {
                    pb.dismiss();
                }
            }
        }
    }

    protected class pushdeviceout extends AsyncTask<String, String, JSONObject> {
        ShowMEProgressDiaLog pb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = new ShowMEProgressDiaLog(getContext(), getString(com.omnihealthgroup.reshining.setting.R.string.webview_loading_title), getString(com.omnihealthgroup.reshining.setting.R.string.msg_tokenget), false, true);
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
                    //                    String ModelNumber = Build.DEVICE + "_" + Build.MODEL + "_" + "SDK" + Build.VERSION.SDK;

                    UserDataDAO userDataDAO = new UserDataDAO(getContext());
                    List<UserData> userList = userDataDAO.getuserdata();
                    String UserUniqueId = null;
                    for (UserData user : userList) {
                        Log.v(TAG + "UserUniqueId", user.getUserUniqueId().toString());
                        UserUniqueId = user.getUserUniqueId();
                    }

                    response = webServiceConnection.pushdeviceout(gcmUtil.getToken(), UserUniqueId);
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
                        if (response.getString("u") != null
                                && response.getString("v") != null) {
                            Log.v(TAG, response.getString("u"));
                            Log.v(TAG, response.getString("v"));
                            pushId = response.getString("u");

                            String salt = webServiceConnection.Salt_push;
                            String password = webServiceConnection.Password_push;
                            String AES_decrypt = webServiceConnection.decrypt(response.getString("v"), salt.getBytes(), password.getBytes());
                            Log.v(TAG + "AES_decrypt", AES_decrypt);

                            SHA_encrypt = webServiceConnection.getSHA(AES_decrypt);
                            Log.v(TAG + "SHA_encrypt", SHA_encrypt);

                            pb.dismiss();
                            new pushdeviceout_valid().execute();

                            //                            new Thread(new Runnable() {
                            //                                @Override
                            //                                public void run() {
                            //                                    String response = null;
                            //                                    if (!isCancelled() && pushId != null && SHA_encrypt != null) {
                            //                                        try {
                            //                                            response = webServiceConnection.pushdeviceout_valid(pushId, SHA_encrypt);
                            //                                        } catch (Exception e) {
                            //                                            e.printStackTrace();
                            //                                        }
                            //                                    }
                            //                                    Log.v(TAG, response);
                            //                                }
                            //                            }).start();

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

    protected class pushdeviceout_valid extends AsyncTask<String, String, String> {
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
                    response = webServiceConnection.pushdeviceout_valid(pushId, SHA_encrypt);
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

                            CancelAlertDialog();

                        } else {
                            Toast.makeText(getContext(), getString(com.omnihealthgroup.reshining.setting.R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), getString(com.omnihealthgroup.reshining.setting.R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(getContext(), getString(com.omnihealthgroup.reshining.setting.R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                } finally {
                    pb.dismiss();
                }
            }
        }
    }

}
