package com.hctrom.romcontrol;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.hctrom.romcontrol.prefs.OpenAppPreference;
import com.hctrom.romcontrol.prefs.ThemeSwitch;
import com.hctrom.romcontrol.prefs.UriSelectionPreference;


public class BloqueoDePantallaFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, UriSelectionPreference.OnUriSelectionRequestedListener {
    HandlePreferenceFragments hpf;
    private static final String LOG_TAG = BloqueoDePantallaFragment.class.getSimpleName();
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private String mUriPreferenceKey;

    public static final String PREF_NAME_KEY = "pref_key";

    public BloqueoDePantallaFragment() {
        //empty public constructor
    }

    static BloqueoDePantallaFragment newInstance(String prefName) {
        BloqueoDePantallaFragment prefsFragment = new BloqueoDePantallaFragment();
        Bundle args = new Bundle();
        args.putString(PREF_NAME_KEY, prefName);
        prefsFragment.setArguments(args);
        return prefsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeSwitch.getIconsColor(getActivity());
        hpf = new HandlePreferenceFragments(getActivity(), this, "bloqueo_de_pantalla_prefs");
        mContext = HCTControl.getContext();
        String prefName = getArguments().getString(PREF_NAME_KEY);
        int prefId = mContext.getResources().getIdentifier(prefName, "xml", mContext.getPackageName());
        if (prefId != 0) {
            //getPreferenceManager().setSharedPreferencesName(prefName);
            //addPreferencesFromResource(prefId);
            mSharedPreferences = getPreferenceManager().getSharedPreferences();
            iteratePrefs(getPreferenceScreen());
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 46:
                Settings.System.putString(mContext.getContentResolver(), mUriPreferenceKey, data.getData().toString());
                mSharedPreferences.edit().putString(mUriPreferenceKey, data.getData().toString()).apply();
                ((UriSelectionPreference) findPreference(mUriPreferenceKey)).attemptToSetIcon(data.getData().toString());
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);

        }

    }

    private void iteratePrefs(PreferenceGroup preferenceGroup) {
        for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
            Preference preference = preferenceGroup.getPreference(i);
            Log.d(LOG_TAG, "iteratePrefs " + preference.getClass().getSimpleName());
            if (preference instanceof PreferenceGroup) {
                if (preference instanceof PreferenceScreen) {
                    preference.setOnPreferenceClickListener(this);
                }
                if (((PreferenceGroup) preference).getPreferenceCount() > 0) {
                    iteratePrefs((PreferenceGroup) preference);
                }
            } else if (preference instanceof OpenAppPreference) {
                if (!((OpenAppPreference) preference).isInstalled()) {
                    preferenceGroup.removePreference(preference);
                }
            } else if (preference instanceof UriSelectionPreference) {
                ((UriSelectionPreference) preference).setOnUriSelectionRequestedListener(this);
            }
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (((PreferenceScreen) preference).getPreferenceCount() > 0) {
            setUpNestedPreferenceLayout((PreferenceScreen) preference);
        } else if(preference.getIntent() != null) {
            if(HCTControl.getContext().getPackageManager().resolveActivity(preference.getIntent(), 0) != null) {
                startActivity(preference.getIntent());
            }
        }
        return true;
    }

    private void setUpNestedPreferenceLayout(PreferenceScreen preference) {
        final Dialog dialog = preference.getDialog();
        if (dialog != null) {
            LinearLayout rootView = (LinearLayout) dialog.findViewById(android.R.id.list).getParent();
            View decorView = dialog.getWindow().getDecorView();
            if (decorView != null && rootView != null) {
                Toolbar toolbar = (Toolbar) LayoutInflater.from(getActivity()).inflate(R.layout.toolbar_default, rootView, false);
                toolbar.setTitle(preference.getTitle());
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                rootView.addView(toolbar, 0);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        }
    }


    @Override
    public void onUriSelectionRequested(String key) {
        mUriPreferenceKey = key;
        Intent getContentIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getContentIntent.setType("image/*");
        startActivityForResult(getContentIntent, 46);
    }
}
