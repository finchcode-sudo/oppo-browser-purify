package com.heytap.purify.hooks;

import android.content.ContentResolver;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * 恶意网址 / 禁止访问页 解除
 *
 * ===== 逆向结论（com.heytap.browser 40.10.19.17） =====
 *
 *  渲染页文案资源：
 *    esx = "根据国家有关规定，该页面禁止访问"
 *    esz = "页面禁止访问"
 *    et0 = "该页面禁止访问"
 *
 *  拦截引擎（TAG = "UrlBlocker"）：
 *    com.heytap.browser.web.security.block.t
 *      S(WebSecurityInfo)  -> 结果处理
 *        info.getType().isForbidden()          <== 唯一判定开关
 *        t.O(WarningPageObject, info)          -> goToForbiddenPage
 *      F(String) -> WebSecurityInfo             -> checkInfoFromOnlineMap
 *      O(...)                                   -> goToForbiddenPage
 *
 *  SecurityType（枚举）：
 *    isForbidden() 内部：$EnumSwitchMapping$0[ordinal] == 4 || == 5
 *      -> FORBIDDEN_FORCE / FORBIDDEN_DANGER
 *
 *  ===== 策略 =====
 *  核心：SecurityType.isForbidden() -> false，一击切断整条禁止页链路。
 *  兜底：UrlBlocker.O() 吞掉跳转；未成年人模式 H()/I() 亦放开。
 *  注意：绝不可主动读写 platform.minors.a 的静态字段，
 *        其 <clinit> 依赖未初始化的 Application，会直接闪退。
 */
public final class ForbiddenUnblockHook {

    private static final String TAG = "禁止访问解除";

    private static final String CLS_SECURITY_TYPE =
            "com.heytap.browser.api.web.security.SecurityType";
    private static final String CLS_URL_BLOCKER =
            "com.heytap.browser.web.security.block.t";
    private static final String CLS_MINORS_DATA =
            "com.heytap.browser.browser.minors.MinorsDataManager";

    private ForbiddenUnblockHook() {
    }

    public static void install(ClassLoader classLoader) {
        hookSecurityType(classLoader);
        hookUrlBlocker(classLoader);
        hookMinorsDataManager(classLoader);
        hookSecureSettings(classLoader);
    }

    /** 核心：SecurityType.isForbidden() / isWarningOrForbidden() -> false。 */
    private static void hookSecurityType(ClassLoader classLoader) {
        try {
            Class<?> clazz = XposedHelpers.findClass(CLS_SECURITY_TYPE, classLoader);

            XposedBridge.hookAllMethods(clazz, "isForbidden", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) {
                    try {
                        return !HookPrefs.isUnblockForbidden();
                    } catch (Throwable t) {
                        XposedBridge.log(TAG + " isForbidden 异常: " + t.getMessage());
                        return null;
                    }
                }
            });

            XposedBridge.hookAllMethods(clazz, "isWarningOrForbidden", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) {
                    try {
                        if (!HookPrefs.isUnblockForbidden()) {
                            return null;
                        }
                        return Boolean.FALSE;
                    } catch (Throwable t) {
                        XposedBridge.log(TAG + " isWarningOrForbidden 异常: " + t.getMessage());
                        return null;
                    }
                }
            });

            XposedBridge.log(TAG + " SecurityType.isForbidden 已挂载");
        } catch (Throwable t) {
            XposedBridge.log(TAG + " SecurityType hook 安装失败: " + t.getMessage());
        }
    }

    /** 兜底：拦截引擎禁止页跳转 O(WarningPageObject, WebSecurityInfo) 直接吞掉。 */
    private static void hookUrlBlocker(ClassLoader classLoader) {
        try {
            Class<?> clazz = XposedHelpers.findClass(CLS_URL_BLOCKER, classLoader);

            XposedBridge.hookAllMethods(clazz, "O", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    try {
                        if (!HookPrefs.isUnblockForbidden()) {
                            return;
                        }
                        if (param.args != null && param.args.length == 2) {
                            param.setResult(null);
                        }
                    } catch (Throwable t) {
                        XposedBridge.log(TAG + " UrlBlocker.O 异常: " + t.getMessage());
                    }
                }
            });

            XposedBridge.log(TAG + " UrlBlocker.O 已挂载");
        } catch (Throwable t) {
            XposedBridge.log(TAG + " UrlBlocker hook 安装失败: " + t.getMessage());
        }
    }

    /** 保底：未成年人模式下的 H()/I() 拦截判定恒 false。 */
    private static void hookMinorsDataManager(ClassLoader classLoader) {
        try {
            Class<?> clazz = XposedHelpers.findClass(CLS_MINORS_DATA, classLoader);

            XposedBridge.hookAllMethods(clazz, "H", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) {
                    return HookPrefs.isUnblockForbidden() ? Boolean.FALSE : null;
                }
            });

            XposedBridge.hookAllMethods(clazz, "I", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) {
                    return HookPrefs.isUnblockForbidden() ? Boolean.FALSE : null;
                }
            });

            XposedBridge.log(TAG + " MinorsDataManager.H/I 已挂载");
        } catch (Throwable t) {
            XposedBridge.log(TAG + " MinorsDataManager hook 安装失败: " + t.getMessage());
        }
    }

    /** 保底：系统 minors_mode 读值伪造为 0。 */
    private static void hookSecureSettings(ClassLoader classLoader) {
        try {
            Class<?> settings = XposedHelpers.findClass(
                    "android.provider.Settings$Secure", classLoader);

            XposedHelpers.findAndHookMethod(settings, "getInt",
                    ContentResolver.class, String.class, int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            try {
                                if (!HookPrefs.isUnblockForbidden()) {
                                    return;
                                }
                                Object key = (param.args != null && param.args.length > 1)
                                        ? param.args[1] : null;
                                if ("minors_mode".equals(key)) {
                                    param.setResult(0);
                                }
                            } catch (Throwable t) {
                                XposedBridge.log(TAG + " Settings.getInt 异常: " + t.getMessage());
                            }
                        }
                    });

            XposedBridge.log(TAG + " Settings$Secure.getInt 已挂载");
        } catch (Throwable t) {
            XposedBridge.log(TAG + " Settings$Secure hook 安装失败: " + t.getMessage());
        }
    }
}