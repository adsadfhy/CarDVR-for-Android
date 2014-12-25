package com.example.cardvr;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.test.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewActivity extends Activity {
	private ListView mVideoListView = null;
	private ImageButton mReturnButton = null;
	private MyListAdapter myAdapter = null;
	ListViewActivity arrayList = null;
	private String[] mListTitle = { "时间", "性别", "年龄", "居住地", "邮箱" };
	private String[] mListStr = { "雨松MOMO", "男", "25", "北京",
			"xuanyusong@gmail.com" };

	private ArrayList<VideoInfo> mVideoInfoList = null;

	protected class VideoInfo {
		public VideoInfo(Bitmap thumbnail, Date startTime, String path) {
			// TODO Auto-generated constructor stub
			mThumbnail = thumbnail;
			mStartTime = startTime;
			mPath = path;
		}

		public Bitmap mThumbnail;
		public Date mStartTime;
		public String mPath;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		arrayList = this;
		setContentView(R.layout.activity_list_view);

		mVideoInfoList = new ArrayList<VideoInfo>();

		InitialVideoThumbnails();

		mVideoListView = (ListView) findViewById(R.id.listView1);
		myAdapter = new MyListAdapter(this, R.layout.arraylist);
		mVideoListView.setAdapter(myAdapter);

		mReturnButton = (ImageButton) findViewById(R.id.goBackImageButton);
		mReturnButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent intent = new Intent(ListViewActivity.this,
				// MainActivity.class);
				// startActivity(intent);

				Intent returnIntent = new Intent(ListViewActivity.this,
						MainActivity.class);
				startActivity(returnIntent);
			}
		});

	}

	private void InitialVideoThumbnails() {
		// MediaStore.Video.Thumbnails.DATA:视频缩略图的文件路径
		String[] thumbColumns = { MediaStore.Video.Thumbnails.DATA,
				MediaStore.Video.Thumbnails.VIDEO_ID };

		// MediaStore.Video.Media.DATA：视频文件路径；
		// MediaStore.Video.Media.DISPLAY_NAME : 视频文件名，如 testVideo.mp4
		// MediaStore.Video.Media.TITLE: 视频标题 : testVideo
		String[] mediaColumns = { MediaStore.Video.Media._ID,
				MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE };
		// selection
		String selection = MediaStore.Video.Media.DATA
				+ " like '%mnt/sdcard/DVR%'";
		// set query directory
		String path = Environment.getExternalStorageDirectory() + "/DVR";
		// selectionArgs：
		String[] selectionArgs = { path };

		ContentResolver cr = this.getContentResolver();
		Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				null, selection, null, null);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;

		if (cursor.moveToFirst()) {
			do {
				int id = cursor.getInt(cursor
						.getColumnIndex(MediaStore.Video.Media._ID));

				Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(cr,
						id, Images.Thumbnails.MICRO_KIND, options);
				// mVideoThumbnailers.add(thumbnail);

				String title = cursor
						.getString(cursor
								.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
				String timeMillis = title.substring(3, title.indexOf(".3gp"));
				Date date = new Date(Long.parseLong(timeMillis));
				// mVideoStartTime.add(date.toLocaleString());

				String videoPath = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
				// mVideoPaths.add(videoPath);

				this.mVideoInfoList.add(new VideoInfo(thumbnail, date,
						videoPath));
				Collections.sort(this.mVideoInfoList, new VideoComparator());

			} while (cursor.moveToNext());
		}
		cursor.close();
	}

	public class VideoComparator implements Comparator<VideoInfo> {

		@Override
		public int compare(VideoInfo lhs, VideoInfo rhs) {
			// TODO Auto-generated method stub
			if (lhs.mStartTime.after(rhs.mStartTime)) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	public class MyListAdapter extends ArrayAdapter<Object> {
		int mTextViewResourceID = 0;
		private Context mContext;

		public MyListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			mTextViewResourceID = textViewResourceId;
			mContext = context;
		}

		private int[] colors = new int[] { 0xff626569, 0xff4f5257 };

		public int getCount() {
			return mVideoInfoList.size();
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ImageView image = null;
			TextView title = null;
			TextView text = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						mTextViewResourceID, null);
			}
			image = (ImageView) convertView.findViewById(R.id.array_image);
			image.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// Toast.makeText(arrayList, "您点击的第" + position + "个按钮",
					// Toast.LENGTH_LONG).show();
					// Uri uri = Uri.parse(mVideoPaths.get(position));
					// // use system media player
					// Intent intent = new Intent(Intent.ACTION_VIEW);
					// Log.v("URI:::::::::", uri.toString());
					// intent.setDataAndType(uri, "video/3gpp");
					// startActivity(intent);
					Intent intent = new Intent(ListViewActivity.this,
							PlayVideoActivity.class);
					intent.setData(Uri.parse(mVideoInfoList.get(position).mPath));
					startActivity(intent);
				}
			});
			title = (TextView) convertView.findViewById(R.id.array_title);
			text = (TextView) convertView.findViewById(R.id.array_text);

			int colorPos = position % colors.length;
			convertView.setBackgroundColor(colors[colorPos]);
			title.setText(mVideoInfoList.get(position).mPath);
			text.setText(mVideoInfoList.get(position).mStartTime.toLocaleString());
			image.setImageBitmap(mVideoInfoList.get(position).mThumbnail);
			// if (colorPos == 0)
			// iamge.setImageResource(R.drawable.jay);
			// else
			// iamge.setImageResource(R.drawable.image);
			return convertView;
		}
	}
}
