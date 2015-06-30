/*
 * Copyright (C) 2015 creativeongreen
 *
 * Licensed either under the Apache License, Version 2.0, or (at your option)
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation (subject to the "Classpath" exception),
 * either version 2, or any later version (collectively, the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     http://www.gnu.org/licenses/
 *     http://www.gnu.org/software/classpath/license.html
 *
 * or as provided in the LICENSE.txt file that accompanied this code.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.creativeongreen.imageeffects;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.creativeongreen.imageeffects.listview.FileListAdapter;
import com.creativeongreen.imageeffects.listview.FileListEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author creativeongreen
 * 
 *         MainActivity
 * 
 */
public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	private static final String LOG_TAG = "IMGEFE_MainActivity";
	private static final boolean DEBUG = false;

	private static final int MSG_HANDLER_IMAGE_PROCESSING_FINISHED = 1;
	private static final String DIR_STORAGE_IMAGE = "effects";

	private static final Object[][] EFFECT_PARAMS = {
			// {R.layout.fragment_main, {R.id.iv_1, R.id.tv_param_1, R.id.sb_slider_1}},
			{ R.layout.fragment_main }, { R.layout.fragment_main } };

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	private static ImageView iv1;
	private static ListView lv1;
	private static Bitmap bmImageSrc, bmImageEffected;
	private static ProgressBar pbOnProcessing;
	private static boolean selectImageClicked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (DEBUG)
			Log.d(LOG_TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		bmImageSrc = getBitmapFromAsset(this, "image_src_1.jpg");
		bmImageEffected = bmImageSrc;

		int w = 10;
		int h = 10;
		double radius = 2;
		double cx = w / 2; // bmImage.getWidth() / 2;
		double cy = h / 2; // bmImage.getHeight() / 2;

		for (int i = 0; i < w; i++) {
			Log.d(LOG_TAG, "----- i=" + i);
			for (int j = 0; j < h; j++) {
				double r = Math.sqrt(Math.pow(i - cx, 2) + Math.pow(j - cy, 2));
				// compute angle atan2(y, x) in range (-PI..PI] n polar coordinate system
				double a = Math.atan2(j - cy, i - cx);
				// rn = r ^ k, k=1..2
				double rn = Math.pow(r, radius);// / 10.0) / (radius);
				// compute mapping point and then shift to center point
				int kx = (int) (rn * Math.cos(a) + cx);
				int ky = (int) (rn * Math.sin(a) + cy);
				// if (i == 0)
				Log.d(LOG_TAG, "(" + i + "," + j + ") -> (" + kx * w / rn + ","
						+ ky * h / rn + ")");
			}
		}

	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		if (DEBUG)
			Log.d(LOG_TAG, "onNavigationDrawerItemSelected()/position= "
					+ position);
		// update the main content by replacing fragments
		Fragment fragment = PlaceholderFragment.newInstance(position + 1);
		;
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container, fragment)
				.commit();
	}

	public void onSectionAttached(int number) {
		if (DEBUG)
			Log.d(LOG_TAG, "onSectionAttached()/number= " + number);

		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		case 4:
			mTitle = getString(R.string.title_section4);
			break;
		case 5:
			mTitle = getString(R.string.title_section5);
			break;
		case 6:
			mTitle = getString(R.string.title_section6);
			break;
		case 7:
			mTitle = getString(R.string.title_section7);
			break;
		case 8:
			mTitle = getString(R.string.title_section8);
			break;
		case 9:
			mTitle = getString(R.string.title_section9);
			break;
		case 10:
			mTitle = getString(R.string.title_section10);
			break;
		case 11:
			mTitle = getString(R.string.title_section11);
			break;
		case 12:
			mTitle = getString(R.string.title_section12);
			break;
		}
	}

	public void restoreActionBar() {
		if (DEBUG)
			Log.d(LOG_TAG, "restoreActionBar()");
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (DEBUG)
			Log.d(LOG_TAG, "onCreateOptionsMenu()");
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (DEBUG)
			Log.d(LOG_TAG, "onOptionsItemSelected()");
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements
			OnSeekBarChangeListener {
		/**
		 * The fragment argument representing the section number for this fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		private static int selectedSectionNumber = 0;

		private TextView tv1, tv2, tv3;
		private SeekBar seekBar1, seekBar2, seekBar3;
		private int mCurrentValue, mMinValue, mMaxValue;
		private int mCurrentValue2, mMinValue2, mMaxValue2;
		private int mCurrentValue3, mMinValue3, mMaxValue3;

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			if (DEBUG)
				Log.d(LOG_TAG,
						"PlaceholderFragment/newInstance()/sectionNumber= "
								+ sectionNumber);
			selectedSectionNumber = sectionNumber;
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
			if (DEBUG)
				Log.d(LOG_TAG, "PlaceholderFragment()");
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = null;
			Thread tImageProcessing;

			if (DEBUG)
				Log.d(LOG_TAG, "PlaceholderFragment/onCreateView()");

			switch (selectedSectionNumber) {
			case 1:
				rootView = inflater.inflate(R.layout.fragment_main, container,
						false);
				lv1 = (ListView) rootView.findViewById(R.id.lv_1);
				lv1.setVisibility(rootView.GONE);
				iv1 = (ImageView) rootView.findViewById(R.id.iv_1);
				iv1.setImageBitmap(bmImageSrc);
				break;
			case 2:
				rootView = inflater.inflate(R.layout.fragment_main, container,
						false);
				lv1 = (ListView) rootView.findViewById(R.id.lv_1);
				lv1.setVisibility(rootView.GONE);
				tv1 = (TextView) rootView.findViewById(R.id.tv_param_1);
				iv1 = (ImageView) rootView.findViewById(R.id.iv_1);
				pbOnProcessing = (ProgressBar) rootView.findViewById(R.id.pb_1);
				pbOnProcessing.setVisibility(rootView.VISIBLE);

				tImageProcessing = new Thread(new Runnable() {
					@Override
					public void run() {
						bmImageEffected = gray(bmImageSrc);
						hImageProcessingMessageReceiver.obtainMessage(
								MSG_HANDLER_IMAGE_PROCESSING_FINISHED)
								.sendToTarget();
					} // /run()
				});

				tImageProcessing.start();
				break;
			case 3:
				rootView = inflater.inflate(R.layout.fragment_main_1,
						container, false);
				lv1 = (ListView) rootView.findViewById(R.id.lv_1);
				lv1.setVisibility(rootView.GONE);
				tv1 = (TextView) rootView.findViewById(R.id.tv_param_1);
				iv1 = (ImageView) rootView.findViewById(R.id.iv_1);
				pbOnProcessing = (ProgressBar) rootView.findViewById(R.id.pb_1);
				pbOnProcessing.setVisibility(rootView.VISIBLE);

				mCurrentValue = 10;
				mMinValue = -255;
				mMaxValue = 255;
				tv1.setText("Brightness: " + mCurrentValue);
				seekBar1 = (SeekBar) rootView.findViewById(R.id.sb_slider_1);
				seekBar1.setMax(mMaxValue - mMinValue);
				seekBar1.setProgress(mCurrentValue - mMinValue);
				seekBar1.setOnSeekBarChangeListener(this);

				tImageProcessing = new Thread(new Runnable() {
					@Override
					public void run() {
						bmImageEffected = bright(bmImageSrc, mCurrentValue);
						hImageProcessingMessageReceiver.obtainMessage(
								MSG_HANDLER_IMAGE_PROCESSING_FINISHED)
								.sendToTarget();
					} // /run()
				});

				tImageProcessing.start();
				break;
			case 4:
				rootView = inflater.inflate(R.layout.fragment_main, container,
						false);
				lv1 = (ListView) rootView.findViewById(R.id.lv_1);
				lv1.setVisibility(rootView.GONE);
				tv1 = (TextView) rootView.findViewById(R.id.tv_param_1);
				iv1 = (ImageView) rootView.findViewById(R.id.iv_1);
				pbOnProcessing = (ProgressBar) rootView.findViewById(R.id.pb_1);
				pbOnProcessing.setVisibility(rootView.VISIBLE);

				tImageProcessing = new Thread(new Runnable() {
					@Override
					public void run() {
						bmImageEffected = invert(bmImageSrc);
						hImageProcessingMessageReceiver.obtainMessage(
								MSG_HANDLER_IMAGE_PROCESSING_FINISHED)
								.sendToTarget();
					} // /run()
				});

				tImageProcessing.start();
				break;
			case 5:
				rootView = inflater.inflate(R.layout.fragment_main_1,
						container, false);
				lv1 = (ListView) rootView.findViewById(R.id.lv_1);
				lv1.setVisibility(rootView.GONE);
				tv1 = (TextView) rootView.findViewById(R.id.tv_param_1);
				iv1 = (ImageView) rootView.findViewById(R.id.iv_1);
				pbOnProcessing = (ProgressBar) rootView.findViewById(R.id.pb_1);
				pbOnProcessing.setVisibility(rootView.VISIBLE);
				mCurrentValue = 90;
				mMinValue = 0;
				mMaxValue = 360;
				tv1.setText("Angle: " + mCurrentValue);
				seekBar1 = (SeekBar) rootView.findViewById(R.id.sb_slider_1);
				seekBar1.setMax(mMaxValue - mMinValue);
				seekBar1.setProgress(mCurrentValue - mMinValue);
				seekBar1.setOnSeekBarChangeListener(this);

				tImageProcessing = new Thread(new Runnable() {
					@Override
					public void run() {
						bmImageEffected = tint(bmImageSrc, mCurrentValue);
						hImageProcessingMessageReceiver.obtainMessage(
								MSG_HANDLER_IMAGE_PROCESSING_FINISHED)
								.sendToTarget();
					} // /run()
				});

				tImageProcessing.start();
				break;
			case 6:
				rootView = inflater.inflate(R.layout.fragment_main_1,
						container, false);
				lv1 = (ListView) rootView.findViewById(R.id.lv_1);
				lv1.setVisibility(rootView.GONE);
				tv1 = (TextView) rootView.findViewById(R.id.tv_param_1);
				iv1 = (ImageView) rootView.findViewById(R.id.iv_1);
				pbOnProcessing = (ProgressBar) rootView.findViewById(R.id.pb_1);
				pbOnProcessing.setVisibility(rootView.VISIBLE);
				mCurrentValue = 5;
				mMinValue = 1;
				mMaxValue = 30;
				tv1.setText("Blur radius: " + mCurrentValue);
				seekBar1 = (SeekBar) rootView.findViewById(R.id.sb_slider_1);
				seekBar1.setMax(mMaxValue - mMinValue);
				seekBar1.setProgress(mCurrentValue - mMinValue);
				seekBar1.setOnSeekBarChangeListener(this);

				tImageProcessing = new Thread(new Runnable() {
					@Override
					public void run() {
						bmImageEffected = cartoonify(bmImageSrc, mCurrentValue);
						hImageProcessingMessageReceiver.obtainMessage(
								MSG_HANDLER_IMAGE_PROCESSING_FINISHED)
								.sendToTarget();
					} // /run()
				});

				tImageProcessing.start();
				break;
			case 7:
				rootView = inflater.inflate(R.layout.fragment_main_1,
						container, false);
				lv1 = (ListView) rootView.findViewById(R.id.lv_1);
				lv1.setVisibility(rootView.GONE);
				tv1 = (TextView) rootView.findViewById(R.id.tv_param_1);
				iv1 = (ImageView) rootView.findViewById(R.id.iv_1);
				pbOnProcessing = (ProgressBar) rootView.findViewById(R.id.pb_1);
				pbOnProcessing.setVisibility(rootView.VISIBLE);
				mCurrentValue = 10;
				mMinValue = 1;
				mMaxValue = 30;
				tv1.setText("Blur radius: " + mCurrentValue);
				seekBar1 = (SeekBar) rootView.findViewById(R.id.sb_slider_1);
				seekBar1.setMax(mMaxValue - mMinValue);
				seekBar1.setProgress(mCurrentValue - mMinValue);
				seekBar1.setOnSeekBarChangeListener(this);

				tImageProcessing = new Thread(new Runnable() {
					@Override
					public void run() {
						bmImageEffected = pencilSketch(bmImageSrc,
								mCurrentValue);
						hImageProcessingMessageReceiver.obtainMessage(
								MSG_HANDLER_IMAGE_PROCESSING_FINISHED)
								.sendToTarget();
					} // /run()
				});

				tImageProcessing.start();
				break;
			case 8:
				rootView = inflater.inflate(R.layout.fragment_main_1,
						container, false);
				lv1 = (ListView) rootView.findViewById(R.id.lv_1);
				lv1.setVisibility(rootView.GONE);
				tv1 = (TextView) rootView.findViewById(R.id.tv_param_1);
				iv1 = (ImageView) rootView.findViewById(R.id.iv_1);
				pbOnProcessing = (ProgressBar) rootView.findViewById(R.id.pb_1);
				pbOnProcessing.setVisibility(rootView.VISIBLE);

				mCurrentValue = 10;
				mMinValue = 1;
				mMaxValue = 100;
				tv1.setText("Intensity: " + mCurrentValue);
				seekBar1 = (SeekBar) rootView.findViewById(R.id.sb_slider_1);
				seekBar1.setMax(mMaxValue - mMinValue);
				seekBar1.setProgress(mCurrentValue - mMinValue);
				seekBar1.setOnSeekBarChangeListener(this);

				tImageProcessing = new Thread(new Runnable() {
					@Override
					public void run() {
						bmImageEffected = fastblur(bmImageSrc, mCurrentValue);
						hImageProcessingMessageReceiver.obtainMessage(
								MSG_HANDLER_IMAGE_PROCESSING_FINISHED)
								.sendToTarget();
					} // /run()
				});

				tImageProcessing.start();
				break;
			case 9:
				rootView = inflater.inflate(R.layout.fragment_main_1,
						container, false);
				lv1 = (ListView) rootView.findViewById(R.id.lv_1);
				lv1.setVisibility(rootView.GONE);
				tv1 = (TextView) rootView.findViewById(R.id.tv_param_1);
				iv1 = (ImageView) rootView.findViewById(R.id.iv_1);
				pbOnProcessing = (ProgressBar) rootView.findViewById(R.id.pb_1);
				pbOnProcessing.setVisibility(rootView.VISIBLE);
				mCurrentValue = 5;
				mMinValue = 1;
				mMaxValue = 50;
				tv1.setText("Filter size: " + mCurrentValue);
				seekBar1 = (SeekBar) rootView.findViewById(R.id.sb_slider_1);
				seekBar1.setMax(mMaxValue - mMinValue);
				seekBar1.setProgress(mCurrentValue - mMinValue);
				seekBar1.setOnSeekBarChangeListener(this);

				tImageProcessing = new Thread(new Runnable() {
					@Override
					public void run() {
						bmImageEffected = mozac(bmImageSrc, mCurrentValue);
						hImageProcessingMessageReceiver.obtainMessage(
								MSG_HANDLER_IMAGE_PROCESSING_FINISHED)
								.sendToTarget();
					} // /run()
				});

				tImageProcessing.start();
				break;
			case 10:
				rootView = inflater.inflate(R.layout.fragment_main_3,
						container, false);
				lv1 = (ListView) rootView.findViewById(R.id.lv_1);
				lv1.setVisibility(rootView.GONE);
				tv1 = (TextView) rootView.findViewById(R.id.tv_param_1);
				tv2 = (TextView) rootView.findViewById(R.id.tv_param_2);
				tv3 = (TextView) rootView.findViewById(R.id.tv_param_3);
				iv1 = (ImageView) rootView.findViewById(R.id.iv_1);
				pbOnProcessing = (ProgressBar) rootView.findViewById(R.id.pb_1);
				pbOnProcessing.setVisibility(rootView.VISIBLE);
				mCurrentValue = 15;
				mMinValue = 10;
				mMaxValue = 20;
				tv1.setText("Radius: " + mCurrentValue / 10.0);
				seekBar1 = (SeekBar) rootView.findViewById(R.id.sb_slider_1);
				seekBar1.setMax(mMaxValue - mMinValue);
				seekBar1.setProgress(mCurrentValue - mMinValue);
				seekBar1.setOnSeekBarChangeListener(this);

				mCurrentValue2 = bmImageSrc.getWidth() / 2;
				mMinValue2 = 0;
				mMaxValue2 = bmImageSrc.getWidth() - 1;
				tv2.setText("Point X: " + mCurrentValue2);
				seekBar2 = (SeekBar) rootView.findViewById(R.id.sb_slider_2);
				seekBar2.setMax(mMaxValue2 - mMinValue2);
				seekBar2.setProgress(mCurrentValue2 - mMinValue2);
				seekBar2.setOnSeekBarChangeListener(this);

				mCurrentValue3 = bmImageSrc.getHeight() / 2;
				mMinValue3 = 0;
				mMaxValue3 = bmImageSrc.getHeight() - 1;
				tv3.setText("Point Y: " + mCurrentValue3);
				seekBar3 = (SeekBar) rootView.findViewById(R.id.sb_slider_3);
				seekBar3.setMax(mMaxValue3 - mMinValue3);
				seekBar3.setProgress(mCurrentValue3 - mMinValue3);
				seekBar3.setOnSeekBarChangeListener(this);

				tImageProcessing = new Thread(new Runnable() {
					@Override
					public void run() {
						bmImageEffected = bulging(bmImageSrc, mCurrentValue,
								mCurrentValue2, mCurrentValue3);
						hImageProcessingMessageReceiver.obtainMessage(
								MSG_HANDLER_IMAGE_PROCESSING_FINISHED)
								.sendToTarget();
					} // /run()
				});

				tImageProcessing.start();
				break;
			case 11:
				rootView = inflater.inflate(R.layout.fragment_main_1,
						container, false);
				lv1 = (ListView) rootView.findViewById(R.id.lv_1);
				lv1.setVisibility(rootView.GONE);
				tv1 = (TextView) rootView.findViewById(R.id.tv_param_1);
				iv1 = (ImageView) rootView.findViewById(R.id.iv_1);
				pbOnProcessing = (ProgressBar) rootView.findViewById(R.id.pb_1);
				pbOnProcessing.setVisibility(rootView.VISIBLE);
				mCurrentValue = 50;
				mMinValue = 1;
				mMaxValue = bmImageSrc.getWidth();
				tv1.setText("Amplitude: " + mCurrentValue);
				seekBar1 = (SeekBar) rootView.findViewById(R.id.sb_slider_1);
				seekBar1.setMax(mMaxValue - mMinValue);
				seekBar1.setProgress(mCurrentValue - mMinValue);
				seekBar1.setOnSeekBarChangeListener(this);

				tImageProcessing = new Thread(new Runnable() {
					@Override
					public void run() {
						bmImageEffected = myTest(bmImageSrc, mCurrentValue);
						hImageProcessingMessageReceiver.obtainMessage(
								MSG_HANDLER_IMAGE_PROCESSING_FINISHED)
								.sendToTarget();
					} // /run()
				});

				tImageProcessing.start();
				break;
			case 12:
				rootView = inflater.inflate(R.layout.fragment_main_1,
						container, false);
				lv1 = (ListView) rootView.findViewById(R.id.lv_1);
				lv1.setVisibility(rootView.GONE);
				tv1 = (TextView) rootView.findViewById(R.id.tv_param_1);
				iv1 = (ImageView) rootView.findViewById(R.id.iv_1);
				pbOnProcessing = (ProgressBar) rootView.findViewById(R.id.pb_1);
				pbOnProcessing.setVisibility(rootView.VISIBLE);
				mCurrentValue = 50;
				mMinValue = 1;
				mMaxValue = bmImageSrc.getWidth();
				tv1.setText("Amplitude: " + mCurrentValue);
				seekBar1 = (SeekBar) rootView.findViewById(R.id.sb_slider_1);
				seekBar1.setMax(mMaxValue - mMinValue);
				seekBar1.setProgress(mCurrentValue - mMinValue);
				seekBar1.setOnSeekBarChangeListener(this);

				tImageProcessing = new Thread(new Runnable() {
					@Override
					public void run() {
						bmImageEffected = myTest(bmImageSrc, mCurrentValue);
						hImageProcessingMessageReceiver.obtainMessage(
								MSG_HANDLER_IMAGE_PROCESSING_FINISHED)
								.sendToTarget();
					} // /run()
				});

				tImageProcessing.start();
				break;

			} // /switch (selectedSectionNumber)

			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			if (DEBUG)
				Log.d(LOG_TAG, "PlaceholderFragment/onAttach()");

			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}

		// @Override
		protected void onSetInitialValue(boolean restorePersistedValue,
				Object defaultValue) {
			if (restorePersistedValue) {
				mCurrentValue = 90;
			} else {
				mCurrentValue = (int) (defaultValue);
			}
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// value of progress will be within the range between 0 .. (maxValue - minValue)
			// example: minValue = 50; maxValue = 100;
			// progress equal to 25 (0..100-50) means 50% progress,
			// and its actual value will be 75 (25+50)
			switch (selectedSectionNumber) {
			case 1:
				break;
			case 2:
				break;
			case 3:
				mCurrentValue = progress + mMinValue;
				tv1.setText("Brightness: " + mCurrentValue);
				break;
			case 4:
				break;
			case 5:
				mCurrentValue = progress + mMinValue;
				tv1.setText("Angle: " + mCurrentValue);
				break;
			case 6:
				mCurrentValue = progress + mMinValue;
				tv1.setText("Blur radius: " + mCurrentValue);
				break;
			case 7:
				mCurrentValue = progress + mMinValue;
				tv1.setText("Blur radius: " + mCurrentValue);
				break;
			case 8:
				mCurrentValue = progress + mMinValue;
				tv1.setText("Filter size: " + mCurrentValue);
				break;
			case 9:
				mCurrentValue = progress + mMinValue;
				tv1.setText("Intensity: " + mCurrentValue);
				break;
			case 10:
				if (seekBar == seekBar1) {
					mCurrentValue = progress + mMinValue;
					tv1.setText("Radius: " + mCurrentValue / 10.0);
				} else if (seekBar == seekBar2) {
					mCurrentValue2 = progress + mMinValue2;
					tv2.setText("Point X: " + mCurrentValue2);
				} else {
					mCurrentValue3 = progress + mMinValue3;
					tv3.setText("Point Y: " + mCurrentValue3);
				}
				break;
			case 11:
				mCurrentValue = progress + mMinValue;
				tv1.setText("Amplitude: " + mCurrentValue);
				break;
			case 12:
				mCurrentValue = progress + mMinValue;
				tv1.setText("Amplitude: " + mCurrentValue);
				break;
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

			pbOnProcessing.setVisibility(View.VISIBLE);
			Thread tImageProcessing = new Thread(new Runnable() {
				@Override
				public void run() {
					bmImageEffected = tint(bmImageSrc, mCurrentValue);
					switch (selectedSectionNumber) {
					case 1:
						bmImageEffected = bmImageSrc;
						break;
					case 2:
						bmImageEffected = gray(bmImageSrc);
						break;
					case 3:
						bmImageEffected = bright(bmImageSrc, mCurrentValue);
						break;
					case 4:
						bmImageEffected = invert(bmImageSrc);
						break;
					case 5:
						bmImageEffected = tint(bmImageSrc, mCurrentValue);
						break;
					case 6:
						bmImageEffected = cartoonify(bmImageSrc, mCurrentValue);
						break;
					case 7:
						bmImageEffected = pencilSketch(bmImageSrc,
								mCurrentValue);
						break;
					case 8:
						bmImageEffected = fastblur(bmImageSrc, mCurrentValue);
						break;
					case 9:
						bmImageEffected = mozac(bmImageSrc, mCurrentValue);
						break;
					case 10:
						bmImageEffected = bulging(bmImageSrc, mCurrentValue,
								mCurrentValue2, mCurrentValue3);
						break;
					case 11:
						bmImageEffected = myTest(bmImageSrc, mCurrentValue);
						break;
					case 12:
						bmImageEffected = myTest(bmImageSrc, mCurrentValue);
						break;
					}

					hImageProcessingMessageReceiver.obtainMessage(
							MSG_HANDLER_IMAGE_PROCESSING_FINISHED)
							.sendToTarget();
				} // /run()
			});

			tImageProcessing.start();
		}
	} // /class PlaceholderFragment

	private final static Handler hImageProcessingMessageReceiver = new Handler() {

		@Override
		public void handleMessage(final Message message) {
			switch (message.what) {
			case MSG_HANDLER_IMAGE_PROCESSING_FINISHED:
				pbOnProcessing.setVisibility(View.GONE);
				iv1.setImageBitmap(bmImageEffected);
				break;

			default:
				throw new IllegalArgumentException("cannot handle message");
			} // switch
		} // handleMessage(Message)

	};

	private static Bitmap getBitmapFromAsset(Context context, String file) {
		InputStream is = null;
		Bitmap bitmap = null;

		try {
			is = context.getAssets().open(file);
			bitmap = BitmapFactory.decodeStream(is);
		} catch (final IOException e) {
			bitmap = null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ignored) {
				}
			}
		}

		return bitmap;
	}

	private static Bitmap getBitmapFromExternalStorage(
			OnItemClickListener onItemClickListener, String file) {
		Bitmap bitmap = null;

		String fullPathFile = Environment
				.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_PICTURES).getAbsolutePath()
				.toString()
				+ File.separator + DIR_STORAGE_IMAGE + File.separator + file;

		// take action to prevent OutOfMemoryError: bitmap sized VM budget
		// when executing BitmapFactory
		// Runtime.getRuntime().gc(); // not work on this
		// solution: down size sampling
		BitmapFactory.Options bmFactoryOptions = new BitmapFactory.Options();
		bmFactoryOptions.inJustDecodeBounds = true; // the decoder will return null (no bitmap)
		bitmap = BitmapFactory.decodeFile(fullPathFile, bmFactoryOptions);

		// calculate the down size scale factor
		int inSampleSize = 1;
		int targetWidth = 360;
		int targetHeight = 480;

		if (bmFactoryOptions.outHeight > targetHeight
				|| bmFactoryOptions.outWidth > targetWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math
					.round((float) bmFactoryOptions.outHeight
							/ (float) targetHeight);
			final int widthRatio = Math.round((float) bmFactoryOptions.outWidth
					/ (float) targetWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		// configure scale size
		bmFactoryOptions = new BitmapFactory.Options();
		bmFactoryOptions.inJustDecodeBounds = false; // the decoder will return bitmap
		bmFactoryOptions.inSampleSize = inSampleSize;
		bitmap = BitmapFactory.decodeFile(fullPathFile, bmFactoryOptions);

		return bitmap;
	}

	public static Bitmap gray(Bitmap bmImage) {
		Bitmap bmTemp = Bitmap.createBitmap(bmImage.getWidth(),
				bmImage.getHeight(), bmImage.getConfig());

		// The W3C Algorithm
		double intensityRed = 0.299;
		double intensityGreen = 0.587;
		double intensityBlue = 0.114;

		for (int i = 0; i < bmImage.getWidth(); i++) {
			for (int j = 0; j < bmImage.getHeight(); j++) {
				int p = bmImage.getPixel(i, j);
				int r = Color.red(p);
				int g = Color.green(p);
				int b = Color.blue(p);

				/*
				 * r = g = b = (int) (r * intensityRed + g * intensityGreen + b intensityBlue);
				 * 
				 * bmTemp.setPixel(i, j, Color.argb(Color.alpha(p), r, g, b));
				 */
				// int rgb = (r * 77 + g * 151 + b * 28) >> 8; // NTSC luma
				int rgb = (int) Math.sqrt(r * r * 0.241 + g * g * 0.691 + b * b
						* 0.068); // HSP, where the P stands for Perceived brightness
				bmTemp.setPixel(i, j, (p & 0xff000000) | (rgb << 16)
						| (rgb << 8) | rgb);
			}
		}

		return bmTemp;
	}

	public static Bitmap bright(Bitmap bmImage, int brightness) {
		Bitmap bmTemp = Bitmap.createBitmap(bmImage.getWidth(),
				bmImage.getHeight(), bmImage.getConfig());

		for (int i = 0; i < bmImage.getWidth(); i++) {
			for (int j = 0; j < bmImage.getHeight(); j++) {
				int p = bmImage.getPixel(i, j);
				int r = Color.red(p);
				int g = Color.green(p);
				int b = Color.blue(p);
				int alpha = Color.alpha(p);

				r += brightness;
				g += brightness;
				b += brightness;
				alpha += brightness;
				r = r < 0 ? 0 : (r > 255 ? 255 : r);
				g = g < 0 ? 0 : (g > 255 ? 255 : g);
				b = b < 0 ? 0 : (b > 255 ? 255 : b);
				alpha = alpha < 0 ? 0 : (alpha > 255 ? 255 : alpha);

				bmTemp.setPixel(i, j, Color.argb(alpha, r, g, b));
			}
		}

		return bmTemp;
	}

	public static Bitmap tint(Bitmap bmImage, int degree) {
		Bitmap bmTemp = Bitmap.createBitmap(bmImage.getWidth(),
				bmImage.getHeight(), bmImage.getConfig());

		double angle = (3.14159d * (double) degree) / 180.0d;
		int S = (int) (256.0d * Math.sin(angle));
		int C = (int) (256.0d * Math.cos(angle));

		for (int i = 0; i < bmImage.getWidth(); i++) {
			for (int j = 0; j < bmImage.getHeight(); j++) {
				int p = bmImage.getPixel(i, j);
				int r = Color.red(p);
				int g = Color.green(p);
				int b = Color.blue(p);
				// int alpha = Color.alpha(p);

				int RY = (70 * r - 59 * g - 11 * b) / 100;
				int BY = (-30 * r - 59 * g + 89 * b) / 100;
				int Y = (30 * r + 59 * g + 11 * b) / 100;
				int RYY = (S * BY + C * RY) / 256;
				int BYY = (C * BY - S * RY) / 256;
				int GYY = (-51 * RYY - 19 * BYY) / 100;
				r = Y + RYY;
				r = (r < 0) ? 0 : ((r > 255) ? 255 : r);
				g = Y + GYY;
				g = (g < 0) ? 0 : ((g > 255) ? 255 : g);
				b = Y + BYY;
				b = (b < 0) ? 0 : ((b > 255) ? 255 : b);
				bmTemp.setPixel(i, j, Color.argb(Color.alpha(p), r, g, b));
			}
		}

		return bmTemp;
	}

	public static Bitmap invert(Bitmap bmImage) {
		Bitmap bmTemp = Bitmap.createBitmap(bmImage.getWidth(),
				bmImage.getHeight(), bmImage.getConfig());

		for (int i = 0; i < bmImage.getWidth(); i++) {
			for (int j = 0; j < bmImage.getHeight(); j++) {
				int p = bmImage.getPixel(i, j);
				bmTemp.setPixel(i, j, (p & 0xff000000) | (~p & 0x00ffffff));
			}
		}

		return bmTemp;
	}

	public static Bitmap mozac(Bitmap bmImage, int level) {
		Bitmap bmTemp = Bitmap.createBitmap(bmImage.getWidth(),
				bmImage.getHeight(), bmImage.getConfig());

		for (int i = 0; i < bmImage.getWidth(); i += level) {
			for (int j = 0; j < bmImage.getHeight(); j += level) {
				int p = 0;
				int r = 0;
				int g = 0;
				int b = 0;

				// compute neighboring area index
				int kx_start = i - level;
				int kx_end = i + level;
				int ky_start = j - level;
				int ky_end = j + level;

				// filter out boundary index
				kx_start = Math.max(0, kx_start);
				kx_end = Math.min(bmImage.getWidth() - 1, kx_end);
				ky_start = Math.max(0, ky_start);
				ky_end = Math.min(bmImage.getHeight() - 1, ky_end);

				// summing and averaging color value within the neighboring area
				for (int ki = kx_start; ki <= kx_end; ki++) {
					for (int kj = ky_start; kj <= ky_end; kj++) {
						p = bmImage.getPixel(ki, kj);
						r += Color.red(p);
						g += Color.green(p);
						b += Color.blue(p);
					}
				}

				int n = (kx_end - kx_start + 1) * (ky_end - ky_start + 1);
				r /= n;
				g /= n;
				b /= n;

				// copy color value to each pixel on neighboring area
				for (int kx = kx_start; kx <= kx_end; kx++) {
					for (int ky = ky_start; ky <= ky_end; ky++) {
						bmTemp.setPixel(kx, ky,
								Color.argb(Color.alpha(p), r, g, b));
					}
				}

			} // /for(j)
		} // /for(i)

		return bmTemp;
	}

	// a rounded swelling or protuberance that distorts a flat surface
	public static Bitmap bulging(Bitmap bmImage, int radius, int pointX,
			int pointY) {
		Bitmap bmTemp = Bitmap.createBitmap(bmImage.getWidth(),
				bmImage.getHeight(), bmImage.getConfig());
		// Bitmap bmTemp = bmImage.copy(Config.ARGB_8888, true);

		for (int i = 0; i < bmImage.getWidth(); i++) {
			for (int j = 0; j < bmImage.getHeight(); j++) {

				// get center point
				double cx = pointX; // bmImage.getWidth() / 2;
				double cy = pointY; // bmImage.getHeight() / 2;

				// compute distance to center point
				double r = Math.sqrt(Math.pow(i - cx, 2) + Math.pow(j - cy, 2));
				// compute angle atan2(y, x) in range (-PI..PI] n polar coordinate system
				double a = Math.atan2(j - cy, i - cx);
				// rn = r ^ k, k=1..2
				double rn = Math.pow(r, radius / 10.0) / (radius);
				// compute mapping point and then shift to center point
				int kx = (int) (rn * Math.cos(a) + cx);
				int ky = (int) (rn * Math.sin(a) + cy);

				if (kx >= 0 && kx < bmImage.getWidth() && ky >= 0
						&& ky < bmImage.getHeight())
					bmTemp.setPixel(i, j, bmImage.getPixel(kx, ky));
				else
					bmTemp.setPixel(i, j, 0x00ffffff);

			} // /for(j)
		} // /for(i)

		return bmTemp;
	}

	public static Bitmap myTest(Bitmap bmImage, int amplitude) {
		Bitmap bmTemp = Bitmap.createBitmap(bmImage.getWidth(),
				bmImage.getHeight(), bmImage.getConfig());
		// Bitmap bmTemp = bmImage.copy(Config.ARGB_8888, true);

		for (int i = 0; i < bmImage.getWidth(); i++) {
			for (int j = 0; j < bmImage.getHeight(); j++) {

				int sign;
				if (i <= bmImage.getWidth() / 2 || j <= bmImage.getHeight() / 2)
					sign = 1;
				else
					sign = -1;
				int ki = (int) (i + amplitude * sign
						* Math.cos(j * 2 * Math.PI / bmImage.getHeight()));
				int kj = (int) (j + amplitude * sign
						* Math.cos(i * 2 * Math.PI / bmImage.getWidth()));
				// * Math.tan(i * 2 * Math.PI / bmImage.getWidth()));
				// * Math.sin(i * 2 * Math.PI / bmImage.getWidth()));

				// k = Math.max(0, k);
				// k = Math.min(bmImage.getHeight() - 1, k);

				if (ki >= 0 && ki < bmImage.getWidth() && kj >= 0
						&& kj < bmImage.getHeight())
					bmTemp.setPixel(i, j, bmImage.getPixel(ki, kj));
				else
					bmTemp.setPixel(i, j, 0x00ffffff);

			} // /for(j)
		} // /for(i)

		return bmTemp;
	}

	//
	// src = source image
	// src-gray = Convert-To-Gray-Scale(src)
	// gray-invert = Invert-Colors(src-gray)
	// invert-blur = Apply-Gaussian-Blur(gray-invert)
	// pencilSketch = Color-Dodge-Blend-Merge(invert-blur, src-gray)
	//
	public static Bitmap pencilSketch(Bitmap bmImage, int blurRadius) {
		Bitmap bmTemp = Bitmap.createBitmap(bmImage.getWidth(),
				bmImage.getHeight(), bmImage.getConfig());

		Bitmap bmGrey = gray(bmImage);
		Bitmap bmInverted = invert(bmGrey);
		Bitmap bmBlurred = fastblur(bmInverted, blurRadius);
		Bitmap bmColorDodgeBlend = colorDodgeBlend(bmBlurred, bmGrey);

		return bmColorDodgeBlend;
	}

	//
	// src = source image
	// src-gray = Convert-To-Gray-Scale(src)
	// gray-invert = Invert-Colors(src-gray)
	// invert-blur = Apply-Gaussian-Blur(gray-invert)
	// pencilSketch = Color-Dodge-Blend-Merge(invert-blur, src-gray)
	// src-blur = Apply-Gaussian-Blur(src)
	// cartoon = Apply-Color(src-blur, pencilSketck)
	//
	public static Bitmap cartoonify(Bitmap bmImage, int blurRadius) {
		Bitmap bmTemp = Bitmap.createBitmap(bmImage.getWidth(),
				bmImage.getHeight(), bmImage.getConfig());

		Bitmap bmGrey = gray(bmImage);
		Bitmap bmInverted = invert(bmGrey);
		Bitmap bmBlurred = fastblur(bmInverted, blurRadius);
		Bitmap bmColorDodgeBlend = colorDodgeBlend(bmBlurred, bmGrey);
		Bitmap bmSrcBlurred = fastblur(bmImage, blurRadius);
		Bitmap bmCartoon = getCartoonizedBitmap(bmSrcBlurred,
				bmColorDodgeBlend, 90, 90, 90, 10, 10);

		return bmCartoon;
	}

	public static Bitmap blurRenderScript(Bitmap bmImage, int radius, View v) {
		final float BITMAP_SCALE = 0.4f;
		final float BLUR_RADIUS = 7.5f;

		int width = Math.round(bmImage.getWidth() * BITMAP_SCALE);
		int height = Math.round(bmImage.getHeight() * BITMAP_SCALE);

		Bitmap inputBitmap = Bitmap.createScaledBitmap(bmImage, width, height,
				false);
		Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

		RenderScript rs = RenderScript.create(v.getContext());
		Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
		Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
		//Allocation tmpIn = Allocation.createFromBitmap(rs, bmImage,
		//		Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
		//Allocation tmpOut = Allocation.createTyped(rs, tmpIn.getType());
		ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs,
				Element.U8_4(rs));
		theIntrinsic.setRadius(radius);
		theIntrinsic.setInput(tmpIn);
		theIntrinsic.forEach(tmpOut);
		tmpOut.copyTo(outputBitmap);

		return outputBitmap;
	}

	public static Bitmap fastblur(Bitmap bmImage, int radius) {

		// Stack Blur v1.0 from
		// http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
		//
		// Java Author: Mario Klingemann <mario at quasimondo.com>
		// http://incubator.quasimondo.com
		// created Feburary 29, 2004
		// Android port : Yahel Bouaziz <yahel at kayenko.com>
		// http://www.kayenko.com
		// ported april 5th, 2012

		// This is a compromise between Gaussian Blur and Box blur
		// It creates much better looking blurs than Box Blur, but is
		// 7x faster than my Gaussian Blur implementation.
		//
		// I called it Stack Blur because this describes best how this
		// filter works internally: it creates a kind of moving stack
		// of colors whilst scanning through the image. Thereby it
		// just has to add one new block of color to the right side
		// of the stack and remove the leftmost color. The remaining
		// colors on the topmost layer of the stack are either added on
		// or reduced by one, depending on if they are on the right or
		// on the left side of the stack.
		//
		// If you are using this algorithm in your code please add
		// the following line:
		//
		// Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

		Bitmap bmTemp = bmImage.copy(bmImage.getConfig(), true);

		if (radius < 1) {
			return (null);
		}

		int w = bmTemp.getWidth();
		int h = bmTemp.getHeight();

		int[] pix = new int[w * h];
		bmTemp.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}

		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
						| (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		bmTemp.setPixels(pix, 0, w, 0, 0, w, h);

		return bmTemp;
	}

	// hue, saturarion, value intervals size are for reduce colors on Bitmap
	// saturation, value percents are for increment or decrement [0..100..)
	public static Bitmap getCartoonizedBitmap(Bitmap realBitmap,
			Bitmap dodgeBlendBitmap, int hueIntervalSize,
			int saturationIntervalSize, int valueIntervalSize,
			int saturationPercent, int valuePercent) {
		// Bitmap bitmap = Bitmap.createBitmap(scaledBitmap);
		// //fastblur(scaledBitmap, 4);
		Bitmap base = fastblur(realBitmap, 3).copy(Config.ARGB_8888, true);
		Bitmap dodge = dodgeBlendBitmap.copy(Config.ARGB_8888, false);
		try {
			int realColor;
			int color;
			float top = 0.87f;// VALUE_TOP; // Between 0.0f .. 1.0f I use 0.87f
			IntBuffer templatePixels = IntBuffer.allocate(dodge.getWidth()
					* dodge.getHeight());
			IntBuffer scaledPixels = IntBuffer.allocate(base.getWidth()
					* base.getHeight());
			IntBuffer buffOut = IntBuffer.allocate(base.getWidth()
					* base.getHeight());

			base.copyPixelsToBuffer(scaledPixels);
			dodge.copyPixelsToBuffer(templatePixels);

			templatePixels.rewind();
			scaledPixels.rewind();
			buffOut.rewind();

			while (buffOut.position() < buffOut.limit()) {
				color = (templatePixels.get());
				realColor = scaledPixels.get();

				float[] realHSV = new float[3];
				Color.colorToHSV(realColor, realHSV);

				realHSV[0] = getRoundedValue(realHSV[0], hueIntervalSize);

				realHSV[2] = (getRoundedValue(realHSV[2] * 100,
						valueIntervalSize) / 100) * (valuePercent / 100);
				realHSV[2] = realHSV[2] < 1.0 ? realHSV[2] : 1.0f;

				realHSV[1] = realHSV[1] * (saturationPercent / 100);
				realHSV[1] = realHSV[1] < 1.0 ? realHSV[1] : 1.0f;

				float[] HSV = new float[3];
				Color.colorToHSV(color, HSV);

				boolean putBlackPixel = HSV[2] <= top;

				realColor = Color.HSVToColor(realHSV);

				if (putBlackPixel) {
					buffOut.put(color);
				} else {
					buffOut.put(realColor);
				}
			}// END WHILE
			dodge.recycle();
			buffOut.rewind();
			base.copyPixelsFromBuffer(buffOut);

		} catch (Exception e) {
			// TODO: handle exception
		}

		return base;
	}

	public static Bitmap colorDodgeBlend(Bitmap source, Bitmap layer) {
		Bitmap base = source.copy(Config.ARGB_8888, true);
		Bitmap blend = layer.copy(Config.ARGB_8888, false);

		IntBuffer buffBase = IntBuffer.allocate(base.getWidth()
				* base.getHeight());
		base.copyPixelsToBuffer(buffBase);
		buffBase.rewind();

		IntBuffer buffBlend = IntBuffer.allocate(blend.getWidth()
				* blend.getHeight());
		blend.copyPixelsToBuffer(buffBlend);
		buffBlend.rewind();

		IntBuffer buffOut = IntBuffer.allocate(base.getWidth()
				* base.getHeight());
		buffOut.rewind();

		while (buffOut.position() < buffOut.limit()) {
			int filterInt = buffBlend.get();
			int srcInt = buffBase.get();

			int redValueFilter = Color.red(filterInt);
			int greenValueFilter = Color.green(filterInt);
			int blueValueFilter = Color.blue(filterInt);

			int redValueSrc = Color.red(srcInt);
			int greenValueSrc = Color.green(srcInt);
			int blueValueSrc = Color.blue(srcInt);

			int redValueFinal = colordodge(redValueFilter, redValueSrc);
			int greenValueFinal = colordodge(greenValueFilter, greenValueSrc);
			int blueValueFinal = colordodge(blueValueFilter, blueValueSrc);

			int pixel = Color.argb(255, redValueFinal, greenValueFinal,
					blueValueFinal);

			/*
			 * float[] hsv = new float[3]; Color.colorToHSV(pixel, hsv); hsv[1] = 0.0f; float top =
			 * VALUE_TOP; // Setting this as 0.95f gave the best result so far if (hsv[2] <= top) {
			 * hsv[2] = 0.0f; } else { hsv[2] = 1.0f; } pixel = Color.HSVToColor(hsv);
			 */

			buffOut.put(pixel);
		}

		buffOut.rewind();

		base.copyPixelsFromBuffer(buffOut);
		blend.recycle();

		return base;
	}

	private static int colordodge(int in1, int in2) {
		float image = (float) in2;
		float mask = (float) in1;
		return ((int) ((image == 255) ? image : Math.min(255,
				(((long) mask << 8) / (255 - image)))));

	}

	public static float getRoundedValue(float value, int intervalSize) {
		float result = Math.round(value);
		int mod = ((int) result) % intervalSize;
		result += mod < (intervalSize / 2) ? -mod : intervalSize - mod;
		return result;

	}

	public void selectImage() {

		if (selectImageClicked) {
			lv1.setVisibility(View.GONE);
			selectImageClicked = false;
			return;
		} else {
			lv1.setVisibility(View.VISIBLE);
			selectImageClicked = true;
		}

		String filePath = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES).toString();
		final File pictureStorageDir = new File(filePath, DIR_STORAGE_IMAGE);
		if (!pictureStorageDir.exists()) {
			Toast.makeText(this, getString(R.string.msg_no_image_file),
					Toast.LENGTH_SHORT).show();
			lv1.setVisibility(View.GONE);
			selectImageClicked = false;
			return;
		}

		File pictureListFiles[] = pictureStorageDir
				.listFiles(new FileExtensionFilter());

		if (pictureListFiles.length == 0) {
			Toast.makeText(this, getString(R.string.msg_no_image_file),
					Toast.LENGTH_SHORT).show();
			return;
		}

		final ArrayList<FileListEntry> alPictures = new ArrayList<FileListEntry>();

		int thumbWidth = 64, thumbHeight = 64;
		for (int i = 0; i < pictureListFiles.length; i++) {
			String file = pictureListFiles[i].getName();
			String fullPathFile = Environment
					.getExternalStoragePublicDirectory(
							Environment.DIRECTORY_PICTURES).getAbsolutePath()
					.toString()
					+ File.separator
					+ DIR_STORAGE_IMAGE
					+ File.separator
					+ file;
			String fileSize = new DecimalFormat("#,##0.0#")
					.format(pictureListFiles[i].length() / 1024.0) + " KB";

			BitmapFactory.Options bmFactoryOptions = new BitmapFactory.Options();
			bmFactoryOptions.inJustDecodeBounds = true; // the decoder will return null (no bitmap)
			BitmapFactory.decodeFile(fullPathFile, bmFactoryOptions);

			FileListEntry mFileEntry = null;
			try {
				Bitmap bmThumbnail = ThumbnailUtils.extractThumbnail(
						BitmapFactory.decodeFile(fullPathFile), thumbWidth,
						thumbHeight);
				mFileEntry = new FileListEntry(bmThumbnail,
						pictureListFiles[i].getName(), "\nSize: " + fileSize
								+ "\nDimensions: " + bmFactoryOptions.outWidth
								+ " x " + bmFactoryOptions.outHeight);
			} catch (Throwable e) {
				// handle OOM error on BitmapFactory
				mFileEntry = new FileListEntry(null,
						pictureListFiles[i].getName(), "\nSize: " + fileSize);
			}

			alPictures.add(mFileEntry);
		}

		FileListAdapter adapter = new FileListAdapter(getApplicationContext(),
				R.layout.file_list_entry, alPictures);

		lv1.setAdapter(adapter);

		// listening to single listitem click
		lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting listitem index
				lv1.setVisibility(View.GONE);
				selectImageClicked = false;
				bmImageSrc = getBitmapFromExternalStorage(this,
						alPictures.get(position).getFilename());
				iv1.setImageBitmap(bmImageSrc);
			}

		});
	}

	/**
	 * Class to filter files which are having image extension
	 * */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".jpg") || name.endsWith(".jpeg") || name
					.endsWith(".png"));
		}
	}

	public void showAbout() {
		final StringBuilder about_string = new StringBuilder();
		about_string.append(getString(R.string.app_name));
		String version_name = "UNKNOWN_VERSION";
		int version_code = -1;
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			version_name = pInfo.versionName;
			version_code = pInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.w(LOG_TAG, "showAbout/NameNotFoundException: " + e.getMessage());
			e.printStackTrace();
		}
		about_string.append(" v" + version_name + "\n");
		about_string.append(getString(R.string.version_code) + ": "
				+ version_code + "\n");
		about_string.append(getString(R.string.about_desc));

		popNotification(getString(R.string.action_about),
				about_string.toString());
	}

	private void popNotification(String title, String message) {
		// new AlertDialog.Builder(this).setTitle(title).setMessage(message).create().show();
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int i) {
			}
		});

		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.show();
	}

}
