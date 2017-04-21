package com.omnihealthgroup.reshining.custom.IO;

import android.content.Context;

import com.omnihealthgroup.reshining.custom.R;
import com.omnihealthgroup.reshining.custom.Util.ShowMEProgressDiaLog;

/**
 * Created by Administrator on 2016/8/20.
 */
public class LoadPageView {

    public LoadPageView(Context context) {
        final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(context
                , context.getString(R.string.webview_loading_title)
                , context.getString(R.string.msg_tokenget), false, true);
        pb.show();
        pb.dismiss();
    }

}
