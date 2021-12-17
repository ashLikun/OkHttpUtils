package com.ashlikun.okhttputils.simple

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ashlikun.gson.GsonHelper
import com.ashlikun.okhttputils.http.OkHttpUtils
import com.ashlikun.okhttputils.http.cache.CacheEntity
import com.ashlikun.okhttputils.http.cache.CacheMode
import com.ashlikun.okhttputils.http.callback.AbsCallback
import com.ashlikun.okhttputils.http.callback.ProgressCallBack
import com.ashlikun.okhttputils.http.download.DownloadManager
import com.ashlikun.okhttputils.http.download.DownloadTask
import com.ashlikun.okhttputils.http.download.DownloadTaskListener
import com.ashlikun.okhttputils.simple.data.GoodListData
import com.ashlikun.okhttputils.simple.data.HuodongData
import com.ashlikun.okhttputils.simple.databinding.ActivityMainBinding
import com.ashlikun.okhttputils.simple.retrofit.Test
import java.io.File

class MainActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    var task: DownloadTask = DownloadTask("resmp;csr=1bbd",
        url = "http://s.shouji.qihucdn.com/200407/28e9b1ac86444ec466f56ff0df9aa7b9/com.qihoo360.mobilesafe_267.apk?en=curpage%3D%26exp%3D1587610389%26from%3Dopenbox_detail_index%26m2%3D%26ts%3D1587005589%26tok%3D8ac5571c83e9bb8497076a3435f3c90d%26f%3Dz.apk",
        object : DownloadTaskListener {
            override fun onDownloading(
                downloadTask: DownloadTask,
                completedSize: Long,
                totalSize: Long,
                percent: Double
            ) {
                Log.e("aaa", "onDownloading percent = $percent")
            }

            override fun onPause(
                downloadTask: DownloadTask,
                completedSize: Long,
                totalSize: Long,
                percent: Double
            ) {
                Log.e("aaa", "onPause percent = $percent")
            }

            override fun onCancel(downloadTask: DownloadTask) {
                Log.e("aaa", "onCancel ")
            }

            override fun onDownloadSuccess(downloadTask: DownloadTask, file: File) {
                Log.e("aaa", "onDownloadSuccess  " + file.path)
            }

            override fun onError(downloadTask: DownloadTask, errorCode: Int) {
                Log.e("aaa", "onError $errorCode")
            }
        })

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        DownloadManager.initPath(getApplication().getCacheDir().getPath())
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

    fun onButt00Click(view: View?) {
        Test.start()
    }

    fun onButt0Click(view: View?) {
        task.setCancel()
    }

    fun onButt1Click(view: View?) {}
    fun onButt2Click(view: View?) {
        val data = HuodongData()
        data.hashCode()
        val stringBuilder = StringBuilder()
        for (i in 0..299) {
            stringBuilder.append(aa)
        }
        //        HttpUtils.runNewThread(new Runnable() {
//            @Override
//            public void run() {
//                TypeToken a =new TypeToken<HttpListResult<List<GoodListData.ListBean>>>(){};
//                HttpListResult<List<GoodListData.ListBean>> aa = null;
//                try {
//                    aa = OkHttpUtils.post("https://api-sip.510gow.com/interface?action=recommend")
//                            .addHeader("action", "recommend")
//                            .setCacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
//                            .setCacheTime(3600000)
//                            .buildCall()
//                            .progressCallback(new AbsProgressCallback() {
//                                @Override
//                                public void onLoading(long progress, long total, boolean done, boolean isUpdate) {
//                                    Log.e("aaa", "progress = " + progress + "   total = " + total + "    done = " + done + "   isUpdate = " + isUpdate);
//                                }
//                            })
//                            .syncExecute(a.getType());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                Log.e("aaa", aa.toString());
//            }
//        });
        OkHttpUtils.post("https://api-sip.510gow.com/interface?action=recommend")
            .apply {
                addHeader("action", "recommend")
                cacheMode = CacheMode.FIRST_CACHE_THEN_REQUEST
                cacheTime = 3600000
            }
            .buildCall()
            .execute(object : AbsCallback<GoodListData>() {
                override val progressCallBack: ProgressCallBack =
                    { progress, total, done, isUpdate ->
                        Log.e(
                            "aaa",
                            "progress = $progress   total = $total    done = $done   isUpdate = $isUpdate"
                        )
                    }

                override fun onSuccess(responseBody: GoodListData) {
                    Log.e("onSuccess", responseBody.getStringValueDef("", "aaa", "bbb"))
                    Log.e("onSuccess", GsonHelper.getGson().toJson(responseBody))
                }

                override fun onCacheSuccess(entity: CacheEntity, responseBody: GoodListData) {
                    super.onCacheSuccess(entity, responseBody)
                    Log.e(
                        "onCacheSuccess",
                        GsonHelper.getGson().toJson(responseBody) + "\n\n\n" + entity.result
                    )
                }
            })
    }


    var aa =
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
}