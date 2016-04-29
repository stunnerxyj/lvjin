package com.lvjing.conversation.config;

import android.content.Context;

public final class AppParameterConfig {
	
	private static AppParameterConfig mInstance;
	
	public static AppParameterConfig getInstance(){
		if(mInstance == null){
			synchronized (AppParameterConfig.class) {
				if(mInstance == null){
					mInstance = new AppParameterConfig();
				}
			}
		}
		return mInstance;
	}
	
	private boolean mSavedTtsFileToSD;
	
	private AppParameterConfig(){
		mSavedTtsFileToSD = false;
	}
	
	public boolean isSavedTtsFileToSD(){
		return mSavedTtsFileToSD;
	}
	
	public void setSavedTtsFileToSD(boolean mSavedTtsFileToSD){
		this.mSavedTtsFileToSD = mSavedTtsFileToSD;
	}
	
	public void setSavedTtsFileToSD(boolean mSavedTtsFileToSD, Context mContext){
		this.mSavedTtsFileToSD = mSavedTtsFileToSD;
	}

}
