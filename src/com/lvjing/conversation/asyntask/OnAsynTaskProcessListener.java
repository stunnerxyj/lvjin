package com.lvjing.conversation.asyntask;

/**
 * 异步任务处理回调
 * 
 * @author A Shuai
 *
 */
public interface OnAsynTaskProcessListener<T> {

	/**
	 * 任务开始
	 */
	public void onTaskStart();
	
	/**
	 * 任务完成
	 */
	public void onTaskComplete(T result);
	
	/**
	 * 任务失败
	 * 
	 * @param error
	 */
	public void onTaskFail(String error);
	
}
