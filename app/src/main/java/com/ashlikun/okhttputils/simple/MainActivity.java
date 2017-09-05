package com.ashlikun.okhttputils.simple;

import android.app.Application;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ashlikun.okhttputils.http.Callback;
import com.ashlikun.okhttputils.http.HttpException;
import com.ashlikun.okhttputils.http.OkHttpUtils;
import com.ashlikun.okhttputils.http.request.RequestParam;
import com.ashlikun.okhttputils.http.response.HttpResponse;
import com.ashlikun.okhttputils.liteorm.LiteOrmUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LiteOrmUtil.init(new LiteOrmUtil.OnNeedListener() {
            @Override
            public Application getApplication() {
                return getApplication();
            }

            @Override
            public boolean isDebug() {
                return true;
            }

            @Override
            public int getVersionCode() {
                return 1;
            }
        });
        setContentView(R.layout.activity_main);
        RequestParam p = new RequestParam("http://jielehua.vcash.cn/api/jlh/apply/getApplyProgress/");
        p.get();
        p.addHeader("accessToken", "A8C5CF33-64A1-49F4-ADBC-4DBF05D5F94B");
        //4690943?accessToken=8079CE15-038E-4977-8443-E885730DE268
        p.appendPath("118915");
        p.addParam("accessToken", "11111");
        p.addParam("aasda", "22222");
        p.addParam("9966", "33333");
        p.addParam("aaaaa", "44444");
        OkHttpUtils.getInstance().execute(p, new Callback<String>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(HttpException e) {

            }

            @Override
            public void onSuccess(String responseBody) {
                Log.e("onSuccess", responseBody);
            }
        });
        //https://jlhpredeploy.vcash.cn/api/jlh/apply/getCustomerApplyInfo/4690943?accessToken=DE3AB2FCF409231F0C7F9D5EE306264D

        HttpResponse response = new HttpResponse();
        response.json = "{\n" +
                "\t\"code\": 0,\n" +
                "\t\"msg\": \"\\u767b\\u9646\\u6210\\u529f\",\n" +
                "\t\"data\": {\n" +
                "\t\t\"type\": 2,\n" +
                "\t\t\"id\": \"5\",\n" +
                "\t\t\"token\": \"zn8UmgHJT\"\n" +
                "\t}\n" +
                "}";
        Log.e("aaa", response.getIntValue("type") + "");
    }
}
