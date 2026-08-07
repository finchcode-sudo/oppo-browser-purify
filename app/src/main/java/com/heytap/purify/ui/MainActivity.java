package com.heytap.purify.ui;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.heytap.purify.AppPrefs;
import com.heytap.purify.R;

public class MainActivity extends AppCompatActivity {

    private static final int TAB_HOME = 0;
    private static final int TAB_ABOUT = 1;

    private MaterialSwitch mNovelSwitch;
    private MaterialSwitch mVideoSwitch;
    private MaterialSwitch mHomeSwitch;
    private MaterialSwitch mLauncherIconSwitch;


    private MaterialSwitch mMyFunctionsSwitch;
    private MaterialSwitch mMyNightSwitch;
    private MaterialSwitch mMyCardSwitch;
    private MaterialSwitch mMyNovelSwitch;
    private MaterialSwitch mMyToolsSwitch;
    private MaterialSwitch mMyMessageSwitch;
    private MaterialSwitch mMyScanSwitch;
    private MaterialSwitch mMyBadgeSwitch;
    private MaterialSwitch mMyCashSwitch;
    private MaterialSwitch mDlCleanSwitch;
    private MaterialSwitch mAiCornerSwitch;
    private MaterialSwitch mAiInputSwitch;
    private MaterialSwitch mAiLogoSwitch;
    private MaterialSwitch mAdBlockSwitch;
    private MaterialSwitch mPureSearchSwitch;
    private MaterialSwitch mTopChannelSwitch;

    private LinearLayout mHomeRoot;
    private LinearLayout mAboutRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applySystemBars();
        applyLauncherIconState();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(color(com.google.android.material.R.attr.colorSurface));


        FrameLayout content = new FrameLayout(this);
        content.addView(buildHomeTab());
        content.addView(buildAboutTab());
        root.addView(content, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));


        BottomNavigationView nav = buildBottomNav();
        root.addView(nav, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setContentView(root);
        switchTab(TAB_HOME);
        refreshSwitch();
    }


    private BottomNavigationView buildBottomNav() {
        BottomNavigationView nav = new BottomNavigationView(this);
        nav.getMenu().add(0, TAB_HOME, 0, "首页").setIcon(R.drawable.ic_home);
        nav.getMenu().add(0, TAB_ABOUT, 0, "关于").setIcon(R.drawable.ic_info);
        nav.setLabelVisibilityMode(com.google.android.material.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        nav.setOnItemSelectedListener(item -> {
            switchTab(item.getItemId());
            return true;
        });
        return nav;
    }


    private void switchTab(int id) {
        mHomeRoot.setVisibility(id == TAB_HOME ? View.VISIBLE : View.GONE);
        mAboutRoot.setVisibility(id == TAB_ABOUT ? View.VISIBLE : View.GONE);
    }



    private ViewGroup buildHomeTab() {
        mHomeRoot = new LinearLayout(this);
        mHomeRoot.setOrientation(LinearLayout.VERTICAL);


        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(mHomeRoot, (v, insets) -> {
            androidx.core.graphics.Insets bars = insets.getInsets(
                    androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, bars.top + dp(4), 0, 0);
            return insets;
        });


        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(20), 0, dp(20), dp(24));

        LinearLayout textBlock = new LinearLayout(this);
        textBlock.setOrientation(LinearLayout.VERTICAL);
        textBlock.setLayoutParams(new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(this);
        title.setText("OPPO浏览器净化");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        title.setTypeface(Typeface.create("sans-serif-black", Typeface.NORMAL));
        title.setTextColor(color(com.google.android.material.R.attr.colorOnSurface));
        title.setLineSpacing(0, 1.0f);
        textBlock.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("让进步持续，让改变可见");
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        subtitle.setTextColor(color(com.google.android.material.R.attr.colorOnSurfaceVariant));
        subtitle.setPadding(0, dp(6), 0, 0);
        textBlock.addView(subtitle);

        header.addView(textBlock);


        android.widget.ImageButton restartBtn = new android.widget.ImageButton(this);
        restartBtn.setImageResource(R.drawable.ic_refresh);

        restartBtn.setColorFilter(color(com.google.android.material.R.attr.colorOnSurfaceVariant));
        restartBtn.setScaleType(android.widget.ImageView.ScaleType.CENTER);

        android.graphics.drawable.RippleDrawable ripple =
                new android.graphics.drawable.RippleDrawable(
                        android.content.res.ColorStateList.valueOf(0x22000000),
                        new android.graphics.drawable.GradientDrawable() {{
                            setShape(android.graphics.drawable.GradientDrawable.OVAL);
                            setColor(color(com.google.android.material.R.attr.colorSurfaceContainerHigh));
                        }},
                        null);
        restartBtn.setBackground(ripple);
        restartBtn.setContentDescription("强行停止 OPPO 浏览器");
        restartBtn.setOnClickListener(v -> forceStopBrowser());
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(dp(44), dp(44));
        btnLp.leftMargin = dp(12);
        restartBtn.setLayoutParams(btnLp);
        header.addView(restartBtn);

        mHomeRoot.addView(header);


        FrameLayout body = new FrameLayout(this);
        mHomeRoot.addView(body, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        scroll.setVerticalScrollBarEnabled(false);
        scroll.setHorizontalScrollBarEnabled(false);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(scroll, (v, insets) -> {

            v.setPadding(dp(20), 0, dp(20), 0);
            return insets;
        });
        body.addView(scroll, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(root, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        root.addView(groupLabel("底栏隐藏"));
        MaterialCardView card = new MaterialCardView(this);
        card.setRadius(dp(24));
        card.setCardElevation(dp(0));
        card.setStrokeWidth(dp(0));
        root.addView(card, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout cardBody = new LinearLayout(this);
        cardBody.setOrientation(LinearLayout.VERTICAL);
        cardBody.setBackgroundColor(color(com.google.android.material.R.attr.colorSurfaceContainerLow));
        card.addView(cardBody);

        mHomeSwitch = createSwitch();
        cardBody.addView(settingRow("隐藏首页 Tab", "隐藏底栏首页入口，隐藏后默认起始页变成 AI 视界",
                R.drawable.ic_home, mHomeSwitch));
        cardBody.addView(divider());
        mVideoSwitch = createSwitch();
        cardBody.addView(settingRow("隐藏视频 Tab", "隐藏浏览器底栏的视频入口",
                R.drawable.ic_video, mVideoSwitch));
        cardBody.addView(divider());
        mNovelSwitch = createSwitch();
        cardBody.addView(settingRow("隐藏小说 Tab", "隐藏浏览器底栏的小说入口",
                R.drawable.ic_novel, mNovelSwitch));


        root.addView(space(dp(28)));
        root.addView(groupLabel("我的页面精简"));
        MaterialCardView myCard = new MaterialCardView(this);
        myCard.setRadius(dp(24));
        myCard.setCardElevation(dp(0));
        myCard.setStrokeWidth(dp(0));
        root.addView(myCard, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout myBody = new LinearLayout(this);
        myBody.setOrientation(LinearLayout.VERTICAL);
        myBody.setBackgroundColor(color(com.google.android.material.R.attr.colorSurfaceContainerLow));
        myCard.addView(myBody);

        mMyMessageSwitch = createSwitch();
        myBody.addView(settingRow("隐藏信息中心图标", "隐藏右上角信息中心入口",
                R.drawable.ic_message, mMyMessageSwitch));
        myBody.addView(divider());
        mMyScanSwitch = createSwitch();
        myBody.addView(settingRow("隐藏扫一扫图标", "隐藏右上角扫一扫入口",
                R.drawable.ic_scan, mMyScanSwitch));
        myBody.addView(divider());
        mMyBadgeSwitch = createSwitch();
        myBody.addView(settingRow("隐藏主题徽标", "隐藏头像区用户名下的主题徽标",
                R.drawable.ic_badge, mMyBadgeSwitch));
        myBody.addView(divider());
        mMyCashSwitch = createSwitch();
        myBody.addView(settingRow("隐藏领现金按钮", "隐藏头像区右侧的领现金按钮",
                R.drawable.ic_cash, mMyCashSwitch));
        myBody.addView(divider());
        mMyFunctionsSwitch = createSwitch();
        myBody.addView(settingRow("隐藏我的功能入口", "隐藏书签、历史记录、下载管理、我的关注",
                R.drawable.ic_functions, mMyFunctionsSwitch));
        myBody.addView(divider());
        mMyNightSwitch = createSwitch();
        myBody.addView(settingRow("隐藏浏览器工具", "隐藏浏览器工具相关入口",
                R.drawable.ic_night, mMyNightSwitch));
        myBody.addView(divider());
        mMyCardSwitch = createSwitch();
        myBody.addView(settingRow("隐藏卡片", "隐藏天天领现金等活动卡片",
                R.drawable.ic_card, mMyCardSwitch));
        myBody.addView(divider());
        mMyNovelSwitch = createSwitch();
        myBody.addView(settingRow("隐藏小说区域", "隐藏我的页面中的小说书架",
                R.drawable.ic_bookshelf, mMyNovelSwitch));
        myBody.addView(divider());
        mMyToolsSwitch = createSwitch();
        myBody.addView(settingRow("隐藏我的工具", "隐藏我的工具区域",
                R.drawable.ic_extension, mMyToolsSwitch));


        root.addView(space(dp(28)));
        root.addView(groupLabel("下载管理精简"));
        MaterialCardView dlCard = new MaterialCardView(this);
        dlCard.setRadius(dp(24));
        dlCard.setCardElevation(dp(0));
        dlCard.setStrokeWidth(dp(0));
        root.addView(dlCard, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout dlBody = new LinearLayout(this);
        dlBody.setOrientation(LinearLayout.VERTICAL);
        dlBody.setBackgroundColor(color(com.google.android.material.R.attr.colorSurfaceContainerLow));
        dlCard.addView(dlBody);
        mDlCleanSwitch = createSwitch();
        dlBody.addView(settingRow("隐藏垃圾清理", "隐藏下载管理页面底部的清理垃圾控件",
                R.drawable.ic_clean, mDlCleanSwitch));


        root.addView(space(dp(28)));
        root.addView(groupLabel("AI 视界精简"));
        MaterialCardView aiCard = new MaterialCardView(this);
        aiCard.setRadius(dp(24));
        aiCard.setCardElevation(dp(0));
        aiCard.setStrokeWidth(dp(0));
        root.addView(aiCard, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout aiBody = new LinearLayout(this);
        aiBody.setOrientation(LinearLayout.VERTICAL);
        aiBody.setBackgroundColor(color(com.google.android.material.R.attr.colorSurfaceContainerLow));
        aiCard.addView(aiBody);
        mAiCornerSwitch = createSwitch();
        aiBody.addView(settingRow("移除资源帮找标签", "移除资源帮找等条目右上角的追番剧等标签",
                R.drawable.ic_refresh, mAiCornerSwitch));
        aiBody.addView(divider());
        mAiInputSwitch = createSwitch();
        aiBody.addView(settingRow("隐藏问AI", "隐藏AI视界底部的问AI按钮",
                R.drawable.ic_ai, mAiInputSwitch));
        aiBody.addView(divider());
        mAiLogoSwitch = createSwitch();
        aiBody.addView(settingRow("隐藏搜索框上方大图", "隐藏AI视界搜索框上方的大图",
                R.drawable.ic_image, mAiLogoSwitch));


        root.addView(space(dp(28)));
        root.addView(groupLabel("搜索引擎替换"));
        MaterialCardView searchCard = new MaterialCardView(this);
        searchCard.setRadius(dp(24));
        searchCard.setCardElevation(dp(0));
        searchCard.setStrokeWidth(dp(0));
        root.addView(searchCard, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout searchBody = new LinearLayout(this);
        searchBody.setOrientation(LinearLayout.VERTICAL);
        searchBody.setBackgroundColor(color(com.google.android.material.R.attr.colorSurfaceContainerLow));
        searchCard.addView(searchBody);
        mPureSearchSwitch = createSwitch();
        searchBody.addView(settingRow("搜索引擎替换", "将搜索引擎替换为Bing/谷歌/百度",
                R.drawable.ic_search, mPureSearchSwitch));
        searchBody.addView(divider());
        mTopChannelSwitch = createSwitch();
        searchBody.addView(settingRow("顶部搜索引擎替换", "将搜索引擎替换为百度/Bing/谷歌",
                R.drawable.ic_channel, mTopChannelSwitch));


        root.addView(space(dp(28)));
        root.addView(groupLabel("通用"));
        MaterialCardView generalCard = new MaterialCardView(this);
        generalCard.setRadius(dp(24));
        generalCard.setCardElevation(dp(0));
        generalCard.setStrokeWidth(dp(0));
        root.addView(generalCard, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout generalBody = new LinearLayout(this);
        generalBody.setOrientation(LinearLayout.VERTICAL);
        generalBody.setBackgroundColor(color(com.google.android.material.R.attr.colorSurfaceContainerLow));
        generalCard.addView(generalBody);
        mAdBlockSwitch = createSwitch();
        generalBody.addView(settingRow("阻止广告SDK初始化", "阻止浏览器的广告SDK初始化，减少广告与资源占用",
                R.drawable.ic_tools, mAdBlockSwitch));
        generalBody.addView(divider());
        mLauncherIconSwitch = createSwitch();
        generalBody.addView(settingRow("隐藏桌面图标", "隐藏模块桌面图标，仍可从 LSPosed 进入设置",
                R.drawable.ic_app, mLauncherIconSwitch));


        root.addView(space(dp(24)));

        return mHomeRoot;
    }



    private ViewGroup buildAboutTab() {
        mAboutRoot = new LinearLayout(this);
        mAboutRoot.setOrientation(LinearLayout.VERTICAL);

        mAboutRoot.setGravity(Gravity.CENTER);
        mAboutRoot.setPadding(dp(20), dp(12), dp(20), dp(12));


        ImageView logo = new ImageView(this);
        logo.setImageResource(R.mipmap.ic_launcher);
        mAboutRoot.addView(logo, lpWrap(0, dp(48), dp(48)));

        TextView name = new TextView(this);
        name.setText("OPPO浏览器净化");
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        name.setTypeface(Typeface.create("sans-serif-black", Typeface.NORMAL));
        name.setTextColor(color(com.google.android.material.R.attr.colorOnSurface));
        mAboutRoot.addView(name, lpWrap(dp(6), -1, -1));

        TextView version = new TextView(this);
        version.setText("版本 1.0.0");
        version.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        version.setTextColor(color(com.google.android.material.R.attr.colorOnSurfaceVariant));
        mAboutRoot.addView(version, lpWrap(dp(2), -1, -1));


        SpannableString link = new SpannableString("Github@Fentia");
        int start = link.toString().indexOf("Fentia");
        int end = link.length();
        int linkColor = color(com.google.android.material.R.attr.colorPrimary);
        link.setSpan(new ForegroundColorSpan(linkColor), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        link.setSpan(new UnderlineSpan(), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        link.setSpan(new URLSpan("https://github.com/Fentia"), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView devInfo = new TextView(this);
        devInfo.setText(link);
        devInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        devInfo.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        mAboutRoot.addView(devInfo, lpWrap(dp(6), -1, -1));

        return mAboutRoot;
    }


    private LinearLayout.LayoutParams lpWrap(int topMargin, int widthDp, int heightDp) {
        int w = widthDp > 0 ? dp(widthDp) : ViewGroup.LayoutParams.WRAP_CONTENT;
        int h = heightDp > 0 ? dp(heightDp) : ViewGroup.LayoutParams.WRAP_CONTENT;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, h);
        if (topMargin > 0) {
            lp.topMargin = dp(topMargin);
        }
        return lp;
    }




    private void forceStopBrowser() {
        new Thread(() -> {
            try {
                Process p = Runtime.getRuntime().exec(new String[]{"su", "-c",
                        "am force-stop com.heytap.browser"});
                int rc = p.waitFor();
                runOnUiThread(() -> {
                    if (rc == 0) {
                        Toast.makeText(this, "已强行停止 OPPO 浏览器", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "强行停止失败，请确认已授予 root 权限", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Throwable t) {
                runOnUiThread(() -> Toast.makeText(this,
                        "需要 root 权限才能强行停止浏览器", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private TextView groupLabel(String text) {
        TextView label = new TextView(this);
        label.setText(text);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        label.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        label.setTextColor(color(com.google.android.material.R.attr.colorPrimary));
        label.setPadding(dp(4), 0, dp(4), dp(12));
        return label;
    }

    private MaterialDivider divider() {
        MaterialDivider d = new MaterialDivider(this);
        d.setDividerThickness(dp(1));
        d.setDividerColor(color(com.google.android.material.R.attr.colorOutlineVariant));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = dp(16);
        lp.rightMargin = dp(16);
        d.setLayoutParams(lp);
        return d;
    }

    private MaterialSwitch createSwitch() {
        MaterialSwitch sw = new MaterialSwitch(this);
        LinearLayout.LayoutParams swLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        swLp.leftMargin = dp(12);
        sw.setLayoutParams(swLp);
        return sw;
    }


    private LinearLayout settingRow(String title, String desc, int iconRes, MaterialSwitch sw) {
        int rowH = dp(72);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(16), 0, dp(12), 0);
        row.setMinimumHeight(rowH);

        android.graphics.drawable.RippleDrawable ripple =
                new android.graphics.drawable.RippleDrawable(
                        android.content.res.ColorStateList.valueOf(0x26000000),
                        new android.graphics.drawable.ColorDrawable(0x00000000), null);
        row.setBackground(ripple);
        row.setClickable(true);
        row.setFocusable(true);
        row.setOnClickListener(v -> sw.toggle());
        row.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout iconBox = new LinearLayout(this);
        iconBox.setGravity(Gravity.CENTER);
        iconBox.setBackground(iconBg(color(com.google.android.material.R.attr.colorSecondaryContainer),
                dp(12)));
        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(40), dp(40));
        iconLp.rightMargin = dp(16);
        iconBox.setLayoutParams(iconLp);

        ImageView icon = new ImageView(this);
        Drawable d = getDrawable(iconRes);
        if (d != null) {
            d = d.mutate();
            d.setTint(color(com.google.android.material.R.attr.colorOnSecondaryContainer));
            icon.setImageDrawable(d);
        }
        iconBox.addView(icon, new LinearLayout.LayoutParams(dp(24), dp(24)));
        row.addView(iconBox);

        LinearLayout textWrap = new LinearLayout(this);
        textWrap.setOrientation(LinearLayout.VERTICAL);
        textWrap.setGravity(Gravity.CENTER_VERTICAL);
        textWrap.setLayoutParams(new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        TextView rowTitle = new TextView(this);
        rowTitle.setText(title);
        rowTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        rowTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        rowTitle.setTextColor(color(com.google.android.material.R.attr.colorOnSurface));
        textWrap.addView(rowTitle);

        TextView rowDesc = new TextView(this);
        rowDesc.setText(desc);
        rowDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        rowDesc.setTextColor(color(com.google.android.material.R.attr.colorOnSurfaceVariant));
        rowDesc.setPadding(0, dp(2), 0, 0);
        textWrap.addView(rowDesc);

        row.addView(textWrap);
        row.addView(sw);
        return row;
    }

    private GradientDrawable iconBg(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    private View space(int height) {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        return v;
    }

    private int color(int attr) {
        return MaterialColors.getColor(this, attr, 0xFF000000);
    }

    private int dp(int value) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics()));
    }

    private int sp(int value) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics()));
    }

    private boolean isNight() {
        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }

    private void applySystemBars() {
        Window window = getWindow();
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false);
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        window.setNavigationBarColor(android.graphics.Color.TRANSPARENT);
        View decor = window.getDecorView();
        int vis = decor.getSystemUiVisibility();
        if (isNight()) {
            decor.setSystemUiVisibility(vis
                    & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    & ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        } else {
            decor.setSystemUiVisibility(vis
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }


    private void refreshSwitch() {
        if (mNovelSwitch == null) {
            return;
        }
        mNovelSwitch.setOnCheckedChangeListener(null);
        mNovelSwitch.setChecked(AppPrefs.isHideNovelTab(this));
        mNovelSwitch.setOnCheckedChangeListener(mNovelSwitchListener);

        mVideoSwitch.setOnCheckedChangeListener(null);
        mVideoSwitch.setChecked(AppPrefs.isHideVideoTab(this));
        mVideoSwitch.setOnCheckedChangeListener(mVideoSwitchListener);

        mHomeSwitch.setOnCheckedChangeListener(null);
        mHomeSwitch.setChecked(AppPrefs.isHideHomeTab(this));
        mHomeSwitch.setOnCheckedChangeListener(mHomeSwitchListener);

        if (mLauncherIconSwitch != null) {
            mLauncherIconSwitch.setOnCheckedChangeListener(null);
            mLauncherIconSwitch.setChecked(AppPrefs.isHideLauncherIcon(this));
            mLauncherIconSwitch.setOnCheckedChangeListener(mLauncherIconSwitchListener);
        }

        bindSwitch(mMyFunctionsSwitch, AppPrefs.isHideMyFunctions(this), mMyFunctionsListener);
        bindSwitch(mMyNightSwitch, AppPrefs.isHideMyNight(this), mMyNightListener);
        bindSwitch(mMyCardSwitch, AppPrefs.isHideMyCard(this), mMyCardListener);
        bindSwitch(mMyNovelSwitch, AppPrefs.isHideMyNovel(this), mMyNovelListener);
        bindSwitch(mMyToolsSwitch, AppPrefs.isHideMyTools(this), mMyToolsListener);
        bindSwitch(mMyMessageSwitch, AppPrefs.isHideMyMessage(this), mMyMessageListener);
        bindSwitch(mMyScanSwitch, AppPrefs.isHideMyScan(this), mMyScanListener);
        bindSwitch(mMyBadgeSwitch, AppPrefs.isHideMyBadge(this), mMyBadgeListener);
        bindSwitch(mMyCashSwitch, AppPrefs.isHideMyCash(this), mMyCashListener);
        bindSwitch(mDlCleanSwitch, AppPrefs.isHideDlClean(this), mDlCleanListener);
        bindSwitch(mAiCornerSwitch, AppPrefs.isHideAiCorner(this), mAiCornerListener);
        bindSwitch(mAiInputSwitch, AppPrefs.isHideAiInput(this), mAiInputListener);
        bindSwitch(mAiLogoSwitch, AppPrefs.isHideAiLogo(this), mAiLogoListener);
        bindSwitch(mAdBlockSwitch, AppPrefs.isAdBlock(this), mAdBlockListener);
        bindSwitch(mPureSearchSwitch, AppPrefs.isPureSearch(this), mPureSearchListener);
        bindSwitch(mTopChannelSwitch, AppPrefs.isReplaceTopChannel(this), mTopChannelListener);
    }

    private void bindSwitch(MaterialSwitch sw, boolean checked,
                            CompoundButton.OnCheckedChangeListener listener) {
        if (sw == null) {
            return;
        }
        sw.setOnCheckedChangeListener(null);
        sw.setChecked(checked);
        sw.setOnCheckedChangeListener(listener);
    }

    private void applyLauncherIconState() {
        int state = AppPrefs.isHideLauncherIcon(this)
                ? android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                : android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        getPackageManager().setComponentEnabledSetting(
                new android.content.ComponentName(this, "com.heytap.purify.ui.MainActivityLauncher"),
                state, android.content.pm.PackageManager.DONT_KILL_APP);
    }

    private final CompoundButton.OnCheckedChangeListener mNovelSwitchListener =
            (b, checked) -> AppPrefs.setHideNovelTab(MainActivity.this, checked);
    private final CompoundButton.OnCheckedChangeListener mVideoSwitchListener =
            (b, checked) -> AppPrefs.setHideVideoTab(MainActivity.this, checked);
    private final CompoundButton.OnCheckedChangeListener mHomeSwitchListener =
            (b, checked) -> AppPrefs.setHideHomeTab(MainActivity.this, checked);
    private final CompoundButton.OnCheckedChangeListener mLauncherIconSwitchListener =
            (b, checked) -> {
                AppPrefs.setHideLauncherIcon(MainActivity.this, checked);
                applyLauncherIconState();
            };

    private final CompoundButton.OnCheckedChangeListener mMyFunctionsListener =
            (b, checked) -> AppPrefs.setHideMyFunctions(MainActivity.this, checked);
    private final CompoundButton.OnCheckedChangeListener mMyNightListener =
            (b, checked) -> AppPrefs.setHideMyNight(MainActivity.this, checked);
    private final CompoundButton.OnCheckedChangeListener mMyCardListener =
            (b, checked) -> AppPrefs.setHideMyCard(MainActivity.this, checked);
    private final CompoundButton.OnCheckedChangeListener mMyNovelListener =
            (b, checked) -> AppPrefs.setHideMyNovel(MainActivity.this, checked);
    private final CompoundButton.OnCheckedChangeListener mMyToolsListener =
            (b, checked) -> AppPrefs.setHideMyTools(MainActivity.this, checked);
    private final CompoundButton.OnCheckedChangeListener mMyMessageListener =
            (b, checked) -> AppPrefs.setHideMyMessage(MainActivity.this, checked);
    private final CompoundButton.OnCheckedChangeListener mMyScanListener =
            (b, checked) -> AppPrefs.setHideMyScan(MainActivity.this, checked);
    private final CompoundButton.OnCheckedChangeListener mMyBadgeListener =
            (b, checked) -> AppPrefs.setHideMyBadge(MainActivity.this, checked);
    private final CompoundButton.OnCheckedChangeListener mMyCashListener =
            (b, checked) -> AppPrefs.setHideMyCash(MainActivity.this, checked);
    private final CompoundButton.OnCheckedChangeListener mDlCleanListener =
            (b, checked) -> AppPrefs.setHideDlClean(MainActivity.this, checked);
    private final CompoundButton.OnCheckedChangeListener mAiCornerListener =
            (b, checked) -> AppPrefs.setHideAiCorner(MainActivity.this, checked);
    private final CompoundButton.OnCheckedChangeListener mAiInputListener =
            (b, checked) -> AppPrefs.setHideAiInput(MainActivity.this, checked);
    private final CompoundButton.OnCheckedChangeListener mAiLogoListener =
            (b, checked) -> AppPrefs.setHideAiLogo(MainActivity.this, checked);
    private final CompoundButton.OnCheckedChangeListener mAdBlockListener =
            (b, checked) -> AppPrefs.setAdBlock(MainActivity.this, checked);
    private final CompoundButton.OnCheckedChangeListener mPureSearchListener =
            (b, checked) -> AppPrefs.setPureSearch(MainActivity.this, checked);
    private final CompoundButton.OnCheckedChangeListener mTopChannelListener =
            (b, checked) -> AppPrefs.setReplaceTopChannel(MainActivity.this, checked);
}
