package com.hctrom.romcontrol.prefs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

import com.hctrom.romcontrol.HCTControl;
import com.hctrom.romcontrol.R;

/*      Created by Roberto Mariani and Anna Berkovitch, 2015-2016
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

public class OpenAppPreference extends Preference {
    private ResolveInfo mResolveInfo;
    private Context mContext;
    private Intent mIntent;


    public OpenAppPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = HCTControl.getContext();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.OpenAppPreference);
        String componentName = typedArray.getString(R.styleable.OpenAppPreference_componentName);
        initPreference(componentName);
        typedArray.recycle();
    }

    private void initPreference(String componentName) {
        String[] components = componentName.split("/");
        mIntent = new Intent();
        ComponentName component = new ComponentName(components[0], components[1]);
        mIntent.setComponent(component);
        PackageManager packageManager = mContext.getPackageManager();
        mResolveInfo = packageManager.resolveActivity(mIntent, 0);

    }


    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        if (mResolveInfo != null) {
            Drawable icon = mResolveInfo.activityInfo.loadIcon(mContext.getPackageManager());

            if (getIcon() == null) {
                setIcon(icon);
            }
            if (getTitle() == null) {
                setTitle(mResolveInfo.activityInfo.loadLabel(mContext.getPackageManager()));
            }
        }
    }



    @Override
    protected void onClick() {
        super.onClick();
        if (mResolveInfo != null) {
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(mIntent);
        }

    }

    public boolean isInstalled() {
        return mResolveInfo != null;
    }
}
