package com.lvjing.conversation.config;

import com.lvjing.conversation.R;

/**
 * 语音识别模块的参数配置类
 * 
 * @author A Shuai
 *
 */
public final class SpeechRecognizeConfig {
	
	private static SpeechRecognizeConfig mInstance;
	
	public static SpeechRecognizeConfig getInstance(){
		if(mInstance == null){
			synchronized (SpeechRecognizeConfig.class) {
				if(mInstance == null){
					mInstance = new SpeechRecognizeConfig();
				}
			}
		}
		return mInstance;
	}
	
	private final int mStartSountResID;
	private final int mEndSoundResID;
	private final int mSuccessSoundResID;
	private final int mErrorSoundResID;
	private final int mCancelSoundResID;
	
	private SpeechRecognizeConfig(){
		
		mStartSountResID = R.raw.bdspeech_recognition_start;
		mEndSoundResID = R.raw.bdspeech_speech_end;
		mSuccessSoundResID = R.raw.bdspeech_recognition_success;
		mErrorSoundResID = R.raw.bdspeech_recognition_error;
		mCancelSoundResID = R.raw.bdspeech_recognition_cancel;
		
	}

	public int getStartSountResID() {
		return mStartSountResID;
	}

	public int getEndSoundResID() {
		return mEndSoundResID;
	}

	public int getSuccessSoundResID() {
		return mSuccessSoundResID;
	}

	public int getErrorSoundResID() {
		return mErrorSoundResID;
	}

	public int getCancelSoundResID() {
		return mCancelSoundResID;
	}
	
}
