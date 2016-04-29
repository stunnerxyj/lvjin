package com.lvjing.conversation;

import com.lvjing.conversation.config.AppParameterConfig;
import com.lvjing.conversation.sharedprefer.SharedPreferForConfig;

import android.app.Application;

public class ConversationApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		
		SharedPreferForConfig.initParameterConfig(AppParameterConfig.getInstance(), this);
	}

}
