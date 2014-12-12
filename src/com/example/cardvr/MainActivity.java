package com.example.cardvr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test.R;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
	private static final String tag = "yan";

	private SurfaceView mPreviewSV = null; // 预览SurfaceView
	private SurfaceHolder mSurfaceHolder = null;
	private boolean isPreview = false;
	private ImageView mVideoThumnail = null;
	private ImageButton mVideoButton = null;
	private TextView mTextView = null;
	private Camera mCamera = null;
	private ArrayList<View> mListViewChildren = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

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

		mVideoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String msgFromListViewAct = data.getExtras().getString("Test");
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
			Log.i(tag, "SurfaceHolder.Callback: surfaceCreated!");
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
		if (null != mCamera) {
			// TODO Auto-generated method stub
			Log.i(tag, "SurfaceHolder.Callback:surfaceChanged!");
			// 已经获得Surface的width和height，设置Camera的参数

			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(width, height);
			List<Size> vSizeList = parameters.getSupportedPictureSizes();

			for (int num = 0; num < vSizeList.size(); num++) {
				Size vSize = vSizeList.get(num);
			}
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
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.i(tag, "SurfaceHolder.Callback：Surface Destroyed");
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
}
