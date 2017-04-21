package com.omnihealthgroup.reshining.custom.IO;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.omnihealthgroup.reshining.custom.R;
import com.omnihealthgroup.reshining.custom.Util.ShowMEProgressDiaLog;
import com.omnihealthgroup.reshining.custom.Util.WebServiceConnection;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lhm05 on 2016/08/17.
 */
public class TokenGet extends AsyncTask<String, String, JSONObject> {
    private static String TAG = TokenGet.class.getSimpleName();

    private ShowMEProgressDiaLog pb;
    private Context context;
    private WebServiceConnection webServiceConnection;
    private SharedPreferences prf;
    private String authCode;

    public TokenGet(Context context, String authCode) {
        this.context = context;
        webServiceConnection = new WebServiceConnection();
        prf = context.getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        this.authCode = authCode;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pb = new ShowMEProgressDiaLog(context
                , context.getString(R.string.webview_loading_title)
                , context.getString(R.string.msg_tokenget), true, false);

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
//                            .putInt("day_count", 3) //初始筆數
                            .commit();

                    pb.dismiss();
                    new LoadPageView(context);

                } else {
                    Toast.makeText(context, context.getString(R.string.msg_net_faild)
                            , Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, context.getString(R.string.msg_net_faild)
                        , Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.msg_net_faild)
                    , Toast.LENGTH_SHORT).show();
        } finally {
            pb.dismiss();
        }
    }
}