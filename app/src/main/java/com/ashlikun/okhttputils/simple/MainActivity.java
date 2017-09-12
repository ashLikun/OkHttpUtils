package com.ashlikun.okhttputils.simple;

import android.app.Application;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ashlikun.okhttputils.http.Callback;
import com.ashlikun.okhttputils.http.OkHttpUtils;
import com.ashlikun.okhttputils.http.request.RequestParam;
import com.ashlikun.okhttputils.http.response.HttpResponse;
import com.ashlikun.okhttputils.http.response.HttpResult;
import com.ashlikun.okhttputils.json.GsonHelper;
import com.ashlikun.okhttputils.liteorm.LiteOrmUtil;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

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
        p.addParamFile("aa", "filePath");
        OkHttpUtils.getInstance().execute(p, new Callback<String>() {
            @Override
            public void onSuccess(String responseBody) {
                Log.e("onSuccess", responseBody);
            }

        });

//        try {
//            String string = OkHttpUtils.getInstance().syncExecute(p,String.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //https://jlhpredeploy.vcash.cn/api/jlh/apply/getCustomerApplyInfo/4690943?accessToken=DE3AB2FCF409231F0C7F9D5EE306264D

        HttpResponse response = new HttpResponse();
        String json = "{\n" +
                "\t\"code\": 888,\n" +
                "\t\"msg\": \"\\u83b7\\u53d6\\u4fe1\\u606f\\u5931\\u8d25\",\n" +
                "\t\"data\": \"\"\n" +
                "}";
        HttpResult result;
        try {
            result = GsonHelper.getGson().fromJson(json, new TypeToken<HttpResult<UserData>>() {
            }.getType());
        } catch (JsonSyntaxException exception) {

        }
        HttpResponse response1 = new HttpResponse();
        response1.json = null;
        response1.getIntValue("");

        Log.e("aaa", "" + "");
    }
}
