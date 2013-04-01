package com.example.circularimageview;

import com.example.circularimageview.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rounding);
		
		CircularImageView im = (CircularImageView) findViewById(R.id.imageView1);
		im.setOnClickListener(new CircularImageView.onClickListener() {

			@Override
			public void onCircularButtonClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "I Clicked onCircularButtonClick ",Toast.LENGTH_LONG).show();
			}
		});
		
	}	
}
