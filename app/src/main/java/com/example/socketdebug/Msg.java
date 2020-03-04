package com.example.socketdebug;

/**
 * @author iwenLjr
 * Create to 2020/3/3 14:34
 */
public class Msg {
    public static final int TYPE_RECEICED = 0; // 接收消息的标志
    public static final int TYPE_SEND = 1; // 发送消息的标志
    private String content;
    private int type;

    public Msg(String content,int type){
        this.content = content;
        this.type = type;
    }

    // 在后面设置文本内容时调用
    public String getContent(){
        return content;
    }

    //条件语句的判断依据
    public int getType(){
        return type;
    }
}
