package com.will.weiyue;


import com.avos.avoscloud.AVOSCloud;
import com.will.weiyue.component.ApplicationComponent;
import com.will.weiyue.component.DaggerApplicationComponent;
import com.will.weiyue.module.ApplicationModule;
import com.will.weiyue.module.HttpModule;
import com.will.weiyue.utils.ContextUtils;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackManager;

/**
 * desc: .
 * author: Will .
 * date: 2017/9/2 .
 */
public class MyApp extends LitePalApplication {

    private ApplicationComponent mApplicationComponent;

    private static MyApp sMyApp;

    public static int width = 0;

    public static int height = 0;


    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"DExyy3gNUbwd5kFEnPw4gMdN-gzGzoHsz","KmGhrLY408fWyfbL67WTltxb");
        // 放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
        AVOSCloud.setDebugLogEnabled(true);

        sMyApp = this;
        BGASwipeBackManager.getInstance().init(this);
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .httpModule(new HttpModule())
                .build();
        LitePal.initialize(this);
        width = ContextUtils.getSreenWidth(MyApp.getContext());
        height = ContextUtils.getSreenHeight(MyApp.getContext());

    }

    public static MyApp getInstance() {
        return sMyApp;
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }

}
