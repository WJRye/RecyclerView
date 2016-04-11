package wj.com.recyclerviewdemo;/**
 * Created by wangjiang on 2016/4/7.
 */

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * User: WangJiang(wangjiang7747@gmail.com)
 * Date: 2016-04-07
 * Time: 18:52
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(getContentView());
    }

    public abstract View getContentView();
}
