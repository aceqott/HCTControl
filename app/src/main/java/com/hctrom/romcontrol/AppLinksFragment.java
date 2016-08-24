package com.hctrom.romcontrol;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.hctrom.romcontrol.prefs.ThemeSwitch;


public class AppLinksFragment extends PreferenceFragment {
    HandlePreferenceFragments hpf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeSwitch.getIconsColor(getActivity());
        //addPreferencesFromResource(R.xml.app_links_prefs);
        hpf = new HandlePreferenceFragments(getActivity(), this, "app_links_prefs");
    }

    @Override
    public void onResume() {
        super.onResume();
        hpf.onResumeFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
        hpf.onPauseFragment();
    }

}
