package com.ashlikun.okhttputils.simple;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ashlikun.gson.GsonHelper;
import com.ashlikun.okhttputils.http.OkHttpUtils;
import com.ashlikun.okhttputils.http.cache.CacheEntity;
import com.ashlikun.okhttputils.http.cache.CacheMode;
import com.ashlikun.okhttputils.http.callback.AbsCallback;
import com.ashlikun.okhttputils.http.download.DownloadManager;
import com.ashlikun.okhttputils.http.download.DownloadTask;
import com.ashlikun.okhttputils.http.download.DownloadTaskListener;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadManager.initPath(getApplication().getCacheDir().getPath());
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
        DownloadManager.getInstance().addDownloadTask(new DownloadTask.Builder().setId("resmp;csr=1bbd")
                .setUrl("https://imtt.dd.qq.com/16891/E75CC08F59CA2836B8A48263E7DDEE4C.apk?fsname=com.nmlg.renrenying_1.4.2_12.apk&amp;csr=1bbd")
                .setListener(new DownloadTaskListener() {


                    @Override
                    public void onDownloading(DownloadTask downloadTask, long completedSize, long totalSize, double percent) {
                        Log.e("aaa", "onDownloading percent = " + percent);
                    }

                    @Override
                    public void onPause(DownloadTask downloadTask, long completedSize, long totalSize, double percent) {

                    }

                    @Override
                    public void onCancel(DownloadTask downloadTask) {

                    }

                    @Override
                    public void onDownloadSuccess(DownloadTask downloadTask, File file) {

                    }

                    @Override
                    public void onError(DownloadTask downloadTask, int errorCode) {

                    }
                }).build());
    }

    public void onButt2Click(View view) {
        HuodongData data = new HuodongData();
        data.hashCode();

        OkHttpUtils.post("http://ly.lingyunexpress.com/tools/kapp_tool.ashx")
                .addHeader("action", "tchat_users_list")
                .addParam("accessToken", "A8C5CF33-64A1-49F4-ADBC-4DBF05D5F94B")
                .addParam("id", 325)
                .addParam("page", 1)
                .addParam("type", 2)
                .addParam("NM_REFERER", "/goods/1313517")
                .addParam("NM_URI", "/category/427/325")
                .setCacheMode(CacheMode.NO_CACHE)
                .setCacheTime(3600000)
                .toJson()
                .buildCall()
                .execute(new AbsCallback<GoodListData>() {
                    @Override
                    public void onSuccess(GoodListData responseBody) {
                        Log.e("onSuccess", GsonHelper.getGson().toJson(responseBody));
                    }

                    @Override
                    public void onCacheSuccess(CacheEntity entity, GoodListData responseBody) {
                        super.onCacheSuccess(entity, responseBody);
                        Log.e("onCacheSuccess", GsonHelper.getGson().toJson(responseBody) + "\n\n\n" + entity.result);
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
