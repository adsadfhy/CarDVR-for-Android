package com.example.cardvr;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore.Images;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.test.R;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
	private static final String TAG = "CarDVR";

	private SurfaceView mPreviewSV = null; // 预览SurfaceView
	private SurfaceHolder mSurfaceHolder = null;
	private boolean isPreview = false;
	private ImageView mVideoThumnail = null;
	private ImageButton mVideoButton = null;
	// private TextView mTextView = null;
	private Camera mCamera = null;
	private WakeLock mWakeLock = null;
	
	private int mScreenBrightness = 0; 
	
	private final int maxDurationInMs = 3 * 60000;
//	private final long maxFileSizeInBytes = 500000;
	private final int videoFramesPerSecond = 15;
	private final int recordScreenBrightness = 25;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		mScreenBrightness = getScreenBrightness(MainActivity.this);
		
		mPreviewSV = (SurfaceView) this.findViewById(R.id.surfaceView1);

		mSurfaceHolder = mPreviewSV.getHolder();

		mSurfaceHolder.addCallback(this);

		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mVideoThumnail = (ImageView) findViewById(R.id.imageView1);
		mVideoButton = (ImageButton) findViewById(R.id.imageButton1);

		this.mVideoThumnail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						ListViewActivity.class);
				startActivityForResult(intent, 0);
			}
		});

		mVideoButton.setOnClickListener(new RecordVideoClickListener());
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		setScreenBrightness(MainActivity.this, mScreenBrightness);
		return true;
	}
	
	
	public void updateGallery(String filename) {

		try {
			MediaScannerConnection.scanFile(this, new String[] { filename },
					null, new MediaScannerConnection.OnScanCompletedListener() {

						@Override
						public void onScanCompleted(String path, Uri uri) {
							// TODO Auto-generated method stub
							Log.i("ExternalStorage", "Scanned " + path + ":");
							Log.i("ExternalStorage", "-> uri=" + uri);
						}

					});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int getScreenBrightness(Activity activity) {
		int value = 0;
		ContentResolver cr = activity.getContentResolver();
		try {
			value = Settings.System.getInt(cr,
					Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {

		}
		return value;
	}

	public static void setScreenBrightness(Activity activity, int value) {
		WindowManager.LayoutParams params = activity.getWindow()
				.getAttributes();
		params.screenBrightness = value / 255f;
		activity.getWindow().setAttributes(params);
	}

	protected class RecordVideoClickListener implements View.OnClickListener {
		private boolean isRecording = false;
		private MediaRecorder mMediaRecorder = null;
		private File mVideoFile = null;
		private Timer timer = null;

		@Override
		public void onClick(View v) {

			// TODO Auto-generated method stub
			if (!isRecording) {
				if (mMediaRecorder == null)
					mMediaRecorder = new MediaRecorder();
				else {
					mMediaRecorder.reset();
				}
				// Unlock the camera object before passing it to media recorder.
				mCamera.unlock();
				ConfigureMediaRecorder();
							
				try {
					mMediaRecorder.prepare();
					mMediaRecorder.start(); // Recording is now started
										
					// lower screen brightness to save battery
					setScreenBrightness(MainActivity.this, recordScreenBrightness);
					mVideoButton.setImageResource(R.drawable.stoprecord);
					isRecording = true;
				} catch (IllegalStateException e) {
					Log.e(TAG, e.getMessage());
					e.printStackTrace();
					return;
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
					e.printStackTrace();
					return;
				}

				TimerTask task = new TimerTask() {
					@Override
					public void run() {

						runOnUiThread(new Runnable() { // UI thread
							@Override
							public void run() {
								if (isRecording) {
									try {
										mMediaRecorder.stop();
										mMediaRecorder.reset();
										try {
											mCamera.reconnect();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										SetThumnail();

										mCamera.unlock();
										ConfigureMediaRecorder();

										mMediaRecorder.prepare();
										mMediaRecorder.start();
										setScreenBrightness(MainActivity.this, recordScreenBrightness);

									} catch (IllegalStateException e) {
										Log.e(TAG, e.getMessage());
										e.printStackTrace();
										return;
									} catch (IOException e) {
										Log.e(TAG, e.getMessage());
										e.printStackTrace();
										return;
									}
								}
							}
						});
					}
				};

				timer = new Timer();
				timer.schedule(task, maxDurationInMs, maxDurationInMs);

			} else {
				timer.cancel();
				timer = null;
				this.isRecording = false;
				mMediaRecorder.stop();
				mMediaRecorder.reset();
				mMediaRecorder.release();
				mMediaRecorder = null;
				updateGallery(Environment.getExternalStorageDirectory()
						+ "/DVR");
				SetThumnail();

				if (mCamera != null) {
					try {
						mCamera.reconnect();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				setScreenBrightness(MainActivity.this, mScreenBrightness);
				mVideoButton.setImageResource(R.drawable.startrecord);
			}
		}

		private void SetThumnail() {

			Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(
					mVideoFile.getAbsolutePath(), Images.Thumbnails.MICRO_KIND);
			if (thumbnail != null) {
				mVideoThumnail.setImageBitmap(thumbnail);
			}
		}

		private void ConfigureMediaRecorder() {
			mMediaRecorder.setCamera(mCamera);
			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// .3gp
			mMediaRecorder
					.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			// video encoder
			mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
			// audio encoder
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			File dir = new File(Environment.getExternalStorageDirectory()
					+ "/DVR");
			if (!dir.exists()) {
				dir.mkdir();
			}

			mVideoFile = new File(dir, "dvr" + System.currentTimeMillis()
					+ ".3gp");
			mMediaRecorder.setOutputFile(mVideoFile.getPath());

			mMediaRecorder.setVideoSize(960, 720);
			mMediaRecorder.setVideoFrameRate(videoFramesPerSecond);
			mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
			mMediaRecorder.setMaxDuration(maxDurationInMs);
		}

//		private Bitmap GetCurrentVideoThumbnail() {
//			// selection
//			String selection = MediaStore.Video.Media.DATA + " = '"
//					+ this.mVideoFile + "'";
//
//			ContentResolver cr = getContentResolver();
//			Cursor cursor = cr.query(
//					MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//					new String[] { MediaStore.Video.Media._ID }, selection,
//					null, null);
//
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inDither = false;
//			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//
//			if (cursor.moveToFirst()) {
//				int id = cursor.getInt(cursor
//						.getColumnIndex(MediaStore.Video.Media._ID));
//
//				Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(cr,
//						id, Images.Thumbnails.MICRO_KIND, options);
//				cursor.close();
//				return thumbnail;
//			} else {
//				cursor.close();
//				return null;
//			}
//		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// String msgFromListViewAct = data.getExtras().getString("Test");
		switch (resultCode) {
		case 0: {
			// mTextView = (TextView) findViewById(R.id.textView1);
			// mTextView.setText(msgFromListViewAct);
			break;
		}
		default:
			break;
		}
	}

	public class NavigateToListViewListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mCamera = Camera.open();
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
			Log.i(TAG, "SurfaceHolder.Callback: surfaceCreated!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (null != mCamera) {
				mCamera.release();
				mCamera = null;
			}
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (isPreview) {
			mCamera.stopPreview();
		}
		if (null != mCamera) {
			// TODO Auto-generated method stub
			Log.i(TAG, "SurfaceHolder.Callback:surfaceChanged!");
			// 已经获得Surface的width和height，设置Camera的参数

			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(width, height);
//			List<Size> vSizeList = parameters.getSupportedPictureSizes();
//
//			for (int num = 0; num < vSizeList.size(); num++) {
//				Size vSize = vSizeList.get(num);
//			}
			if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
				// 如果是竖屏
				// parameters.set("orientation", "portrait");
				// 在2.2以上可以使用
				mCamera.setDisplayOrientation(90);
			} else {
				parameters.set("orientation", "landscape");
				// 在2.2以上可以使用
				mCamera.setDisplayOrientation(0);
			}
			// mCamera.setParameters(parameters);
			try {
				// 设置显示
				mCamera.setPreviewDisplay(holder);
			} catch (IOException exception) {
				mCamera.release();
				mCamera = null;
			}

			// 开始预览
			mCamera.startPreview();
			isPreview = true;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.i(TAG, "SurfaceHolder.Callback：Surface Destroyed");
		if (null != mCamera) {
			mCamera.setPreviewCallback(null); /*
											 * 在启动PreviewCallback时这个必须在前不然退出出错。
											 * 这里实际上注释掉也没关系
											 */
			mCamera.stopPreview();
			isPreview = false;
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		PowerManager pManager = ((PowerManager) getSystemService(POWER_SERVICE));
		mWakeLock = pManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE, TAG);
		mWakeLock.acquire();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (null != mWakeLock) {
			mWakeLock.release();
		}
	}
	
	
}
