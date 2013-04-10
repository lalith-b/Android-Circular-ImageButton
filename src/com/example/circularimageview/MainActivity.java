package com.example.circularimageview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.circularimageview.CircularImageView.onCircularClickListener;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rounding);
		
		CircularImageView im = (CircularImageView) findViewById(R.id.imageView1);
		im.setOnCircularClickListener(new onCircularClickListener() {

			@Override
			public void onCircularButtonClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "I Clicked onCircularButtonClick ",Toast.LENGTH_LONG).show();
				((CircularImageView) v).setCircularImageDrawable(getResources().getDrawable(R.drawable.student_rectangle));
			}
		});		
				
	}	
	
	public void test(View v){
		Toast.makeText(getApplicationContext(), "I Clicked test through XML ",Toast.LENGTH_LONG).show();
		v.invalidate();
	}
}
