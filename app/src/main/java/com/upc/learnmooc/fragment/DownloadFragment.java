package com.upc.learnmooc.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.upc.learnmooc.R;
import com.upc.learnmooc.utils.SystemBarTintManager;

import java.io.File;

/**
 * 下载页
 * Created by Explorer on 2016/2/10.
 */
public class DownloadFragment extends BaseFragment {


	private String totalStr;
	private String availStr;

	@ViewInject(R.id.tv_total_size)
	private TextView tvTotalSize;
	@ViewInject(R.id.tv_surplus)
	private TextView tvSurplusSize;

	@ViewInject(R.id.lv_download_list)
	private ListView mListView;
	private String hasUsedStr;
	private File file;
	private String[] videoList;
	private ViewStub viewStub;

	@Override
	public View initViews() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
			SystemBarTintManager tintManager = new SystemBarTintManager(mActivity);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintResource(R.color.status_color);//通知栏所需颜色
		}
		View view = View.inflate(mActivity, R.layout.download_fragment, null);
		viewStub = (ViewStub) view.findViewById(R.id.vs_blank_content);

		//view和事件注入
		ViewUtils.inject(this, view);
		return view;
	}

	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {
		Window win = mActivity.getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	@Override
	public void initData() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			long availableBlocks = stat.getAvailableBlocks();

			long totalSize = totalBlocks * blockSize;
			long availSize = availableBlocks * blockSize;

			long hasUsed = totalSize - availSize;

			totalStr = Formatter.formatFileSize(mActivity, totalSize);
			availStr = Formatter.formatFileSize(mActivity, availSize);
			hasUsedStr = Formatter.formatFileSize(mActivity, hasUsed);

			file = new File(Environment.getExternalStorageDirectory() + "/downloadVieo");

		} else {
			//如果没有SD卡
			File path2 = Environment.getDataDirectory();
			StatFs stat2 = new StatFs(path2.getPath());
			long blockSize2 = stat2.getBlockSize();
			long totalBlocks2 = stat2.getBlockCount();
			long availableBlocks2 = stat2.getAvailableBlocks();

			long totalSize2 = totalBlocks2 * blockSize2;
			long availSize2 = availableBlocks2 * blockSize2;

			long hasUsed = totalSize2 - availSize2;

			totalStr = Formatter.formatFileSize(mActivity, totalSize2);
			availStr = Formatter.formatFileSize(mActivity, availSize2);
			hasUsedStr = Formatter.formatFileSize(mActivity, hasUsed);

			file = new File(Environment.getDataDirectory() + "/downloadVieo");
		}
		tvTotalSize.setText(hasUsedStr);
		tvSurplusSize.setText(availStr);

		videoList = file.list();
		if (videoList != null) {
			mListView.setAdapter(new DownLoadListAdapter());
		} else {
			View contentView = viewStub.inflate();
			contentView.setVisibility(View.VISIBLE);
			TextView tvHint = (TextView) contentView.findViewById(R.id.tv_hint);
			TextView tvHintDetail = (TextView) contentView.findViewById(R.id.tv_hint_detail);
			tvHint.setText("没有下载内容");
			tvHintDetail.setText("快去下载喜欢的课程学习吧^_^");

		}
	}

	/**
	 * listview数据适配器
	 */
	class DownLoadListAdapter extends BaseAdapter {


		@Override
		public int getCount() {
			return videoList.length;
		}

		@Override
		public Object getItem(int position) {
			return videoList[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mActivity, R.layout.item_download_listview, null);
				holder = new ViewHolder();
				holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_video);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tvTitle.setText(videoList[position]);
			return convertView;
		}
	}

	static class ViewHolder {
		public TextView tvTitle;
	}

}
