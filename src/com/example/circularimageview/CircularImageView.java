package com.example.circularimageview;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class CircularImageView extends ImageView{

	private static int imageWidth;
	private static int imageHeight;

	public interface onCircularClickListener {
		public void onCircularButtonClick(View v);
	}

	/**
	 * 
	 * @param onCircularClickListener
	 */
	public void setOnCircularClickListener(onCircularClickListener onClickListener) {
		// TODO Auto-generated method stub
		this.onClickListener = onClickListener;
	}

	private onCircularClickListener onClickListener;
	private boolean BUTTON_PRESSED;
	private boolean IS_PRESSED;
	private Context context;

	public CircularImageView(Context context, AttributeSet attrs) {
		super(context,attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularImageView);

		// TODO create onClick from xml itself.
		final String handlerName = a.getString(R.styleable.CircularImageView_onCircularButtonClick);

		if (handlerName != null) {
			setOnCircularClickListener(new onCircularClickListener() {
				private Method mHandler;

				public void onCircularButtonClick(View v) {
					if (mHandler == null) {
						try {
							mHandler = getContext().getClass().getMethod(handlerName,
									View.class);
						} catch (NoSuchMethodException e) {
							int id = getId();
							String idText = id == NO_ID ? "" : " with id '"
									+ getContext().getResources().getResourceEntryName(
											id) + "'";
							throw new IllegalStateException("Could not find a method " +
									handlerName + "(View) in the activity "
									+ getContext().getClass() + " for onClick handler"
									+ " on view " + v.getClass() + idText, e);
						}
					}
					try {
						mHandler.invoke(getContext(), v);
					} catch (IllegalAccessException e) {
						throw new IllegalStateException("Could not execute non "
								+ "public method of the activity", e);
					} catch (InvocationTargetException e) {
						throw new IllegalStateException("Could not execute "
								+ "method of the activity", e);
					}
				}
			});
		}

		this.context = context;
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		setCircularImageDrawable(this.getDrawable());
		a.recycle();
	}

	public void setCircularImageDrawable(Drawable resId){
		Bitmap bm = ((BitmapDrawable)resId).getBitmap();	
		this.setImageDrawable((getRoundedRectBitmap(bm)));
	}

	public void setCircularImageResource(int resId){
		Bitmap bm = BitmapFactory.decodeResource(getResources(), resId);
		this.setImageDrawable((getRoundedRectBitmap(bm)));
	}

	public void setCircularImageBitmap(Bitmap resId){
		this.setImageDrawable((getRoundedRectBitmap(resId)));
	}

	private Drawable getRoundedRectBitmap(Bitmap bitmap) {
		CircleFramedDrawable circleFramedRect = null;

		try {

			imageWidth = bitmap.getWidth();
			imageHeight = bitmap.getHeight();

			if(imageWidth > imageHeight){
				// Bitmap is a rectangle with width > height
				circleFramedRect = 
						new CircleFramedDrawable(bitmap, imageWidth, 
								Color.BLUE, 
								3, 
								Color.LTGRAY, 
								2, 
								Color.RED);

			}else if(imageHeight > imageWidth){
				// Bitmap is a rectangle with width < height
				circleFramedRect = 
						new CircleFramedDrawable(bitmap, imageHeight, 
								Color.DKGRAY, 
								3, 
								Color.LTGRAY, 
								2, 
								Color.RED);

			}else{
				// Bitmap is a square
				circleFramedRect = 
						new CircleFramedDrawable(bitmap, imageHeight, 
								Color.DKGRAY, 
								3, 
								Color.LTGRAY, 
								2, 
								Color.RED);
			}
		}catch(Exception e){

		}
		return circleFramedRect;
	}


	@Override
	public void setOnClickListener(OnClickListener l) {
		// TODO Auto-generated method stub
		try {
			throw new Exception("Use circularClickLister instead of onClickListener");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		boolean up = false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			onTouchCircle(x, y, up);
			break;
		case MotionEvent.ACTION_UP:
			up = true;
			onTouchCircle(x, y, up);
			break;
		}
		return true;
	}

	/**
	 * onTouchCircle.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param up the up
	 * 
	 * (x-center_x)^2 + (y - center_y)^2 < radius^2
	 * 
	 */           	 
	private void onTouchCircle(float x, float y, boolean up) {
		float cx = getWidth()/2;
		float cy = getHeight()/2;
		float distance = (float) Math.sqrt(Math.pow((x - cx), 2) + Math.pow((y - cy), 2));
		float radius = getWidth()/2;
		if (distance < radius  && !up) {
			IS_PRESSED = true;
		} else {
			IS_PRESSED = false;

			if(distance<radius){
				BUTTON_PRESSED=true;
			}else{
				BUTTON_PRESSED=false;
			}
		}

		if(up && BUTTON_PRESSED){
			BUTTON_PRESSED=false;
			if(onClickListener!=null){
				onClickListener.onCircularButtonClick(this);
			}
		}

	}
}