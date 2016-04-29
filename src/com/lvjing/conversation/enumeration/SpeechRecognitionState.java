package com.lvjing.conversation.enumeration;

/**
 * 语言识别过程中的状态枚举
 * 
 * @author A Shuai
 *
 */
public enum SpeechRecognitionState {

	/**
	 * 无状态
	 */
	NONE(0),
	
	/**
	 * 等待准备完成
	 */
	WAITING_READY(1),
	
	/**
	 * 准备完成，开始录音识别
	 */
	READY(2),
	
	/**
	 * 用户语音输入中
	 */
	USER_SPEAKING(3),
	
	/**
	 * 识别中
	 */
	RECOGNITION(4),
	
	/**
	 * 朗读中
	 */
	READING(5),
	
	/**
	 * 与服务器对话中，用于处理由于网络不良时过于耗时的情况
	 */
	DIALOGUE_WITH_SERVER(6);
	
	
	private final int mIndex;
	
	private SpeechRecognitionState(int mIndex){
		this.mIndex = mIndex;
	}

	public int getIndex() {
		return mIndex;
	}
	
	public static SpeechRecognitionState valueOf(int mIndex){
		for(SpeechRecognitionState mState : values()){
			if(mState.getIndex() == mIndex){
				return mState;
			}
		}
		throw new IllegalArgumentException("the mIndex parameter is illegal.");
	}
	
}
