package com.example.cardvr;

import com.example.test.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
	private String[] mListTitle = { "姓名", "性别", "年龄", "居住地", "邮箱" };
	private String[] mListStr = { "雨松MOMO", "男", "25", "北京",
			"xuanyusong@gmail.com" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		arrayList = this;
		setContentView(R.layout.activity_list_view);
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
			return mListStr.length;
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
			ImageView iamge = null;
			TextView title = null;
			TextView text = null;
			ImageButton button = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						mTextViewResourceID, null);
				iamge = (ImageView) convertView.findViewById(R.id.array_image);
				title = (TextView) convertView.findViewById(R.id.array_title);
				text = (TextView) convertView.findViewById(R.id.array_text);
				button = (ImageButton) convertView.findViewById(R.id.array_button);
				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Toast.makeText(arrayList, "您点击的第" + position + "个按钮",
								Toast.LENGTH_LONG).show();

					}
				});
			}
			int colorPos = position % colors.length;
			convertView.setBackgroundColor(colors[colorPos]);
			title.setText(mListTitle[position]);
			text.setText(mListStr[position]);
			if (colorPos == 0)
				iamge.setImageResource(R.drawable.jay);
			else
				iamge.setImageResource(R.drawable.image);
			return convertView;
		}
	}
}
