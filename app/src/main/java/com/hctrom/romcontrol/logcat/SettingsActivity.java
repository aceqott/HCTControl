package com.hctrom.romcontrol.logcat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hctrom.romcontrol.R;
import com.hctrom.romcontrol.ThemeSelectorUtility;
import com.hctrom.romcontrol.logcat.helper.PackageHelper;
import com.hctrom.romcontrol.logcat.helper.PreferenceHelper;
import com.hctrom.romcontrol.logcat.util.ArrayUtil;
import com.hctrom.romcontrol.logcat.util.StringUtil;
import com.hctrom.romcontrol.logcat.widget.MockDisabledListPreference;
import com.hctrom.romcontrol.logcat.widget.MultipleChoicePreference;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends PreferenceActivity implements OnPreferenceChangeListener {
	
	private static final int MAX_LOG_LINE_PERIOD = 1000;
	private static final int MIN_LOG_LINE_PERIOD = 1;
	private static final int MAX_DISPLAY_LIMIT = 100000;
	private static final int MIN_DISPLAY_LIMIT = 1000;
	
	private EditTextPreference logLinePeriodPreference, displayLimitPreference;
	private ListPreference textSizePreference, defaultLevelPreference;
	private MultipleChoicePreference bufferPreference;
	private MockDisabledListPreference themePreference;
	
	private boolean bufferChanged = false;

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
				.setFontAttrId(R.attr.fontPath)
				.build()
		);
		ThemeSelectorUtility theme = new ThemeSelectorUtility(this);
		theme.onActivityCreateSetTheme(this);
		int i = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0);
		if (i == 3) {
			getListView().setBackgroundColor(getResources().getColor(R.color.myDrawerBackground));
			getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColorSamsungLight));
		}else if (i == 0){
			getListView().setBackgroundColor(getResources().getColor(R.color.myInverseColor));
			getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColorHCT));
		}else if (i == 1){
			getListView().setBackgroundColor(getResources().getColor(R.color.myInverseColor));
			getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColor));
		}else{
			getListView().setBackgroundColor(getResources().getColor(R.color.myDrawerBackground));
			getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColor));
		}
		LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
		Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar_default, root, false);
		root.addView(bar, 0); // insert at top
		bar.setTitle("Ajustes CatLog");
		bar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
		bar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		addPreferencesFromResource(R.xml.catlog_settings);
		
		setUpPreferences();
	}
	
	private void setUpPreferences() {
		
		displayLimitPreference = (EditTextPreference) findPreference(getString(R.string.pref_display_limit));
		
		int displayLimitValue = PreferenceHelper.getDisplayLimitPreference(this);
		
		displayLimitPreference.setSummary(String.format(getString(R.string.pref_display_limit_summary).toString(),
				displayLimitValue, getString(R.string.pref_display_limit_default)));
		
		displayLimitPreference.setOnPreferenceChangeListener(this);
		
		logLinePeriodPreference = (EditTextPreference) findPreference(getString(R.string.pref_log_line_period));
		
		int logLinePrefValue = PreferenceHelper.getLogLinePeriodPreference(this);
		
		logLinePeriodPreference.setSummary(String.format(getString(R.string.pref_log_line_period_summary).toString(),
				logLinePrefValue, getString(R.string.pref_log_line_period_default)));
		
		logLinePeriodPreference.setOnPreferenceChangeListener(this);
		
		textSizePreference = (ListPreference) findPreference(getString(R.string.pref_text_size));
		textSizePreference.setSummary(textSizePreference.getEntry());
		textSizePreference.setOnPreferenceChangeListener(this);
		
		defaultLevelPreference = (ListPreference) findPreference(getString(R.string.pref_default_log_level));
		defaultLevelPreference.setOnPreferenceChangeListener(this);
		setDefaultLevelPreferenceSummary(defaultLevelPreference.getEntry());
		
		themePreference = (MockDisabledListPreference) findPreference(getString(R.string.pref_theme));
		themePreference.setOnPreferenceChangeListener(this);
		
		bufferPreference = (MultipleChoicePreference) findPreference(getString(R.string.pref_buffer));
		bufferPreference.setOnPreferenceChangeListener(this);
		setBufferPreferenceSummary(bufferPreference.getValue());
		
		boolean donateInstalled = PackageHelper.isCatlogDonateInstalled(this);
		
		String themeSummary = donateInstalled 
				? PreferenceHelper.getColorScheme(this).getDisplayableName(this)
				: getString(R.string.pref_theme_summary_free);
		
		themePreference.setSummary(themeSummary);
		themePreference.setEnabledAppearance(donateInstalled);
		if (!donateInstalled) {
			themePreference.overrideOnClick(new OnPreferenceClickListener() {
				
				@Override
				public boolean onPreferenceClick(Preference preference) {
					openDonateVersionInMarket();
					return true;
				}
			});
		}
	}
	
	private void setDefaultLevelPreferenceSummary(CharSequence entry) {
		defaultLevelPreference.setSummary(
				String.format(getString(R.string.pref_default_log_level_summary), entry));
		
	}

	private void openDonateVersionInMarket() {
		
		Intent intent = new Intent(Intent.ACTION_VIEW, 
				Uri.parse("market://details?id=com.hctrom.romcontrol.logcat.donate"));
		startActivity(intent);
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		
		if (preference.getKey().equals(getString(R.string.pref_display_limit))) {

			// display buffer preference; update summary
			
			String input = ((String)newValue).trim();

			try {
				
				int value = Integer.parseInt(input);
				if (value >= MIN_DISPLAY_LIMIT && value <= MAX_DISPLAY_LIMIT) {
					PreferenceHelper.setDisplayLimitPreference(this, value);
					displayLimitPreference.setSummary(String.format(getString(R.string.pref_display_limit_summary).toString(),
							value, getString(R.string.pref_display_limit_default)));
					
					// notify that a restart is required
					Toast.makeText(this, R.string.toast_pref_changed_restart_required, Toast.LENGTH_LONG).show();
					
					return true;
				}
				
			} catch (NumberFormatException ignore) { }
			

			String invalidEntry = String.format(getString(R.string.toast_invalid_display_limit), MIN_DISPLAY_LIMIT, MAX_DISPLAY_LIMIT);
			Toast.makeText(this, invalidEntry, Toast.LENGTH_LONG).show();
			return false;			
			
		} else if (preference.getKey().equals(getString(R.string.pref_log_line_period))) {
			
			// log line period preference; update summary
			
			String input = ((String)newValue).trim();

			try {
				
				int value = Integer.parseInt(input);
				if (value >= MIN_LOG_LINE_PERIOD && value <= MAX_LOG_LINE_PERIOD) {
					PreferenceHelper.setLogLinePeriodPreference(this, value);
					logLinePeriodPreference.setSummary(String.format(getString(R.string.pref_log_line_period_summary).toString(),
							value, getString(R.string.pref_log_line_period_default)));
					return true;
				}
				
			} catch (NumberFormatException ignore) { }
			

			Toast.makeText(this, R.string.pref_log_line_period_error, Toast.LENGTH_LONG).show();
			return false;
			
		} else if (preference.getKey().equals(getString(R.string.pref_theme))) {
			// update summary
			int index = ArrayUtil.indexOf(themePreference.getEntryValues(),newValue.toString());
			CharSequence newEntry = themePreference.getEntries()[index];
			themePreference.setSummary(newEntry);
			
			return true;		
		} else if (preference.getKey().equals(getString(R.string.pref_buffer))) {
			// buffers pref
			
			// check to make sure nothing was left unchecked
			if (TextUtils.isEmpty(newValue.toString())) {
				Toast.makeText(this, R.string.pref_buffer_none_checked_error, Toast.LENGTH_SHORT).show();
				return false;
			}
			
			// notify the LogcatActivity that the buffer has changed
			if (!newValue.toString().equals(bufferPreference.getValue())) {
				bufferChanged = true;
			}
			
			setBufferPreferenceSummary(newValue.toString());
			return true;
		} else if (preference.getKey().equals(getString(R.string.pref_default_log_level))) {
			// default log level preference
			
			// update the summary to reflect changes
			
			ListPreference listPreference = (ListPreference) preference;
			
			int index = ArrayUtil.indexOf(listPreference.getEntryValues(),newValue);
			CharSequence newEntry = listPreference.getEntries()[index];
			setDefaultLevelPreferenceSummary(newEntry);
			
			return true;
			
		} else { // text size pref
			
			// update the summary to reflect changes
			
			ListPreference listPreference = (ListPreference) preference;
			
			int index = ArrayUtil.indexOf(listPreference.getEntryValues(),newValue);
			CharSequence newEntry = listPreference.getEntries()[index];
			listPreference.setSummary(newEntry);
			
			return true;
		}
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
	    	
	    	// set result and finish
	    	Intent data = new Intent();
	    	data.putExtra("bufferChanged", bufferChanged);
	    	setResult(RESULT_OK, data);
	    	finish();
	    	return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	private void setBufferPreferenceSummary(String value) {
		
		String[] commaSeparated = StringUtil.split(StringUtil.nullToEmpty(value), MultipleChoicePreference.DELIMITER);
		
		List<CharSequence> checkedEntries = new ArrayList<CharSequence>();
		
		for (String entryValue : commaSeparated) {
			int idx = ArrayUtil.indexOf(bufferPreference.getEntryValues(),entryValue);
			checkedEntries.add(bufferPreference.getEntries()[idx]);
		}
		
		String summary = TextUtils.join(getString(R.string.delimiter), checkedEntries);
		
		// add the word "simultaneous" to make it clearer what's going on with 2+ buffers
		if (checkedEntries.size() > 1) {
			summary += getString(R.string.simultaneous);
		}
		bufferPreference.setSummary(summary);
	}
}
