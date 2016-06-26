package com.hctrom.romcontrol.hosts;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CheckHosts extends Service {

    private static final String TAG = "HFMCheckHosts";

    public static final String HFM_DISABLE_ADS = "hfm_disable_ads";

    private Context mContext;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mContext = this;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "Started check");
        HfmHelpers.checkStatus(mContext);
    }
}
