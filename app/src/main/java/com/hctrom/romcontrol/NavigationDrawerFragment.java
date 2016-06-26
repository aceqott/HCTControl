package com.hctrom.romcontrol;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hctrom.romcontrol.prefs.ThemeSwitch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment implements NavigationDrawerCallbacks {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private View mFragmentContainerView;
    private View view;
    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private TypedValue typedValue;
    private TextView editTextNombre;
    CircleImageView circleImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        editTextNombre = ( TextView) view.findViewById(R.id.editTextPersonalizado);
        circleImageView = (CircleImageView) view.findViewById(R.id.profile_image);
        final File file = new File(getActivity().getFilesDir().getPath() + "/imagen_perfil_hctcontrol.jpg");
        SharedPreferences prefs = getActivity().getSharedPreferences("DatosLogin", Context.MODE_PRIVATE);
        if (file.exists()){
            if (prefs.getBoolean("check_image_profile", true)){
                Bitmap bitmap = null;

                try{
                    FileInputStream fileInputStream = new FileInputStream(getActivity().getFilesDir().getPath()+ "/imagen_perfil_hctcontrol.jpg");
                    bitmap = BitmapFactory.decodeStream(fileInputStream);
                }catch (IOException io){
                    io.printStackTrace();
                }
                circleImageView.setImageBitmap(bitmap);
            }else{
                circleImageView.setImageResource(R.drawable.profile);
            }
        }else{
            circleImageView.setImageResource(R.drawable.profile);
        }
        String editText = "" + prefs.getString("check_text_profile", "");
        if (!editText.contentEquals("")){
            editTextNombre.setText(editText);
        }

        circleImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        circleImageView.setBorderWidth(14);
                        circleImageView.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        circleImageView.setBorderWidth(4);
                        circleImageView.invalidate();
                        break;
                    }
                }
                return false;
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ImagenPerfil.class));
            }
        });

        mDrawerList = (RecyclerView) view.findViewById(R.id.drawerList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDrawerList.setLayoutManager(layoutManager);
        mDrawerList.setHasFixedSize(true);
        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(getMenu(), getActivity());
        adapter.setNavigationDrawerCallbacks(this);
        mDrawerList.setAdapter(adapter);
        selectItem(mCurrentSelectedPosition);
        return view;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mActionBarDrawerToggle;
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        selectItem(position);
    }

    /*String array of item names is located in strings.xml under name nav_drawer_items
        * If you wish to add more items you need to:
        * 1. Add item to nav_drawer_items array
        * 2. Add a valid material design icon/image to dir drawable
        * 3. Add that image ID to the integer array below (int[] mIcons
        * 4. The POSITION of your new item in the string array MUST CORRESPOND to the position of your image in the integer array mIcons
        * 5. Create new PreferenceFragment or your own fragment or a method that you would like to invoke when a user clicks on your new item
        * 6. Continue to MainViewActivity (if you haven't done the changes there already) to a method onNavigationDrawerItemSelected(int position) - method after getMenu()
        * 7. Add an action based on position. Remember that positions in array are beginning at 0. So if your item is number 6 in array, it will have a position of 5... etc
        * You need to add the same images to int array in MainViewActivity, which has the same method*/
    public List<NavItem> getMenu() {
        List<NavItem> items = new ArrayList<>();
        String[] mTitles = getResources().getStringArray(R.array.nav_drawer_items);
        int[] mIcons = {R.drawable.ic_ui_mods,
                R.drawable.ic_notificaciones,
                R.drawable.ic_bloqueo,
                R.drawable.ic_sound_notifications,
                R.drawable.ic_phone_mods,
                R.drawable.ic_general_framework,
                R.drawable.ic_apps,
                R.drawable.ic_settings,
                R.drawable.ic_backup_settings,
                R.drawable.ic_menu,
                R.drawable.ic_acerca_de};
        for (int i=0; i<mTitles.length && i<mIcons.length; i++){
            NavItem current = new NavItem();
            current.setText(mTitles[i]);
            current.setDrawable(mIcons[i]);
            items.add(current);
        }

        return items;
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     * @param toolbar      The Toolbar of the activity.
     */
    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar, Context c) {
        mFragmentContainerView = (View) getActivity().findViewById(fragmentId).getParent();
        mDrawerLayout = drawerLayout;
        typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);

        mDrawerLayout.setStatusBarBackgroundColor(typedValue.data);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                ThemeSwitch.getIconsColor(getActivity());
                if (!isAdded()) return;

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
        ((NavigationDrawerAdapter) mDrawerList.getAdapter()).selectPosition(position);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

}
