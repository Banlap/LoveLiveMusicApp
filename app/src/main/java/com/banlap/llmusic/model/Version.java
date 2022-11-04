package com.banlap.llmusic.model;

import java.util.Date;

public class Version {
    public int versionId;
    public String versionName;
    public String versionCode;
    public String versionType;
    public String versionTitle;
    public String versionContent;
    public String versionUrl;
    public Date versionDate;

    public int getVersionId() { return versionId; }

    public void setVersionId(int versionId) { this.versionId = versionId; }

    public String getVersionName() { return versionName; }

    public void setVersionName(String versionName) { this.versionName = versionName; }

    public String getVersionCode() { return versionCode; }

    public void setVersionCode(String versionCode) { this.versionCode = versionCode; }

    public String getVersionType() { return versionType; }

    public void setVersionType(String versionType) { this.versionType = versionType; }

    public String getVersionTitle() { return versionTitle; }

    public void setVersionTitle(String versionTitle) { this.versionTitle = versionTitle; }

    public String getVersionContent() { return versionContent; }

    public void setVersionContent(String versionContent) { this.versionContent = versionContent; }

    public String getVersionUrl() { return versionUrl; }

    public void setVersionUrl(String versionUrl) { this.versionUrl = versionUrl; }

    public Date getVersionDate() { return versionDate; }

    public void setVersionDate(Date versionDate) { this.versionDate = versionDate; }
}
