package com.ashlikun.okhttputils.simple;

import com.google.gson.annotations.SerializedName;

/**
 * 作者　　: 李坤
 * 创建时间:2017/8/13 0013　15:13
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：学生活动列表数据，我的里面，和活动列表
 */

public class HuodongData {


    /**
     * name : 落地活动一
     * status: 0,
     * shijian : 6-7
     * yusuan : 567
     * yufukuan : 100
     * src : ../up/15024439532015-04-16_155656.png
     * companyname : 李坤
     */
    @SerializedName("xid")
    public int id;
    public int type;//活动类型
    private String name;

    @SerializedName("shijian")
    private String time;
    private int yusuan = 10000;
    private int yufukuan = 20000;
    @SerializedName("src")
    public String url;
    private String companyname;
    public int status;//状态
    @SerializedName("leixing")
    public int source;//类型 0就是商家邀请的，就是待确认，如果是1就是待报名
    public int merchantId;//商家id

}
