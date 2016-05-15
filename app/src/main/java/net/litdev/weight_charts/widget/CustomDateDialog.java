package net.litdev.weight_charts.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.flyco.dialog.utils.CornerUtils;
import com.flyco.dialog.widget.base.BaseDialog;
import net.litdev.weight_charts.R;
import net.litdev.weight_charts.inter.DialogDateListener;

import java.util.Calendar;

/**
 * 自定义日期Dialog
 */
public class CustomDateDialog extends BaseDialog<CustomDateDialog> {
    private DatePicker dp_date;
    private Button btn_confirm;
    private Context mContext;
    private DialogDateListener listener;

    public CustomDateDialog(Context context, DialogDateListener listener) {
        super(context);
        mContext = context;
        this.listener = listener;
    }

    @Override
    public View onCreateView() {
        widthScale(0.85f);
        //showAnim(new Swing());
        View inflate = View.inflate(mContext, R.layout.cus_datepick, null);
        inflate.setBackgroundDrawable(
                CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(5)));
        dp_date = (DatePicker) inflate.findViewById(R.id.dp_date);
        btn_confirm = (Button) inflate.findViewById(R.id.btn_confirm);
        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = dp_date.getYear();
                int monthOfYear = dp_date.getMonth();
                int dayOfMonth = dp_date.getDayOfMonth();
                Calendar ca = Calendar.getInstance();
                ca.set(year, monthOfYear, dayOfMonth,0,0);
                listener.resultData(ca);
            }
        });

    }


}