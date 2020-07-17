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
import com.ashlikun.okhttputils.http.callback.AbsProgressCallback;
import com.ashlikun.okhttputils.http.download.DownloadManager;
import com.ashlikun.okhttputils.http.download.DownloadTask;
import com.ashlikun.okhttputils.http.download.DownloadTaskListener;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public DownloadTask task = new DownloadTask.Builder().setId("resmp;csr=1bbd")
            .setUrl("http://s.shouji.qihucdn.com/200407/28e9b1ac86444ec466f56ff0df9aa7b9/com.qihoo360.mobilesafe_267.apk?en=curpage%3D%26exp%3D1587610389%26from%3Dopenbox_detail_index%26m2%3D%26ts%3D1587005589%26tok%3D8ac5571c83e9bb8497076a3435f3c90d%26f%3Dz.apk")
            .setListener(new DownloadTaskListener() {
                @Override
                public void onDownloading(DownloadTask downloadTask, long completedSize, long totalSize, double percent) {
                    Log.e("aaa", "onDownloading percent = " + percent);
                }

                @Override
                public void onPause(DownloadTask downloadTask, long completedSize, long totalSize, double percent) {
                    Log.e("aaa", "onPause percent = " + percent);
                }

                @Override
                public void onCancel(DownloadTask downloadTask) {
                    Log.e("aaa", "onCancel ");
                }

                @Override
                public void onDownloadSuccess(DownloadTask downloadTask, File file) {
                    Log.e("aaa", "onDownloadSuccess  " + file.getPath());
                }

                @Override
                public void onError(DownloadTask downloadTask, int errorCode) {
                    Log.e("aaa", "onError " + errorCode);

                }
            }).build();

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

    public void onButt0Click(View view) {
        task.setCancel();
    }

    public void onButt1Click(View view) {
    }

    public void onButt2Click(View view) {
        HuodongData data = new HuodongData();
        data.hashCode();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            stringBuilder.append(aa);
        }

        OkHttpUtils.post("https://api-sip.510gow.com/interface?action=recommend")
                .addHeader("action", "recommend")
                .setCacheMode(CacheMode.NO_CACHE)
                .setCacheTime(3600000)
                .buildCall()
                .progressCallback(new AbsProgressCallback() {
                    @Override
                    public void onLoading(long progress, long total, boolean done, boolean isUpdate) {
                        Log.e("aaa", "progress = " + progress + "   total = " + total + "    done = " + done + "   isUpdate = " + isUpdate);
                    }
                })
                .execute(new AbsCallback<GoodListData>() {
                    @Override
                    public void onSuccess(GoodListData responseBody) {
                        Log.e("onSuccess", responseBody.getStringValue2("", "aaa", "bbb"));
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

    String aa = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
}
