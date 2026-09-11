package com.heytap.purify.hooks;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * OPPO 浏览器下载提速 Hook
 * 原理:浏览器下载引擎是内置的迅雷 SDK,其下载配置类 e0 默认并发只有 3。
 *      本 Hook 放大并发数,并拦截限速调用。
 * 目标包名: com.heytap.browser
 * 适配版本: 40.10.19.17 (10191700)
 */
public final class SpeedUpHook {

    private static final String TAG = "SpeedUpHook";

    /** 最大并发下载数。原值 3。建议先 8,稳定后再 16。 */
    private static final int MAX_CONCURRENT = 16;

    private SpeedUpHook() {
    }

    public static void install(ClassLoader classLoader) {
        hookDownloadConfig(classLoader);
        hookLimiter(classLoader);
        hookTaskManager(classLoader);
    }

    /* 1) e0: com.heytap.browser.download.fast.e0 implements XDownloadConfig */
    private static void hookDownloadConfig(ClassLoader cl) {
        try {
            Class<?> e0 = XposedHelpers.findClass(
                    "com.heytap.browser.download.fast.e0", cl);

            XposedHelpers.findAndHookMethod(e0, "getMaxConcurrentDownload",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            param.setResult(MAX_CONCURRENT);
                        }
                    });

            XposedHelpers.findAndHookMethod(e0, "getMaxConcurrentDownloadPan",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            param.setResult(MAX_CONCURRENT);
                        }
                    });

            XposedBridge.log(TAG + ": e0 hooked -> " + MAX_CONCURRENT);
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": hook e0 failed: " + t);
        }
    }

    /* 2) DownloadLimiterImpl: 真正落地限速的地方 */
    private static void hookLimiter(ClassLoader cl) {
        try {
            Class<?> limiter = XposedHelpers.findClass(
                    "com.xunlei.downloadprovider.download.architect.DownloadLimiterImpl", cl);

            XposedHelpers.findAndHookMethod(limiter, "setDownloadSpeedLimit",
                    long.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            param.args[0] = -1L;
                        }
                    });

            XposedHelpers.findAndHookMethod(limiter, "setTaskSpeedLimit",
                    long.class, long.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            param.args[1] = -1L;
                        }
                    });

            XposedHelpers.findAndHookMethod(limiter, "setUploadSpeedLimit",
                    long.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            param.args[0] = -1L;
                        }
                    });

            XposedBridge.log(TAG + ": DownloadLimiterImpl hooked");
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": hook limiter failed: " + t);
        }
    }

    /* 3) DownloadTaskManager.getLimitedSpeed(): 强制不限速 */
    private static void hookTaskManager(ClassLoader cl) {
        try {
            Class<?> mgr = XposedHelpers.findClass(
                    "com.xunlei.downloadprovider.download.manager.DownloadTaskManager", cl);

            XposedHelpers.findAndHookMethod(mgr, "getLimitedSpeed",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            param.setResult(-1L);
                        }
                    });

            XposedBridge.log(TAG + ": DownloadTaskManager.getLimitedSpeed hooked");
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": hook task manager failed: " + t);
        }
    }
}
