package wj.com.recyclerviewdemo.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;


public final class DisplayUtil {

    private DisplayUtil() {
    }

    /**
     * 配置文件中可以用：?attr/selectableItemBackground
     *
     * @param context 上下文对象
     * @return 获得系统默认的选择条目背景，有边界，需要API 11以上，另外在API 21以上时是有边界的波纹
     */
    @SuppressLint("InlinedApi")
    public static Drawable getSelectableItemBackground(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
        return context.getResources().getDrawable(typedValue.resourceId);
    }

    /**
     * 获得StatusBar的高度
     *
     * @param context 上下文对象
     * @return 状态栏的高度
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = resources.getDimensionPixelSize(resourceId);
        return statusBarHeight;
    }

    /**
     * 获得ActionBar的高度，注：在配置文件中获得ActionBar高度可通过：?attr/actionBarSize
     *
     * @param activity 当前的Activity对象
     * @return ActionBar的高度
     */
    public static int getActionBarHeight(Activity activity) {
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {// 如果资源是存在的、有效的
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    /**
     * @param activity 当前的Activity对象
     * @return 获得内容的高度
     */
    public static int getContentHeight(Activity activity) {
        return getScreenHeight(activity) - getStatusBarHeight(activity) - getActionBarHeight(activity);
    }

    /**
     * @param context 上下文对象
     * @return 屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * @param context 上下文对象
     * @return 屏幕高度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
