package com.lvjing.conversation.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池类
 * 
 * @author A Shuai
 *
 */
public final class TheadPoolUtil {

	private static TheadPoolUtil mInstance;
	
	public static TheadPoolUtil getInstance(){
		if(mInstance == null){
			synchronized (TheadPoolUtil.class) {
				if(mInstance == null){
					mInstance = new TheadPoolUtil();
				}
			}
		}
		return mInstance;
	}
	
	private volatile boolean isShutdown;
	private ExecutorService mExecutor;
	
	private TheadPoolUtil(){
		isShutdown = false;
		mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}
	
	public void submit(Runnable mTask){
		checkThreadpoolShutdown();
		mExecutor.execute(mTask);
	}
	
	/**
	 * 关闭线程池
	 */
	public synchronized void shutdown(){
		if(!isShutdown){
			isShutdown = true;
			mExecutor.shutdown();
		}
	}
	
	/**
	 * 重建线程池
	 */
	public synchronized void reconstruct(){
		if(isShutdown){
			mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			isShutdown = false;
		}
	}
	
	private final void checkThreadpoolShutdown(){
		if(isShutdown){
			throw new IllegalStateException("the thread pool has been closed, please invoke reconstruct().");
		}
	}
	
}
