package com.hctrom.romcontrol.prefs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.hctrom.romcontrol.HCTControl;
import com.hctrom.romcontrol.R;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import static com.hctrom.romcontrol.HCTControl.getContext;

/*      Created by Roberto Mariani and Anna Berkovitch, 13/06/2016
        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

@SuppressWarnings({"ResultOfMethodCallIgnored", "unused"})
public class Utils {

    static final String SCRIPTS_FOLDER = "scripts";
    public static final String FILES_SCRIPTS_FOLDER_PATH = HCTControl.getContext().getFilesDir().getPath() + File.separator + SCRIPTS_FOLDER;
    public static final String PREF_NAME_KEY = "pref_key";
    private static final String LOG_TAG = "RomControlUtils";


    static void copyAssetFolder() {


        try {
            String[] scriptsInAssets = getContext().getAssets().list(SCRIPTS_FOLDER);
            Log.d(LOG_TAG, "copyAssetFolder " + scriptsInAssets[0]);
            File scriptsFilesDir = new File(FILES_SCRIPTS_FOLDER_PATH);
            //Checking if the "scripts" directory exists in files
            if (!scriptsFilesDir.exists()) {
                new File(FILES_SCRIPTS_FOLDER_PATH).mkdirs();
            }
            for (String file : scriptsInAssets) {
                //If the file name contains  a dot, it's most probably a single file and not dir. So treating it as copying file
                Log.d(LOG_TAG, "copyAssetFolder " + file);
                if (file.contains(".")) {
                    copyAsset(scriptsInAssets, SCRIPTS_FOLDER + File.separator + file, FILES_SCRIPTS_FOLDER_PATH + File.separator + file);
                } else {
                    //Otherwise treating as copying dir
                    copyAssetFolder();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void copyAsset(String[] scriptsInAssets, String from, String to) {
        boolean isCopied = false;
        InputStream in;
        OutputStream out;
        ArrayList<File> scriptsFiles = new ArrayList<>();
        //Creating list of File objects inside assets
        for (String scriptsInAsset : scriptsInAssets) {
            File f = new File(FILES_SCRIPTS_FOLDER_PATH + File.separator + scriptsInAsset);
            scriptsFiles.add(f);
        }
        for (int j = 0; j < scriptsFiles.size(); j++) {
            //If the file doesn't exist in files dir, we copy it
            if (!scriptsFiles.get(j).exists()) {
                try {
                    in = getContext().getAssets().open(from);
                    new File(to).createNewFile();
                    out = new FileOutputStream(to);
                    copyFile(in, out);
                    in.close();
                    out.flush();
                    out.close();
                    isCopied = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    isCopied = false;
                }
            }
        }
        //If the file was just copied, we make it executable
        if (isCopied) {

            try {
                Command c = new Command(0, "chmod -R 755 " + FILES_SCRIPTS_FOLDER_PATH);
                RootTools.getShell(false).add(c);

            } catch (IOException | RootDeniedException | TimeoutException e) {
                e.printStackTrace();
            }
        }

    }

    //Actual copying of the file
    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static Drawable getIconDrawable(Uri uri) {
        Drawable drawable = null;
        if (uri != null) {
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 5, bitmap.getHeight() / 5, false);
                drawable = new BitmapDrawable(getContext().getResources(), scaledBitmap);
                bitmap.recycle();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return drawable;
    }

    public static void showKillPackageDialog(final String packageName, Context context) {
        try {
            ApplicationInfo applicationInfo = getContext().getPackageManager().getApplicationInfo(packageName, 0);
            String appLabel = applicationInfo.loadLabel(getContext().getPackageManager()).toString();
            Drawable appIcon = applicationInfo.loadIcon(getContext().getPackageManager());
            new AlertDialog.Builder(context)
                    .setTitle(R.string.app_reboot_required_dialog_title)
                    .setMessage(String.format(Locale.getDefault(), getContext().getString(R.string.app_reboot_required_dialog_message), appLabel))
                    .setIcon(appIcon)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            killPackage(packageName);
                        }
                    })
                    .create().show();
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(getContext(), "App not installed", Toast.LENGTH_SHORT).show();
        }

    }

    public static void showRebootRequiredDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.reboot_required_dialog_title)
                .setMessage(R.string.reboot_required_dialog_message)
                .setNegativeButton(R.string.reboot_later_negative_button, null)
                .setPositiveButton(R.string.reboot_now_dialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                        powerManager.reboot(null);
                    }
                })
                .create()
                .show();
    }


    public static boolean isPackageInstalled(String packageName) {
        try {
            getContext().getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void killPackage(String packageNameToKill) {
        Command command = new Command(0, "pkill " + packageNameToKill);
        try {
            RootTools.getShell(true).add(command);
        } catch (IOException | TimeoutException | RootDeniedException e) {
            e.printStackTrace();
        }
    }
}
