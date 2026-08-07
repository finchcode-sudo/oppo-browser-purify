package com.heytap.purify.hooks;

import android.view.View;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public final class AiVisionPurifyHook {

    private static final String TAG = "AI视界精简";
    private static final String AGENT_HELPER = "com.heytap.browser.ai.page.ai.vision.agent.b";

    private AiVisionPurifyHook() {
    }

    public static void install(ClassLoader classLoader) {
        try {
            Class<?> helper = XposedHelpers.findClass(AGENT_HELPER, classLoader);
            Class<?> bean = XposedHelpers.findClass(
                    "com.heytap.browser.browser.search_ui.agent.AgentEntryBean", classLoader);
            Class<?> drawee = XposedHelpers.findClass(
                    "com.heytap.browser.image_loader.ui.BrowserDraweeView", classLoader);


            XposedHelpers.findAndHookMethod(helper, "f",
                    android.content.Context.class, bean, drawee, drawee,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            if (!HookPrefs.isHideAiCorner()) {
                                return;
                            }
                            Object corner = param.args[3];
                            if (corner instanceof View) {
                                ((View) corner).setVisibility(View.GONE);
                            }
                        }
                    });

            XposedHelpers.findAndHookMethod(helper, "e", bean, drawee,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            if (!HookPrefs.isHideAiCorner()) {
                                return;
                            }
                            Object corner = param.args[1];
                            if (corner instanceof View) {
                                ((View) corner).setVisibility(View.GONE);
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log(TAG + " Hook 安装失败: " + t.getMessage());
        }


        hookHideQueryButton(classLoader);

        hookHideLogo(classLoader);
    }


    private static void hookHideQueryButton(ClassLoader classLoader) {

        try {
            final Class<?> queryBtn = XposedHelpers.findClass(
                    "com.heytap.browser.ai.page.ai.view.AiQueryButtonView", classLoader);
            XposedHelpers.findAndHookMethod(View.class, "setVisibility", int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (!HookPrefs.isHideAiInput()) {
                                return;
                            }
                            int vis = (Integer) param.args[0];
                            if (vis != View.VISIBLE) {
                                return;
                            }
                            if (queryBtn.isInstance(param.thisObject)) {
                                param.args[0] = View.GONE;
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log("问AI按钮 setVisibility 拦截失败: " + t.getMessage());
        }


        try {
            Class<?> container = XposedHelpers.findClass(
                    "com.heytap.browser.ai.page.ai.base.BaseAiContainer", classLoader);
            XposedHelpers.findAndHookMethod(container, "a3", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (!HookPrefs.isHideAiInput()) {
                        return;
                    }
                    Object btn = XposedHelpers.callMethod(param.thisObject, "getAiQueryButtonView");
                    if (btn == null) {
                        return;
                    }
                    Object view = XposedHelpers.callMethod(btn, "getView");
                    if (view instanceof View) {
                        ((View) view).setVisibility(View.GONE);
                    }
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("隐藏AI视界底部问AI按钮 hook 安装失败: " + t.getMessage());
        }
    }


    private static void hookHideLogo(ClassLoader classLoader) {
        try {
            Class<?> container = XposedHelpers.findClass(
                    "com.heytap.browser.ai.page.ai.vision.AiVisionMainViewContainer", classLoader);
            XposedHelpers.findAndHookMethod(container, "b3", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        if (!HookPrefs.isHideAiLogo() || !(param.thisObject instanceof View)) {
                            return;
                        }
                        View v = (View) param.thisObject;
                        int id = v.getResources().getIdentifier("iu", "id",
                                v.getContext().getPackageName());
                        if (id == 0) {
                            XposedBridge.log("隐藏AI视界搜索框上方大图: 未找到 id=iu");
                            return;
                        }
                        View logo = v.findViewById(id);
                        if (logo == null) {
                            XposedBridge.log("隐藏AI视界搜索框上方大图: view 为 null");
                            return;
                        }

                        logo.setVisibility(View.INVISIBLE);
                    } catch (Throwable t) {
                        XposedBridge.log("隐藏AI视界搜索框上方大图 失败: " + t.getMessage());
                    }
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("隐藏AI视界搜索框上方大图 hook 安装失败: " + t.getMessage());
        }
    }
}
