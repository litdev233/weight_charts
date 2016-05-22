package net.litdev.weight_charts.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by litde on 2016/5/22.
 */
public class UtilsSharedPreferences {

    private static SharedPreferences sp;

    /**
     * 添加
     * @param context
     * @param key
     * @param val
     * @return
     */
    public static boolean addString(Context context, String key, String val){
        if(sp == null){
            sp =context.getSharedPreferences(CommonConstants.SP_NAME,context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,val);
        return editor.commit();
    }

    /**
     * 获取数据
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context,String key){
        if(sp == null){
            sp =context.getSharedPreferences(CommonConstants.SP_NAME,context.MODE_PRIVATE);
        }
        return sp.getString(key,"");
    }

}
