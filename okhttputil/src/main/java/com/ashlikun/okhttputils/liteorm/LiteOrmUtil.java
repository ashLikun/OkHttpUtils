package com.ashlikun.okhttputils.liteorm;


import android.app.Application;

import com.ashlikun.okhttputils.R;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;

public class LiteOrmUtil {
    private static LiteOrm liteOrm;
    private static Application app;
    private static int versionCode;
    private static boolean debugged;

    private LiteOrmUtil() {
    }

    public static void init(Application application, int versionCode, boolean debugged) {
        LiteOrmUtil.app = application;
        LiteOrmUtil.versionCode = versionCode;
        LiteOrmUtil.debugged = debugged;
    }

    public static LiteOrm getLiteOrm() {
        if (liteOrm == null) {
            synchronized (LiteOrmUtil.class) {
                if (liteOrm == null) {
                    init();
                }
            }
        }
        return liteOrm;
    }

    private static void init() {
        if (liteOrm == null) {
            DataBaseConfig config = new DataBaseConfig(app, app.getString(R.string.app_name_letter));
            config.debugged = debugged;
            config.dbVersion = versionCode;
            config.onUpdateListener = null;
            liteOrm = LiteOrm.newSingleInstance(config);
        }
    }
}
