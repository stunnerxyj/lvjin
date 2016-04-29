package com.lvjing.conversation.enumeration;

public enum CopyTtsFileToSDState {

	/**
	 * 初始化状态
	 */
	INIT,
	
	/**
	 * 无需拷贝
	 */
	NONCOPY,
	
	/**
	 * 准备拷贝
	 */
	PREPARE_COPY,
	
	/**
	 * 拷贝中
	 */
	COPYING,
	
	/**
	 * 成功拷贝
	 */
	SUCCESS,
	
	/**
	 * 拷贝失败
	 */
	FAIL
	
	
}
