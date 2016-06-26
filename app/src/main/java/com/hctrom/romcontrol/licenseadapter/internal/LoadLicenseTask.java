package com.hctrom.romcontrol.licenseadapter.internal;


import android.os.AsyncTask;

import com.hctrom.romcontrol.licenseadapter.LicenseEntry;

/**
 * Created by yshrsmz on 2016/04/20.
 */
public class LoadLicenseTask extends AsyncTask<LicenseEntry, Void, Void> {
  @Override
  protected Void doInBackground(LicenseEntry... params) {
    for (LicenseEntry entry : params) {
      entry.load();
    }
    return null;
  }
}
