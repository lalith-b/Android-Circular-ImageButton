package com.example.circularimagebuttonexample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.deathlord87.libs.circularimagebutton.*;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rounding);
		
		RoundedImageView im = (RoundedImageView) findViewById(R.id.lb_circularimageview04);
		im.setOnCircularClickListener(new RoundedImageView.onCircularClickListener() {
			@Override
			public void onCircularButtonClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "I Clicked onCircularButtonClick ",Toast.LENGTH_LONG).show();
				((RoundedImageView) v).setImageResource(R.drawable.student_rectangle);
			}
		});		
//		im.setStrokeWidth(5.0f);
//		im.setFrameColor(Color.BLACK);
	}	
	
	public void test(View v){
		Toast.makeText(getApplicationContext(), "I Clicked test through XML ",Toast.LENGTH_LONG).show();
	}
}
