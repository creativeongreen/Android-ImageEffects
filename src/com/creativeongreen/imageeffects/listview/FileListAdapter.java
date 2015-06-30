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

package com.creativeongreen.imageeffects.listview;

import java.util.List;

import com.creativeongreen.imageeffects.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author creativeongreen
 * 
 *         file listings
 * 
 */
public class FileListAdapter extends ArrayAdapter<FileListEntry> {

	Context context;

	public FileListAdapter(Context context, int resourceId,
			List<FileListEntry> items) {
		super(context, resourceId, items);
		this.context = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		FileListEntry rowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.file_list_entry, null);
			holder = new ViewHolder();
			holder.ivThumbnail = (ImageView) convertView
					.findViewById(R.id.iv_thumbnail);
			holder.tvFilename = (TextView) convertView
					.findViewById(R.id.tv_filename);
			holder.tvFileDetails = (TextView) convertView
					.findViewById(R.id.tv_file_details);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.ivThumbnail.setImageBitmap(rowItem.getThumbnail());
		holder.tvFilename.setText(rowItem.getFilename());
		holder.tvFileDetails.setText(rowItem.getFileDetails());

		return convertView;
	}

	private class ViewHolder {
		ImageView ivThumbnail;
		TextView tvFilename;
		TextView tvFileDetails;
	}

}
