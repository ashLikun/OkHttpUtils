[![Release](https://jitpack.io/v/ashLikun/OkHttpUtils.svg)](https://jitpack.io/#ashLikun/OkHttpUtils)


# **OkhttpUtils**
基于okhttp的封装
## 使用方法

build.gradle文件中添加:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
并且:

```gradle
dependencies {
    implementation 'com.github.ashLikun:OkHttpUtils:{latest version}'
    //依赖的第三方库
     implementation 'io.reactivex.rxjava2:rxandroid:2.0.1'
     implementation 'io.reactivex.rxjava2:rxjava:2.x.y'
     implementation 'com.squareup.okhttp3:okhttp:3.9.0'
     implementation 'com.google.code.gson:gson:2.8.5'
     implementation 'com.github.ashLikun:MultiTypeGson:1.0.6'
     //如果使用下载，或者自定义缓存（不是http默认的）就要用到数据库
     implementation 'com.github.ashLikun:LiteOrm:2.0.2'
}
```
        RequestParam p =  RequestParam.get();//get请求,可以是post--->RequestParam.post();
        //添加头部
        p.addHeader("accessToken", "A8C5CF33-64A1-49F4-ADBC-4DBF05D5F94B");
        //添加path
        p.appendPath("118915");
        //添加参数
        p.addParam("accessToken", "11111");
        p.addParam("aasda", "22222");
        p.addParam("9966", "33333");
        p.addParam("aaaaa", "44444");
        //添加文件参数
        p.addParamFile("aa", "filePath");
        //发起异步请求
        OkHttpUtils.getInstance().execute(p, new Callback<String>() {
            @Override
            public void onSuccess(String responseBody) {
                Log.e("onSuccess", responseBody);
            }

        });

        
        try {
            //同步请求
            String string = OkHttpUtils.getInstance().syncExecute(p,String.class);
            syncExecute(p, HttpResult.class, new TypeToken<List<EmUserData>>() {
                    }.getType());
            syncExecute(p, HttpResult.class, List.class,EmUserData.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        //返回值使用
        Callback<HttpResult<UserData>>() {
                    @Override
                    public void onSuccess(HttpResult<UserData> responseBody) {
                    }
        
                }
                
        HttpResult 这个对象是处理了返回结果，对于code，msg，data的解析，
        data类型就是HttpResult的泛型类型，可以是list

        HttpResponse  这个对象只是处理了code和msg
        
        如果上面两个对象不能满足需求，调用者也可以自定义返回类型，因为Callback是泛型的
		
		//Retrofit初始化
		Retrofit.get().init(createUrl = {
            if (it.url.isNullOrEmpty()) {
                if (it.path.isNullOrEmpty()) {
                    createUrl(it.action)
                } else {
                    createUrl(it.action, it.path)
                }
            } else it.url
        }, createRequest = { HttpRequestParam.create(it.url) }) { request, result, params ->
            val handle = params?.find { it is HttpUiHandle? } as HttpUiHandle?
            request.syncExecute<Any>(handle, result.resultType)
        }
		
		
### 混肴
#### Okhttputils
        -dontwarn okio.**
        -dontwarn javax.annotation.Nullable
        -dontwarn javax.annotation.ParametersAreNonnullByDefault
        
        okhttp 4.0以后
        # JSR 305 annotations are for embedding nullability information.
        -dontwarn javax.annotation.**
        
        # A resource is loaded with a relative path so the package of this class must be preserved.
        -keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
        
        # Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
        -dontwarn org.codehaus.mojo.animal_sniffer.*
        
        # OkHttp platform used only on JVM and when Conscrypt dependency is available.
        -dontwarn okhttp3.internal.platform.ConscryptPlatform

        #kotlin
        -keep class kotlin.** { *; }
        -keep class kotlin.Metadata { *; }
        -dontwarn kotlin.**
        -keepclassmembers class **$WhenMappings {
            <fields>;
        }
        -keepclassmembers class kotlin.Metadata {
            public <methods>;
        }
        -assumenosideeffects class kotlin.jvm.internal.Intrinsics {
            static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
        }