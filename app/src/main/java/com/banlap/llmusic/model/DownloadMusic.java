package com.banlap.llmusic.model;

public class DownloadMusic {

    public String downloadId;
    public String fileName;
    public String url;
    /**
     * 0=成功 1=正在下载 2=失败 3=等待
     * */
    public String status;

    /**
     * 下载成功
     * */
    public static final String DownloadSuccess = "0";
    /**
     * 正在下载
     * */
    public static final String Downloading = "1";
    /**
     * 下载失败
     * */
    public static final String DownloadError = "2";
    /**
     * 等待下载
     * */
    public static final String DownloadWaiting = "3";

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
