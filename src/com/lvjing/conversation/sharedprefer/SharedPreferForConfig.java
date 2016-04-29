package com.lvjing.conversation.sharedprefer;

import com.lvjing.conversation.config.AppParameterConfig;

import android.content.Context;
import android.content.SharedPreferences;

public final class SharedPreferForConfig extends BaseSharedPreference{
	
	/**
     * 共享存储文件的文件名 *
     */
    private static final String DEFAULT_SHAREDNAME = "SP_Config";
    
    private SharedPreferForConfig(){}
    
    public static final void initParameterConfig(AppParameterConfig mParameterConfig, Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(DEFAULT_SHAREDNAME, Context.MODE_PRIVATE);
        mParameterConfig.setSavedTtsFileToSD(sharedPreferences.getBoolean(KEY_SAVETTSFILETOSD, false));
    }

    /**
     * 存取引导页的版本号
     */
    private static final String KEY_SAVETTSFILETOSD = "SaveTtsFileToSD";

    public static final boolean isSavedTtsFileToSD(Context mContext) {
        return getBooleanValue(mContext, DEFAULT_SHAREDNAME, KEY_SAVETTSFILETOSD, false);
    }

    public static final void setSavedTtsFileToSD(Context mContext, boolean mNewVer) {
        setBooleanValue(mContext, DEFAULT_SHAREDNAME, KEY_SAVETTSFILETOSD, mNewVer);
    }

}
