package com.heytap.purify.hooks;

import android.view.View;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public final class DownloadManagerPurifyHook {

    private static final String TAG = "下载管理精简";

    private DownloadManagerPurifyHook() {
    }

    public static void install(ClassLoader classLoader) {
        try {
            Class<?> fragment = XposedHelpers.findClass(
                    "com.heytap.browser.download.ui.downloadlist.fragment.DownloadManagerFragment",
                    classLoader);
            XposedHelpers.findAndHookMethod(fragment, "Sg", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (!HookPrefs.isHideDlClean()) {
                        return;
                    }
                    Object view = XposedHelpers.callMethod(param.thisObject, "getView");
                    if (!(view instanceof View)) {
                        return;
                    }
                    View root = (View) view;
                    int id = root.getResources().getIdentifier("efs", "id",
                            root.getContext().getPackageName());
                    if (id == 0) {
                        XposedBridge.log(TAG + ": 未找到 id=efs");
                        return;
                    }
                    View clean = root.findViewById(id);
                    if (clean == null) {
                        XposedBridge.log(TAG + ": view 为 null id=efs");
                        return;
                    }
                    clean.setVisibility(View.GONE);
                }
            });
        } catch (Throwable t) {
            XposedBridge.log(TAG + " Hook 安装失败: " + t.getMessage());
        }
    }
}
