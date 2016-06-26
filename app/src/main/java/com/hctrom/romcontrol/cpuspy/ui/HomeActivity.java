//-----------------------------------------------------------------------------
//
// (C) Brandon Valosek, 2011 <bvalosek@gmail.com>
//
//-----------------------------------------------------------------------------

package com.hctrom.romcontrol.cpuspy.ui;

// imports

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hctrom.romcontrol.R;
import com.hctrom.romcontrol.ThemeSelectorUtility;
import com.hctrom.romcontrol.cpuspy.CpuSpyApp;
import com.hctrom.romcontrol.cpuspy.CpuStateMonitor;
import com.hctrom.romcontrol.cpuspy.CpuStateMonitor.CpuState;

import java.util.ArrayList;
import java.util.List;

;

/** build_prop_main activity class */
public class HomeActivity extends AppCompatActivity
{
    private static final String TAG = "CpuSpy";
    private Toolbar toolbar;
    private CpuSpyApp _app = null;

    // the views
    private LinearLayout    _uiStatesView = null;
    private TextView        _uiAdditionalStates = null;
    private TextView        _uiTotalStateTime = null;
    private TextView        _uiHeaderAdditionalStates = null;
    private TextView        _uiHeaderTotalStateTime = null;
    private TextView        _uiStatesWarning = null;
    private TextView        _uiKernelString = null;

    /** whether or not we're updating the data in the background */
    private boolean     _updatingData = false;

    /** Initialize the Activity */
    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ThemeSelectorUtility theme = new ThemeSelectorUtility(this);
        theme.onActivityCreateSetTheme(this);
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0) == 3) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColorSamsungLight));
        }else if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0) == 0){
            getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColorHCT));
        }else{
            getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColor));
        }
        // inflate the view, stash the app build_prop_context, and get all UI elements
        setContentView(R.layout.home_layout);
        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("CPUSpy");
        }
        _app = (CpuSpyApp)getApplicationContext();
        findViews();

        // see if we're updating data during a config change (rotate screen)
        if (savedInstanceState != null) {
            _updatingData = savedInstanceState.getBoolean("updatingData");
        }
    }

    /** When the activity is about to change orientation */
    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("updatingData", _updatingData);
    }


    /** Update the view when the application regains focus */
    @Override public void onResume () {
        super.onResume();
        refreshData();
    }

    /** Map all of the UI elements to member variables */
    private void findViews() {
        _uiStatesView = (LinearLayout)findViewById(R.id.ui_states_view);
        _uiKernelString = (TextView)findViewById(R.id.ui_kernel_string);
        _uiAdditionalStates = (TextView)findViewById(
                R.id.ui_additional_states);
        _uiHeaderAdditionalStates = (TextView)findViewById(
                R.id.ui_header_additional_states);
        _uiHeaderTotalStateTime = (TextView)findViewById(
                R.id.ui_header_total_state_time);
        _uiStatesWarning = (TextView)findViewById(R.id.ui_states_warning);
        _uiTotalStateTime = (TextView)findViewById(R.id.ui_total_state_time);
    }

    /** called when we want to infalte the menu */
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // request inflater from activity and inflate into its menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);

        // made it
        return true;
    }

    /** called to handle a menu event */
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // what it do mayne
        switch (item.getItemId()) {
            /* pressed the load menu button */
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.menu_refresh:
                refreshData();
                break;
            case R.id.menu_reset:
                try {
                    _app.getCpuStateMonitor().setOffsets();
                } catch (CpuStateMonitor.CpuStateMonitorException e) {
                    // TODO: something
                }

                _app.saveOffsets();
                updateView();
                break;
            case R.id.menu_restore:
                _app.getCpuStateMonitor().removeOffsets();
                _app.saveOffsets();
                updateView();
                break;
        }

        // made it
        return true;
    }

    /** Generate and update all UI elements */
    public void updateView() {
        /** Get the CpuStateMonitor from the app, and iterate over all states,
         * creating a row if the duration is > 0 or otherwise marking it in
         * extraStates (missing) */
        CpuStateMonitor monitor = _app.getCpuStateMonitor();
        _uiStatesView.removeAllViews();
        List<String> extraStates = new ArrayList<String>();
        for (CpuStateMonitor.CpuState state : monitor.getStates()) {
            if (state.duration > 0) {
                generateStateRow(state, _uiStatesView);
            } else {
                if (state.freq == 0) {
                    extraStates.add("Deep Sleep");
                } else {
                    extraStates.add(state.freq/1000 + " MHz");
                }
            }
        }

        // show the red warning label if no states found
        if ( monitor.getStates().size() == 0) {
            _uiStatesWarning.setVisibility(View.VISIBLE);
            _uiHeaderTotalStateTime.setVisibility(View.GONE);
            _uiTotalStateTime.setVisibility(View.GONE);
            _uiStatesView.setVisibility(View.GONE);
        }

        // update the total state time
        long totTime = monitor.getTotalStateTime() / 100;
        _uiTotalStateTime.setText(sToString(totTime));

        // for all the 0 duration states, add the the Unused State area
        if (extraStates.size() > 0) {
            int n = 0;
            String str = "";

            for (String s : extraStates) {
                if (n++ > 0)
                    str += ", ";
                str += s;
            }

            _uiAdditionalStates.setVisibility(View.VISIBLE);
            _uiHeaderAdditionalStates.setVisibility(View.VISIBLE);
            _uiAdditionalStates.setText(str);
        } else {
            _uiAdditionalStates.setVisibility(View.GONE);
            _uiHeaderAdditionalStates.setVisibility(View.GONE);
        }

        // kernel line
        _uiKernelString.setText(_app.getKernelVersion());
    }

    /** Attempt to update the time-in-state info */
    public void refreshData() {
        if (!_updatingData) {
            new RefreshStateDataTask().execute((Void)null);
        }
    }

    /** @return A nicely formatted String representing tSec seconds */
    private static String sToString(long tSec) {
        long h = (long)Math.floor(tSec / (60*60));
        long m = (long)Math.floor((tSec - h*60*60) / 60);
        long s = tSec % 60;
        String sDur;
        sDur = h + ":";
        if (m < 10)
            sDur += "0";
        sDur += m + ":";
        if (s < 10)
            sDur += "0";
        sDur += s;

        return sDur;
    }

    /**
     * @return a View that correpsonds to a CPU freq state row as specified
     * by the state parameter
     */
    private View generateStateRow(CpuState state, ViewGroup parent) {
        // inflate the XML into a view in the parent
        LayoutInflater inf = LayoutInflater.from((Context)_app);
        LinearLayout theRow = (LinearLayout)inf.inflate(R.layout.state_row, parent, false);

        // what percetnage we've got
        CpuStateMonitor monitor = _app.getCpuStateMonitor();
        float per = (float)state.duration * 100 /
            monitor.getTotalStateTime();
        String sPer = (int)per + "%";

        // state name
        String sFreq;
        if (state.freq == 0) {
            sFreq = "Deep Sleep";
        } else {
            sFreq = state.freq / 1000 + " MHz";
        }

        // duration
        long tSec = state.duration / 100;
        String sDur = sToString(tSec);

        // map UI elements to objects
        TextView freqText = (TextView)theRow.findViewById(R.id.ui_freq_text);
        TextView durText = (TextView)theRow.findViewById(
                R.id.ui_duration_text);
        TextView perText = (TextView)theRow.findViewById(
                R.id.ui_percentage_text);
        ProgressBar bar = (ProgressBar)theRow.findViewById(R.id.ui_bar);
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0) == 3) {
            bar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.myAccentColorSamsungLight)));
        }else if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0) == 0){
            bar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.myAccentColorHCT)));
        }else{
            bar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.myAccentColor)));
        }

        // modify the row
        freqText.setText(sFreq);
        perText.setText(sPer);
        durText.setText(sDur);
        bar.setProgress((int)per);

        // add it to parent and return
        parent.addView(theRow);
        return theRow;
    }

    /** Keep updating the state data off the UI thread for slow devices */
    protected class RefreshStateDataTask extends AsyncTask<Void, Void, Void> {

        /** Stuff to do on a seperate thread */
        @Override protected Void doInBackground(Void... v) {
            CpuStateMonitor monitor = _app.getCpuStateMonitor();
            try {
                monitor.updateStates();
            } catch (CpuStateMonitor.CpuStateMonitorException e) {
                Log.e(TAG, "Problema obteniendo estado de la CPU");
            }

            return null;
        }

        /** Executed on the UI thread right before starting the task */
        @Override protected void onPreExecute() {
            log("Actualizando datos");
            _updatingData = true;
        }

        /** Executed on UI thread after task */
        @Override protected void onPostExecute(Void v) {
            log("Actualización de datos terminada");
            _updatingData = false;
            updateView();
        }
    }

    /** logging */
    private void log(String s) {
        Log.d(TAG, s);
    }
}
