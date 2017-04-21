package com.omnihealthgroup.reshining.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.omnihealthgroup.reshining.custom.Util.ShowMEProgressDiaLog;
import com.omnihealthgroup.reshining.custom.Util.WebServiceConnection;

import org.json.JSONObject;


/**
 * Created by Administrator on 2016/7/26.
 */
public class SettingPassWordFragment extends DialogFragment {
    private static final String TAG = SettingPassWordFragment.class.getSimpleName();

    private EditText password_old, password_new, password_check;
    private Button btn_commit;

    private View rootView;
    private WebServiceConnection webServiceConnection;
    private SharedPreferences prf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        prf = getContext().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.dialogfragment_setting_password, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //editview 不會自動跳出鍵盤 in DialogFragment

        initView();

        initHandler();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");

    }

    private void initView() {
        password_old = (EditText) rootView.findViewById(R.id.password_old);
        password_new = (EditText) rootView.findViewById(R.id.password_new);
        password_check = (EditText) rootView.findViewById(R.id.password_check);

        btn_commit = (Button) rootView.findViewById(R.id.btn_commit);
    }

    private void initHandler() {
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password_old.getText().toString().trim().equals("") || password_old.getText().toString().trim().length() < 1) {
                    webServiceConnection.editTextAlertDialog(getContext(), "請輸入您的舊登入密碼");
                    return;
                } else if (password_new.getText().toString().trim().equals("") || password_new.getText().toString().trim().length() < 1) {
                    webServiceConnection.editTextAlertDialog(getContext(), "請輸入您的新登入密碼");
                    return;
                } else if (password_check.getText().toString().trim().equals("") || password_check.getText().toString().trim().length() < 1) {
                    webServiceConnection.editTextAlertDialog(getContext(), "請確認您的新登入密碼");
                    return;
                } else if (!password_check.getText().toString().trim().equals(password_new.getText().toString().trim())) {
                    webServiceConnection.editTextAlertDialog(getContext(), "您所輸入的兩次新登入密碼並不相同");
                    return;
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle(getString(R.string.message_title))
                            .setMessage("是否確定要修改您的登入密碼")
                            .setPositiveButton(getString(R.string.msg_confirm), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(getContext()
                                            , getString(R.string.webview_loading_title)
                                            , getString(R.string.msg_tokenget), false, true);
                                    pb.show();
                                    if (webServiceConnection.isOnline(getContext())) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    while (true) {
                                                        Log.i(TAG, "manual upload data");
                                                        JSONObject reUpdateResponse = webServiceConnection.changepassword(prf.getString("access_token", ""), password_old.getText().toString().trim(), password_check.getText().toString().trim());
                                                        //                    if (reUpdateResponse != null && reUpdateResponse.equals("{\"Message\" : \"A01\"}")) {
                                                        if (reUpdateResponse != null && reUpdateResponse.getString("message").toString().equals("Success.")) {
                                                            Looper.prepare();
                                                            Toast.makeText(getContext(), "密碼修改成功", Toast.LENGTH_LONG).show();
                                                            pb.dismiss();
                                                            dismiss();
                                                            Looper.loop();
                                                            break;
                                                        } else {
                                                            Looper.prepare();
                                                            Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_LONG).show();

                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    password_old.setText("");
                                                                    password_new.setText("");
                                                                    password_check.setText("");
                                                                }
                                                            });

                                                            pb.dismiss();
                                                            Looper.loop();
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                    } else {
                                        new android.app.AlertDialog.Builder(getContext())
                                                .setTitle(getString(R.string.msg_connect_faild_title))
                                                .setMessage(getString(R.string.msg_connect_faild))
                                                .setPositiveButton(getString(R.string.msg_confirm),
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                                dismiss();
                                                            }
                                                        }).show();
                                    }
                                }
                            })
                            .setNegativeButton(getString(R.string.msg_cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dismiss();
                                    //                                    return;
                                }
                            }).show();
                }
            }
        });
    }


}
