package com.heytap.purify.hooks;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public final class HookPrefs {

    private static final String PREFS_NAME = "purify_config";
    private static final String KEY_HIDE_NOVEL_TAB = "hide_novel_tab";
    private static final String KEY_HIDE_VIDEO_TAB = "hide_video_tab";
    private static final String KEY_HIDE_HOME_TAB = "hide_home_tab";


    private static final String KEY_HIDE_MY_FUNCTIONS = "hide_my_functions";
    private static final String KEY_HIDE_MY_NIGHT = "hide_my_night";
    private static final String KEY_HIDE_MY_CARD = "hide_my_card";
    private static final String KEY_HIDE_MY_NOVEL = "hide_my_novel";
    private static final String KEY_HIDE_MY_TOOLS = "hide_my_tools";
    private static final String KEY_HIDE_MY_MESSAGE = "hide_my_message";
    private static final String KEY_HIDE_MY_SCAN = "hide_my_scan";
    private static final String KEY_HIDE_MY_BADGE = "hide_my_badge";
    private static final String KEY_HIDE_MY_CASH = "hide_my_cash";
    private static final String KEY_HIDE_DL_CLEAN = "hide_dl_clean";
    private static final String KEY_HIDE_AI_CORNER = "hide_ai_corner";
    private static final String KEY_HIDE_AI_INPUT = "hide_ai_input";
    private static final String KEY_HIDE_AI_LOGO = "hide_ai_logo";
    private static final String KEY_AD_BLOCK = "ad_block";
    private static final String KEY_PURE_SEARCH = "pure_search";
    private static final String KEY_REPLACE_TOP_CHANNEL = "replace_top_channel";

    private static volatile XSharedPreferences sPrefs;

    private HookPrefs() {
    }

    public static void init() {
        try {
            if (sPrefs == null) {
                synchronized (HookPrefs.class) {
                    if (sPrefs == null) {
                        sPrefs = new XSharedPreferences("com.heytap.purify", PREFS_NAME);
                    }
                }
            }
        } catch (Throwable t) {
            XposedBridge.log("读取配置失败: " + t.getMessage());
        }
    }

    private static boolean getBoolean(String key, boolean defValue) {
        try {
            init();
            return sPrefs != null ? sPrefs.getBoolean(key, defValue) : defValue;
        } catch (Throwable t) {
            XposedBridge.log("读取配置失败: " + t.getMessage());
            return defValue;
        }
    }


    public static boolean isHideNovelTab() {
        return getBoolean(KEY_HIDE_NOVEL_TAB, false);
    }


    public static boolean isHideVideoTab() {
        return getBoolean(KEY_HIDE_VIDEO_TAB, false);
    }


    public static boolean isHideHomeTab() {
        return getBoolean(KEY_HIDE_HOME_TAB, false);
    }


    public static boolean isHideMyFunctions() {
        return getBoolean(KEY_HIDE_MY_FUNCTIONS, false);
    }


    public static boolean isHideMyNight() {
        return getBoolean(KEY_HIDE_MY_NIGHT, false);
    }


    public static boolean isHideMyCard() {
        return getBoolean(KEY_HIDE_MY_CARD, false);
    }


    public static boolean isHideMyNovel() {
        return getBoolean(KEY_HIDE_MY_NOVEL, false);
    }


    public static boolean isHideMyTools() {
        return getBoolean(KEY_HIDE_MY_TOOLS, false);
    }


    public static boolean isHideMyMessage() {
        return getBoolean(KEY_HIDE_MY_MESSAGE, false);
    }


    public static boolean isHideMyScan() {
        return getBoolean(KEY_HIDE_MY_SCAN, false);
    }


    public static boolean isHideMyBadge() {
        return getBoolean(KEY_HIDE_MY_BADGE, false);
    }


    public static boolean isHideMyCash() {
        return getBoolean(KEY_HIDE_MY_CASH, false);
    }


    public static boolean isHideDlClean() {
        return getBoolean(KEY_HIDE_DL_CLEAN, false);
    }


    public static boolean isHideAiCorner() {
        return getBoolean(KEY_HIDE_AI_CORNER, false);
    }


    public static boolean isHideAiInput() {
        return getBoolean(KEY_HIDE_AI_INPUT, false);
    }


    public static boolean isHideAiLogo() {
        return getBoolean(KEY_HIDE_AI_LOGO, false);
    }


    public static boolean isAdBlock() {
        return getBoolean(KEY_AD_BLOCK, false);
    }


    public static boolean isPureSearch() {
        return getBoolean(KEY_PURE_SEARCH, false);
    }


    public static boolean isReplaceTopChannel() {
        return getBoolean(KEY_REPLACE_TOP_CHANNEL, false);
    }

}
