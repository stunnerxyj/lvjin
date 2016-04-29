package com.lvjing.conversation.asyntask;

import java.io.File;

import android.content.Context;

import com.lvjing.conversation.config.TtsFilePathConfig;
import com.lvjing.conversation.util.ExternalFileUtil;

import static com.lvjing.conversation.Constant.SPEECH_FEMALE_MODEL_NAME;
import static com.lvjing.conversation.Constant.SPEECH_MALE_MODEL_NAME;
import static com.lvjing.conversation.Constant.TEXT_MODEL_NAME;
import static com.lvjing.conversation.Constant.LICENSE_FILE_NAME;
import static com.lvjing.conversation.Constant.ENGLISH_DIRECTORY_NAME;
import static com.lvjing.conversation.Constant.ENGLISH_SPEECH_FEMALE_MODEL_NAME;
import static com.lvjing.conversation.Constant.ENGLISH_SPEECH_MALE_MODEL_NAME;
import static com.lvjing.conversation.Constant.ENGLISH_TEXT_MODEL_NAME;

public final class CopyTtsFileToSDTask extends AbsAsynTask<String>{

	private String mResult;
	private String mError;
	
	public CopyTtsFileToSDTask(OnAsynTaskProcessListener<String> mListener, Context mContext) {
		super(mListener, mContext);
	}

	@Override
	protected boolean process() {
		
		Context mContext = getContext();
		if(mContext == null){
			return false;
		}
		String mRootDir = ExternalFileUtil.getCacheDir(mContext);
		if(!new File(mRootDir).exists() && !ExternalFileUtil.mkdirs(mRootDir)){
			return false;
		}
		
		if(!ExternalFileUtil.copyFileFromAssetsToSD(mContext, SPEECH_FEMALE_MODEL_NAME, mRootDir)){
			return false;
		}
		if(!ExternalFileUtil.copyFileFromAssetsToSD(mContext, SPEECH_MALE_MODEL_NAME, mRootDir)){
			return false;
		}
		if(!ExternalFileUtil.copyFileFromAssetsToSD(mContext, TEXT_MODEL_NAME, mRootDir)){
			return false;
		}
		if(!ExternalFileUtil.copyFileFromAssetsToSD(mContext, LICENSE_FILE_NAME, mRootDir)){
			return false;
		}
		if(!ExternalFileUtil.copyFileFromAssetsToSD(mContext, 
				ENGLISH_DIRECTORY_NAME + File.separator + ENGLISH_SPEECH_FEMALE_MODEL_NAME, mRootDir, 
				ENGLISH_SPEECH_FEMALE_MODEL_NAME)){
			return false;
		}
		if(!ExternalFileUtil.copyFileFromAssetsToSD(mContext, 
				ENGLISH_DIRECTORY_NAME + File.separator + ENGLISH_SPEECH_MALE_MODEL_NAME, mRootDir, 
				ENGLISH_SPEECH_MALE_MODEL_NAME)){
			return false;
		}
		if(!ExternalFileUtil.copyFileFromAssetsToSD(mContext, 
				ENGLISH_DIRECTORY_NAME + File.separator + ENGLISH_TEXT_MODEL_NAME, mRootDir, 
				ENGLISH_TEXT_MODEL_NAME)){
			return false;
		}
		
		TtsFilePathConfig.getInstance().setRootDir(mRootDir);
		
		return true;
	}

	@Override
	protected String getResult() {
		return mResult;
	}

	@Override
	protected String getError() {
		return mError;
	}

}
