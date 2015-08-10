package com.cj.dreams.video.util;

import android.graphics.Bitmap;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MemoryCache {

	// ���뻺��ʱ�Ǹ�ͬ������
	// LinkedHashMap���췽�������һ������true������map���Ԫ�ؽ��������ʹ�ô������ٵ������У���LRU
	// ����ĺô������Ҫ�������е�Ԫ���滻�����ȱ�����������ʹ�õ�Ԫ�����滻�����Ч��
	private Map<String, Bitmap> cache = Collections
			.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));
	// ������ͼƬ��ռ�õ��ֽڣ���ʼ0����ͨ��˱����ϸ���ƻ�����ռ�õĶ��ڴ�
	private long size = 0;// current allocated size
	// ����ֻ��ռ�õ������ڴ�
	private long limit = 1000000;// max memory in bytes

	public MemoryCache() {
		// use 25% of available heap size
		setLimit(Runtime.getRuntime().maxMemory() / 10);
	}

	public void setLimit(long new_limit) {
		limit = new_limit;
	}

	public Bitmap get(String id) {
		try {
			if (!cache.containsKey(id))
				return null;
			return cache.get(id);
		} catch (NullPointerException ex) {
			return null;
		}
	}

	public void put(String id, Bitmap bitmap) {
		try {
			if (cache.containsKey(id))
				size -= getSizeInBytes(cache.get(id));
			cache.put(id, bitmap);
			size += getSizeInBytes(bitmap);
			checkSize();
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	/**
	 * �ϸ���ƶ��ڴ棬���������滻�������ʹ�õ��Ǹ�ͼƬ����
	 * 
	 */
	private void checkSize() {
		if (size > limit) {
			// �ȱ����������ʹ�õ�Ԫ��
			Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, Bitmap> entry = iter.next();
				size -= getSizeInBytes(entry.getValue());
				iter.remove();
				if (size <= limit)
					break;
			}
		}
	}

	public void clear() {
		cache.clear();
	}

	/**
	 * ͼƬռ�õ��ڴ�
	 * 
	 * 
	 * @return
	 */
	long getSizeInBytes(Bitmap bitmap) {
		if (bitmap == null)
			return 0;
		return bitmap.getRowBytes() * bitmap.getHeight();
	}
}
