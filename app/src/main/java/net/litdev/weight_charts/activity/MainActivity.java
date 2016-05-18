package net.litdev.weight_charts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

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

    private PullToRefreshListView ll_list;
    private List<WeightData> data_list;
    private AdapterHomeList adapter;
    private View footView;

    private final int curSize=10;
    //当前第几页
    private int curPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(this, getString(R.string.BmobAppID));
        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        footView = View.inflate(this,R.layout.footview_loading,null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ll_list = (PullToRefreshListView) findViewById(R.id.ll_list);
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

        //刷新配置
        //ll_list.setMode(PullToRefreshBase.Mode.BOTH);
        ILoadingLayout startLables = ll_list.getLoadingLayoutProxy(true,false);
        startLables.setPullLabel("下拉刷新...");//刚下拉时,显示的提示
        startLables.setRefreshingLabel("正在载入...");//刷新时
        startLables.setReleaseLabel("放开刷新...");//// 下拉到一定距离时，显示的提示

        //针对上拉
        // ILoadingLayout endLabels = lv_home.getLoadingLayoutProxy(false,true);
        // endLabels.setPullLabel("上拉加载更多...");// 刚下拉时，显示的提示
        // endLabels.setRefreshingLabel("正在载入...");// 刷新时
        // endLabels.setReleaseLabel("放开加载...");// 下来达到一定距离时，显示的提示

        loadData(1);

        //下拉刷新
        ll_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData(1);
            }
        });
        //最后一项显示时自动加载下一页
        ll_list.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                loadData(curPage + 1);
            }
        });

        //下拉刷新和上拉加载更多的方式
        /*ll_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData(1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData(curPage+1);
            }
        });*/

        ll_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UtilsToast.show(MainActivity.this,"点击的位置："+position+",编号："+id);
            }
        });

        ll_list.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                UtilsToast.show(MainActivity.this,"长按了："+view.getId()+",position:"+position+",id:"+id);
                return  true;
            }
        });

    }

    /**
     * 加载数据
     * @param page 当前页码
     */
    private void loadData(final int page){
        final BmobQuery<WeightData> query = new BmobQuery<>();
        query.order("-AddTime");
        query.setSkip(curSize * (page - 1));
        query.setLimit(curSize);

        query.findObjects(this, new FindListener<WeightData>() {
            @Override
            public void onSuccess(List<WeightData> list) {
                if(page == 1){
                    data_list.clear();
                }
                curPage = page;
                addData(list);
            }

            @Override
            public void onError(int i, String s) {
                UtilsToast.show(MainActivity.this,"数据加载失败："+s);
                ll_list.onRefreshComplete();
            }
        });
    }

    /**
     * 构造数据
     * @param list
     */
    private void addData(List<WeightData> list) {
        for (WeightData item: list) {
            if(!data_list.contains(item)){
                data_list.add(item);
            }
        }
        adapter.notifyDataSetChanged();

        if(list.size() != 0){
            addFootView(ll_list,footView);
        }else{
            UtilsToast.show(MainActivity.this,"没有更多数据了");
            removeFootView(ll_list,footView);
        }
        ll_list.onRefreshComplete();
    }

    private void addFootView(PullToRefreshListView plv, View footView) {
        ListView lv = plv.getRefreshableView();
        if (lv.getFooterViewsCount() == 1) {
            lv.addFooterView(footView);
        }
    }

    private void removeFootView(PullToRefreshListView plv, View footView) {
        ListView lv = plv.getRefreshableView();
        if (lv.getFooterViewsCount() > 1) {
            lv.removeFooterView(footView);
        }
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
