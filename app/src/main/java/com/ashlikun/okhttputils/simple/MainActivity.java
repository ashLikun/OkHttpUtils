package com.ashlikun.okhttputils.simple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ashlikun.okhttputils.http.Callback;
import com.ashlikun.okhttputils.http.OkHttpUtils;
import com.ashlikun.okhttputils.http.request.RequestParam;
import com.ashlikun.okhttputils.liteorm.LiteOrmUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LiteOrmUtil.init(getApplication(), 1, true);
        setContentView(R.layout.activity_main);
        RequestParam p = new RequestParam("http://jielehua.vcash.cn/api/jlh/apply/getApplyProgress/");
        p.get();
        p.addHeader("accessToken", "A8C5CF33-64A1-49F4-ADBC-4DBF05D5F94B");
        //4690943?accessToken=8079CE15-038E-4977-8443-E885730DE268
        p.appendPath("118915");
        p.addParam("accessToken", "A8C5CF33-64A1-49F4-ADBC-4DBF05D5F94B");
        OkHttpUtils.getInstance().execute(p, new Callback<String>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onSuccess(String responseBody) {
                Log.e("onSuccess", responseBody);
            }
        });
        //https://jlhpredeploy.vcash.cn/api/jlh/apply/getCustomerApplyInfo/4690943?accessToken=DE3AB2FCF409231F0C7F9D5EE306264D

    }
}
