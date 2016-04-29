package com.lvjing.conversation;

import com.lvjing.conversation.asyntask.CopyTtsFileToSDTask;
import com.lvjing.conversation.asyntask.OnAsynTaskProcessListener;
import com.lvjing.conversation.config.AppParameterConfig;
import com.lvjing.conversation.config.BaiduSpeechApiConfig;
import com.lvjing.conversation.enumeration.CopyTtsFileToSDState;
import com.lvjing.conversation.util.TheadPoolUtil;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Logo页
 * 
 * @author A Shuai
 *
 */
public final class LogoActivity extends BaseFragmentActivity{

	private final AppParameterConfig mConfig = AppParameterConfig.getInstance();
	private final TheadPoolUtil mTheadPool = TheadPoolUtil.getInstance();

	private final CommonCallkackListener mCommonListener = new CommonCallkackListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_logo);

		ImageView mBackImage = findView(R.id.activity_logo_background);
		Animation mFadeInAnim = AnimationUtils.loadAnimation(this, R.anim.logo_fadein);
		mFadeInAnim.setAnimationListener(mCommonListener);
		mBackImage.startAnimation(mFadeInAnim);

		BaiduSpeechApiConfig.getInstance();
	}

	private class CommonCallkackListener implements Animation.AnimationListener, OnAsynTaskProcessListener<String>{

		private boolean animFinish;
		private CopyTtsFileToSDState mState = CopyTtsFileToSDState.INIT;

		@Override
		public void onAnimationStart(Animation animation) {
			animFinish = false;
			if(mConfig.isSavedTtsFileToSD()){
				mState = CopyTtsFileToSDState.NONCOPY;
			} else {
				mState = CopyTtsFileToSDState.PREPARE_COPY;
				mTheadPool.submit(new CopyTtsFileToSDTask(this, mContext));
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			animFinish = true;
			if(mState == CopyTtsFileToSDState.SUCCESS || mState == CopyTtsFileToSDState.NONCOPY){
				startNewActivity(MainActivity.class, 0, 0, true, null);
			}
		}

		@Override
		public void onTaskStart() {
			mState = CopyTtsFileToSDState.COPYING;
		}

		@Override
		public void onTaskComplete(String result) {
			mState = CopyTtsFileToSDState.SUCCESS;
			mConfig.setSavedTtsFileToSD(true, mContext);
			if(animFinish){
				startNewActivity(MainActivity.class, 0, 0, true, null);
			}
		}

		@Override
		public void onTaskFail(String error) {
			mState = CopyTtsFileToSDState.FAIL;
		}

	}

	@Override
	protected boolean onBackKeyDown() {
		finish();
		return true;
	}

}
