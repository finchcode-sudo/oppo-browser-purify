package com.heytap.purify;

import android.content.Context;
import android.content.SharedPreferences;

public final class AppPrefs {


    public static final String PREFS_NAME = "purify_config";

    private static final String KEY_HIDE_NOVEL_TAB = "hide_novel_tab";
    private static final String KEY_HIDE_VIDEO_TAB = "hide_video_tab";
    private static final String KEY_HIDE_HOME_TAB = "hide_home_tab";
    private static final String KEY_HIDE_LAUNCHER_ICON = "hide_launcher_icon";


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



    private AppPrefs() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_WORLD_READABLE);
    }

    public static boolean isHideNovelTab(Context context) {
        try {
            return prefs(context).getBoolean(KEY_HIDE_NOVEL_TAB, false);
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static void setHideNovelTab(Context context, boolean value) {
        try {
            prefs(context).edit().putBoolean(KEY_HIDE_NOVEL_TAB, value).apply();
        } catch (Throwable ignored) {
        }
    }

    public static boolean isHideVideoTab(Context context) {
        try {
            return prefs(context).getBoolean(KEY_HIDE_VIDEO_TAB, false);
        } catch (Throwable t) {
            return false;
        }
    }

    public static void setHideVideoTab(Context context, boolean value) {
        try {
            prefs(context).edit().putBoolean(KEY_HIDE_VIDEO_TAB, value).apply();
        } catch (Throwable ignored) {
        }
    }

    public static boolean isHideHomeTab(Context context) {
        try {
            return prefs(context).getBoolean(KEY_HIDE_HOME_TAB, false);
        } catch (Throwable t) {
            return false;
        }
    }

    public static void setHideHomeTab(Context context, boolean value) {
        try {
            prefs(context).edit().putBoolean(KEY_HIDE_HOME_TAB, value).apply();
        } catch (Throwable ignored) {
        }
    }


    public static boolean isHideLauncherIcon(Context context) {
        try {
            return prefs(context).getBoolean(KEY_HIDE_LAUNCHER_ICON, false);
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static void setHideLauncherIcon(Context context, boolean value) {
        try {
            prefs(context).edit().putBoolean(KEY_HIDE_LAUNCHER_ICON, value).apply();
        } catch (Throwable ignored) {
        }
    }



    public static boolean isHideMyFunctions(Context context) {
        return getBool(context, KEY_HIDE_MY_FUNCTIONS);
    }

    public static void setHideMyFunctions(Context context, boolean value) {
        putBool(context, KEY_HIDE_MY_FUNCTIONS, value);
    }

    public static boolean isHideMyNight(Context context) {
        return getBool(context, KEY_HIDE_MY_NIGHT);
    }

    public static void setHideMyNight(Context context, boolean value) {
        putBool(context, KEY_HIDE_MY_NIGHT, value);
    }

    public static boolean isHideMyCard(Context context) {
        return getBool(context, KEY_HIDE_MY_CARD);
    }

    public static void setHideMyCard(Context context, boolean value) {
        putBool(context, KEY_HIDE_MY_CARD, value);
    }

    public static boolean isHideMyNovel(Context context) {
        return getBool(context, KEY_HIDE_MY_NOVEL);
    }

    public static void setHideMyNovel(Context context, boolean value) {
        putBool(context, KEY_HIDE_MY_NOVEL, value);
    }

    public static boolean isHideMyTools(Context context) {
        return getBool(context, KEY_HIDE_MY_TOOLS);
    }

    public static void setHideMyTools(Context context, boolean value) {
        putBool(context, KEY_HIDE_MY_TOOLS, value);
    }

    public static boolean isHideMyMessage(Context context) {
        return getBool(context, KEY_HIDE_MY_MESSAGE);
    }

    public static void setHideMyMessage(Context context, boolean value) {
        putBool(context, KEY_HIDE_MY_MESSAGE, value);
    }

    public static boolean isHideMyScan(Context context) {
        return getBool(context, KEY_HIDE_MY_SCAN);
    }

    public static void setHideMyScan(Context context, boolean value) {
        putBool(context, KEY_HIDE_MY_SCAN, value);
    }

    public static boolean isHideMyBadge(Context context) {
        return getBool(context, KEY_HIDE_MY_BADGE);
    }

    public static void setHideMyBadge(Context context, boolean value) {
        putBool(context, KEY_HIDE_MY_BADGE, value);
    }

    public static boolean isHideMyCash(Context context) {
        return getBool(context, KEY_HIDE_MY_CASH);
    }

    public static void setHideMyCash(Context context, boolean value) {
        putBool(context, KEY_HIDE_MY_CASH, value);
    }

    public static boolean isHideDlClean(Context context) {
        return getBool(context, KEY_HIDE_DL_CLEAN);
    }

    public static void setHideDlClean(Context context, boolean value) {
        putBool(context, KEY_HIDE_DL_CLEAN, value);
    }

    public static boolean isHideAiCorner(Context context) {
        return getBool(context, KEY_HIDE_AI_CORNER);
    }

    public static void setHideAiCorner(Context context, boolean value) {
        putBool(context, KEY_HIDE_AI_CORNER, value);
    }

    public static boolean isHideAiInput(Context context) {
        return getBool(context, KEY_HIDE_AI_INPUT);
    }

    public static void setHideAiInput(Context context, boolean value) {
        putBool(context, KEY_HIDE_AI_INPUT, value);
    }

    public static boolean isHideAiLogo(Context context) {
        return getBool(context, KEY_HIDE_AI_LOGO);
    }

    public static void setHideAiLogo(Context context, boolean value) {
        putBool(context, KEY_HIDE_AI_LOGO, value);
    }

    public static boolean isAdBlock(Context context) {
        return getBool(context, KEY_AD_BLOCK);
    }

    public static void setAdBlock(Context context, boolean value) {
        putBool(context, KEY_AD_BLOCK, value);
    }

    public static boolean isPureSearch(Context context) {
        return getBool(context, KEY_PURE_SEARCH);
    }

    public static void setPureSearch(Context context, boolean value) {
        putBool(context, KEY_PURE_SEARCH, value);
    }

    public static boolean isReplaceTopChannel(Context context) {
        return getBool(context, KEY_REPLACE_TOP_CHANNEL);
    }

    public static void setReplaceTopChannel(Context context, boolean value) {
        putBool(context, KEY_REPLACE_TOP_CHANNEL, value);
    }

    private static boolean getBool(Context context, String key) {
        try {
            return prefs(context).getBoolean(key, false);
        } catch (Throwable t) {
            return false;
        }
    }

    private static void putBool(Context context, String key, boolean value) {
        try {
            prefs(context).edit().putBoolean(key, value).apply();
        } catch (Throwable ignored) {
        }
    }
}
