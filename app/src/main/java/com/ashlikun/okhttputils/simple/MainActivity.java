package com.ashlikun.okhttputils.simple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ashlikun.okhttputils.http.OkHttpUtils;
import com.ashlikun.okhttputils.http.SimpleCallback;
import com.ashlikun.okhttputils.http.request.RequestParam;
import com.ashlikun.okhttputils.json.GsonHelper;

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
//
        RequestParam p = RequestParam.get("https://jielehua.vcash.cn/api/jlh/apply/getApplyProgress/");
        p.addHeader("accessToken", "A8C5CF33-64A1-49F4-ADBC-4DBF05D5F94B");
        //4690943?accessToken=8079CE15-038E-4977-8443-E885730DE268
        p.appendPath("118915");
        p.addParam("accessToken", 111111);
        p.addParam("aasda", 2222.333);
        p.addParam("9966", "33333");
        p.addParam("aaaaa", "44444");
        p.addParamFilePath("aa", "filePath");
        p.toJson();
        OkHttpUtils.getInstance().execute(p, new SimpleCallback<HomeHttpResult<JlhData>>() {
            @Override
            public void onSuccess(HomeHttpResult<JlhData> responseBody) {
            }

        });

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

    public void onTestGsonClick(View view) {
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
    }

    public static class TestData {
        String aaa = "111";
        String bbb = null;
        String ccc = "222";
        String ddd = "333";
    }
}
