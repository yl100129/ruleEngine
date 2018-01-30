package com.hiaward.ruleengine.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hiaward.ruleengine.exception.IExceptionCode;

public class ClassUtils implements IExceptionCode {

	private static final Log log = LogFactory.getLog(ClassUtils.class);

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String className, Class<T> clazz) {
		if (StringUtils.isBlank(className) || null == clazz) {
			return null;
		}
		T t = null;
		try {
			Object o = Class.forName(className).newInstance();
			if (clazz.isInstance(o)) {
				t = (T) o;
			}
		} catch (InstantiationException e) {
			if (log.isErrorEnabled()) {
				log.error(
						ERROR_CODE_NEW + className + "不能实例化成" + clazz.getName(),
						e);
			}
		} catch (IllegalAccessException e) {
			if (log.isErrorEnabled()) {
				log.error(
						ERROR_CODE_NEW + className + "不能实例化成" + clazz.getName(),
						e);
			}
		} catch (ClassNotFoundException e) {
			if (log.isErrorEnabled()) {
				log.error(
						ERROR_CODE_NEW + className + "不能实例化成" + clazz.getName(),
						e);
			}
		}
		return t;
	}

	@SuppressWarnings("rawtypes")
	public static File getResourceFile(String classpath, Class clazz) {
		if (StringUtils.isBlank(classpath)) {
			return null;
		}
		File file = null;
		URL url = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null && clazz != null) {
			loader = clazz.getClassLoader();
			if (loader != null) {
				url = loader.getResource(classpath);
				if (url != null)
					file = new File(url.getFile());
			}
		} else {
			url = loader.getResource(classpath);
			if (url != null)
				file = new File(url.getFile());
			if (file != null && !file.exists() && clazz != null) {
				loader = clazz.getClassLoader();
				if (loader != null) {
					url = loader.getResource(classpath);
					if (url != null)
						file = new File(url.getFile());
				}
			}
		}
		return file;

	}

	@SuppressWarnings("rawtypes")
	public static InputStream getReourceStream(String classpath, Class clazz) {
		if (StringUtils.isBlank(classpath)) {
			return null;
		}
		InputStream inStream = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null && clazz != null) {
			loader = clazz.getClassLoader();
			if (loader != null) {
				inStream = loader.getResourceAsStream(classpath);
			}
		} else {
			inStream = loader.getResourceAsStream(classpath);
			if (inStream == null && clazz != null) {
				loader = clazz.getClassLoader();
				if (loader != null) {
					inStream = loader.getResourceAsStream(classpath);
				}
			}
		}
		return inStream;
	}

	public static String getClassName(Object o) {
		if (null == o) {
			return null;
		}
		String name = null;
		name = o.getClass().getName();
		name = name.substring(name.lastIndexOf(".") + 1);
		return name;
	}

	public static String getClassName(String clazz) {
		if (StringUtils.isBlank(clazz)) {
			return null;
		}
		String name = null;
		name = clazz.substring(clazz.lastIndexOf(".") + 1);
		return name;
	}

	public static String replaceClasspath(String classpath) {
		String result = null;
		result = classpath.replaceAll("/", ".");
		return result;
	}
}
