package com.example.socketdebug;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.socketdebug.adapters.MsgAdapter;
import com.example.socketdebug.utils.Prompt;
import com.example.socketdebug.utils.Utils;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {

    EditText iptoedit;//ip编辑框对象
    EditText porttoedit;//端口编辑框对象
    InputView datatoedit;//数据编辑框对象
    android.widget.Button Button;//连接服务器按钮对象
    RecyclerView msgRecyclerView;//接收的数据显示编辑框对象
    Socket Socket = null;//Socket
    boolean buttontitle = true;//定义一个逻辑变量，用于判断连接服务器按钮状态
    boolean RD = false;//用于控制读数据线程是否执行
    OutputStream OutputStream = null;//定义数据输出流，用于发送数据
    InputStream InputStream = null;//定义数据输入流，用于接收数据
    List<Msg> msgList = new ArrayList<>();
    MsgAdapter adapter;
    ImageView mSendIv;

    private Prompt mPrompt;
    private Utils mUtils;
    private long mPressedTime = 0;//记录是否有首次按键

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNavBar(false,"Tcp客户端欢迎您");

        init();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);

        mSendIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = datatoedit.getInputStr();
                if(!"".equals(content)){
                    //验证编辑框用户输入内容是否合法
                    if (mUtils.thisif(getApplicationContext(),iptoedit, porttoedit, datatoedit)) {
                        // 启动一个新的线程，用于发送数据
                        ThreadSendData t1 = new ThreadSendData();
                        t1.start();
                    } else{
                        //这个地方默认是没有反应，可以自行修改成信息框提示
                        return;
                    }
                    Msg msg = new Msg(content,Msg.TYPE_SEND);
                    msgList.add(msg);
                    adapter.notifyItemChanged(msgList.size()-1);//当有新消息时，刷新ListView中的显示
                    msgRecyclerView.scrollToPosition(msgList.size()-1);//将ListView定位到最后一行
                    datatoedit.setInputStr();//消息发出后清空输入框中的内容
                }else{
                    mPrompt.setToast(getApplicationContext(),"请输入要发送的内容！");
                }

            }
        });
    }

    // 双击退出程序
    @Override
    public void onBackPressed() {
        long mNowTime = System.currentTimeMillis();//获取第一次按键时间
        if ((mNowTime - mPressedTime) > 2000){
            mPrompt.setToast(this,"再按一次退出程序");
            mPressedTime = mNowTime;
        }else{
            //退出程序
            this.finish();
            System.exit(0);
        }
    }
    // 初始化控件
    private void init(){
        iptoedit = fd(R.id.IPEdit);//获取ip地址编辑框对象
        porttoedit = fd(R.id.portEdit);//获取端口编辑框对象
        datatoedit = fd(R.id.dataEdit);//获取欲发送的数据编辑框对象
        Button = fd(R.id.Button_link);//获取连接服务器按钮对象
        msgRecyclerView = fd(R.id.msg_recycler_view);
        mSendIv = fd(R.id.IvSend);
        mPrompt = new Prompt();
        mUtils = new Utils();
    }

    // 连接服务器按钮按下
    public void link(final View view){
        if (!TextUtils.isEmpty(iptoedit.getText()) && !TextUtils.isEmpty(porttoedit.getText())){
            //判断按钮状态
            if (buttontitle == true){
                //如果按钮没有被按下，则按钮状态改为按下
                buttontitle = false;
                iptoedit.setFocusable(false);// 输入框不可编辑
                porttoedit.setFocusable(false);
                iptoedit.setFocusableInTouchMode(false);
                porttoedit.setFocusableInTouchMode(false);
                //读数据线程可以执行
                RD = true;
                //并创建一个新的线程，用于初始化socket
                Connect_Thread Connect_thread = new Connect_Thread();
                Connect_thread.start();
                //提示用户并改变按钮标题
                mPrompt.setToast(getApplicationContext(),"连接成功");
                Button.setText("断 开 连 接");
                //startActivity(new Intent(this,sendActivity.class));
            }else{
                mPrompt.setDialog(MainActivity.this,
                        "确定吗？", "与服务器断开连接", "确定", "取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //如果按钮已经被按下，则改变按钮标题
                                mPrompt.setToast(getApplicationContext(), "已断开连接");
                                Button.setText("连 接 服 务 器");
                                iptoedit.setFocusable(true);// 输入框不可编辑
                                porttoedit.setFocusable(true);
                                iptoedit.setFocusableInTouchMode(true);
                                porttoedit.setFocusableInTouchMode(true);
                                //储存状态的变量反转
                                buttontitle = true;
                                try{
                                    //取消socket
                                    Socket.close();
                                    //socket设置为空
                                    Socket = null;
                                    //读数据线程不执行
                                    RD = false;
                                }catch (Exception e){
                                    //如果想写的严谨一点，可以自行改动
                                    e.printStackTrace();
                                }
                            }
                        },null);
            }
        }else {
            mPrompt.setToast(getApplicationContext(),"ip和端口号错误");
        }
    }

    // 用线程创建Socket连接
    class Connect_Thread extends Thread{
        public void run(){
            //定义一个变量用于储存ip
            InetAddress ipAddress;
            try {
                //判断socket的状态，防止重复执行
                if (Socket == null) {
                    //如果socket为空则执行
                    //获取输入的IP地址
                    ipAddress = InetAddress.getByName(iptoedit.getText().toString());
                    //获取输入的端口
                    int port = Integer.valueOf(porttoedit.getText().toString());
                    //新建一个socket
                    Socket = new Socket(ipAddress, port);
                    //获取socket的输入流和输出流
                    InputStream = Socket.getInputStream();
                    OutputStream = Socket.getOutputStream();

                    //新建一个线程读数据
                    ThreadReadData t1 = new ThreadReadData();
                    t1.start();
                }
            } catch (Exception e) {
                //如果有错误则在这里返回
                e.printStackTrace();
            }
        }
    }

    //用线程执行读取服务器发来的数据
    class ThreadReadData extends Thread{
        public void run() {
            //定义一个变量用于储存服务器发来的数据
            String textdata;
            //根据RD变量的值判断是否执行读数据
            while (RD) {
                try {
                    //定义一个字节集，存放输入的数据，缓存区大小为2048字节
                    final byte[] ReadBuffer = new byte[2048];
                    //用于存放数据量
                    final int ReadBufferLengh;

                    //从输入流获取服务器发来的数据和数据宽度
                    //ReadBuffer为参考变量，在这里会改变为数据
                    //输入流的返回值是服务器发来的数据宽度
                    ReadBufferLengh = InputStream.read(ReadBuffer);

                    //验证数据宽度，如果为-1则已经断开了连接
                    if (ReadBufferLengh == -1) {
                        //重新归位到初始状态
                        RD = false;
                        Socket.close();
                        Socket = null;
                        buttontitle = true;
                        Button.setText("连接服务器");
                    } else {
                        //如果有数据正常返回则进行处理显示
                        //先恢复成GB2312编码
                        textdata = new String(ReadBuffer,0,ReadBufferLengh,"GBK");//原始编码数据
                        Msg msg = new Msg(textdata,Msg.TYPE_RECEICED);
                        msgList.add(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 用线程发送数据
    class ThreadSendData extends Thread{
        public void run(){
            try {
                //用输出流发送数据
                OutputStream.write(datatoedit.getInputStr().getBytes());
//                //发送数据之后会自动断开连接，所以，恢复为最初的状态
//                //有个坑要说一下，因为发送完数据还得等待服务器返回，所以，不能把Socket也注销掉
//                buttontitle = true;
//                //改变按钮标题
//                Button.setText("连 接 服 务 器");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
