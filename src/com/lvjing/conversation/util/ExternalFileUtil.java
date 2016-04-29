package com.lvjing.conversation.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import static com.lvjing.conversation.Constant.ROOTFILE_NAME;

/**
 * 外部文件工具类
 * 
 * @author A Shuai
 *
 */
public final class ExternalFileUtil {

	private ExternalFileUtil(){}

	/**
	 *	取得缓存数据的根目录文件夹
	 * @param mContext
	 * @return
	 */
	public static String getCacheDir( Context mContext ){

		String path = (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && !isExternalStorageRemovable())
				? getExternalCacheDir() : getInteriorCacheDir( mContext );
		return new File(path + File.separator + ROOTFILE_NAME).getPath();
	}

	/**
	 * 检查外部存储状态是否移除
	 * 
	 * @return 如果外部存储已移除则返回 True
	 */
	public static boolean isExternalStorageRemovable() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

	/**
	 *	取得外部存储器的文件路径
	 * @param context
	 * @return
	 */
	public static String getExternalCacheDir() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	/**
	 *	取得内部存储器的缓存路径
	 * @param mContext
	 * @return
	 */
	public static String getInteriorCacheDir( Context mContext ){
		return mContext.getCacheDir().getPath();
	}
	
	public static boolean mkdirs(String mDir){
		File mFile = new File(mDir);
		return mFile.mkdirs();
	}
	
	public static boolean copyFileFromAssetsToSD(Context mContext, String mOriFileName, String mDestFileDir){
		return copyFileFromAssetsToSD(mContext, mOriFileName, mDestFileDir, mOriFileName);
	}
	
	public static boolean copyFileFromAssetsToSD(Context mContext, String mOriFileName, String mDestFileDir, String mDestFileName){
		InputStream mInput = null;
		FileOutputStream mFileOutput = null;
		try{
			mInput = mContext.getResources().getAssets().open(mOriFileName);
			mFileOutput = new FileOutputStream(new File(mDestFileDir, mDestFileName));
			
			byte[] buffer = new byte[1024];
			int size = 0;
			while ((size = mInput.read(buffer, 0, 1024)) >= 0) {
				mFileOutput.write(buffer, 0, size);
			}
			
		} catch (Exception e){
			e.printStackTrace();
			return false;
		} finally {
			if(mInput != null){
				try{
					mInput.close();
				} catch (Exception e){} finally {
					mInput = null;
				}
			}
			if(mFileOutput != null){
				try{
					mFileOutput.close();
				} catch (Exception e){} finally {
					mFileOutput = null;
				}
			}
		}
		return true;
	}

}
