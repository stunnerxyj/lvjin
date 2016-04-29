package com.lvjing.conversation.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.lvjing.conversation.Constant;
import com.lvjing.conversation.entity.NetResultEntity;

public final class NetUtil {

	private NetUtil(){}

	/**
	 * 与服务器对话网络访问方法
	 * @param type 类别，包括故事，诗歌等
	 * @param str 此次语音识别到的句子
	 * @param lastStr 上一句对话语句
	 * @return
	 */
	public static NetResultEntity dialogueWithServer(String type,String str, String lastStr) {
		
		NetResultEntity mReturnResult = new NetResultEntity();
		
		HttpURLConnection mURLConn = null;
		DataOutputStream mDataOutput = null;
		BufferedReader mReader = null;
		
		try {
			URL url = new URL(Constant.URL);
			mURLConn = (HttpURLConnection) url.openConnection();
			mURLConn.setDoInput(true);
			mURLConn.setDoOutput(true);
			mURLConn.setRequestMethod("POST");
			mURLConn.setUseCaches(false);
			mURLConn.setInstanceFollowRedirects(true);
			mURLConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			mURLConn.setRequestProperty("Charset", "UTF-8");
			mURLConn.connect();

			mDataOutput = new DataOutputStream(mURLConn.getOutputStream());
//			String type="story";
			mDataOutput.writeBytes("type="+URLEncoder.encode(type,"utf-8"));
			mDataOutput.writeBytes("&sentence=" + URLEncoder.encode(str, "utf-8"));
			if(lastStr!=null){
				mDataOutput.writeBytes("&lastsentence=" + URLEncoder.encode(lastStr, "utf-8"));
			}
			mDataOutput.flush();

			mReader = new BufferedReader(new InputStreamReader(mURLConn.getInputStream()));
			StringBuilder mResult = new StringBuilder();
			String readline;
			while ((readline = mReader.readLine()) != null) {
				mResult.append(readline);
			}
			mReturnResult.setResult(mResult.toString());
			mReturnResult.setSuccess(true);
			
		} catch (Exception e) {
			e.printStackTrace();
			mReturnResult.setSuccess(false);
			mReturnResult.setError("访问服务器出错");
			return mReturnResult;
		} finally {
			if(mURLConn != null){
				try{
					mURLConn.disconnect();
				} catch (Exception e){} finally {
					mURLConn = null;
				}
			}
			if(mDataOutput != null){
				try{
					mDataOutput.close();
				} catch (Exception e){} finally {
					mDataOutput = null;
				}
			}
			if(mReader != null){
				try{
					mReader.close();
				} catch (Exception e){} finally {
					mReader = null;
				}
			}
		}
		return mReturnResult;
	}

}
