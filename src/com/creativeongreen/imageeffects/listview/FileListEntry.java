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

import android.graphics.Bitmap;

/**
 * 
 * @author creativeongreen
 * 
 *         define file list items
 * 
 */
public class FileListEntry {

	private Bitmap bmThumbnail;
	private String filename;
	private String fileDetails;

	public FileListEntry(Bitmap bm, String s1, String s2) {
		this.bmThumbnail = bm;
		this.filename = s1;
		this.fileDetails = s2;
	}

	public Bitmap getThumbnail() {
		return bmThumbnail;
	}

	public void setThumbnail(Bitmap bm) {
		bmThumbnail = bm;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String s) {
		filename = s;
	}

	public String getFileDetails() {
		return fileDetails;
	}

	public void setFileDetails(String s) {
		fileDetails = s;
	}

}
