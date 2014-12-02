package com.example.cardvr;

import java.util.ArrayList;

import com.example.test.R;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;


public class MainActivity extends Activity {
	private SurfaceView mPreviewSV = null; //‘§¿¿SurfaceView  
	private ImageView mVideoThumnail = null;
	private ImageButton mVideoButton = null;
	
	private ListView mVideoListView = null;
	private ImageButton mReturnButton = null;
	
	private Camera mCamera = null;
	private ArrayList<View> mListViewChildren = null;
		
	private static final String[] strs = new String[] {
	    "first1", "second2", "third3", "fourth", "fifth"
	    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_main);						
		
//		mGridLayout2 = (GridLayout) findViewById(R.id.gridLayout2);
//		mGridLayout2.setVisibility(android.view.View.INVISIBLE);
		
//		mGridLayout = (GridLayout) findViewById(R.id.gridLayout1);
		mVideoThumnail = (ImageView) findViewById(R.id.imageView1);
		mVideoButton = (ImageButton) findViewById(R.id.imageButton1);				
		
		this.mVideoThumnail.setOnClickListener(new NavigateToListViewListener()); 
		
		mVideoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	
			}
		});
		
	}

	public class NavigateToListViewListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub				
			setContentView(R.layout.gridforvideolistview);
			mVideoListView = (ListView) findViewById(R.id.listView1);
			mVideoListView.setAdapter(new ArrayAdapter<String>(v.getContext(), R.layout.textview, strs));
			
			mReturnButton = (ImageButton) findViewById(R.id.imageButton1);
			mReturnButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setContentView(R.layout.activity_main);	
					mVideoThumnail = (ImageView) findViewById(R.id.imageView1);									
					mVideoThumnail.setOnClickListener(new NavigateToListViewListener()); 
					
				}
			});
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
}
