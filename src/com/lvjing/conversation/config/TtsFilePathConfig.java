package com.lvjing.conversation.config;

import java.io.File;

import com.lvjing.conversation.Constant;

import android.text.TextUtils;

public final class TtsFilePathConfig {
	
	private static TtsFilePathConfig mInstance;
	
	public static TtsFilePathConfig getInstance(){
		if(mInstance == null){
			synchronized (TtsFilePathConfig.class) {
				if(mInstance == null){
					mInstance = new TtsFilePathConfig();
				}
			}
		}
		return mInstance;
	}
	
	private String mRootDir;
	
	private TtsFilePathConfig(){
		mRootDir = null;
	}
	
	private final void checkLegal(){
		if(TextUtils.isEmpty(mRootDir)){
			throw new IllegalStateException("Please invoke setRootDir function first.");
		}
	}
	
	public synchronized void setRootDir(String mRootDir){
		this.mRootDir = mRootDir;
	}
	
	public String getSpeechFemalePath(){
		checkLegal();
		return mRootDir + File.separator + Constant.SPEECH_FEMALE_MODEL_NAME;
	}
	
	public String getSpeechMalePath(){
		checkLegal();
		return mRootDir + File.separator + Constant.SPEECH_MALE_MODEL_NAME;
	}
	
	public String getTextModelPath(){
		checkLegal();
		return mRootDir + File.separator + Constant.TEXT_MODEL_NAME;
	}
	
	public String getLicenseFilePath(){
		checkLegal();
		return mRootDir + File.separator + Constant.LICENSE_FILE_NAME;
	}
	
	public String getEnSpeechFemalePath(){
		checkLegal();
		return mRootDir + File.separator + Constant.ENGLISH_SPEECH_FEMALE_MODEL_NAME;
	}
	
	public String getEnSpeechMalePath(){
		checkLegal();
		return mRootDir + File.separator + Constant.ENGLISH_SPEECH_MALE_MODEL_NAME;
	}
	
	public String getEnTextModelPath(){
		checkLegal();
		return mRootDir + File.separator + Constant.ENGLISH_TEXT_MODEL_NAME;
	}

}
