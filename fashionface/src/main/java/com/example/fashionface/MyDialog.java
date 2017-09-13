package com.example.fashionface;

/**
 * Created by QiQi on 2017/7/10.
 */

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 创建自定义的dialog，主要学习其实现原理
 * Created by chengguo on 2016/3/22.
 */

public class MyDialog extends Dialog{

    private ImageButton close_button;
    private ImageView imageView;
    private TextView textView;
    private closeOnclickListener closeclickListener;//取消按钮被点击了的监听器
    private Bitmap bm;
    private String message;

    public MyDialog(Context context) {
        super(context,R.style.MyDialog);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_style);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        init();
    }

    private void init(){
        close_button=(ImageButton) findViewById(R.id.close_imageButton);
        imageView=(ImageView) findViewById(R.id.dialog_imageView);
        textView=(TextView) findViewById(R.id.dialog_textView);
        close_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (closeclickListener != null) {
                    closeclickListener.closeClick();
                }
            }
        });

        imageView.setImageBitmap(bm);
        textView.setText(message);
    }

    public void setImageView(Bitmap bmBitmap) {
        if (bmBitmap!= null) {
            this.bm = bmBitmap;
        }
    }

    public void setTextView(String string) {
        if (string!=null) {
            this.message=string;
        }
    }

    /**
     * 设置关闭按钮被点击的接口
     */
    public interface closeOnclickListener {
        public void closeClick();
    }

    /**
     * 设置关闭按钮的监听
     */
    public void setCloseOnclickListener(closeOnclickListener closeclickListener) {
        this.closeclickListener = closeclickListener;
    }
}
