package com.hctrom.romcontrol.hosts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class IntentReceiver extends BroadcastReceiver {

    private static final String TAG = "HFMBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Started Receiver");
        Intent serv = new Intent(context, CheckHosts.class);
        serv.setAction(intent.getAction());
        serv.putExtras(intent);
        context.startService(serv);
    }
}
