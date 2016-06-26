package com.hctrom.romcontrol.licenseadapter.internal;

import android.view.View;
import android.widget.TextView;

import com.hctrom.romcontrol.R;
import com.hctrom.romcontrol.licenseadapter.LicenseEntry;

/**
 * Created by yshrsmz on 2016/04/26.
 */
public class ContentViewHolder extends LicenseViewHolder<ContentWrapper> {

  private TextView content;

  public ContentViewHolder(View itemView) {
    super(itemView);
    content = Views.byId(itemView, R.id.license);
  }

  @Override
  public void bind(ContentWrapper item) {
    LicenseEntry entry = item.entry();

    content.setText(entry.license().text);
  }
}
