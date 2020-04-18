package com.qkwl.common.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;


/**
 * Spring上下文工具类
 * @author ZKF
 */
public class SpringContextUtils implements BeanFactoryAware {
	private BeanFactory factory = null;
	private static SpringContextUtils instance = null;

	@Override
	public void setBeanFactory(BeanFactory factory) throws BeansException {
		instance = this;
		this.factory = factory;

	}

	public static BeanFactory getBeanFacotory() {
		if (instance == null)
			throw new RuntimeException("系统还未初始化，请注意检查代码！");

		return instance.factory;
	}

	/**
	 * 根据名称获取Bean
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) getBeanFacotory().getBean(name);
	}

	/**
	 * 根据类获取Bean
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> T getBean(Class<T> clazz) {
		return getBeanFacotory().getBean(clazz);
	}

	/**
	 * 获取所有实现此接口的类(默认包名为当前接口所在的包) @Title:
	 * getAllImplClassByInterface @Description: TODO @param
	 * clazz @return @return List<T> @throws
	 */
	@SuppressWarnings("rawtypes")
	public static List<Class> getAllImplClassByInterface(Class<?> clazz) {
		return getAllImplClassByInterface(clazz, clazz.getPackage().getName());
	}

	/**
	 * 获取所有实现此接口的类 @Title: getAllImplClassByInterface @Description: TODO @param
	 * clazz @param packagePath @return @return List<T> @throws
	 */
	@SuppressWarnings("rawtypes")
	public static List<Class> getAllImplClassByInterface(Class<?> clazz, String packagePath) {
		List<Class> returnClassList = null;
		try {
			if (clazz.isInterface()) {
				// 获取当前包下以及子包下所以的类
				List<Class> allClass = (List<Class>) getClasses(packagePath);
				if (allClass != null) {
					returnClassList = new ArrayList<Class>();
					for (Class classes : allClass) {
						// 判断是否是同一个接口
						if (clazz.isAssignableFrom((Class<?>) classes)) {
							// 本身不加入进去
							if (!clazz.equals(classes)) {
								returnClassList.add(classes);

							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnClassList;
	}

	/**
	 * 获取指定包下面实现此接口的所有类实例 @Title:
	 * getAllImplClassInstanceByInterface @Description: TODO @param clazz @param
	 * packagePath @return @return List<T> @throws
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getAllImplClassInstanceByInterface(Class<T> clazz, String packagePath) {
		List<T> returnClassList = null;
		try {
			if (clazz.isInterface()) {
				// 获取当前包下以及子包下所以的类
				List<T> allClass = (List<T>) getClasses(packagePath);
				if (allClass != null) {
					returnClassList = new ArrayList<T>();
					for (T classes : allClass) {
						// 判断是否是同一个接口
						if (clazz.isAssignableFrom((Class<?>) classes)) {
							// 本身不加入进去
							if (!clazz.equals(classes)) {
								/**
								 * 必须加实例 不是加类
								 */
								String cls = classes.toString().split(" ")[1];
								T t = (T) Class.forName(cls).newInstance();
								returnClassList.add(t);

							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnClassList;
	}

	/**
	 * 获取该接口所在包下面实现此接口的所有类实例
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> getAllImplClassInstanceByInterface(Class<T> clazz) {
		return getAllImplClassInstanceByInterface(clazz, clazz.getPackage().getName());
	}

	/**
	 * 从一个包中查找出所有的类，在jar包中不能查找
	 * 
	 * @param packageName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private static List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		// 用'/'代替'.'路径
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes;
	}

	/**
	 * 查找指定包下的类
	 * 
	 * @param directory
	 * @param packageName
	 * @return
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings({ "rawtypes" })
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				// assert !file.getName().contains(".");
				// System.out.println("---package
				// name:"+packageName+"---name:"+file.getName());
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				if (packageName.contains("com.qkwl.common.oss")
						|| packageName.contains("com.qkwl.common.pay")
						|| packageName.contains("com.qkwl.common.util")
						|| packageName.contains("com.qkwl.common.redis")) {
					continue;
				}
				// 去掉'.class'
				String classStr = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
				// classStr=classStr.replace("/", ".");
				classes.add(Class.forName(classStr));
				// System.out.println("package
				// name:"+packageName+"---name:"+file.getName());

			}
		}
		return classes;
	}

	/**
	 * 获取jar包中实现类 @Title: getAllClassByInterfaceFromJar @Description:
	 * TODO @param clazz @param packagePath @return @return List<T> @throws
	 */
	@SuppressWarnings({ "rawtypes" })
	public static List<Class> getAllImplClassByInterfaceFromJar(Class<?> clazz, String packagePath) {
		List<Class> returnClassList = null;
		try {
			if (clazz.isInterface()) {
				// 获取当前包下以及子包下所以的类
				List<Class> allClass = (List<Class>) getClassesFromJar(packagePath);
				if (allClass != null) {
					returnClassList = new ArrayList<Class>();
					for (Class classes : allClass) {
						// 判断是否是同一个接口
						if (clazz.isAssignableFrom((Class<?>) classes)) {
							// 本身不加入进去
							if (!clazz.equals(classes)) {
								returnClassList.add(classes);

							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnClassList;
	}

	/**
	 * 读取jar包中实现 类返回实例 @Title:
	 * getAllClassInstanceByInterfaceFromJar @Description: TODO @param
	 * clazz @param packagePath @return @return List<T> @throws
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getAllImplClassInstanceByInterfaceFromJar(Class<T> clazz, String packagePath) {
		List<T> returnClassList = null;
		try {
			if (clazz.isInterface()) {
				// 获取当前包下以及子包下所以的类
				List<T> allClass = (List<T>) getClassesFromJar(packagePath);
				if (allClass != null) {
					returnClassList = new ArrayList<T>();
					for (T classes : allClass) {
						// 判断是否是同一个接口
						if (clazz.isAssignableFrom((Class<?>) classes)) {
							// 本身不加入进去
							if (!clazz.equals(classes)) {
								String cls = classes.toString().split(" ")[1];
								T t = (T) Class.forName(cls).newInstance();
								returnClassList.add(t);

							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnClassList;
	}

	/**
	 * 获取jar包中指定的包下的所有类 @Title: getClassesFromJar @Description: TODO @param
	 * packageName @return @throws ClassNotFoundException @throws
	 * IOException @return List<Class> @throws
	 */
	@SuppressWarnings({ "resource", "rawtypes" })
	private static List<Class> getClassesFromJar(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		// 用'/'代替'.'路径
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		List<Class> classes = new ArrayList<Class>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));

			String name = resource.getFile().split("!")[0];
			name = name.substring(name.indexOf('/') + 1);
			if (OSUtils.OsIsLinux()) {
				name = "/" + name;
			}
			// System.out.println("jar name="+name);
			JarFile jarFile = new JarFile(name);
			Enumeration<JarEntry> ens = jarFile.entries();

			while (ens.hasMoreElements()) {
				JarEntry je = ens.nextElement();
				if (je.isDirectory())
					continue;
				// System.out.println(je.getName());
				if (je.getName().endsWith(".class")) {
					name = je.getName().substring(0, je.getName().length() - 6);
					String classStr = name.replace("/", ".");
					// System.out.println("classStr:"+classStr);
					classes.add(Class.forName(classStr));
				}

			}
		}
		// for(Class cz :classes){
		// System.out.println(cz);
		// }
		return classes;
	}
}
