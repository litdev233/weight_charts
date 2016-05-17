package net.litdev.weight_charts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import net.litdev.weight_charts.R;
import net.litdev.weight_charts.adapter.AdapterHomeList;
import net.litdev.weight_charts.entity.WeightData;
import net.litdev.weight_charts.utils.UtilsToast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends AppCompatActivity {

    private ListView ll_list;
    private List<WeightData> data_list;
    private AdapterHomeList adapter;
    private final int limit=10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(this, getString(R.string.BmobAppID));
        initView();

        initData();
    }

    private void initData(){
        final BmobQuery<WeightData> query = new BmobQuery<>();
        query.order("-AddTime");
        query.setLimit(limit);

        query.findObjects(this, new FindListener<WeightData>() {
            @Override
            public void onSuccess(List<WeightData> list) {
                if(list.size() > 0){
                    for (WeightData item: list) {
                        if(!data_list.contains(item)){
                            data_list.add(item);
                        }
                    }
                    adapter.notifyDataSetChanged();

                }
                else {
                    UtilsToast.show(MainActivity.this,"没有更多数据了");
                }
            }

            @Override
            public void onError(int i, String s) {
                UtilsToast.show(MainActivity.this,"数据加载失败："+s);
            }
        });
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ll_list = (ListView) findViewById(R.id.ll_list);
        data_list =new ArrayList<>();
        adapter = new AdapterHomeList(data_list,this);
        ll_list.setAdapter(adapter);
        setSupportActionBar(toolbar);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        final FloatingActionButton fab_charts = (FloatingActionButton) findViewById(R.id.fab_charts);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AddWeight.class));
            }
        });
        fab_charts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ShowCharts.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private long lastClickTime = 0;
    @Override
    public void onBackPressed() {
        if(lastClickTime <= 0){
            UtilsToast.show(MainActivity.this,"再按一次退出应用");
            lastClickTime = System.currentTimeMillis();
        }else{
            long currentClickTime = System.currentTimeMillis();
            if(currentClickTime - lastClickTime < 1000){
                finish();
            }else{
                UtilsToast.show(MainActivity.this,"再按一次退出应用");
                lastClickTime = currentClickTime;
            }
        }
    }

}
