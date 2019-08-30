package com.ashlikun.okhttputils.simple;

import com.ashlikun.okhttputils.http.response.HttpResponse;

import java.util.List;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/5/10 0010　下午 3:16
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class GoodListData extends HttpResponse {

    public int currentPage;
    public int pageCount;
    public int currentRows;
    public List<ListBean> data;


    public static class ListBean {
        public String goods_id;
        public String goods_name;
        public String type;
        public String Price;
        public String wlPrice;
        public String scPrice;
        public String default_image;
        public String seller_id;
        public int sale;
        public String goodsNowStock;
        public String good_rate;
        public String has_storecoupon;
        public String act_image;
    }
}
