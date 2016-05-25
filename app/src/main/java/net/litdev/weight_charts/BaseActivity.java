package net.litdev.weight_charts;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import net.litdev.weight_charts.utils.AppManager;

/**
 * Created by litde on 2016/5/25.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppManager.getAppManager().addActivity(this);

        //Log.d(getClass().getName(),"onCreate...."+this.getLocalClassName());

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //Log.d(getClass().getName(),"屏幕改变");

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Log.d(getClass().getName(),"onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();

        //Log.d(getClass().getName(),"onResume");
    }
}
