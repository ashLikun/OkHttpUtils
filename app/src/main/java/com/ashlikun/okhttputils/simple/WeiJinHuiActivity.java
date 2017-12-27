package com.ashlikun.okhttputils.simple;

import android.app.Application;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ashlikun.okhttputils.http.OkHttpUtils;
import com.ashlikun.okhttputils.http.SimpleCallback;
import com.ashlikun.okhttputils.http.request.RequestParam;
import com.ashlikun.okhttputils.http.response.HttpResponse;
import com.ashlikun.okhttputils.simple.data.WeiJinModel;
import com.ashlikun.orm.LiteOrmUtil;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.util.List;

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
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeiJinHuiData();
            }
        });
    }

    public void getWeiJinHuiData() {
        RequestParam p = RequestParam.post("https://www.vmoney.cn/p2p/data/ws/rest/transferMobile/searchTransferListMobile");
        p.setContentJson("{\n" +
                "\t\"orderBy\": {},\n" +
                "\t\"pageSize\": 1000,\n" +
                "\t\"pageIndex\": 1\n" +
                "}");
        OkHttpUtils.getInstance().execute(p, new SimpleCallback<HttpResponse>() {
            @Override
            public void onSuccess(HttpResponse response) {
                try {
                    List<WeiJinModel> lists = response.getValue(new TypeToken<List<WeiJinModel>>() {
                    }.getType(), "data", "list");
                    Log.e("aaaaa", lists.size() + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
}
