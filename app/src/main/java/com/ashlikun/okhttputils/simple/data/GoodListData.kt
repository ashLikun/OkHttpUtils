package com.ashlikun.okhttputils.simple.data

import com.ashlikun.okhttputils.http.response.HttpResponse

/**
 * 作者　　: 李坤
 * 创建时间: 2018/5/10 0010　下午 3:16
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：
 */
class GoodListData : HttpResponse() {
    var currentPage = 0
    var pageCount = 0
    var currentRows = 0
    var list: List<ListBean>? = null

    class ListBean {
        var title = 0
        var goods_id: String? = null
        var goods_name: String? = null
        var type: String? = null
        var Price: String? = null
        var wlPrice: String? = null
        var scPrice: String? = null
        var default_image: String? = null
        var seller_id: String? = null
        var sale = 0
        var goodsNowStock: String? = null
        var good_rate: String? = null
        var has_storecoupon: String? = null
        var act_image: String? = null
    }
}