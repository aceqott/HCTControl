package com.hctrom.romcontrol.licenseadapter.internal;


import com.hctrom.romcontrol.licenseadapter.LicenseEntry;

/**
 * Created by yshrsmz on 2016/04/26.
 */
public interface Wrapper {
  ViewType type();

  LicenseEntry entry();

  boolean isExpanded();

  void setExpanded(boolean expand);
}
