package com.hctrom.romcontrol.packagehunter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hctrom.romcontrol.R;

import java.util.List;

class RVDetailsAdapter extends RecyclerView.Adapter<RVDetailsAdapter.ItemViewHolder> {

  private List<ElementInfo> dataList;

  public RVDetailsAdapter(List<ElementInfo> dataList) {
    this.dataList = dataList;
  }

  @Override public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.appinfo_rv_details_item, parent, false);

    return new ItemViewHolder(view);
  }

  @Override public void onBindViewHolder(ItemViewHolder holder, int position) {
    holder.txtvw_header.setText(dataList.get(position).getHeader());
    holder.txtvw_details.setText(dataList.get(position).toString());
  }

  @Override public int getItemCount() {
    return dataList.size();
  }

  public class ItemViewHolder extends RecyclerView.ViewHolder {
    final TextView txtvw_header;
    final TextView txtvw_details;

    public ItemViewHolder(View itemView) {
      super(itemView);
      txtvw_header = (TextView) itemView.findViewById(R.id.txtvw_header);
      txtvw_details = (TextView) itemView.findViewById(R.id.txtvw_details);
    }
  }

}
