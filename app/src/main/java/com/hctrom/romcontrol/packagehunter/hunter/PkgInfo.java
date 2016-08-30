/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hctrom.romcontrol.packagehunter.hunter;

import android.os.Parcel;
import android.os.Parcelable;

public class PkgInfo implements Parcelable {
  private String appName;
  private String packageName;
  private String versionName;
  private int versionCode = 0;
  private long firstInstallTime;
  private long lastUpdateTime;

  public PkgInfo() {
    versionName = "0.0";
    versionCode = 0;
    firstInstallTime = 0;
    lastUpdateTime = 0;
  }

  protected PkgInfo(Parcel in) {
    appName = in.readString();
    packageName = in.readString();
    versionName = in.readString();
    versionCode = in.readInt();
    firstInstallTime = in.readLong();
    lastUpdateTime = in.readLong();
  }

  public static final Creator<PkgInfo> CREATOR = new Creator<PkgInfo>() {
    @Override public PkgInfo createFromParcel(Parcel in) {
      return new PkgInfo(in);
    }

    @Override public PkgInfo[] newArray(int size) {
      return new PkgInfo[size];
    }
  };

  public long getFirstInstallTime() {
    return firstInstallTime;
  }

  public void setFirstInstallTime(long firstInstallTime) {
    this.firstInstallTime = firstInstallTime;
  }

  public long getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(long lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public void setVersionCode(int versionCode) {
    this.versionCode = versionCode;
  }

  public String getVersionName() {
    return versionName;
  }

  public void setVersionName(String versionName) {
    this.versionName = versionName;
  }

  @Override public String toString() {
    super.toString();
    return "AppName : "
        + appName
        + " | PackageName :"
        + packageName
        + "\nVersion :"
        + versionName
        + " | VersionCode :"
        + versionCode;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(appName);
    parcel.writeString(packageName);
    parcel.writeString(versionName);
    parcel.writeInt(versionCode);
    parcel.writeLong(firstInstallTime);
    parcel.writeLong(lastUpdateTime);
  }
}

