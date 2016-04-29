package com.lvjing.conversation;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.lvjing.conversation.listener.IFastClickDetect;
import com.lvjing.conversation.util.AbsHandler;
import com.lvjing.conversation.util.FastClickDetectionUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Created by A Shuai on 2015/4/30.
 * Activity窗口的基础类
 */
public abstract class BaseFragmentActivity extends AppCompatActivity implements ISimpleDialogListener, IFastClickDetect {

	/**
	 * 上下文对象 *
	 */
	protected Context mContext;

	/**
	 * FragmentManager对象 *
	 */
	protected FragmentManager mFragmentManager;

	/**
	 * LocalBroadcastManager对象 *
	 */
	protected LocalBroadcastManager mLocalBroadcastManager;

	/**
	 * Cursor对象 *
	 */
	protected Cursor mCursor;

	//快速点击侦测工具对象
	protected final FastClickDetectionUtil mFastClickDetectionUtil = FastClickDetectionUtil.getInstance();

	/**
	 * 可供子类使用的Handler对象，子类可自行覆写{@link #handleMessage(Message, Bundle)}方法进行处理
	 */
	protected final BaseUpdateHandler mBaseHandler = new BaseUpdateHandler(this);

	/**
	 * 基础的点击事件回调监听器，子类可直接使用，同时根据情况覆写{@link #onViewClick(View)}
	 * 和{@link #onViewLongClick(View)}方法即可
	 */
	protected final BaseCommonCallbackListener mBaseCommonListener = new BaseCommonCallbackListener(this);

	/**
	 * 本框架的根类的FragmentActivity实现的基本公共内容初始化，用户可根据自定义自行覆写完成必要的自定义
	 *
	 * @param savedInstanceState 用于状态恢复的Bundle数据集
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/**
		 *	隐藏物理菜单键
		 *	以便多余的菜单项能全部显示在ActionBar上
		 */
		try {
			ViewConfiguration mconfig = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(mconfig, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		mContext = this;
		mFragmentManager = getSupportFragmentManager();

		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		mFastClickDetectionUtil.clear();
	}

	/**
	 * 从当前布局中查询指定ID控件，消除了类型转换的麻烦
	 *
	 * @param id  指定控件的ID
	 * @param <T> 待进行类型转换的目标类型
	 * @return 返回指定ID和指定目标类型的视图
	 */
	@SuppressWarnings("unchecked")
	protected final <T extends View> T findView(int id) {
		try {
			return (T) findViewById(id);
		} catch (ClassCastException ex) {
			throw new ClassCastException(ex.getMessage());
		}
	}

	/**
	 * 替换当前Activity中指定布局ID的内容Fragment
	 *
	 * @param mContentId 被替换Fragment的布局ID
	 * @param mFragment  替换的Fragment
	 * @param mTag       标签
	 * @param enterAnim  替换进入动画
	 * @param exitAnim   替换退出动画
	 */
	protected final void replaceFragments(int mContentId, Fragment mFragment, String mTag, int enterAnim, int exitAnim) {
		Fragment mOldFragment = mFragmentManager.findFragmentByTag(mTag);
		FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
		if (mOldFragment != null) {
			mFragmentTransaction.remove(mOldFragment);
		}
		mFragmentTransaction.setCustomAnimations(enterAnim, exitAnim);
		mFragmentTransaction.replace(mContentId, mFragment, mTag);
		mFragmentTransaction.commit();
	}

	/**
	 * Back键按下回调事件
	 *
	 * @return true 表示事件被消费
	 */
	protected abstract boolean onBackKeyDown();

	/**
	 * Menu键按下的回调事件，各子类Activity可根据需要自行覆写该方法即可
	 *
	 * @return ture 表示事件被消费
	 */
	protected boolean onMenuKeyDown() {
		return false;
	}

	/**
	 * 禁止覆写此方法，
	 * 需要监听返回按钮和菜单按钮事件的请覆写{@link #onBackKeyDown()}和{@link #onMenuKeyDown()}；
	 * 需要监听另外的物理按钮请覆写{@link #onKeyDown(int, KeyEvent, int)}方法
	 */
	@Override
	public final boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (onBackKeyDown()) {
				return true;
			}
			break;
		case KeyEvent.KEYCODE_MENU:
			if (onMenuKeyDown()) {
				return true;
			}
			break;
		default:
			break;
		}
		return onKeyDown(keyCode, event, 1);
	}

	/**
	 * 物理按键事件的处理方法，flag标示的形参不具有任何意义，
	 * 仅作与{@link #onKeyDown(int, KeyEvent)}的区分
	 *
	 * @param keyCode 按键事件代码
	 * @param event   事件对象
	 * @param flag    无意义，仅作为区分标志位
	 * @return true表示事件被处理
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event, int flag) {
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 简单的打开一个新的Activity，并传入必要的参数
	 *
	 * @param target    要打开的Activity的Class对象
	 * @param enterAnim 使用0表示不指定动画
	 * @param exitAnim  使用0表示不指定动画
	 * @param isFinish  是否终止当前activity
	 * @param mBundle   给新的Activity传递的参数
	 */
	public final void startNewActivity(Class<? extends Activity> target, int enterAnim, int exitAnim, boolean isFinish, Bundle mBundle) {
		Intent mIntent = new Intent(this, target);
		if (mBundle != null) {
			mIntent.putExtras(mBundle);
		}
		startActivity(mIntent);
		overridePendingTransition(enterAnim, exitAnim);
		if (isFinish) {
			super.finish();
		}
	}

	/**
	 * 打开一个可以回调数据的Activity，并传入必要的参数
	 *
	 * @param target      要打开的Activity的Class对象
	 * @param enterAnim   使用0表示不指定动画
	 * @param exitAnim    使用0表示不指定动画
	 * @param requestCode 请求码
	 * @param mBundle     给新打开的activity传递参数
	 */
	public final void startNewActivityForResult(Class<? extends Activity> target, int enterAnim, int exitAnim, int requestCode, Bundle mBundle) {
		Intent mIntent = new Intent(this, target);
		if (mBundle != null) {
			mIntent.putExtras(mBundle);
		}
		startActivityForResult(mIntent, requestCode);
		overridePendingTransition(enterAnim, exitAnim);
	}

	/**
	 * 以给定动画形式结束当前Activity
	 *
	 * @param enterAnim 前一个Activity的进入动画，使用0表示不指定动画
	 * @param exitAnim  当前Activity的退出动画，使用0表示不指定动画
	 */
	public final void finish(int enterAnim, int exitAnim) {
		finish();
		overridePendingTransition(enterAnim, exitAnim);
	}

	/**
	 * 开启一个普通的后台服务
	 *
	 * @param target  指定打开的Service的class对象
	 * @param mBundle 按需传递的数据Bundle
	 */
	public final void startService(Class<? extends Service> target, Bundle mBundle) {
		Intent mIntent = new Intent(this, target);
		if (mBundle != null) {
			mIntent.putExtras(mBundle);
		}
		startService(mIntent);
	}

	/**
	 * 关闭一个普通的后台服务
	 *
	 * @param target  指定关闭的Service的class对象
	 * @param mBundle 按需传递的数据Bundle
	 */
	public final void stopService(Class<? extends Service> target, Bundle mBundle) {
		Intent mIntent = new Intent(this, target);
		if (mBundle != null) {
			mIntent.putExtras(mBundle);
		}
		stopService(mIntent);
	}

	/**
	 * 关闭数据库数据集游标
	 * 使用完类的数据集游标成员变量mCursor后，务必调用此方法关闭数据集游标
	 */
	protected final void closeCursor() {
		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
		}
	}

	/**
	 * <p>AndroidStyledDialog库的对话框回调方法，可使用其中的Dialog，若需对Dialog回调事件进行处理，可自行覆写这些方法</p>
	 * <p>取消按钮回调</p>
	 *
	 * @param code 对话框代码
	 */
	@Override
	public void onNegativeButtonClicked(int code) {
	}

	/**
	 * Dialog中立按钮回调
	 *
	 * @param code 对话框代码
	 */
	@Override
	public void onNeutralButtonClicked(int code) {
	}

	/**
	 * Dialog确定按钮回调
	 *
	 * @param code 对话框代码
	 */
	@Override
	public void onPositiveButtonClicked(int code) {
	}

	@Override
	public boolean isLegalClick(View v) {
		return mFastClickDetectionUtil.isLegalClick(v);
	}

	/**
	 * 点击事件回调方法，子类可自行覆写处理。
	 *
	 * @param v 被点击的按钮
	 */
	public void onViewClick(View v) {

	}

	/**
	 * 长按事件回调方法，子类可自行覆写处理。
	 *
	 * @param v 被长按的按钮
	 * @return true表示事件被处理，false为未处理
	 */
	public boolean onViewLongClick(View v) {
		return false;
	}

	/**
	 * 基类提供的常用的公共点击事件回调监听器
	 */
	protected static final class BaseCommonCallbackListener implements View.OnClickListener, View.OnLongClickListener {

		private final WeakReference<BaseFragmentActivity> mActivityRef;

		public BaseCommonCallbackListener(BaseFragmentActivity mActivity) {
			mActivityRef = new WeakReference<>(mActivity);
		}

		@Override
		public void onClick(View v) {
			BaseFragmentActivity mActivity = mActivityRef.get();
			if (mActivity == null) {
				return;
			}
			//这里已经对按钮的快速点击进行了处理
			if (mActivity.mFastClickDetectionUtil.isLegalClick(v)) {
				mActivity.onViewClick(v);
			}
		}

		@Override
		public boolean onLongClick(View v) {
			BaseFragmentActivity mActivity = mActivityRef.get();
			return mActivity != null && mActivity.onViewLongClick(v);
		}

	}

	/**
	 * Handler对Message的回调处理方法，子类可自行覆写处理
	 *
	 * @param msg     Message对象
	 * @param mBundle 可能为null，子类使用时需注意
	 */
	public void handleMessage(Message msg, Bundle mBundle) {

	}

	/**
	 * 基类提供的一个Handler对象的内部实现类。
	 */
	public static final class BaseUpdateHandler extends AbsHandler<BaseFragmentActivity> {

		public BaseUpdateHandler(BaseFragmentActivity mActivity) {
			super(mActivity);
		}

		@Override
		protected void handleMessage(BaseFragmentActivity mActivity, Message msg, Bundle mBundle) {
			mActivity.handleMessage(msg, mBundle);
		}
	}

}
