package com.eyun.file.utils;

import org.apache.http.util.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;

public class BigFileFiler {
	// 过滤出80K以上并且是图片类型的数据
	public static boolean accept(String fileName) {
		FileChannel fc = null;
		FileInputStream fis = null;
		try {
			File file = new File(fileName);
			fis = new FileInputStream(file);
			fc = fis.getChannel();
			long size = fc.size();

			String type = getFileType(file.getAbsolutePath());
			if (size > 80000 && isImage(type)) {
				return true;
			}
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (fc != null) {
					fc.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}

	public static boolean isImage(String type) {
		if (type != null && (type.equals("jpg") || type.equals("gif") || type.equals("png") || type.equals("jpeg"))) {
			return true;
		}
		return false;
	}

	public static String getFileType(String fileName) {
		if (fileName != null) {
			int typeIndex = fileName.lastIndexOf(".");
			if (typeIndex != -1) {
				String fileType = fileName.substring(typeIndex + 1).toLowerCase();
				return fileType;
			}
		}
		return "";
	}

	public static Long getFileSize(String fileName) {
		if (TextUtils.isEmpty(fileName)) {
			return 0l;
		}
		FileChannel fc = null;
		FileInputStream fis = null;
		try {
			File file = new File(fileName);
			fis = new FileInputStream(file);
			fc = fis.getChannel();
			return fc.size();
		} catch (Exception e) {
			return 0l;
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (fc != null) {
					fc.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
