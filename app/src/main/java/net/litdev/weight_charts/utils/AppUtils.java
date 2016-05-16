package net.litdev.weight_charts.utils;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

public class AppUtils {
	private AppUtils(){
		throw new Error("Do not need instantiate!");
	}

	/**
	 * 得到软件版本名字
	 * @param context  上下文
	 * @return 当前版本 版本名字
	 */
	public static String getVerName(Context context) {
		String verName = "1.0";
		try {
			String packageName = context.getPackageName();
			verName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verName;
	}

	/**
	 * 获取当前应用的包名
	 * @param context
	 * @return
	 */
	public static String getPageName(Context context) {
		String pageName = "";
		try {
			pageName = context.getPackageName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pageName;
	}
	
	/**
	 * 获取应用签名
	 *
	 * @param context 上下文
	 * @param pkgName 包名
	 * @return 返回应用的签名
	 */
	public static String getSign(Context context, String pkgName) {
		try {
			PackageInfo pis = context.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
			return hexdigest(pis.signatures[0].toByteArray());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 将签名字符串转换成需要的32位签名
	 * 
	 * @param paramArrayOfByte
	 *            签名byte数组
	 * @return 32位签名字符串
	 */
	private static String hexdigest(byte[] paramArrayOfByte) {
		final char[] hexDigits = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
		try {
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(paramArrayOfByte);
			byte[] arrayOfByte = localMessageDigest.digest();
			char[] arrayOfChar = new char[32];
			for (int i = 0, j = 0;; i++, j++) {
				if (i >= 16) {
					return new String(arrayOfChar);
				}
				int k = arrayOfByte[i];
				arrayOfChar[j] = hexDigits[(0xF & k >>> 4)];
				arrayOfChar[++j] = hexDigits[(k & 0xF)];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 得到软件版本号
	 * @param context 上下文
	 * @return 当前版本  Code
	 */
	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			String packageName = context.getPackageName();
			verCode = context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verCode;
	}

	/**
	 * 获取应用运行的最大内存
	 * @return 最大内存
	 */
	public static long getMaxMemory() {
		return Runtime.getRuntime().maxMemory() / 1024;
	}

	/**
	 * 安装apk
	 * @param context 上下文
	 * @param file  APK文件
	 */
	public static void installApk(Context context, File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 安装apk
	 * @param context 上下文
	 * @param file APK文件uri
	 */
	public static void installApk(Context context, Uri file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(file, "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 卸载apk
	 * @param context  上下文
	 * @param packageName 包名
	 */
	public static void uninstallApk(Context context, String packageName) {
		Intent intent = new Intent(Intent.ACTION_DELETE);
		Uri packageURI = Uri.parse("package:" + packageName);
		intent.setData(packageURI);
		context.startActivity(intent);
	}

	/**
	 * 检测服务是否运行
	 * @param context  上下文
	 * @param className  类名
	 * @return 是否运行的状态
	 */
	public static boolean isServiceRunning(Context context, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> servicesList = activityManager.getRunningServices(Integer.MAX_VALUE);
		for (RunningServiceInfo si : servicesList) {
			if (className.equals(si.service.getClassName())) {
				isRunning = true;
			}
		}
		
		return isRunning;
	}

	/**
	 * 停止运行服务
	 * @param context 上下文
	 * @param className 类名
	 * @return 是否执行成功
	 */
	public static boolean stopRunningService(Context context, String className) {
		Intent intent_service = null;
		boolean ret = false;
		try {
			intent_service = new Intent(context, Class.forName(className));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (intent_service != null) {
			ret = context.stopService(intent_service);
		}
		return ret;
	}
	
	/**
	 * 获取系统中分享应用列表
	 * @param context
	 * @return
	 */
	public static List<EntityALLAppInfo> getShareAllApps(Context context){
		List<EntityALLAppInfo> list = new ArrayList<EntityALLAppInfo>();
		PackageManager pManager = context.getPackageManager();
		List<ResolveInfo> paklist = getShareApps(context);
		EntityALLAppInfo entity = null;
		for (ResolveInfo item : paklist) {
			entity = new EntityALLAppInfo();
			//entity.setSystemApp(false);
			if ((item.activityInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
				//手动装的应用
				entity.setSystemApp(false);
			}else{
				//系统内置的应用
				entity.setSystemApp(true);
			}
			entity.setAppName(item.loadLabel(pManager).toString());
			entity.setIcon(item.loadIcon(pManager));
			entity.setFristInstallTime(new Date());
			entity.setLastUpdateTime(new Date());
			entity.setPackageName(item.activityInfo.packageName);
			entity.setVersionCode(0);
			entity.setVersionName("");
			
			list.add(entity);
		}
		
		return list;
	}
	
    /** 
     * 查询手机内所有支持分享的应用 
     * @param context 
     * @return 
     */  
    private static List<ResolveInfo> getShareApps(Context context){  
        List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();    
        Intent intent=new Intent(Intent.ACTION_SEND,null);    
        intent.addCategory(Intent.CATEGORY_DEFAULT);    
        intent.setType("text/plain");    
        PackageManager pManager = context.getPackageManager();  
        mApps = pManager.queryIntentActivities(intent,PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        return mApps;    
    }  

	/**
	 * 获取系统中所有的应用
	 * @param context 上下文
	 * @return 应用信息List
	 */
	public static List<EntityALLAppInfo> getAllApps(Context context) {
		List<EntityALLAppInfo> list = new ArrayList<EntityALLAppInfo>();
		PackageManager pManager = context.getPackageManager();
		List<PackageInfo> paklist = pManager.getInstalledPackages(0);
		EntityALLAppInfo entity = null;
		for (PackageInfo  item : paklist) {
			entity = new EntityALLAppInfo();
			if ((item.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
				//手动装的应用
				entity.setSystemApp(false);
			}else{
				//系统内置的应用
				entity.setSystemApp(true);
			}
			entity.setAppName(pManager.getApplicationLabel(item.applicationInfo).toString());
			entity.setIcon(pManager.getApplicationIcon(item.applicationInfo));
			entity.setFristInstallTime(new Date(item.firstInstallTime));
			entity.setLastUpdateTime(new Date(item.lastUpdateTime));
			entity.setPackageName(item.packageName);
			entity.setVersionCode(item.versionCode);
			entity.setVersionName(item.versionName);
			list.add(entity);
		}
		
		return list;
	}

	/**
	 * SD卡判断
	 * @return
	 */
	public static boolean isSDCardAvailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 创建App文件夹
	 *
	 * @param appName
	 * @param application
	 * @return
	 */
	public static String createAPPFolder(String appName, Application application) {
		return createAPPFolder(appName, application, null);
	}

	/**
	 * 创建App文件夹
	 * 
	 * @param appName
	 * @param application
	 * @param folderName
	 * @return
	 */
	public static String createAPPFolder(String appName, Application application, String folderName) {
		File root = Environment.getExternalStorageDirectory();
		File folder;
		/**
		 * 如果存在SD卡
		 */
		if (isSDCardAvailable() && root != null) {
			folder = new File(root, appName);
			if (!folder.exists()) {
				folder.mkdirs();
			}
		} else {
			/**
			 * 不存在SD卡，就放到缓存文件夹内
			 */
			root = application.getCacheDir();
			folder = new File(root, appName);
			if (!folder.exists()) {
				folder.mkdirs();
			}
		}
		if (folderName != null) {
			folder = new File(folder, folderName);
			if (!folder.exists()) {
				folder.mkdirs();
			}
		}
		return folder.getAbsolutePath();
	}

	/**
	 * 通过Uri找到File
	 *
	 * @param context
	 * @param uri
	 * @return
	 */
	public static File uri2File(Activity context, Uri uri) {
		File file;
		String[] project = { MediaStore.Images.Media.DATA };
		Cursor actualImageCursor = context.getContentResolver().query(uri, project, null, null, null);
		if (actualImageCursor != null) {
			int actual_image_column_index = actualImageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			actualImageCursor.moveToFirst();
			String img_path = actualImageCursor.getString(actual_image_column_index);
			file = new File(img_path);
		} else {
			file = new File(uri.getPath());
		}
		if (actualImageCursor != null)
			actualImageCursor.close();
		return file;
	}

	/**
	 * 获取AndroidManifest里 Application节点下的meta-data的值
	 *
	 * @param context
	 * @param name
	 * @return
	 */
	public static String getMetaData(Context context, String name) {
		String value = null;
		try {
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			value = appInfo.metaData.getString(name);
			if(TextUtils.isEmpty(value)){
				value = Long.toString(System.currentTimeMillis());
			}
		} catch (NameNotFoundException e) {
			if(TextUtils.isEmpty(value)){
				value = Long.toString(System.currentTimeMillis());
			}
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 所有app的信息
	 * 
	 * @项目名: LitdevLibrary
	 * @包名: net.litdev.litdevlibrary.utils
	 * @类名: DeviceALLAppInfo
	 * @创建者: litdev
	 * @创建日期:2016年5月5日 下午2:30:03
	 * @描述: TODO
	 */
	public static class EntityALLAppInfo {
		private String packageName;
		private String appName;
		private int VersionCode;
		private String VersionName;
		private boolean isSystemApp;
		private Drawable icon; // 图标
		private Date fristInstallTime; // 安装时间
		private Date lastUpdateTime; // 最后更新时间
		

		public String getPackageName() {
			return packageName;
		}

		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public int getVersionCode() {
			return VersionCode;
		}

		public void setVersionCode(int versionCode) {
			VersionCode = versionCode;
		}

		public String getVersionName() {
			return VersionName;
		}

		public void setVersionName(String versionName) {
			VersionName = versionName;
		}

		/**
		 * 是否是系统预装的应用
		 * 
		 * @return
		 */
		public boolean isSystemApp() {
			return isSystemApp;
		}

		/**
		 * 是否是系统预装的应用
		 * 
		 * @param isSystemApp
		 */
		public void setSystemApp(boolean isSystemApp) {
			this.isSystemApp = isSystemApp;
		}

		/**
		 * 图标
		 * 
		 * @return
		 */
		public Drawable getIcon() {
			return icon;
		}

		/**
		 * 图标
		 * 
		 * @param icon
		 */
		public void setIcon(Drawable icon) {
			this.icon = icon;
		}

		/**
		 * 安装时间
		 * 
		 * @return
		 */
		public Date getFristInstallTime() {
			return fristInstallTime;
		}

		/**
		 * 安装时间
		 * 
		 * @param fristInstallTime
		 */
		public void setFristInstallTime(Date fristInstallTime) {
			this.fristInstallTime = fristInstallTime;
		}

		/**
		 * 最后更新时间
		 * 
		 * @return
		 */
		public Date getLastUpdateTime() {
			return lastUpdateTime;
		}

		/**
		 * 最后更新时间
		 * 
		 * @param lastUpdateTime
		 */
		public void setLastUpdateTime(Date lastUpdateTime) {
			this.lastUpdateTime = lastUpdateTime;
		}

	}
}
