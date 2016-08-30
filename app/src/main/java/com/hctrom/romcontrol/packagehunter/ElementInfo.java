package com.hctrom.romcontrol.packagehunter;

class ElementInfo {

  private String header;
  private String[] details;

  public ElementInfo(String header, String[] details) {
    this.details = details;
    this.header = header;
  }

  public String[] getDetails() {
    return details;
  }

  public void setDetails(String[] details) {
    this.details = details;
  }

  public String getHeader() {
    return header;
  }

  public void setHeader(String header) {
    this.header = header;
  }

  @Override public String toString() {
    if (details != null) {
      super.toString();
      StringBuilder stringBuilder = new StringBuilder();
      for (int i = 0; i < details.length; i++) {
        stringBuilder.append(details[i]).append("\n");
      }
      return stringBuilder.toString();
    } else {
      super.toString();
      return "No Contiene";
    }
  }
}
