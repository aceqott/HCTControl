package com.hctrom.romcontrol.diagnostic;

/**
 * Created by Ivan on 28/11/2015.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class DiagnosticViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapterADSE is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapterADSE is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public DiagnosticViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                Tab1CpuSpy tab1 = new Tab1CpuSpy();
                return tab1;
            case 1:
                Tab2Battery tab2 = new Tab2Battery();
                return tab2;
            case 2:
                Tab3AppInfo tab3 = new Tab3AppInfo();
                return tab3;
        }
        return getItem(position);
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
