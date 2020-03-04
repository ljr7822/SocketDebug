package com.example.socketdebug;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * @author iwenLjr
 * Create to 2020/3/3 20:39
 */
public class InputView extends FrameLayout {

    private String inputHint;
    private View mView;
    private EditText mEtInput;

    public InputView(@NonNull Context context) {
        super(context);
        init(context,null);
    }

    public InputView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public InputView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public InputView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        // 获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.inputView);
        inputHint = typedArray.getString(R.styleable.inputView_input_hint);
        typedArray.recycle();

        // 绑定Layout布局
        mView = LayoutInflater.from(context).inflate(R.layout.input_view, this, false);
        mEtInput = mView.findViewById(R.id.ed_input);
        // 将布局和属性绑定
        mEtInput.setHint(inputHint);

        addView(mView);
    }
    /**
     * 获取输入内容
     * @return 返回输入内容
     */
    public String getInputStr(){
        return mEtInput.getText().toString().trim();
    }

    public void setInputStr(){
        mEtInput.setText("");
    }
}
