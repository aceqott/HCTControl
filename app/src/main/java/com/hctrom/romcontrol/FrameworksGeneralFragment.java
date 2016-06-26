package com.hctrom.romcontrol;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.FragmentActivity;

import com.hctrom.romcontrol.alertas.DialogoAlertaBuildEdit;
import com.hctrom.romcontrol.alertas.DialogoAlertaEmojis;
import com.hctrom.romcontrol.hosts.HfmHelpers;

import java.io.IOException;

public class FrameworksGeneralFragment extends PreferenceFragment {
    private static final String TAG = "FrameworksGeneralFragment";
    private static final String HFM_DISABLE_ADS = "hfm_disable_ads";
    public static ProgressDialog pd;
    public static CheckBoxPreference mHfmDisableAds;
    private static FragmentActivity myContext;
    Preference mHfmUpdateHosts;
    Preference mHfmDeleteDominio;
    Preference emojisKey;
    Preference builProp;
    Context context;

    public static Resources res;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeSelectorUtility theme = new ThemeSelectorUtility(getActivity());
        theme.onActivityCreateSetTheme(getActivity());

        context = getActivity();
        res = getResources();
        pd = new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setMessage(res.getString(R.string.hfm_dialog_wait));

        addPreferencesFromResource(R.xml.frameworks_general_prefs);
        PreferenceScreen prefScreen = getPreferenceScreen();

        mHfmDisableAds = (CheckBoxPreference) findPreference(HFM_DISABLE_ADS);
        mHfmDisableAds.setChecked(PreferenceManager.getDefaultSharedPreferences(context).getInt("HFM_DISABLE_ADS", 0) == 1 );
        mHfmUpdateHosts = prefScreen.findPreference("hfm_update_hosts");
        mHfmDeleteDominio = prefScreen.findPreference("hfm_delete_dominio");
        emojisKey = prefScreen.findPreference("emojis_key");
        builProp = prefScreen.findPreference("build_prop");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        SharedPreferences prefs = (PreferenceManager.getDefaultSharedPreferences(context));
        SharedPreferences.Editor editor = prefs.edit();
        if (preference == builProp){
            myContext = (FragmentActivity) context;
            final DialogoAlertaBuildEdit dialogo = new DialogoAlertaBuildEdit();
            dialogo.show(myContext.getSupportFragmentManager(), "tagAlerta");
        }else if (preference == emojisKey){
            myContext = (FragmentActivity) context;
            final DialogoAlertaEmojis dialogo = new DialogoAlertaEmojis();
            dialogo.show(myContext.getSupportFragmentManager(), "tagAlerta");
        }else if (preference == mHfmDeleteDominio){
            startActivity(new Intent(getActivity(), HostWhiteListView.class));
        }else if  (preference == mHfmDisableAds) {
            boolean checked = ((CheckBoxPreference)preference).isChecked();
            editor.putInt("HFM_DISABLE_ADS",checked ? 1:0);
            editor.commit();
            HfmHelpers.checkStatus(getActivity());
        } else if (preference == mHfmUpdateHosts) {
            try {
                HfmHelpers.checkConnectivity(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        return true;
    }
}
