package com.heytap.purify.hooks;

import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public final class AdBlockPurifyHook {

    private static final String TAG = "广告拦截";

    private AdBlockPurifyHook() {
    }

    public static void install(ClassLoader classLoader) {
        try {

            Class<?> ttAdSdk = XposedHelpers.findClass(
                    "com.bytedance.sdk.openadsdk.TTAdSdk", classLoader);
            XposedHelpers.findAndHookMethod(ttAdSdk, "init",
                    Context.class, "com.bytedance.sdk.openadsdk.TTAdConfig",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                if (!HookPrefs.isAdBlock()) {
                                    return;
                                }

                                param.setResult(true);
                            } catch (Throwable t) {
                                XposedBridge.log(TAG + " ShortCircuit 异常: " + t.getMessage());
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log(TAG + " TTAdSdk.init hook 安装失败: " + t.getMessage());
        }
    }
}
