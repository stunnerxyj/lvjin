package com.lvjing.conversation.asyntask;

import com.lvjing.conversation.entity.NetResultEntity;
import com.lvjing.conversation.util.NetUtil;

import android.content.Context;

public final class DialogueWithServerTask extends AbsAsynTask<String>{

	private final String str;
	private final String lastStr;
	private final String type;
	private NetResultEntity mNetResult;
	
	public DialogueWithServerTask(OnAsynTaskProcessListener<String> mListener, Context mContext,
			String type,String str, String lastStr) {
		super(mListener, mContext);
		this.type=type;
		this.str = str;
		this.lastStr = lastStr;
	}

	@Override
	protected boolean process() {
		mNetResult = NetUtil.dialogueWithServer(type,str, lastStr);
		return mNetResult.isSuccess();
	}

	@Override
	protected String getResult() {
		return mNetResult.getResult();
	}

	@Override
	protected String getError() {
		return mNetResult.getError();
	}
	
}
