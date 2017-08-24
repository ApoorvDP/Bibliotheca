package com.apoorv.bibliotheca;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.database.sqlite.SQLiteDatabase;

public class FileChooser extends Activity {

	static List<File> epubs;
	static List<String> names;
	ArrayAdapter<String> adapter;
	static File selected;
	boolean firstTime;
	//SQLiteDatabase db = null;
	//private static String DBNAME = "BOOKMARKS.db";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_chooser_layout);

		if ((epubs == null) || (epubs.size() == 0)) {
			epubs = epubList(Environment.getExternalStorageDirectory());
		}

		ListView list = (ListView) findViewById(R.id.fileListView);
		names = fileNames(epubs);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, names);

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View itemView,
					int position, long itemId) {
				selected = epubs.get(position);
				Intent resultIntent = new Intent();
				resultIntent.putExtra("bpath", selected.getAbsolutePath());
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
		});

		list.setAdapter(adapter);
		
		//db = openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE, null);
	}

	private List<String> fileNames(List<File> files) {
		List<String> res = new ArrayList<String>();
		for (int i = 0; i < files.size(); i++) {
			res.add(files.get(i).getName().replace(".epub", ""));
			/*
			NOTE: future expansion
			res.add(files.get(i).getName().replace(".epub", "").replace(".e0", ""));
			*/
		}
		return res;
	}

	private List<File> epubList(File dir) {
		List<File> res = new ArrayList<File>();
		if (dir.isDirectory()) {
			File[] f = dir.listFiles();
			if (f != null) {
				for (int i = 0; i < f.length; i++) {
					if (f[i].isDirectory()) {
						res.addAll(epubList(f[i]));
					} else {
						String lowerCasedName = f[i].getName().toLowerCase();
						if (lowerCasedName.endsWith(".epub")) {
							res.add(f[i]);
						}

						/*
						 * NOTE: future
						if ((lowerCasedName.endsWith(".epub"))
								|| (lowerCasedName.endsWith(".e0"))) {
							res.add(f[i]);
						}
						*/
					}
				}
			}
		}
		return res;
	}

	private void refreshList() {
		epubs = epubList(Environment.getExternalStorageDirectory());
		names.clear();
		names.addAll(fileNames(epubs));
		this.adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.file_chooser, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.update:
			refreshList();
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
