package com.shnu.androidoscanlendar;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button btn001,btn002;
    private CalendarObserver calObserver;

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn001 = (Button)findViewById(R.id.btn_001);
        btn002 = (Button)findViewById(R.id.btn_002);

        btn001.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CalendarDemo.class));
            }
        });
        btn002.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CalendarDemo2.class));
            }
        });

//        在主类中，实例化并实施监听
                calObserver=new CalendarObserver(this,new Handler(){

            @Override
            public void handleMessage(Message msg) {
                /**当监听到改变时，做业务操作*/
                Log.i("tag", "now ");
                String msgStr=(String)msg.obj;
                System.out.println(msgStr+"----------------日程日程");
                Toast.makeText(MainActivity.this, "日程事件修改被触发", Toast.LENGTH_SHORT).show();
            }
        });
        //注册日程事件监听
        getContentResolver().registerContentObserver(CalendarContract.Events.CONTENT_URI, true, calObserver);
    }
//

}
