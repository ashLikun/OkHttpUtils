# **OkhttpUtils**
基于okhttp的封装


### 1.用法
        使用前，对于Android Studio的用户，可以选择添加:
        compile 'com.github.ashLikun:OkHttpUtils:1.1.9'//okhttp封装
        

        RequestParam p = new RequestParam("");
        p.get();//get请求,可以是post--->p.post();
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
### 混肴
#### Okhttputils
        -dontwarn okio.**
        -dontwarn javax.annotation.Nullable
        -dontwarn javax.annotation.ParametersAreNonnullByDefault

