package net.litdev.weight_charts.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import net.litdev.weight_charts.R;
import net.litdev.weight_charts.entity.WeightData;
import net.litdev.weight_charts.utils.DateUtils;
import net.litdev.weight_charts.utils.UtilsToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
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

public class ShowCharts extends AppCompatActivity {

    private LineChartView chart;
    private LineChartData data;
    private AVLoadingIndicatorView avloadingIndicatorView;
    private TextView tv_error_msg;

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

        chart = (LineChartView) findViewById(R.id.chart);
        avloadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.avloadingIndicatorView);
        tv_error_msg = (TextView) findViewById(R.id.tv_error_msg);
        getSupportActionBar().setTitle("最近体重变化图");

        chart.setOnValueTouchListener(new ValueTouchListener());
        //chart.setInteractive(true); //是否可以缩放
        chart.setValueSelectionEnabled(true);//节点点击后放大
        generateValues();

        //禁用视图重新计算，see toggleCubic() method for more info.
        chart.setViewportCalculationEnabled(false);

        resetViewport();
    }

    /**
     * 重置
     */
    private void resetViewport() {
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 30;
        v.top = 70;
        v.left = 0;
        v.right = numberOfPoints - 1;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    /**
     * 设置X轴数据
     */
    private void generateValues() {
        /*for (int i = 0; i < numberOfLines; ++i) {
            for (int j = 0; j < numberOfPoints; ++j) {
                randomNumbersTab[i][j] = (float) Math.random() * 90f;
            }
        }*/
        chart.setVisibility(View.GONE);
        avloadingIndicatorView.setVisibility(View.VISIBLE);
        tv_error_msg.setVisibility(View.GONE);

        final BmobQuery<WeightData> query = new BmobQuery<>();
        query.setLimit(numberOfPoints);
        query.order("-AddTime");
        query.findObjects(ShowCharts.this, new FindListener<WeightData>() {
            @Override
            public void onSuccess(List<WeightData> list) {
                SimpleDateFormat sdf=  new SimpleDateFormat(DateUtils.DATE_FORMAT_DEFAULT);
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

                chart.setVisibility(View.VISIBLE);
                avloadingIndicatorView.setVisibility(View.GONE);
                tv_error_msg.setVisibility(View.GONE);
                generateData();
            }

            @Override
            public void onError(int i, String s) {
                chart.setVisibility(View.GONE);
                avloadingIndicatorView.setVisibility(View.GONE);
                tv_error_msg.setVisibility(View.VISIBLE);
                tv_error_msg.setText("Error:"+s);
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
            UtilsToast.show(ShowCharts.this,"体重："+value.getY()+"KG");
        }

        @Override
        public void onValueDeselected() {

        }
    }
}
