package com.heytap.purify;

import com.heytap.purify.hooks.AiVisionPurifyHook;
import com.heytap.purify.hooks.AdBlockPurifyHook;
import com.heytap.purify.hooks.BottomTabPurifyHook;
import com.heytap.purify.hooks.DownloadManagerPurifyHook;
import com.heytap.purify.hooks.MyProfilePurifyHook;
import com.heytap.purify.hooks.PureSearchPurifyHook;
import com.heytap.purify.hooks.SpeedUpHook;
import com.heytap.purify.hooks.HookPrefs;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class PurifyEntry implements IXposedHookLoadPackage {

    public static final String BROWSER_PACKAGE = "com.heytap.browser";
    public static final String TAG = "OPPO浏览器净化";

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {

        if (!BROWSER_PACKAGE.equals(lpparam.packageName)) {
            return;
        }

        if (!BROWSER_PACKAGE.equals(lpparam.processName)) {
            return;
        }
        HookPrefs.init();
        install("BottomTabPurifyHook", () -> BottomTabPurifyHook.install(lpparam.classLoader));
        install("MyProfilePurifyHook", () -> MyProfilePurifyHook.install(lpparam.classLoader));
        install("DownloadManagerPurifyHook", () -> DownloadManagerPurifyHook.install(lpparam.classLoader));
        install("AiVisionPurifyHook", () -> AiVisionPurifyHook.install(lpparam.classLoader));
        install("AdBlockPurifyHook", () -> AdBlockPurifyHook.install(lpparam.classLoader));
        install("PureSearchPurifyHook", () -> PureSearchPurifyHook.install(lpparam.classLoader));
        install("SpeedUpHook", () -> SpeedUpHook.install(lpparam.classLoader));
    }

    private static void install(String name, Runnable hook) {
        try {
            hook.run();
        } catch (Throwable t) {
            XposedBridge.log(TAG + " " + name + " 安装失败: " + t);
        }
    }
}
