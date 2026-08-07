package com.heytap.purify.hooks;

import android.view.View;
import android.view.ViewGroup;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public final class MyProfilePurifyHook {

    private static final String MY = "com.heytap.browser.usercenter.my.";
    private static final String TAG = "我的页面精简";

    private MyProfilePurifyHook() {
    }

    public static void install(ClassLoader classLoader) {
        try {
            Class<?> myProfileView = XposedHelpers.findClass(MY + "MyProfileView", classLoader);
            XposedHelpers.findAndHookMethod(myProfileView, "onAttachedToWindow",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            if (!(param.thisObject instanceof View)) {
                                return;
                            }
                            View v = (View) param.thisObject;
                            if (HookPrefs.isHideMyFunctions()) {
                                hideById(v, "czu", "功能入口区");
                            }
                            if (HookPrefs.isHideMyNight()) {

                                hideById(v, "e4i", "夜间模式等设置区");

                                hideById(v, "aqp", "夜间模式等设置区(静态)");
                            }
                            if (HookPrefs.isHideMyCard()) {
                                hideById(v, "qv", "卡片区");
                            }
                            if (HookPrefs.isHideMyNovel()) {
                                hideById(v, "w1", "小说区");
                            }
                            if (HookPrefs.isHideMyTools()) {

                                hideById(v, "wz", "我的工具区");
                            }
                            if (HookPrefs.isHideMyMessage()) {
                                hideById(v, "e59", "信息中心图标");
                            }
                            if (HookPrefs.isHideMyScan()) {
                                hideById(v, "e4s", "扫一扫图标");
                            }
                            if (HookPrefs.isHideMyBadge() || HookPrefs.isHideMyCash()) {


                                hideByIdIn(v, "ftq", "cxl", "主题徽标");
                                hideByIdIn(v, "ftq", "a72", "领现金按钮");
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log(TAG + " onAttachedToWindow hook 安装失败: " + t.getMessage());
        }


        hookForceHide(classLoader);

        hookAccountInfoView(classLoader);
    }


    private static void hookAccountInfoView(ClassLoader classLoader) {
        try {
            Class<?> infoView = XposedHelpers.findClass(
                    "com.platform.sdk.center.widget.HeyTapAccountInfoView", classLoader);

            XposedHelpers.findAndHookMethod(infoView, "setShowLogoView", boolean.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (HookPrefs.isHideMyBadge()) {
                                param.args[0] = false;
                            }
                        }
                    });

            XposedHelpers.findAndHookMethod(infoView, "setShowSignButton", boolean.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (HookPrefs.isHideMyCash()) {
                                param.args[0] = false;
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log("徽标/领现金按钮精准隐藏 hook 安装失败: " + t.getMessage());
        }
    }


    private static void hookForceHide(ClassLoader classLoader) {
        final Class<?> novelCard;
        final Class<?> banner;
        final Class<?> nightContainer;
        try {
            novelCard = XposedHelpers.findClass(MY + "bookshelf.MyBookshelfHistoryCard", classLoader);
            banner = XposedHelpers.findClass(MY + "banner.MyProfileBanners", classLoader);
            nightContainer = XposedHelpers.findClass(MY + "DefaultFunctionContainer", classLoader);
        } catch (Throwable t) {
            XposedBridge.log("setVisibility 拦截初始化失败: " + t.getMessage());
            return;
        }
        try {
            XposedHelpers.findAndHookMethod(View.class, "setVisibility", int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            int visibility = (Integer) param.args[0];
                            if (visibility != View.VISIBLE) {
                                return;
                            }
                            Object self = param.thisObject;
                            if (novelCard.isInstance(self) && HookPrefs.isHideMyNovel()) {
                                param.args[0] = View.GONE;
                                return;
                            }
                            if (banner.isInstance(self) && HookPrefs.isHideMyCard()) {
                                param.args[0] = View.GONE;
                                return;
                            }
                            if (nightContainer.isInstance(self) && HookPrefs.isHideMyNight()) {
                                param.args[0] = View.GONE;
                                return;
                            }

                            if (self instanceof View) {
                                View v = (View) self;
                                if (HookPrefs.isHideMyNight() && isId(v, "e4i")) {
                                    param.args[0] = View.GONE;
                                    return;
                                }
                                if (HookPrefs.isHideMyTools() && isId(v, "wz")) {
                                    param.args[0] = View.GONE;
                                    return;
                                }

                                if (HookPrefs.isHideMyBadge() && (isId(v, "cxl") || isId(v, "by3"))) {
                                    param.args[0] = View.GONE;
                                    return;
                                }

                                if (HookPrefs.isHideMyCash() && isId(v, "a72")) {
                                    param.args[0] = View.GONE;
                                }
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log("setVisibility 强制隐藏 hook 安装失败: " + t.getMessage());
        }
    }


    private static boolean isId(View v, String idName) {
        try {
            if (v.getId() == 0) {
                return false;
            }
            int target = v.getResources().getIdentifier(idName, "id", v.getContext().getPackageName());
            return target != 0 && v.getId() == target;
        } catch (Throwable t) {
            return false;
        }
    }


    private static void hideById(View root, String idName, String tag) {
        try {
            int id = root.getResources().getIdentifier(idName, "id", root.getContext().getPackageName());
            if (id == 0) {
                XposedBridge.log("隐藏" + tag + ": 未找到 id=" + idName);
                return;
            }
            View target = root.findViewById(id);
            if (target == null) {
                XposedBridge.log("隐藏" + tag + ": view 为 null id=" + idName);
                return;
            }
            target.setVisibility(View.GONE);
        } catch (Throwable t) {
            XposedBridge.log("隐藏" + tag + " 失败: " + t.getMessage());
        }
    }


    private static void hideByIdIn(View root, String parentIdName, String childIdName, String tag) {
        try {
            int pid = root.getResources().getIdentifier(parentIdName, "id", root.getContext().getPackageName());
            if (pid == 0) {
                XposedBridge.log("隐藏" + tag + ": 未找到父 id=" + parentIdName);
                return;
            }
            View parent = root.findViewById(pid);
            if (parent == null) {
                XposedBridge.log("隐藏" + tag + ": 父 view 为 null id=" + parentIdName + ", parentClass=" + root.getClass().getSimpleName());

                View found = findViewByStringId(root, childIdName);
                if (found != null) {
                    found.setVisibility(View.GONE);
                    return;
                }
                XposedBridge.log("隐藏" + tag + ": 全树搜索未找到 id=" + childIdName);
                return;
            }
            int cid = root.getResources().getIdentifier(childIdName, "id", root.getContext().getPackageName());
            if (cid == 0) {
                XposedBridge.log("隐藏" + tag + ": 未找到子 id=" + childIdName);
                return;
            }
            View child = parent.findViewById(cid);
            if (child == null) {
                XposedBridge.log("隐藏" + tag + ": 子 view 为 null id=" + childIdName + ", parentType=" + parent.getClass().getSimpleName());
                return;
            }
            child.setVisibility(View.GONE);
        } catch (Throwable t) {
            XposedBridge.log("隐藏" + tag + " 失败: " + t.getMessage());
        }
    }


    private static View findViewByStringId(View root, String idName) {
        try {
            if (!(root instanceof ViewGroup)) {
                return null;
            }
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                if (isId(child, idName)) {
                    return child;
                }
                View deep = findViewByStringId(child, idName);
                if (deep != null) {
                    return deep;
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }
}
