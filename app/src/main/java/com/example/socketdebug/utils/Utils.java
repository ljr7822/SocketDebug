package com.example.socketdebug.utils;

import android.content.ContentProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.example.socketdebug.InputView;

/**
 * @author iwenLjr
 * Create to 2020/3/4 14:07
 */
public class Utils {

    //验证编辑框内容是否合法
    public boolean thisif (Context context, EditText mIptoedit ,EditText mPorttoedit, InputView mDatatoedit){
        //定义一个信息框留作备用
        AlertDialog.Builder message = new AlertDialog.Builder(context);
        message.setPositiveButton("确定",click1);

        //分别获取ip、端口、数据这三项的内容
        String ip = mIptoedit.getText().toString();
        String port = mPorttoedit.getText().toString();
        String data = mDatatoedit.getInputStr();

        //判断是否有编辑框为空
        if (ip == null || ip.length() == 0 || port == null || port.length() == 0){
            //如果有空则弹出提示
            message.setMessage("各数据不能为空！");
            AlertDialog m1 = message.create();
            m1.show();
            return false;
        }else{
            return true;
        }
    }

    //信息框按钮按下事件
    public DialogInterface.OnClickListener click1 = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    };
}
