package com.example.fashionface;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity {
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context=this;
	}
	protected void showToast(Context context,String text){
		//加载Toast布局
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.toastyle,null);
        TextView textView=(TextView)toastRoot.findViewById(R.id.toast_textView);
		textView.setText(text);
		
		Toast toast = new Toast(context);
		toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.setView(toastRoot);
        toast.show();
        Log.v("TAG1", "自定义的toast");
	}
}
