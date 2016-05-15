package net.litdev.weight_charts.entity;

import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by litde on 2016/5/15.
 */
public class WeightData extends BmobObject {
    private Double weight;
    private BmobDate AddTime;


    public BmobDate getAddTime() {
        return AddTime;
    }

    public void setAddTime(BmobDate addTime) {
        AddTime = addTime;
    }

    /**
     * 获取体重数据
     * @return
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * 设置体重数据
     * @param weight
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
