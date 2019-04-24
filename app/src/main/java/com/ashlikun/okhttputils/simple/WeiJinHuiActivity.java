package com.ashlikun.okhttputils.simple;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.ashlikun.okhttputils.http.request.HttpRequest;

/**
 * 作者　　: 李坤
 * 创建时间: 2017/12/27　13:54
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */

public class WeiJinHuiActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeiJinHuiData();
            }
        });
    }

    public void getWeiJinHuiData() {
        HttpRequest p = HttpRequest.post("https://www.vmoney.cn/p2p/data/ws/rest/transferMobile/searchTransferListMobile");
        p.setContentJson("{\n" +
                "\t\"orderBy\": {},\n" +
                "\t\"pageSize\": 1000,\n" +
                "\t\"pageIndex\": 1\n" +
                "}");

    }
}
