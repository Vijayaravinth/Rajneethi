package com.software.cb.rajneethi.models;

/**
 * Created by DELL on 04-04-2018.
 */

public class QCAudioDetails {

    private String audioFileName, isVerified;
    private boolean isDownloaded, isPlaying, isDownloading, isPaused;
    private int progress = 0;

    public QCAudioDetails(String audioFileName, String isVerified, boolean isDownloaded, boolean isPlaying, boolean isDownloading, boolean isPaused, int progress) {
        this.audioFileName = audioFileName;
        this.isVerified = isVerified;
        this.isDownloaded = isDownloaded;
        this.isPlaying = isPlaying;
        this.isDownloading = isDownloading;
        this.isPaused = isPaused;
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getAudioFileName() {
        return audioFileName;
    }

    public void setAudioFileName(String audioFileName) {
        this.audioFileName = audioFileName;
    }

    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }
}
