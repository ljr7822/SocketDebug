package com.example.socketdebug;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;

/**
 * @author iwenLjr
 * Create to 2020/3/3 1:27
 */
public class BaseActivity extends Activity {


    private ImageView mIvBack;
    private TextView mTvTitle;
    /**
     * 初始化NavigationBar
     * @param isShowBack 是否显示放回键
     * @param title 显示什么标题
     */
    protected void initNavBar (boolean isShowBack, String title){

        mIvBack = findViewById(R.id.iv_back);
        mTvTitle = findViewById(R.id.tv_ip);

        mIvBack.setVisibility(isShowBack ? View.VISIBLE : View.GONE);
        mTvTitle.setText(title);

        // 给返回键一个点击事件
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * findViewById
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T fd (@IdRes int id){
        return findViewById(id);
    }
}
