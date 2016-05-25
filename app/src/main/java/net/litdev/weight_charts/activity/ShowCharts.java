package net.litdev.weight_charts.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import net.litdev.weight_charts.BaseActivity;
import net.litdev.weight_charts.R;
import net.litdev.weight_charts.entity.WeightData;
import net.litdev.weight_charts.utils.AppManager;
import net.litdev.weight_charts.utils.UtilsDate;
import net.litdev.weight_charts.utils.UtilsToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class ShowCharts extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private LineChartView chart;
    private LineChartData data;
    private SwipeRefreshLayout swipe_refresh;

    /**
     * 几条折线图
     */
    private int numberOfLines = 1;

    /**
     * 最多多少条数据
     */
    private int numberOfPoints = 5;

    double[][] randomNumbersTab = new double[numberOfLines][numberOfPoints];
    /**
     * X轴文本
     */
    String [] xText = new String[numberOfPoints];

    private boolean hasAxes = true;
    private boolean hasAxesNames = false;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = true;
    private boolean isCubic = true;
    private boolean hasLabelForSelected = false;
    private boolean pointsHaveDifferentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_charts);
        initView();
        generateValues();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //横屏
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            numberOfPoints = 12;
        }else{
            numberOfPoints = 5;
        }

        randomNumbersTab = new double[numberOfLines][numberOfPoints];
        xText = new String[numberOfPoints];
        initView();
        generateValues();

    }

    /**
     * 加载视图
     */
    private void initView() {
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(this);
        //设置下拉出现小圆圈是否是缩放出现，出现的位置，最大的下拉位置
        swipe_refresh.setProgressViewOffset(true, 50, 200);
        //设置下拉圆圈的大小，两个值 LARGE， DEFAULT
        swipe_refresh.setSize(SwipeRefreshLayout.LARGE);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        swipe_refresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        // 通过 setEnabled(false) 禁用下拉刷新
        //mySwipeRefreshLayout.setEnabled(false);

        // 设定下拉圆圈的背景
        swipe_refresh.setProgressBackgroundColorSchemeResource(R.color.colorPrimaryDark);
        //通过 setRefreshing(false) 和 setRefreshing(true) 来手动调用刷新的动画。

        chart = (LineChartView) findViewById(R.id.chart);
        getSupportActionBar().setTitle("最近体重变化图");

        chart.setOnValueTouchListener(new ValueTouchListener());
        //chart.setInteractive(false); //是否可以缩放
        chart.setValueSelectionEnabled(true);//节点点击后放大

        chart.setViewportCalculationEnabled(false);
        resetViewport();
    }

    /**
     * 刷新
     */
    @Override
    public void onRefresh() {
        generateValues();
        //设置刷新完成
        swipe_refresh.setRefreshing(false);
        UtilsToast.show(this,"refresh done");
    }

    /**
     * 重置
     */
    private void resetViewport() {
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 50;
        v.top = 60;
        v.left = 0;
        v.right = numberOfPoints - 1;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    /**
     * 设置X轴数据
     */
    private void generateValues() {
        final BmobQuery<WeightData> query = new BmobQuery<>();
        query.setLimit(numberOfPoints);
        query.order("-AddTime");
        query.findObjects(ShowCharts.this, new FindListener<WeightData>() {
            @Override
            public void onSuccess(List<WeightData> list) {
                SimpleDateFormat sdf=  new SimpleDateFormat(UtilsDate.DATE_FORMAT_DEFAULT);
                Calendar ca = Calendar.getInstance();
                for (int i =0;i< numberOfPoints;++i){
                    WeightData entity = list.get(i);
                    randomNumbersTab[0][i] = entity.getWeight();
                    try {
                        ca.setTime(sdf.parse(entity.getAddTime().getDate()));
                        xText[i] = ca.get(Calendar.DAY_OF_MONTH) +" "+ca.get(Calendar.HOUR_OF_DAY)+":"+ca.get(Calendar.MINUTE);
                    } catch (ParseException e) {
                        xText[i] = "/";
                    }
                }
                generateData();
            }

            @Override
            public void onError(int i, String s) {
                UtilsToast.show(ShowCharts.this,"Error："+s);

            }
        });


    }

    /**
     * 构造统计图
     */
    private void generateData() {
        //X轴文本
        ArrayList<AxisValue> axisValuesX = new ArrayList<AxisValue>();
        List<Line> lines = new ArrayList<Line>();

        for (int i = 0; i < numberOfLines; ++i) {

            List<PointValue> values = new ArrayList<PointValue>();

            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, (float) randomNumbersTab[i][j]));
                axisValuesX.add(new AxisValue(j).setLabel(xText[j]));//设置X轴文本
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLORS[i]);
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);
            line.setFormatter(new SimpleLineChartValueFormatter(2)); //设置Y轴数据保留小数点后面两位
            if (pointsHaveDifferentColor){
                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
            }
            lines.add(line);
        }

        data = new LineChartData(lines);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("日期");
                axisY.setName("体重");
            }
            //X轴文本
            axisX.setValues(axisValuesX);
            axisX.setHasTiltedLabels(true);
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(data);

    }

    /**
     * 点击监听器
     */
    private class ValueTouchListener implements LineChartOnValueSelectListener {
        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            UtilsToast.show(ShowCharts.this, "体重：" + value.getY() + "KG");
        }

        @Override
        public void onValueDeselected() {

        }
    }
}
