package com.zohar.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zohar.beziercurve.MessageCountView;

public class MainActivity extends AppCompatActivity {

    private MessageCountView view_msg_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view_msg_view = (MessageCountView) findViewById(R.id.view_msg_view);
        initMsgView();
    }

    /**
     * 初始化消息view
     * @param view
     */
    public void btnInit(View view){
        initMsgView();
    }

    private void initMsgView(){
        view_msg_view.setMsgCount("333");
    }
}
