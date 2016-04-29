package com.lvjing.conversation.asyntask;

import java.lang.ref.SoftReference;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public abstract class AbsAsynTask<T> implements Runnable{

	private final Handler mMainHandler;
	private final SoftReference<OnAsynTaskProcessListener<T>> mListenerRef;
	private final SoftReference<Context> mContextRef;

	public AbsAsynTask(OnAsynTaskProcessListener<T> mListener, Context mContext) {
		mMainHandler = new Handler(Looper.getMainLooper());
		mListenerRef = new SoftReference<OnAsynTaskProcessListener<T>>(mListener);
		mContextRef = new SoftReference<Context>(mContext);
	}

	@Override
	public void run() {
		
		final OnAsynTaskProcessListener<T> mListener = mListenerRef.get();
		if(mListener == null){
			return;
		} else {
			mMainHandler.post(new Runnable() {
				@Override
				public void run() {
					mListener.onTaskStart();
				}
			});
		}
		
		if(process()){
			completeSuccess(getResult());
		} else {
			completeFail(getError());
		}
		
	}
	
	/**
	 * 任务处理
	 * 
	 * @return true为执行成功，false为执行失败
	 */
	protected abstract boolean process();
	
	/**
	 * 返回执行成功的结果
	 * 
	 * @return
	 */
	protected abstract T getResult();
	
	/**
	 * 返回错误信息
	 * 
	 * @return
	 */
	protected abstract String getError();
	
	
	protected final OnAsynTaskProcessListener<T> getListener(){
		return mListenerRef.get();
	}
	
	protected final Context getContext(){
		return mContextRef.get();
	}
	
	/**
	 * 任务成功执行完成时调用
	 * 
	 * @param result
	 */
	private final void completeSuccess(final T result){
		final OnAsynTaskProcessListener<T> mListener = mListenerRef.get();
		if(mListener == null){
			return;
		} else {
			mMainHandler.post(new Runnable() {
				@Override
				public void run() {
					mListener.onTaskComplete(result);
				}
			});
		}
	}
	
	/**
	 * 任务由于一些原因执行失败时调用
	 * 
	 * @param error
	 */
	private final void completeFail(final String error){
		final OnAsynTaskProcessListener<T> mListener = mListenerRef.get();
		if(mListener == null){
			return;
		} else {
			mMainHandler.post(new Runnable() {
				@Override
				public void run() {
					mListener.onTaskFail(error);
				}
			});
		}
	}
	
}
