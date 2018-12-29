package com.johnwilliams.qq.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.johnwilliams.qq.R;
import com.johnwilliams.qq.tools.Connection.ConnectionTool;
import com.johnwilliams.qq.tools.Utils;


public class LoginActivity extends Activity {
    private EditText stunumEditText;
    private EditText passwordEditText;
    private String my_stunum;
    private static boolean timeout = false;
    final public static ConnectionTool connectionTool = new ConnectionTool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        try {
            if (connectionTool.socket == null || !connectionTool.socket.isConnected())
                connectionTool.ConnectionInit(ConnectionTool.ServerIP, ConnectionTool.ServerPort, connectionTool.LocalPort);
        } catch (Exception e) {

        }
    }

    private void initView(){
        setTheme(R.style.AppTheme);//
        stunumEditText = findViewById(R.id.stunumEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
    }

    public void onLogin(View v)
    {
        try {
            if (connectionTool.socket == null || !connectionTool.socket.isConnected())
                connectionTool.ConnectionInit(ConnectionTool.ServerIP, ConnectionTool.ServerPort, connectionTool.LocalPort);
//            Toast.makeText(this, "连接成功", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Log.v("Error", e.getMessage());
            Toast.makeText(this, "服务器连接失败", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String reply;
            Thread sleep = new Thread(){
                @Override
                public void run(){
                    try{
                        Thread.sleep(5000);
                    } catch (InterruptedException e){
                        Thread.currentThread().interrupt();
                    }
                    LoginActivity.timeout = true;
                }
            };
            sleep.start();
            reply = connectionTool.Login(stunumEditText.getText().toString(),
                    passwordEditText.getText().toString());
            while(reply.equals("") && !LoginActivity.timeout);//2 second login attempt
            if (reply.equals("Error")){
                Toast.makeText(this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                return;
            }
//            sleep.interrupt();
            LoginActivity.timeout = false;
            if (reply.equals("lol"))//verified
            {
                Toast.makeText(this, "验证成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(Utils.MY_STUNUM_EXTRA, stunumEditText.getText().toString());
                startActivity(intent);
                my_stunum = stunumEditText.getText().toString();
            }
            else if (reply.isEmpty()){
                Toast.makeText(this, "登录过于频繁", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "验证失败\n" + reply, Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onDestroy(){
        try {
            connectionTool.ConnectionEnd();
        } catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onResume(){
        super.onResume();
        try{
            connectionTool.ConnectionInit(ConnectionTool.ServerIP, ConnectionTool.ServerPort, connectionTool.LocalPort);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
