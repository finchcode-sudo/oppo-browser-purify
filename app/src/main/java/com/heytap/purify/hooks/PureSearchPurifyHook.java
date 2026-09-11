package com.heytap.purify.hooks;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public final class PureSearchPurifyHook {

    private static final String ENGINES_CLS =
            "com.heytap.browser.search.impl.engine.DefaultSearchEngines";
    private static final String HELPER_CLS =
            "com.heytap.browser.search.impl.engine.m";
    private static final String TAB_LAYOUT_CLS =
            "com.heytap.browser.ai.page.multi.ui.channel.MultiSearchTabLayout";
    private static final String INNER_PAGE_CLS =
            "com.heytap.browser.ai.page.multi.MultiSearchInnerPage";
    private static final String BING_ICON_URL =
            "https://www.bing.com/favicon.ico";
    private static final String GOOGLE_ICON_URL =
            "https://dhfs.heytapimage.com/2025/04/18/dd3f111621e69c4c8ab4d6806ebf8bf7.webp";
    private static final String BAIDU_ICON_URL =
            "https://dhfs.heytapimage.com/2025/04/18/cba8a9472788f6ce15544d27599eaad0.webp";

    private PureSearchPurifyHook() {
    }

    public static void install(ClassLoader classLoader) {
        hookEngineList(classLoader);
        hookCurrentEngine(classLoader);
        hookEngineRefresh(classLoader);
        hookTopChannel(classLoader);
        hookSearchUrl(classLoader);
    }

    private static void hookEngineList(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod(ENGINES_CLS, classLoader, "n", boolean.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                if (!HookPrefs.isPureSearch()) {
                                    return;
                                }
                                param.setResult(buildEngines(classLoader, param.thisObject));
                            } catch (Throwable t) {
                                XposedBridge.log("纯净搜索 n() 拦截失败: " + t.getMessage());
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log("纯净搜索 n() hook 安装失败: " + t.getMessage());
        }
    }


    private static void hookCurrentEngine(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod(ENGINES_CLS, classLoader, "J",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                if (!HookPrefs.isPureSearch()) {
                                    return;
                                }
                                Object self = param.thisObject;

                                String key = (String) XposedHelpers.callMethod(self, "G");
                                List<Object> engines = buildEngines(classLoader, self);
                                for (Object e : engines) {
                                    if (key.equals(XposedHelpers.callMethod(e, "getKey"))) {
                                        param.setResult(e);
                                        return;
                                    }
                                }
                                if (!engines.isEmpty()) {
                                    param.setResult(engines.get(0));
                                }
                            } catch (Throwable t) {
                                XposedBridge.log("纯净搜索 J() 拦截失败: " + t.getMessage());
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log("纯净搜索 J() hook 安装失败: " + t.getMessage());
        }
    }




    private static void hookEngineRefresh(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod(ENGINES_CLS, classLoader, "o0",
                    List.class, boolean.class, boolean.class, boolean.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                if (HookPrefs.isPureSearch()) {
                                    param.setResult(null);
                                }
                            } catch (Throwable t) {
                                XposedBridge.log("纯净搜索 o0() 拦截失败: " + t.getMessage());
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log("纯净搜索 o0() hook 安装失败: " + t.getMessage());
        }
    }


    private static void hookTopChannel(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod(TAB_LAYOUT_CLS, classLoader, "I0",
                    List.class, boolean.class, android.content.Context.class,
                    new XC_MethodHook() {
                        @Override
                        @SuppressWarnings("unchecked")
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                if (!HookPrefs.isReplaceTopChannel()) {
                                    return;
                                }
                                List<Object> original = (List<Object>) param.args[0];
                                if (original == null) {
                                    return;
                                }

                                Class<?> enginesCls = XposedHelpers.findClass(ENGINES_CLS, classLoader);
                                Object companion = XposedHelpers.getStaticObjectField(enginesCls, "p");
                                Object engineInst = XposedHelpers.callMethod(companion, "b");
                                List<Object> engines = buildEngines(classLoader, engineInst);

                                String currentKey = getCurrentEngineKey(engineInst);
                                List<String> order = orderedEngineKeys(currentKey);

                                List<Object> newList = new ArrayList<>(3);
                                for (String key : order) {
                                    for (Object e : engines) {
                                        if (key.equals(XposedHelpers.callMethod(e, "getKey"))) {
                                            newList.add(e);
                                            break;
                                        }
                                    }
                                }
                                if (newList.isEmpty()) {
                                    newList.addAll(engines);
                                }

                                param.args[0] = newList;
                            } catch (Throwable t) {
                                XposedBridge.log("顶部频道替换 拦截失败: " + t.getMessage());
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log("顶部频道替换 hook 安装失败: " + t.getMessage());
        }
    }

    /**
     * 读取当前"默认搜索引擎"的 key（bing_ov / google / baidu）。
     */
    private static String getCurrentEngineKey(Object engineInst) {
        try {
            return (String) XposedHelpers.callMethod(engineInst, "G");
        } catch (Throwable t) {
            XposedBridge.log("读取当前默认引擎失败: " + t.getMessage());
            return null;
        }
    }

    /**
     * 把当前默认引擎排在第一位，剩余两个按固定相对顺序（bing、google、baidu）跟在后面。
     * 这样顶部频道会跟随"默认搜索引擎"设置动态变化，而不是永远百度在前。
     */
    private static List<String> orderedEngineKeys(String currentKey) {
        String[] base = {"bing_ov", "google", "baidu"};
        List<String> order = new ArrayList<>(3);
        if (currentKey != null) {
            for (String key : base) {
                if (key.equals(currentKey)) {
                    order.add(key);
                    break;
                }
            }
        }
        for (String key : base) {
            if (!order.contains(key)) {
                order.add(key);
            }
        }
        return order;
    }


    private static void hookSearchUrl(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod(INNER_PAGE_CLS, classLoader, "Q",
                    "com.heytap.browser.api.search.engine.c", String.class, String.class, String.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                if (!HookPrefs.isReplaceTopChannel()) {
                                    return;
                                }
                                Object self = param.thisObject;

                                Object entity = XposedHelpers.getObjectField(self, "b");
                                int index = (Integer) XposedHelpers.callMethod(entity, "getIndex");

                                Class<?> enginesCls = XposedHelpers.findClass(ENGINES_CLS, classLoader);
                                Object companion = XposedHelpers.getStaticObjectField(enginesCls, "p");
                                Object engineInst = XposedHelpers.callMethod(companion, "b");

                                String currentKey = getCurrentEngineKey(engineInst);
                                List<String> order = orderedEngineKeys(currentKey);
                                if (index < 0 || index >= order.size()) {
                                    return;
                                }
                                String targetKey = order.get(index);
                                String query = (String) param.args[1];
                                Class<?> helperCls = XposedHelpers.findClass(HELPER_CLS, classLoader);
                                Object helper = XposedHelpers.getStaticObjectField(helperCls, "a");
                                Object e = XposedHelpers.callMethod(helper, "c", targetKey);
                                Object target = XposedHelpers.callMethod(engineInst, "T", e);
                                String url = (String) XposedHelpers.callMethod(target, "m", query);
                                if (url != null) {
                                    param.setResult(url);
                                }
                            } catch (Throwable t) {
                                XposedBridge.log("顶部频道搜索 URL 替换失败: " + t.getMessage());
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log("顶部频道搜索 URL hook 安装失败: " + t.getMessage());
        }
    }



    private static List<Object> buildEngines(ClassLoader classLoader, Object engineInstance) {
        List<Object> engines = new ArrayList<>(3);
        try {
            Class<?> helperCls = XposedHelpers.findClass(HELPER_CLS, classLoader);
            Object helper = XposedHelpers.getStaticObjectField(helperCls, "a");
            for (String key : new String[]{"bing_ov", "google", "baidu"}) {
                Object e = XposedHelpers.callMethod(helper, "c", key);
                if ("bing_ov".equals(key)) {
                    XposedHelpers.setObjectField(e, "f", BING_ICON_URL);
                    XposedHelpers.setObjectField(e, "g", BING_ICON_URL);
                } else if ("google".equals(key)) {
                    XposedHelpers.setObjectField(e, "f", GOOGLE_ICON_URL);
                    XposedHelpers.setObjectField(e, "g", GOOGLE_ICON_URL);
                } else if ("baidu".equals(key)) {
                    XposedHelpers.setObjectField(e, "f", BAIDU_ICON_URL);
                    XposedHelpers.setObjectField(e, "g", BAIDU_ICON_URL);
                }
                engines.add(XposedHelpers.callMethod(engineInstance, "T", e));
            }
        } catch (Throwable t) {
            XposedBridge.log("纯净搜索 构造引擎失败: " + t.getMessage());
        }
        return engines;
    }


}
