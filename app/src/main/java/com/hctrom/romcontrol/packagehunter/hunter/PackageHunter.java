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

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class PackageHunter {

  private static final String TAG = "PackageHunter";
  private final PackageManager packageManager;

  private final Context context;

  // Flags
  public static final int APPLICATIONS = 0;
  public static final int PACKAGES = 1;
  public static final int PERMISSIONS = 2;
  public static final int SERVICES = 3;
  public static final int RECEIVERS = 4;
  public static final int ACTIVITIES = 5;
  public static final int PROVIDERS = 6;

  public PackageHunter(Context context) {
    packageManager = context.getPackageManager();
    this.context = context;
  }

  public ArrayList<PkgInfo> searchInList(String query, int flag) {
    String query_lowercase = query.toLowerCase();
    ArrayList<PkgInfo> pkgInfoArrayList = new ArrayList<>();
    ArrayList<PkgInfo> installed_packages_list = getAllPackagesInfo(flag);

    for (int i = 0; i < installed_packages_list.size(); i++) {
      PkgInfo pkgInfo = installed_packages_list.get(i);

      switch (flag) {
        case APPLICATIONS:
          String appname = pkgInfo.getAppName();
          if (appname != null && appname.toLowerCase().contains(query_lowercase)) {
            pkgInfoArrayList.add(pkgInfo);
          }
          break;
        case PACKAGES:
          String packagename = pkgInfo.getPackageName();
          if (packagename != null && packagename.toLowerCase().contains(query_lowercase)) {
            pkgInfoArrayList.add(pkgInfo);
          }
          break;
        case PERMISSIONS: {
          String[] permissions = getPermissionForPkg(pkgInfo.getPackageName());
          if (permissions != null) {
            for (int j = 0; j < permissions.length; j++) {
              if (permissions[j].toLowerCase().contains(query_lowercase)) {
                pkgInfoArrayList.add(pkgInfo);
                break;
              }
            }
          }
          break;
        }
        case SERVICES: {
          String[] services = getServicesForPkg(pkgInfo.getPackageName());
          if (services != null) {
            for (int j = 0; j < services.length; j++) {
              if (services[j].toLowerCase().contains(query_lowercase)) {
                pkgInfoArrayList.add(pkgInfo);
                break;
              }
            }
          }
          break;
        }
        case RECEIVERS: {
          String[] recievers = getReceiverForPkg(pkgInfo.getPackageName());
          if (recievers != null) {
            for (int j = 0; j < recievers.length; j++) {
              if (recievers[j].toLowerCase().contains(query_lowercase)) {
                pkgInfoArrayList.add(pkgInfo);
                break;
              }
            }
          }
          break;
        }
        case ACTIVITIES: {
          String[] activities = getActivitiesForPkg(pkgInfo.getPackageName());
          if (activities != null) {
            for (int j = 0; j < activities.length; j++) {
              if (activities[j].toLowerCase().contains(query_lowercase)) {
                pkgInfoArrayList.add(pkgInfo);
                break;
              }
            }
          }
          break;
        }
        case PROVIDERS: {
          String[] providers = getProvidersForPkg(pkgInfo.getPackageName());
          if (providers != null) {
            for (int j = 0; j < providers.length; j++) {
              if (providers[j].toLowerCase().contains(query_lowercase)) {
                pkgInfoArrayList.add(pkgInfo);
                break;
              }
            }
          }
          break;
        }
        default: {
          String packagename1 = pkgInfo.getPackageName();
          if (packagename1 != null && packagename1.toLowerCase().contains(query_lowercase)) {
            pkgInfoArrayList.add(pkgInfo);
          }
          break;
        }
      }
    }

    return pkgInfoArrayList;
  }

  public ArrayList<PkgInfo> getInstalledPackages() {
    return getAllPackagesInfo(PACKAGES);
  }

  private ArrayList<PkgInfo> getAllPackagesInfo(int flag) {
    ArrayList<PkgInfo> pkgInfoArrayList = new ArrayList<>();

    List<PackageInfo> installed_packages_list;
    switch (flag) {
      case PACKAGES:
        installed_packages_list = packageManager.getInstalledPackages(0);
        break;
      case PERMISSIONS:
        installed_packages_list =
            packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        break;
      case SERVICES:
        installed_packages_list = packageManager.getInstalledPackages(PackageManager.GET_SERVICES);
        break;
      case RECEIVERS:
        installed_packages_list = packageManager.getInstalledPackages(PackageManager.GET_RECEIVERS);
        break;
      case ACTIVITIES:
        installed_packages_list =
            packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        break;
      case PROVIDERS:
        installed_packages_list = packageManager.getInstalledPackages(PackageManager.GET_PROVIDERS);
        break;
      default:
        installed_packages_list = packageManager.getInstalledPackages(0);
        break;
    }

    //get a list of installed packages.
    for (int i = 0; i < installed_packages_list.size(); i++) {
      PackageInfo packageInfo = installed_packages_list.get(i);
      if (!packageInfo.packageName.contains("com.android.")) {
        pkgInfoArrayList.add(getPkgInfoModel(packageInfo, flag));
      }
    }
    return pkgInfoArrayList;
  }

  public String getAppNameForPkg(String packageName) {
    PackageInfo packageInfo = getPkgInfo(packageName, 0);
    return packageInfo != null ? packageInfo.applicationInfo.loadLabel(packageManager).toString()
        : null;
  }

  public String getVersionForPkg(String packageName) {
    PackageInfo packageInfo = getPkgInfo(packageName, 0);
    return packageInfo != null ? packageInfo.versionName : null;
  }

  public String getVersionCodeForPkg(String packageName) {
    PackageInfo packageInfo = getPkgInfo(packageName, 0);
    return String.valueOf(packageInfo.versionCode);
  }

  public long getFirstInstallTimeForPkg(String packageName) {
    PackageInfo packageInfo = getPkgInfo(packageName, 0);
    return packageInfo != null ? packageInfo.firstInstallTime : 0;
  }

  public long getLastUpdatedTimeForPkg(String packageName) {
    PackageInfo packageInfo = getPkgInfo(packageName, 0);
    return packageInfo != null ? packageInfo.lastUpdateTime : 0;
  }

  public Drawable getIconForPkg(String packageName) {
    Drawable icon;
    try {
      icon = packageManager.getApplicationIcon(packageName);
    } catch (PackageManager.NameNotFoundException ex) {
      Log.e(TAG, "Error", ex);

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        icon =
            context.getResources().getDrawable(android.R.drawable.ic_menu_help, context.getTheme());
      } else {
        //noinspection deprecation
        icon = context.getResources().getDrawable(android.R.drawable.ic_menu_help);
      }
    }

    return icon;
  }

  public String[] getPermissionForPkg(String packageName) {
    PackageInfo packageInfo = getPkgInfo(packageName, PackageManager.GET_PERMISSIONS);
    if (packageInfo.requestedPermissions != null) {
      return packageInfo.requestedPermissions;
    } else {
      return null;
    }
  }

  public String[] getServicesForPkg(String packageName) {
    PackageInfo packageInfo = getPkgInfo(packageName, PackageManager.GET_SERVICES);
    if (packageInfo.services != null) {
      ArrayList<String> data = new ArrayList<>(packageInfo.services.length);
      for (int i = 0; i < packageInfo.services.length; i++) {
        data.add(packageInfo.services[i].name);
      }
      return data.toArray(new String[data.size()]);
    } else {
      return null;
    }
  }

  public String[] getActivitiesForPkg(String packageName) {
    PackageInfo packageInfo = getPkgInfo(packageName, PackageManager.GET_ACTIVITIES);
    if (packageInfo.activities != null) {
      ArrayList<String> data = new ArrayList<>(packageInfo.activities.length);
      for (int i = 0; i < packageInfo.activities.length; i++) {
        data.add(packageInfo.activities[i].name);
      }
      return data.toArray(new String[data.size()]);
    } else {
      return null;
    }
  }

  public String[] getProvidersForPkg(String packageName) {
    PackageInfo packageInfo = getPkgInfo(packageName, PackageManager.GET_PROVIDERS);
    if (packageInfo.providers != null) {
      ArrayList<String> data = new ArrayList<>(packageInfo.providers.length);
      for (int i = 0; i < packageInfo.providers.length; i++) {
        data.add(packageInfo.providers[i].name);
      }
      return data.toArray(new String[data.size()]);
    } else {
      return null;
    }
  }

  public String[] getReceiverForPkg(String packageName) {
    PackageInfo packageInfo = getPkgInfo(packageName, PackageManager.GET_RECEIVERS);
    if (packageInfo.receivers != null) {
      ArrayList<String> data = new ArrayList<>(packageInfo.receivers.length);
      for (int i = 0; i < packageInfo.receivers.length; i++) {
        data.add(packageInfo.receivers[i].name);
      }
      return data.toArray(new String[data.size()]);
    } else {
      return null;
    }
  }

  private PackageInfo getPkgInfo(String packageName, int flag) {
    try {
      return packageManager.getPackageInfo(packageName, flag);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  private PkgInfo getPkgInfoModel(PackageInfo p, int flag) {
    // Always available
    PkgInfo newInfo = new PkgInfo();
    if (p != null) {
      newInfo.setAppName(p.applicationInfo.loadLabel(packageManager).toString());
      newInfo.setPackageName(p.packageName);
      newInfo.setVersionCode(p.versionCode);
      newInfo.setVersionName(p.versionName);
      newInfo.setLastUpdateTime(p.lastUpdateTime);
      newInfo.setFirstInstallTime(p.firstInstallTime);
    }

    return newInfo;
  }
}