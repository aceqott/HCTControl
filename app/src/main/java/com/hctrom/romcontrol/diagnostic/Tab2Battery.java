package com.hctrom.romcontrol.diagnostic;

/**
 * Created by Ivan on 28/11/2015.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hctrom.romcontrol.R;


public class Tab2Battery extends Fragment {
    private Context mContext;

    private TextView mTextViewInfo;
    private TextView mTextViewPercentage;
    private ImageView imageView;
    private ProgressBar mProgressBar;
    private TextView mTemp;

    private int mProgressStatus = 0;

    @Override
    public void onDestroy() {
        mContext.unregisterReceiver(mBroadcastReceiver);

        super.onDestroy();
    }

    // Initialize a new BroadcastReceiver instance
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
            float percentage = level/ (float) scale;
            String temp = batteryTemperature(intent);
            String technology = intent.getStringExtra("technology");
            int plugged = intent.getIntExtra("plugged", -1);
            int health = intent.getIntExtra("health", 0);
            int voltage = intent.getIntExtra("voltage", 0);

            mTextViewInfo.setText("Datos de Batería : ");
            mProgressStatus = (int)((percentage)*100);
            mTextViewPercentage.setText("" + mProgressStatus + "%");
            mTextViewInfo.setText(mTextViewInfo.getText() + "\n• Tecnología : " + technology);
            mTextViewInfo.setText(mTextViewInfo.getText() + "\n• Conexión : " + getPlugTypeString(plugged));
            if (isConnected(intent)){
                mTextViewInfo.setText(mTextViewInfo.getText() + "\n• Nivel Batería : "+ mProgressStatus + "%  -");
                if (level == 100){
                    imageView.setImageResource(R.drawable.battery_full);
                }else {
                    imageView.setImageResource(R.drawable.battery_charging);
                }
            }else{
                mTextViewInfo.setText(mTextViewInfo.getText() + "\n• Nivel Batería : "+ mProgressStatus + "%  -");
                if (level == 100) {
                    imageView.setImageResource(R.drawable.battery_full);
                }else if (level >= 16){
                    imageView.setImageResource(R.drawable.battery_descharging);
                }else{
                    imageView.setImageResource(R.drawable.battery_min);
                }
            }
            mTextViewInfo.setText(mTextViewInfo.getText() + "\n• Estado : " + getHealthString(health));
            mTextViewInfo.setText(mTextViewInfo.getText() + "\n• Voltaje : " + voltage);
            mTextViewInfo.setText(mTextViewInfo.getText() + "\n• Temp. Batería : " + temp + "°C");
            mProgressBar.setProgress(mProgressStatus);
            mTemp.setText(temp + "°C");
        }
    };

    private String getHealthString(int health) {
        String healthString = "Desconocido";

        switch (health) {
            case BatteryManager.BATTERY_HEALTH_DEAD:
                healthString = "Malo";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                healthString = "Bueno";
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                healthString = "Exceso Voltaje";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                healthString = "Sobrecalentamiento";
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                healthString = "Fallo";
                break;
        }

        return healthString;
    }

    private String getPlugTypeString(int plugged) {
        String plugType = "Desc.";

        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                plugType = "AC";
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                plugType = "USB";
                break;
        }

        return plugType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.diagnostic_battery,container,false);
        setHasOptionsMenu(false);
        // Request window feature action bar
        //this.getActivity().requestWindowFeature(Window.FEATURE_ACTION_BAR);

        // Get the application context
        mContext = getActivity().getApplicationContext();
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(mBroadcastReceiver,iFilter);

        // Get the widgets reference from XML layout
        mTextViewInfo = (TextView) v.findViewById(R.id.tv_info);
        mTextViewPercentage = (TextView) v.findViewById(R.id.tv_percentage);
        imageView = (ImageView) v.findViewById(R.id.imageViewCharging);
        mProgressBar = (ProgressBar) v.findViewById(R.id.pb);
        mTemp = (TextView) v.findViewById(R.id.textView_temp);

        return v;
    }

    public static String batteryTemperature(Intent intent)
    {
        float  temp   = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0)) / 10;
        return String.valueOf(temp) + "";
    }

    public static boolean isConnected(Intent intent) {
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }
}
