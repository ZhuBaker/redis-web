package com.arronlong.redisweb.common.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类工具
 * 
 * @author cuichenglong@motie.com
 * @date 2018年4月1日 下午5:00:17
 */
public class ClassUtil {

	/**
	 * 递归查找指定目录下的类文件的全路径
	 * 
	 * @param baseFile
	 *            查找文件的入口
	 * @param fileList
	 *            保存已经查找到的文件集合
	 */
	public static void getSubFileNameList(File baseFile, List<String> fileList) {
		if (baseFile.isDirectory()) {
			File[] files = baseFile.listFiles();
			for (File tmpFile : files) {
				getSubFileNameList(tmpFile, fileList);
			}
		}
		String path = baseFile.getPath();
		if (path.endsWith(".java")) {
			String name1 = path.substring(path.indexOf("src") + 4, path.length());
			String name2 = name1.replaceAll("\\\\", ".");
			String name3 = name2.substring(0, name2.lastIndexOf(".java"));
			fileList.add(name3);
		}
	}

	/**
	 * 从jar包读取所有的class文件名
	 */
	public static List<String> getClassNameFrom(String jarName) {
		List<String> fileList = new ArrayList<String>();
		try {
			JarFile jarFile = new JarFile(new File(jarName));
			Enumeration<JarEntry> en = jarFile.entries(); // 枚举获得JAR文件内的实体,即相对路径
			while (en.hasMoreElements()) {
				String name1 = en.nextElement().getName();
				if (!name1.endsWith(".class")) {// 不是class文件
					continue;
				}
				String name2 = name1.substring(0, name1.lastIndexOf(".class"));
				String name3 = name2.replaceAll("/", ".");
				fileList.add(name3);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileList;
	}
}
