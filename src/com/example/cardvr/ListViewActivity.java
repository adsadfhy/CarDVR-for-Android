package com.example.cardvr;

import java.sql.Date;
import java.util.ArrayList;
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
	private String[] mListTitle = { "ʱ��", "�Ա�", "����", "��ס��", "����" };
	private String[] mListStr = { "����MOMO", "��", "25", "����",
			"xuanyusong@gmail.com" };

	private ArrayList<Bitmap> mVideoThumbnailers = null;
	private ArrayList<String> mVideoStartTime = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		arrayList = this;
		setContentView(R.layout.activity_list_view);

		mVideoThumbnailers = new ArrayList<Bitmap>();
		mVideoStartTime = new ArrayList<String>();
		
		InitialVideoThumbnails();

		mVideoListView = (ListView) findViewById(R.id.listView1);
		myAdapter = new MyListAdapter(this, R.layout.arraylist);
		mVideoListView.setAdapter(myAdapter);

		mReturnButton = (ImageButton) findViewById(R.id.imageButton1);
		mReturnButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent intent = new Intent(ListViewActivity.this,
				// MainActivity.class);
				// startActivity(intent);

				Intent returnIntent = new Intent(ListViewActivity.this,
						MainActivity.class);
				returnIntent.putExtra("Test", "Hello, CarDVR!");
				setResult(0, returnIntent);
				finish();
			}
		});

	}

	private void InitialVideoThumbnails() {
		// MediaStore.Video.Thumbnails.DATA:��Ƶ����ͼ���ļ�·��
		String[] thumbColumns = { MediaStore.Video.Thumbnails.DATA,
				MediaStore.Video.Thumbnails.VIDEO_ID };

		// MediaStore.Video.Media.DATA����Ƶ�ļ�·����
		// MediaStore.Video.Media.DISPLAY_NAME : ��Ƶ�ļ������� testVideo.mp4
		// MediaStore.Video.Media.TITLE: ��Ƶ���� : testVideo
		String[] mediaColumns = { MediaStore.Video.Media._ID,
				MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE
				};
		// selection
		String selection = MediaStore.Video.Media.DATA + " like '%mnt/sdcard/DVR%'";
		// set query directory
		String path = Environment.getExternalStorageDirectory() + "/DVR";
		// selectionArgs��
		String[] selectionArgs = { path};

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

				Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(
						cr, id, Images.Thumbnails.MICRO_KIND,
						options);		
				String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
				String timeMillis = title.substring(3, title.indexOf(".3gp"));				
				Date date = new Date(Long.parseLong(timeMillis));
				mVideoStartTime.add(date.toLocaleString());
				mVideoThumbnailers.add(thumbnail);
			} while (cursor.moveToNext());
		}
		cursor.close();		
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
			return mVideoThumbnailers.size();
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
			ImageButton button = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						mTextViewResourceID, null);				
			}
			image = (ImageView) convertView.findViewById(R.id.array_image);
			title = (TextView) convertView.findViewById(R.id.array_title);
			text = (TextView) convertView.findViewById(R.id.array_text);
			button = (ImageButton) convertView
					.findViewById(R.id.array_button);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Toast.makeText(arrayList, "������ĵ�" + position + "����ť",
							Toast.LENGTH_LONG).show();

				}
			});
			
			int colorPos = position % colors.length;
			convertView.setBackgroundColor(colors[colorPos]);
			title.setText(mListTitle[0]);
			text.setText(mVideoStartTime.get(position));
			image.setImageBitmap(mVideoThumbnailers.get(position));
			// if (colorPos == 0)
			// iamge.setImageResource(R.drawable.jay);
			// else
			// iamge.setImageResource(R.drawable.image);
			return convertView;
		}
	}
}
