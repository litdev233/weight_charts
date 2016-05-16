package net.litdev.weight_charts.utils;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.UUID;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * 设备信息帮助类
 */
public class DeviceUtils {
	private DeviceUtils() {
		throw new Error("Do not need instantiate!");
	}
	
	/**
	 * 获取UUID、通用型
	 * @param context
	 * @return
	 */
	public static String getUUID(Context context){
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    final String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    String deviceId = deviceUuid.toString();
	    return deviceId;
	}
	
	/**
	 * 生成设备UUID
	 * @param context
	 * @return
	 */
	public static String generateUUID(Context context)
    {
        String s = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if(s == null)
            s = "";
        String s1 = android.provider.Settings.Secure.getString(context.getContentResolver(), "android_id");
        if(s1 == null)
            s1 = "";
        String s2;
        String s3;
        WifiInfo wifiinfo;
        String s4;
        if(Build.VERSION.SDK_INT >= 9)
        {
            s2 = Build.SERIAL;
            if(s2 == null)
                s2 = "";
        } else
        {
            s2 = getDeviceSerial();
        }
        s3 = "";
        wifiinfo = ((WifiManager)context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        if(wifiinfo != null)
        {
            s3 = wifiinfo.getMacAddress();
            if(s3 == null)
                s3 = "";
        }
        try
        {
            s4 = getMD5String((new StringBuilder()).append(s).append(s1).append(s2).append(s3).toString());
        }
        catch(NoSuchAlgorithmException nosuchalgorithmexception)
        {
            nosuchalgorithmexception.printStackTrace();
            return null;
        }
        return s4;
    }

    private static final String getDeviceSerial()
    {
        String s;
        try
        {
            Method method = Class.forName("android.os.Build").getDeclaredMethod("getString", new Class[] {
                Class.forName("java.lang.String")
            });
            if(!method.isAccessible())
                method.setAccessible(true);
            s = (String)method.invoke(new Build(), new Object[] {
                "ro.serialno"
            });
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            classnotfoundexception.printStackTrace();
            return "";
        }
        catch(NoSuchMethodException nosuchmethodexception)
        {
            nosuchmethodexception.printStackTrace();
            return "";
        }
        catch(InvocationTargetException invocationtargetexception)
        {
            invocationtargetexception.printStackTrace();
            return "";
        }
        catch(IllegalAccessException illegalaccessexception)
        {
            illegalaccessexception.printStackTrace();
            return "";
        }
        return s;
    }

    private static final String getMD5String(String s)
        throws NoSuchAlgorithmException
    {
        byte abyte0[] = MessageDigest.getInstance("SHA-1").digest(s.getBytes());
        Formatter formatter = new Formatter();
        int i = abyte0.length;
        for(int j = 0; j < i; j++)
        {
            byte byte0 = abyte0[j];
            Object aobj[] = new Object[1];
            aobj[0] = Byte.valueOf(byte0);
            formatter.format("%02x", aobj);
        }

        return formatter.toString();
    }

	/**
	 * 得到CPU核心数
	 *
	 * @return CPU核心数
	 */
	public static int getNumCores() {
		try {
			File dir = new File("/sys/devices/system/cpu/");
			File[] files = dir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if (Pattern.matches("cpu[0-9]", pathname.getName())) {
						return true;
					}
					return false;
				}
			});
			return files.length;
		} catch (Exception e) {
			return 1;
		}
	}

	/**
	 * 获取设备的可用内存大小
	 *
	 * @param context
	 *            应用上下文对象context
	 * @return 当前内存大小
	 */
	public static int getDeviceUsableMemory(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// 返回当前系统的可用内存
		return (int) (mi.availMem / (1024 * 1024));
	}

	/**
	 * 获取手机系统SDK版本
	 * 
	 * @return 如API 17 则返回 17
	 */
	public static int getSDKVersion() {
		return Build.VERSION.SDK_INT;
	}

	/**
	 * 是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * Wifi网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 手机网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 网络连接类型
	 * @param context
	 * @return
	 */
	public static int getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}

	/**
	 * 获取设备的唯一标识，deviceId
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		if (deviceId == null) {
			return "";
		} else {
			return deviceId;
		}
	}

	/**
	 * 获取手机品牌
	 *
	 * @return
	 */
	public static String getPhoneBrand() {
		return Build.BRAND;
	}

	/**
	 * 获取手机型号
	 *
	 * @return
	 */
	public static String getPhoneModel() {
		return Build.MODEL;
	}

	/**
	 * 获取手机Android 版本（4.4、5.0、5.1 ...）
	 *
	 * @return
	 */
	public static String getBuildVersion() {
		return Build.VERSION.RELEASE;
	}
}
