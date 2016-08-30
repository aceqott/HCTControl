package com.hctrom.romcontrol.diagnostic;

/**
 * Created by Ivan on 28/11/2015.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hctrom.romcontrol.R;
import com.hctrom.romcontrol.packagehunter.DetailActivity;
import com.hctrom.romcontrol.packagehunter.RVItemClickListener;
import com.hctrom.romcontrol.packagehunter.RVMainAdapter;
import com.hctrom.romcontrol.packagehunter.hunter.PackageHunter;
import com.hctrom.romcontrol.packagehunter.hunter.PkgInfo;

import java.util.ArrayList;


public class Tab3AppInfo extends Fragment {
    private ArrayList<PkgInfo> pkgInfoArrayList;
    private RVMainAdapter adapter;

    private PackageHunter packageHunter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.appinfo_activity_main,container,false);
        setHasOptionsMenu(true);
        packageHunter = new PackageHunter(v.getContext());

        RecyclerView rv = (RecyclerView) v.findViewById(R.id.rv_pkglist);
        pkgInfoArrayList = packageHunter.getInstalledPackages();

        adapter = new RVMainAdapter(v.getContext(), pkgInfoArrayList);
        rv.hasFixedSize();
        rv.setLayoutManager(new LinearLayoutManager(v.getContext()));
        rv.setAdapter(adapter);

        // Set On Click
        rv.addOnItemTouchListener(
                new RVItemClickListener(v.getContext(), new RVItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent i = new Intent(getActivity(), DetailActivity.class);
                        i.putExtra("data", pkgInfoArrayList.get(position).getPackageName());
                        startActivity(i);
                    }
                }));

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.appinfo_menu_main, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();
                return true;
            }

            @Override public boolean onQueryTextChange(String query) {

                pkgInfoArrayList = packageHunter.searchInList(query, PackageHunter.PACKAGES);
                adapter.updateWithNewListData(pkgInfoArrayList);

                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Toast.makeText(getActivity(), "Lista Apps\nActualizada", Toast.LENGTH_SHORT).show();
                pkgInfoArrayList = packageHunter.getInstalledPackages();
                adapter.updateWithNewListData(pkgInfoArrayList);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
