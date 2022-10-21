package com.ashlikun.okhttputils.simple

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ashlikun.gson.GsonHelper
import com.ashlikun.okhttputils.http.HttpException
import com.ashlikun.okhttputils.http.OkHttpUtils
import com.ashlikun.okhttputils.http.cache.CacheEntity
import com.ashlikun.okhttputils.http.cache.CacheMode
import com.ashlikun.okhttputils.http.callback.AbsCallback
import com.ashlikun.okhttputils.http.callback.BitmapCallback
import com.ashlikun.okhttputils.http.callback.ProgressCallBack
import com.ashlikun.okhttputils.http.download.DownloadManager
import com.ashlikun.okhttputils.simple.data.GoodListData
import com.ashlikun.okhttputils.simple.data.HuodongData
import com.ashlikun.okhttputils.simple.databinding.ActivityMainBinding
import com.ashlikun.okhttputils.simple.retrofit.RetrofitTest

class MainActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    val meinv =
        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.jj20.com%2Fup%2Fallimg%2F1114%2F060421091316%2F210604091316-6-1200.jpg&refer=http%3A%2F%2Fimg.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1642340471&t=fb1cdad91178b19878ffd4db8ed90fd0"

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonRetrofit.setOnClickListener {
            RetrofitTest.start()
        }
        binding.buttonStartDown.setOnClickListener {
            DownloadManager.get().addDownloadTask(task)
            DownloadManager.get().addDownloadTask(task2)
            DownloadManager.get().addDownloadTask(task3)
            DownloadManager.get().addDownloadTask(task4)
            DownloadManager.get().addDownloadTask(task5)
            DownloadManager.get().addDownloadTask(task6)
        }
        binding.buttonCancelPaus.setOnClickListener {
            DownloadManager.get().pause(task.id)
        }
        binding.buttonCancelDown.setOnClickListener {
            DownloadManager.get().cancel(task.id)
        }
        binding.buttonImage.setOnClickListener {
            OkHttpUtils.get(meinv).buildCall().execute(object : BitmapCallback() {
                    override fun onSuccess(responseBody: Bitmap) {
                        super.onSuccess(responseBody)
                        binding.image.setImageBitmap(responseBody)
                    }
                })
        }
        binding.buttonCookies.setOnClickListener {
            OkHttpUtils.get("https://wanandroid.com/article/list/1/json?cid=0").apply {
                    addHeader("heeeeee", "adddheeeeee")
                    addParam("action", "recommend")
                    addParam("wwwwwwww", "dddddddddddd")
                    cacheMode = CacheMode.FIRST_CACHE_THEN_REQUEST
                    cacheTime = 3600000
                }.buildCall().execute(object : AbsCallback<GoodListData>() {
                    override fun onSuccess(responseBody: GoodListData) {
                        Log.e("onSuccess", responseBody.getStringValueDef("123", "list", "title"))
                    }

                    override fun onCacheSuccess(entity: CacheEntity, responseBody: GoodListData) {
                        super.onCacheSuccess(entity, responseBody)
                        Log.e("onCacheSuccess", GsonHelper.getGson().toJson(responseBody) + "\n\n\n" + entity.result)
                    }
                })
        }
        binding.buttonDefault.setOnClickListener {
            OkHttpUtils.post("https://api-sip.510gow.com/interface?action=recommend").apply {
                    addHeader("heeeeee", "adddheeeeee")
                    addParam("action", "recommend")
                    addParam("wwwwwwww", "dddddddddddd")
                    cacheMode = CacheMode.FIRST_CACHE_THEN_REQUEST
                    cacheTime = 3600000
                    toJson()
                }.buildCall().execute(object : AbsCallback<GoodListData>() {
                    override val progressCallBack: ProgressCallBack = { progress, total, done, isUpdate ->
                        Log.e("aaa", "progress = $progress   total = $total    done = $done   isUpdate = $isUpdate")
                    }

                    override fun onSuccess(responseBody: GoodListData) {
                        Log.e("onSuccess", responseBody.getValue<Boolean>("list").toString())
//                        Log.e("onSuccess", responseBody.getValue<String>("list"))
                        Log.e("onSuccess", responseBody.getStringValueDef("123", "list", "title"))
                    }

                    override fun onCacheSuccess(entity: CacheEntity, responseBody: GoodListData) {
                        super.onCacheSuccess(entity, responseBody)
                        Log.e("onCacheSuccess", GsonHelper.getGson().toJson(responseBody) + "\n\n\n" + entity.result)
                    }

                    override fun onError(e: HttpException) {
                        super.onError(e)
                        Log.e("aaa", e.toString())
                    }
                })
        }

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

    }


    var aa =
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
}