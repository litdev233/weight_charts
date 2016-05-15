package net.litdev.weight_charts.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.flyco.dialog.utils.CornerUtils;
import com.flyco.dialog.widget.base.BaseDialog;

import net.litdev.weight_charts.R;
import net.litdev.weight_charts.inter.DialogDateListener;

import java.util.Calendar;

/**
 * 自定义时间
 */
public class CustomTimeDialog extends BaseDialog<CustomTimeDialog> {
    private TimePicker dp_time;
    private Button btn_confirm;
    private Context mContext;
    private DialogDateListener listener;

    public CustomTimeDialog(Context context, DialogDateListener listener) {
        super(context);
        mContext = context;
        this.listener = listener;
    }

    @Override
    public View onCreateView() {
        widthScale(0.85f);
        //showAnim(new Swing());
        View inflate = View.inflate(mContext, R.layout.cus_timepick, null);
        inflate.setBackgroundDrawable(
                CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(5)));
        dp_time = (TimePicker) inflate.findViewById(R.id.dp_time);
        btn_confirm = (Button) inflate.findViewById(R.id.btn_confirm);
        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int monthOfYear = calendar.get(Calendar.MONTH);
        final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar ca = Calendar.getInstance();
                int hour = dp_time.getCurrentHour();
                int minute = dp_time.getCurrentMinute();
                ca.set(year, monthOfYear, dayOfMonth,hour,minute);
                listener.resultData(ca);
            }
        });
    }
}