package com.hctrom.romcontrol.dpichanger;


/*
 * Copyright (c) 2014-15 Anna Berkovitch & Roberto Mariani
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;

import com.stericson.RootTools.RootTools;
import com.hctrom.romcontrol.R;


public class DPIActivity extends AppCompatActivity {
    ProgressDialog mProgressDialog;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
/*
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dpi_custom);
        getWindow().setStatusBarColor(getResources().getColor(R.color.statusbar));

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("DPI Charger");
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  finish();
              }
        });
*/
        CheckSu checkit = new CheckSu();
        checkit.execute();
    }


    // Async task to execute SU in the background
    public class CheckSu extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(DPIActivity.this);
            mProgressDialog.setMessage(getString(R.string.gaining_root));
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (RootTools.isAccessGiven()) {


                return null;

            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mProgressDialog.dismiss();
            SetDpiFragment mPresetDPI = new SetDpiFragment();
            FragmentTransaction mPresetDialogTransaction = getFragmentManager().beginTransaction();
            mPresetDialogTransaction.add(mPresetDPI, "dpichange");
            if (!RootTools.isAccessGiven()) {
                //If no su access detected, detach First dialog and throw and alert dialog with single button that will finish the sctivity
                mPresetDialogTransaction.detach(mPresetDPI);
                AlertDialog.Builder mNoSuBuilder = new AlertDialog.Builder(DPIActivity.this);
                mNoSuBuilder.setTitle(R.string.missing_su_title);
                mNoSuBuilder.setMessage(R.string.missing_su);
                mNoSuBuilder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                mNoSuBuilder.create();
                Dialog mNoSu = mNoSuBuilder.create();
                mNoSu.show();
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = getApplicationContext().getTheme();
                theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
                mNoSu.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
            } else {
                //if su access is given, commit the transaction and start dialog sequence. Allowing state loss to escape illegalstateexcep/**/tion
                mPresetDialogTransaction.commitAllowingStateLoss();
            }


        }
    }
}