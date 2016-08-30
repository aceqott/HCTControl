package com.hctrom.romcontrol.packagehunter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hctrom.romcontrol.R;
import com.hctrom.romcontrol.packagehunter.hunter.PackageHunter;
import com.hctrom.romcontrol.packagehunter.hunter.PkgInfo;

import java.util.ArrayList;
import java.util.List;

public class RVMainAdapter extends RecyclerView.Adapter<RVMainAdapter.ItemViewHolder> {

  private List<PkgInfo> dataList;
  private final PackageHunter packageHunter;

  public RVMainAdapter(Context context, ArrayList<PkgInfo> dataList) {
    packageHunter = new PackageHunter(context);
    this.dataList = dataList;
  }

  @Override public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.appinfo_rv_main_item, parent, false);

    return new ItemViewHolder(view);
  }

  @Override public void onBindViewHolder(ItemViewHolder holder, int position) {
    holder.txt_appname.setText(dataList.get(position).getAppName());
    holder.txt_pkgname.setText(dataList.get(position).getPackageName());

    Drawable icon = packageHunter.getIconForPkg(dataList.get(position).getPackageName());
    holder.icon.setImageDrawable(icon);
  }

  @Override public int getItemCount() {
    return dataList.size();
  }

  public class ItemViewHolder extends RecyclerView.ViewHolder {
    final TextView txt_appname;
    final TextView txt_pkgname;
    final ImageView icon;

    public ItemViewHolder(View itemView) {
      super(itemView);
      txt_appname = (TextView) itemView.findViewById(R.id.txtvw_appname);
      txt_pkgname = (TextView) itemView.findViewById(R.id.txtvw_pkgname);
      icon = (ImageView) itemView.findViewById(R.id.imgvw_icn);
    }
  }

  public void updateWithNewListData(List<PkgInfo> newDataList) {
    dataList.clear();
    dataList = null;
    dataList = newDataList;
    notifyDataSetChanged();
  }
}
