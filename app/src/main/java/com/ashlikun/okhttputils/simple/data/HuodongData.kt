package com.ashlikun.okhttputils.simple.data

import com.google.gson.annotations.SerializedName

/**
 * 作者　　: 李坤
 * 创建时间:2017/8/13 0013　15:13
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：学生活动列表数据，我的里面，和活动列表
 */
class HuodongData {
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
    var id = 0
    var type //活动类型
            = 0
    private val name: String? = null

    @SerializedName("shijian")
    private val time: String? = null
    private val yusuan = 10000
    private val yufukuan = 20000

    @SerializedName("src")
    var url: String? = null
    private val companyname: String? = null
    var status //状态
            = 0

    @SerializedName("leixing")
    var source //类型 0就是商家邀请的，就是待确认，如果是1就是待报名
            = 0
    var merchantId //商家id
            = 0
}