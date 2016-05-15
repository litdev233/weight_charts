package net.litdev.weight_charts.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.litdev.weight_charts.R;
import net.litdev.weight_charts.entity.WeightData;
import net.litdev.weight_charts.inter.DialogDateListener;
import net.litdev.weight_charts.utils.UtilsToast;
import net.litdev.weight_charts.widget.CustomDateDialog;
import net.litdev.weight_charts.widget.CustomTimeDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.SaveListener;

public class AddWeight extends AppCompatActivity {

    private CustomDateDialog dateDialog;
    private CustomTimeDialog timeDialog;
    private EditText tv_date;
    private EditText tv_time;
    private EditText tv_weight;
    private Button btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);
        ActionBar bar = getSupportActionBar();
        bar.setTitle("新增记录");

        initView();
    }

    private void initView() {
        tv_date = (EditText) findViewById(R.id.tv_date);
        tv_time = (EditText) findViewById(R.id.tv_time);
        tv_weight = (EditText) findViewById(R.id.tv_weight);
        btn_add = (Button) findViewById(R.id.btn_add);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy/MM/dd");
        tv_date.setText(sdf_date.format(calendar.getTime()));
        SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
        tv_time.setText(sdf_time.format(calendar.getTime()));

        tv_date.setOnClickListener(new DateClickListener());
        tv_time.setOnClickListener(new TimeClickListener());
        btn_add.setOnClickListener(new AddClickListener());

        tv_date.setOnTouchListener(new ETTouchListener());
        tv_time.setOnTouchListener(new ETTouchListener());
    }

    class AddClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            if(TextUtils.isEmpty(tv_weight.getText()))
            {
                UtilsToast.show(AddWeight.this,"体重数据未填写");
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Date date;

            String date_str = tv_date.getText().toString().trim()+" "+tv_time.getText().toString().trim();
            try {
                date = sdf.parse(date_str);
            } catch (ParseException e) {
                e.printStackTrace();
                UtilsToast.show(AddWeight.this,"获取时间有误");
                return;
            }
            btn_add.setEnabled(false);
            WeightData wd = new WeightData();
            wd.setWeight(Double.parseDouble(tv_weight.getText().toString().trim()));
            wd.setAddTime(new BmobDate(date == null ? new Date(): date));

            wd.save(AddWeight.this, new SaveListener() {
                @Override
                public void onSuccess() {
                    btn_add.setEnabled(true);
                    UtilsToast.show(AddWeight.this,"添加成功");
                    startActivity(new Intent(AddWeight.this,MainActivity.class));
                }

                @Override
                public void onFailure(int i, String s) {
                    btn_add.setEnabled(true);
                    UtilsToast.show(AddWeight.this,"添加失败");
                }
            });
        }
    }


    /**
     * 日期
     */
    class DateClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            dateDialog = new CustomDateDialog(AddWeight.this, new DialogDateListener() {
                @Override
                public void resultData(Calendar datetime) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    tv_date.setText(sdf.format(datetime.getTime()));
                    dateDialog.dismiss();
                }
            });
            dateDialog.show();
        }
    }

    /**
     * 时间
     */
    class TimeClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            timeDialog = new CustomTimeDialog(AddWeight.this, new DialogDateListener() {
                @Override
                public void resultData(Calendar datetime) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    tv_time.setText(sdf.format(datetime.getTime()));
                    timeDialog.dismiss();
                }
            });
            timeDialog.show();
        }
    }

    /**
     * 文本框
     */
    class ETTouchListener implements View.OnTouchListener
    {
        EditText temp;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            temp= (EditText)v;
            temp.setInputType(InputType.TYPE_NULL);
            return false;
        }
    }

}
