package com.lvjing.conversation.config;

/**
 * 百度语音API Key配置类
 * 
 * @author A Shuai
 *
 */
public class BaiduSpeechApiConfig {

	private static BaiduSpeechApiConfig mInstance = null;
	
	public static BaiduSpeechApiConfig getInstance(){
		if(mInstance == null){
			synchronized (BaiduSpeechApiConfig.class) {
				if(mInstance == null){
					mInstance = new BaiduSpeechApiConfig();
				}
			}
		}
		return mInstance;
	}
	
	private final String mAppID;
	private final String mApiKey;
	private final String mSecretKey;
	
	private BaiduSpeechApiConfig(){
		mAppID = "6243366";
		mApiKey = "XN15fIcU4Gpn26HxraTG6vLY";
		mSecretKey = "bf7c486557c85b37d96f0043335fe8ab";
	}

	public String getAppID() {
		return mAppID;
	}

	public String getApiKey() {
		return mApiKey;
	}

	public String getSecretKey() {
		return mSecretKey;
	}
	
}
