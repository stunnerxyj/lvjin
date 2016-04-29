package com.lvjing.conversation;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.baidu.speech.VoiceRecognitionService;
import com.baidu.tts.answer.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.lvjing.conversation.asyntask.DialogueWithServerTask;
import com.lvjing.conversation.asyntask.OnAsynTaskProcessListener;
import com.lvjing.conversation.config.BaiduSpeechApiConfig;
import com.lvjing.conversation.config.SpeechRecognizeConfig;
import com.lvjing.conversation.config.TtsFilePathConfig;
import com.lvjing.conversation.enumeration.SpeechRecognitionState;
import com.lvjing.conversation.util.TheadPoolUtil;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import static com.lvjing.conversation.Constant.GREET_WORDS;

/**
 * 主页
 * 
 * @author A Shuai
 *
 */
public class MainActivity extends BaseFragmentActivity {

	private final SpeechRecognizeConfig mSRC = SpeechRecognizeConfig.getInstance();
	private final VoiceRecogListener mVRL = new VoiceRecogListener();
	private final VoiceCombineListener mVCL = new VoiceCombineListener();

	
	//线程池
	private final TheadPoolUtil mThreadPool = TheadPoolUtil.getInstance();

	private TextView mResultTextView;
	private TextView mLogTextView;
	private Button mButton;
	private ImageButton button;

	//类型
	private String type="story";
	public final String[] tp=new String[]{"故事","诗歌"};
	public final String[] tpEN=new String[]{"story","poetry"};
	//语音识别
	private SpeechRecognizer mSpeechRecognizer;
	//语音合成
	private SpeechSynthesizer mSpeechSynthesizer;

	//语音识别状态
	private SpeechRecognitionState mSpeechRecogState;

	//上下文字符串
	private String mContextString;
	private boolean isExit = false;
    private String lw;
    private String abouttoplay=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		.detectDiskReads().detectDiskWrites().detectNetwork()
		.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
		.penaltyLog().penaltyDeath().build());

		mLogTextView = findView(R.id.activity_main_log);
		mResultTextView=findView(R.id.activity_main_result);
		button=findView(R.id.voicebtn );

		mLogTextView.setMovementMethod(new ScrollingMovementMethod());
		button.setOnClickListener(mBaseCommonListener);
		mSpeechRecogState = SpeechRecognitionState.NONE;

		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
		mSpeechRecognizer.setRecognitionListener(mVRL);

		mSpeechSynthesizer = SpeechSynthesizer.getInstance();
		mSpeechSynthesizer.setContext(getApplicationContext());
		mSpeechSynthesizer.setSpeechSynthesizerListener(mVCL);

		TtsFilePathConfig mTtsFilePathConfig = TtsFilePathConfig.getInstance();

		// 文本模型文件路径 (离线引擎使用)
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mTtsFilePathConfig.getTextModelPath());
		// 声学模型文件路径 (离线引擎使用)
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mTtsFilePathConfig.getSpeechFemalePath());
		// 本地授权文件路径,如未设置将使用默认路径.设置临时授权文件路径，LICENCE_FILE_NAME请替换成临时授权文件的实际路径，仅在使用临时license文件时需要进行设置，如果在[应用管理]中开通了离线授权，不需要设置该参数，建议将该行代码删除（离线引擎）
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, mTtsFilePathConfig.getLicenseFilePath());
		BaiduSpeechApiConfig mConfig = BaiduSpeechApiConfig.getInstance();
		// 请替换为语音开发者平台上注册应用得到的App ID (离线授权)
		mSpeechSynthesizer.setAppId(mConfig.getAppID());
		// 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
		mSpeechSynthesizer.setApiKey(mConfig.getApiKey(), mConfig.getSecretKey());
		// 授权检测接口
		AuthInfo authInfo = this.mSpeechSynthesizer.auth(TtsMode.MIX);
		if (authInfo.isSuccess()) {
			print("auth success");
			mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, SpeechSynthesizer.SPEAKER_FEMALE);
			mSpeechSynthesizer.initTts(TtsMode.MIX);
			int result = mSpeechSynthesizer.loadEnglishModel(mTtsFilePathConfig.getEnTextModelPath(), mTtsFilePathConfig.getEnSpeechFemalePath());
			print("loadEnglishModel result=" + result);
//			mButton.setEnabled(true);

			read(GREET_WORDS);

		} else {
			String errorMsg = authInfo.getTtsError().getDetailMessage();
			print("auth failed errorMsg=" + errorMsg);
//			mButton.setEnabled(false);
		}

	}

	//添加目录
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
	//给目录添加
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    int id = item.getItemId();
	    //当点击不同的menu item 是执行不同的操作
	    switch (id) {
	        case R.id.action_settings:
	        	new AlertDialog.Builder(mContext)
	        	.setTitle("请选择要听的内容（只能选一种哦！）")
	        	.setSingleChoiceItems(tp,0,
	            new DialogInterface.OnClickListener() {      
	        	     public void onClick(DialogInterface dialog, int which) {  
//	        	        dialog.dismiss();  
	        	        type=tpEN[which];
	        	     }  
	        	  }  )
	        	  .setPositiveButton("确定",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 自动生成的方法存根	
						dialog.dismiss();
					}
				})
	        	  .show();
	            break;
	        default:
	            break;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mSpeechRecognizer.destroy();
		mSpeechSynthesizer.release();
	}

	/**
	 * 开始语音识别
	 */
	private void startSpeechRecog(){
		mLogTextView.setText("");
		mResultTextView.setText("");
		Intent mIntent = new Intent();
		buildSpeechRecogParam(mIntent);
		mSpeechRecognizer.startListening(mIntent);
	}

	private void buildSpeechRecogParam(Intent mIntent){
		mIntent.putExtra(Constant.EXTRA_SOUND_START, mSRC.getStartSountResID());
		mIntent.putExtra(Constant.EXTRA_SOUND_END, mSRC.getEndSoundResID());
		mIntent.putExtra(Constant.EXTRA_SOUND_SUCCESS, mSRC.getSuccessSoundResID());
		mIntent.putExtra(Constant.EXTRA_SOUND_ERROR, mSRC.getErrorSoundResID());
		mIntent.putExtra(Constant.EXTRA_SOUND_CANCEL, mSRC.getCancelSoundResID());
	}

	/**
	 * 取消语音识别
	 */
	private void cancelSpeechRecog(){
		mSpeechRecognizer.cancel();
		mSpeechRecogState = SpeechRecognitionState.NONE;
		print("点击了取消");
	}

	/**
	 * 停止语音识别，用于在语音输入中时进行了中断
	 */
	private void stopSpeechRecog(){
		mSpeechRecognizer.stopListening();
		print("点击了\\“说完了\\”");
	}

	/**
	 * 停止朗读
	 */
	private void stopReading(){
		mSpeechSynthesizer.pause();
		print("停止朗读");
		startSpeechRecog();
	}

	private void print(final String str) {
		mBaseHandler.post(new Runnable() {

			@Override
			public void run() {
				mResultTextView.setText(str);
//				mLogTextView.append(str + "\n");
				
			}
		});

	}

	@Override
	public void onViewClick(View v) {
		if(v.getId() == R.id.voicebtn){
			switch(mSpeechRecogState){
			case NONE:
				startSpeechRecog();
//				mButton.setText("取消");
				mSpeechRecogState = SpeechRecognitionState.WAITING_READY;
				break;
			case WAITING_READY:
				cancelSpeechRecog();
//				mButton.setText("开始");
				mSpeechRecogState = SpeechRecognitionState.NONE;
				break;
			case READY:
				cancelSpeechRecog();
//				mButton.setText("开始");
				mSpeechRecogState = SpeechRecognitionState.NONE;
				break;
			case USER_SPEAKING:
				stopSpeechRecog();
//				mButton.setText("识别中");
				mSpeechRecogState = SpeechRecognitionState.RECOGNITION;
				break;
			case RECOGNITION:
				cancelSpeechRecog();
//				mButton.setText("开始");
				mSpeechRecogState = SpeechRecognitionState.NONE;
				break;
			case READING:
				stopReading();
//				mButton.setText("开始");
				mSpeechRecogState = SpeechRecognitionState.READY;
				break;
			default:
				throw new IllegalStateException();
			}
		}
	}

	@Override
	protected boolean onBackKeyDown() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(this, R.string.activity_main_exit_tip, Toast.LENGTH_LONG).show();
			new Timer().schedule(new TimerTask() {
				public void run() {
					isExit = false;
				}
			}, 2000);
		} else {
			finish();
		}
		return true;
	}

	/**
	 * 语音识别回调监听器
	 * 
	 * @author A Shuai
	 *
	 */
	private class VoiceRecogListener implements RecognitionListener{

		@Override
		public void onReadyForSpeech(Bundle params) {
			mSpeechRecogState = SpeechRecognitionState.READY;
			print("准备就绪，可以开始说话");
//			mButton.setText("取消");
		}

		@Override
		public void onBeginningOfSpeech() {
			mSpeechRecogState = SpeechRecognitionState.USER_SPEAKING;
			print("检测到用户开始说话");
//			mButton.setText("说完了");
		}

		@Override
		public void onRmsChanged(float rmsdB) {  }

		@Override
		public void onBufferReceived(byte[] buffer) {  }

		@Override
		public void onEndOfSpeech() {
			mSpeechRecogState = SpeechRecognitionState.RECOGNITION;
			print("检测到用户的已经停止说话");
			print("识别中....");
//			mButton.setText("取消");
		}

		@Override
		public void onError(int error) {
			mSpeechRecogState = SpeechRecognitionState.NONE;
			StringBuilder sb = new StringBuilder();
			switch (error) {
			case SpeechRecognizer.ERROR_AUDIO:
				sb.append("音频问题");
				startSpeechRecog();
				break;
			case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
				sb.append("没有语音输入");
				if(lw!=null)
					read(lw);
				else
					read(GREET_WORDS);
				break;
			case SpeechRecognizer.ERROR_CLIENT:
				sb.append("其它客户端错误");
				startSpeechRecog();
				break;
			case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
				sb.append("权限不足");
				startSpeechRecog();
				break;
			case SpeechRecognizer.ERROR_NETWORK:
				sb.append("网络问题");  
				startSpeechRecog();
				break;
			case SpeechRecognizer.ERROR_NO_MATCH:
				sb.append("没有匹配的识别结果");
				startSpeechRecog();
				break;
			case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
				sb.append("引擎忙");	
				startSpeechRecog();
				break;
			case SpeechRecognizer.ERROR_SERVER:
				sb.append("服务端错误");
				startSpeechRecog();
				break;
			case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
				sb.append("连接超时");	
				startSpeechRecog();
				break;
			}
			sb.append(":" + error);
			print("识别失败：" + sb.toString());
//			mButton.setText("开始");
		}

		@Override
		public void onResults(Bundle results) {
			mSpeechRecogState = SpeechRecognitionState.NONE;
			ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			String mTempStr = nbest.get(0);
			mResultTextView.setText(mTempStr);
			mThreadPool.submit(new DialogueWithServerTask(new DialogueWithServerListener(), mContext,type, mTempStr, mContextString));
			mContextString = null;			
		}

		@Override
		public void onPartialResults(Bundle partialResults) {  }

		@Override
		public void onEvent(int eventType, Bundle params) {  }

	}

	private String Analysis(String returnstr) {
		String [] content;
		content=returnstr.split("\"内容\"");
		String [] output;
		output=content[0].split("结果\":\"");
		String toplay[]=output[1].split("。");
		String play[]=toplay[0].split("播放");
		if(output[1].contains("将播放")){
//			abouttoplay=toplay[0];
			abouttoplay="将播放《"+play[1]+"》";
			return output[1]+content[1];
		}
		else
			return content[1];
	}

	private void Analysis1(String returnstr) {
		String content = null;
		if(returnstr==null)
			mContextString="";
		else{
			int start= returnstr.indexOf("分析");
			int end=returnstr.indexOf("\"结果\"");
			print(start+end+"");
			mContextString=returnstr.substring(returnstr.indexOf("分析")+4,returnstr.indexOf(",\"结果\""));
			int sc=returnstr.indexOf("\"内容\":\"");
			int se=returnstr.indexOf("\"}");
			content=returnstr.substring(sc+6, se);
		}
		
//		print(mContextString);
		mLogTextView.append(content);
	}
	private void ToExit(String returnstr){
		if(returnstr.contains("statu\":99")&&returnstr.contains("confirm\":true"))
			finish();
		if(returnstr=="null")
			System.exit(0);
	}

	private void read(String str){
		int result = mSpeechSynthesizer.speak(str);
		if (result < 0) {
			System.out.println("error,please look up error code in doc or URL:http://yuyin.baidu.com/docs/tts/122 ");
		} else {
			System.out.println("000000000000");
		}
	}

	/**
	 * 语音合成回调监听器
	 * 
	 * @author A Shuai
	 *
	 */
	private class VoiceCombineListener implements SpeechSynthesizerListener{

		@Override
		public void onError(String arg0, SpeechError arg1) {  }

		@Override
		public void onSpeechStart(String arg0) {
			mBaseHandler.post(new Runnable() {

				@Override
				public void run() {
					print("朗读开始");
					if(abouttoplay!=null){
						print(abouttoplay);
					}
					abouttoplay=null;
				}
			});
			mSpeechRecogState = SpeechRecognitionState.READING;

		}

		@Override
		public void onSpeechFinish(String arg0) {
			mBaseHandler.post(new Runnable() {

				@Override
				public void run() {
					print("朗读结束");
					startSpeechRecog();
				}
			});
			
		}

		@Override
		public void onSpeechProgressChanged(String arg0, int arg1) {  }


		@Override
		public void onSynthesizeDataArrived(String arg0, byte[] arg1, int arg2) {  }

		@Override
		public void onSynthesizeStart(String arg0) {  }

		@Override
		public void onSynthesizeFinish(String arg0) {  }

	}

	/**
	 * 访问服务器的过程处理回调
	 * 
	 * @author A Shuai
	 *
	 */
	private class DialogueWithServerListener implements OnAsynTaskProcessListener<String>{
		
		@Override
		public void onTaskStart() {  }

		@Override
		public void onTaskComplete(String mResult) {
			ToExit(mResult);
			Analysis1(mResult);
			lw=Analysis(mResult);
			read(Analysis(mResult));
		}
		@Override
		public void onTaskFail(String error) {

		}

	}


}
