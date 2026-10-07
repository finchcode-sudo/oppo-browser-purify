package com.heytap.purify.hooks;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public final class BottomTabPurifyHook {

    private static final String TAG = "OPPO浏览器净化";

    private static final String TAB_MANAGER_CLS = "com.heytap.browser.trait.tab.BottomTabManager";
    private static final String DEFAULT_TAB_SETTING_CLS = "com.heytap.browser.platform.settings.DefaultTabSetting";
    private static final String NORMAL_HOME_CLS = "com.heytap.browser.main.home.normal.NormalHome";

    private static final int TAB_HOME = 1;
    private static final int TAB_VIDEO = 2;
    private static final int TAB_AI = 9;
    private static final int TAB_EXPLORE = 4;
    private static final int TAB_NOVEL_BUILTIN = 5;
    private static final int TAB_NOVEL_CUSTOM = 8;

    private BottomTabPurifyHook() {
    }

    public static void install(ClassLoader classLoader) {
        hookTabList(classLoader);
        hookDefaultTab(classLoader);
        hookHomeRedirect(classLoader);
        hookDefaultTabBlock(classLoader);
        hookHomePageSetting(classLoader);
    }



    private static void hookDefaultTabBlock(ClassLoader classLoader) {

        try {
            XposedHelpers.findAndHookMethod(DEFAULT_TAB_SETTING_CLS, classLoader, "i",
                    String.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                if (!HookPrefs.isHideHomeTab()) {
                                    return;
                                }
                                String tabStr = (String) param.args[0];
                                if (tabStr == null) {
                                    return;
                                }


                                boolean isHome = false;
                                Object list = XposedHelpers.getObjectField(param.thisObject, "d");
                                if (list instanceof List && !((List<?>) list).isEmpty()) {
                                    for (Object item : (List<?>) list) {
                                        Object type = XposedHelpers.callMethod(item, "c");
                                        if (Integer.valueOf(TAB_HOME).equals(type)) {
                                            Object name = XposedHelpers.callMethod(
                                                    XposedHelpers.callMethod(item, "b"), "a");
                                            if (tabStr.equals(name)) {
                                                isHome = true;
                                            }
                                            break;
                                        }
                                    }
                                } else {

                                    isHome = "首页".equals(tabStr);
                                }
                                if (isHome) {

                                    param.setResult(false);
                                }
                            } catch (Throwable ignored) {
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log("阻止起始页(写入) hook 安装失败: " + t.getMessage());
        }
    }


    private static void hookHomePageSetting(ClassLoader classLoader) {
        try {
            final Class<?> frag = XposedHelpers.findClass(
                    "com.heytap.browser.settings.homepage.HomeTabPreferenceFragment", classLoader);

            XposedHelpers.findAndHookMethod(frag, "initView", View.class, new XC_MethodHook() {
                @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                if (!HookPrefs.isHideHomeTab()) {
                                    return;
                                }
                                Object self = param.thisObject;

                                View cardList = (View) XposedHelpers.getObjectField(self, "m");
                                if (cardList != null) {
                                    cardList.setVisibility(View.GONE);
                                }

                                View hint = (View) XposedHelpers.getObjectField(self, "q");
                                if (hint != null) {
                                    hint.setVisibility(View.GONE);
                                }

                                View title = (View) XposedHelpers.getObjectField(self, "p");
                                if (title != null) {
                                    title.setVisibility(View.GONE);
                                }
                            } catch (Throwable t) {
                                XposedBridge.log("主页设置精简异常: " + t.getMessage());
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log("主页设置精简 hook 安装失败: " + t.getMessage());
        }
    }


    private static void hookTabList(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod(TAB_MANAGER_CLS, classLoader, "k",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Object result = param.getResult();
                            if (!(result instanceof List)) {
                                return;
                            }
                            List<?> tabs = (List<?>) result;
                            List<Object> filtered = new ArrayList<Object>(tabs.size());
                            for (Object tab : tabs) {
                                if (!isHiddenTab(tab)) {
                                    filtered.add(tab);
                                }
                            }
                            if (filtered.size() != tabs.size()) {
                                param.setResult(filtered);
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log(TAG + " 底栏列表 hook 安装失败: " + t);
        }
    }


    private static void hookDefaultTab(ClassLoader classLoader) {
        try {
            final Class<?> tabBean = XposedHelpers.findClass(
                    DEFAULT_TAB_SETTING_CLS + "$b", classLoader);



            XposedHelpers.findAndHookMethod(DEFAULT_TAB_SETTING_CLS, classLoader, "c", tabBean,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                if (!HookPrefs.isHideHomeTab()) {
                                    return;
                                }
                                Object self = param.thisObject;

                                Object list = XposedHelpers.callMethod(self, "g");
                                if (!(list instanceof List)) {
                                    XposedBridge.log("默认页改道: g() 返回非 List");
                                    return;
                                }

                                Object aiTab = null;
                                for (Object item : (List<?>) list) {
                                    Object t = XposedHelpers.callMethod(item, "c");
                                    if (Integer.valueOf(TAB_AI).equals(t)
                                            || Integer.valueOf(TAB_EXPLORE).equals(t)) {
                                        aiTab = item;
                                        break;
                                    }
                                }
                                if (aiTab == null) {
                                    XposedBridge.log("默认页改道: 未找到 AI tab(nativeId 4/9)");
                                    return;
                                }

                                XposedHelpers.setObjectField(self, "c", aiTab);

                                Object ctx = XposedHelpers.callMethod(self, "getContext");
                                if (ctx instanceof android.content.Context) {
                                    int id = (Integer) XposedHelpers.callMethod(aiTab, "a");
                                    int nativeId = (Integer) XposedHelpers.callMethod(aiTab, "c");
                                    android.content.Context c = (android.content.Context) ctx;
                                    c.getSharedPreferences(c.getPackageName() + "_preferences",
                                            android.content.Context.MODE_PRIVATE)
                                            .edit().putInt("key_setting_default_tab",
                                                    id * 100 + nativeId).apply();
                                }
                            } catch (Throwable t) {
                                XposedBridge.log("默认页改道异常: " + t.getMessage());
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log("默认页改道 hook 安装失败: " + t.getMessage());
        }
    }



    private static void hookHomeRedirect(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod(NORMAL_HOME_CLS, classLoader, "s2",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (!HookPrefs.isHideHomeTab()) {
                                return;
                            }
                            Object aiPage = null;
                            try {
                                aiPage = XposedHelpers.callMethod(param.thisObject, "W0");
                            } catch (Throwable t) {
                                XposedBridge.log("获取 AI 视界页失败: " + t.getMessage());
                            }
                            if (aiPage == null) {
                                return;
                            }

                            param.setResult(null);
                            try {
                                XposedHelpers.callMethod(param.thisObject, "m2", aiPage);
                            } catch (Throwable t) {
                                XposedBridge.log("改道 AI 视界失败: " + t.getMessage());
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log("返回改道 hook 安装失败: " + t.getMessage());
        }

        try {
            XposedHelpers.findAndHookMethod(NORMAL_HOME_CLS, classLoader, "p2", boolean.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (!HookPrefs.isHideHomeTab()) {
                                return;
                            }
                            try {
                                Object aiPage = XposedHelpers.callMethod(param.thisObject, "W0");
                                if (aiPage != null) {
                                    XposedHelpers.callMethod(param.thisObject, "m2", aiPage);
                                    param.setResult(true);
                                }
                            } catch (Throwable t) {
                                XposedBridge.log("恢复主页改道 AI 视界失败: " + t.getMessage());
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log("恢复主页 p2 hook 安装失败: " + t.getMessage());
        }

        try {
            XposedHelpers.findAndHookMethod(NORMAL_HOME_CLS, classLoader, "v2",
                    "rg3.l", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (!HookPrefs.isHideHomeTab()) {
                                return;
                            }
                            try {
                                Object aiPage = XposedHelpers.callMethod(param.thisObject, "W0");
                                if (aiPage == null) {
                                    return;
                                }


                                XposedHelpers.callMethod(param.thisObject, "m2", aiPage);
                                param.setResult(null);
                            } catch (Throwable t) {
                                XposedBridge.log("返回主页 v2 改道 AI 视界失败: " + t.getMessage());
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log("返回主页 v2 hook 安装失败: " + t.getMessage());
        }
    }

    private static boolean isHiddenTab(Object tab) {
        try {
            Object value = XposedHelpers.callMethod(tab, "j");
            if (value instanceof Integer) {
                int id = ((Integer) value).intValue();
                return (HookPrefs.isHideNovelTab() && (id == TAB_NOVEL_BUILTIN || id == TAB_NOVEL_CUSTOM))
                        || (HookPrefs.isHideVideoTab() && id == TAB_VIDEO)
                        || (HookPrefs.isHideHomeTab() && id == TAB_HOME);
            }
        } catch (Throwable ignored) {
        }
        return false;
    }
}
