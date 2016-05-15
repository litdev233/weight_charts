package net.litdev.weight_charts.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by litde on 2016/5/15.
 */
public class UtilsToast {
    private static Toast mToast;

    /**
     * 显示吐司
     * @param context
     * @param msg
     */
    public static void show(Context context,CharSequence msg){
        if(mToast == null){
            mToast = Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        }else{
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    /**
     * 显示吐司
     * @param context
     * @param msg
     * @param duration
     */
    public static void show(Context context,CharSequence msg,int duration){
        if(mToast == null){
            mToast = Toast.makeText(context,msg,duration);
        }else{
            mToast.setText(msg);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

}
