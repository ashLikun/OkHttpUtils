package com.ashlikun.okhttputils.simple;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ashlikun.okhttputils.http.OkHttpUtils;
import com.ashlikun.okhttputils.http.cache.CacheEntity;
import com.ashlikun.okhttputils.http.callback.AbsCallback;
import com.ashlikun.okhttputils.http.callback.FileCallback;
import com.ashlikun.okhttputils.json.GsonHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        LiteOrmUtil.init(new LiteOrmUtil.OnNeedListener() {
//            @Override
//            public Application getApplication() {
//                return getApplication();
//            }
//
//            @Override
//            public boolean isDebug() {
//                return true;
//            }
//
//            @Override
//            public int getVersionCode() {
//                return 1;
//            }
//        });
//                //4690943?accessToken=8079CE15-038E-4977-8443-E885730DE268

//        try {
//            String string = OkHttpUtils.getInstance().syncExecute(p,String.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //https://jlhpredeploy.vcash.cn/api/jlh/apply/getCustomerApplyInfo/4690943?accessToken=DE3AB2FCF409231F0C7F9D5EE306264D

//        HttpResponse response = new HttpResponse();
//        String json = "[]";
//        HttpResult result;
//        try {
//            result = GsonHelper.getGson().fromJson(json, new TypeToken<HttpResult<UserData>>() {
//            }.getType());
//        } catch (JsonSyntaxException exception) {
//            exception.printStackTrace();
//        }


//        Log.e("aaa", "" + "");
//
//        String json2 ="{\n" +
//                "\t\"code\": 0,\n" +
//                "\t\"msg\": \"\\u4e0a\\u4f20\\u56fe\\u7247\\u6210\\u529f\",\n" +
//                "\t\"imagePath\": \"..\\/up\\/15097930441447110546.jpg\"\n" +
//                "}";
////        HuodongData data = GsonHelper.getGson().fromJson(json2, HuodongData.class);
//        response.json = json2;
//        try {
//            response.getStringValue("imagePath");
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        }
//        Log.e("aaa", "" + "");
    }

    public void onButt1Click(View view) {
        OkHttpUtils.get("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=399118516,1470088676&fm=27&gp=0.jpg")
                .buildCall()
                .execute(new FileCallback() {
                    @Override
                    public void onLoading(long progress, long total, boolean done, boolean isUpdate) {
                        Log.e("onLoading", "progress = " + progress + "    total = " + total + "  done = " + done + " isUpdate = " + isUpdate);
                    }

                    @Override
                    public void onSuccess(File responseBody) {
                        super.onSuccess(responseBody);
                        ImageView imageView = (ImageView) findViewById(R.id.image);
                        imageView.setImageBitmap(BitmapFactory.decodeFile(responseBody.getAbsolutePath()));
                    }
                });
    }

    public void onButt2Click(View view) {
        HuodongData data = new HuodongData();
        HuodongData data2 = new HuodongData();
        data.hashCode();
        Map<String, String> map = new HashMap<>();
        map.put("aaa", "111");
        map.put("bbb", null);
        map.put("ccc", "222");
        map.put("ddd", "333");
        String json = GsonHelper.getGson().toJson(new TestData());
        Log.e("ddd", json);


        OkHttpUtils.post("https://api.0gow.com/interface?action=n_goodslist")
                .addHeader("accessToken", "A8C5CF33-64A1-49F4-ADBC-4DBF05D5F94B")
                .addParam("id", 325)
                .addParam("page", 1)
                .addParam("type", 2)
                .addParam("NM_REFERER", "/goods/1313517")
                .addParam("NM_URI", "/category/427/325")
                .buildCall()
                .firstUseCache()
                .execute(new AbsCallback<GoodListData>() {
                    @Override
                    public void onSuccess(GoodListData responseBody) {
                        Log.e("onSuccess", responseBody.toString());
                    }

                    @Override
                    public void onCacheSuccess(CacheEntity entity, GoodListData responseBody) {
                        super.onCacheSuccess(entity, responseBody);
                        Log.e("onCacheSuccess", responseBody.toString() + entity.result);
                    }
                });
    }

    public static class TestData {
        String aaa = "111";
        String bbb = null;
        String ccc = "222";
        String ddd = "333";
    }
}
