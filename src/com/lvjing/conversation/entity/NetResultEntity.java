package com.lvjing.conversation.entity;

/**
 * 访问网络结果数据实体类
 * 
 * @author A Shuai
 *
 */
public final class NetResultEntity {

	private boolean success;
	private String mResult;
	private String mError;
	
	public NetResultEntity(){
		success = false;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getResult() {
		return mResult;
	}

	public void setResult(String mResult) {
		this.mResult = mResult;
	}

	public String getError() {
		return mError;
	}

	public void setError(String mError) {
		this.mError = mError;
	}
	
}
