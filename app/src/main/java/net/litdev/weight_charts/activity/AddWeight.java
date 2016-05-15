package net.litdev.weight_charts.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.litdev.weight_charts.R;
import net.litdev.weight_charts.inter.DialogDateListener;
import net.litdev.weight_charts.widget.CustomDateDialog;
import net.litdev.weight_charts.widget.CustomTimeDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddWeight extends AppCompatActivity {

    private CustomDateDialog dateDialog;
    private CustomTimeDialog timeDialog;
    private EditText tv_date;
    private EditText tv_time;
    private EditText tv_weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);
        ActionBar bar = getSupportActionBar();
        bar.setTitle("新增记录");

        tv_date = (EditText) findViewById(R.id.tv_date);
        tv_time = (EditText) findViewById(R.id.tv_time);
        tv_weight = (EditText) findViewById(R.id.tv_weight);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy/MM/dd");
        tv_date.setText(sdf_date.format(calendar.getTime()));
        SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
        tv_time.setText(sdf_time.format(calendar.getTime()));

        tv_date.setOnClickListener(new DateClickListener());
        tv_time.setOnClickListener(new TimeClickListener());

        tv_date.setOnTouchListener(new ETTouchListener());
        tv_time.setOnTouchListener(new ETTouchListener());
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
