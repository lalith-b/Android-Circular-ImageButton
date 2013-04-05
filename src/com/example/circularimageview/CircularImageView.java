package com.example.circularimageview;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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

	private onCircularClickListener onClickListener;
	private boolean BUTTON_PRESSED;
	private boolean IS_PRESSED;

	public CircularImageView(Context context, AttributeSet attrs) {
		super(context,attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Options);
		int maskAlphaFactor = a.getInteger(R.styleable.Options_alpha,0);		
		if(maskAlphaFactor >1){
			try {
				throw new Exception("Cannot take alpha inputs greater than 1");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// TODO create onClick from xml itself.
		final String handlerName = a.getString(R.styleable.Options_onCircularButtonClick);

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

		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		setCircularImageDrawable(this.getDrawable(),maskAlphaFactor);
		a.recycle();
	}

	public void setCircularImageDrawable(Drawable resId,int alpha){
		Bitmap bm = ((BitmapDrawable)resId).getBitmap();
		this.setImageBitmap(getRoundedRectBitmap(bm));
	}

	public void setCircularImageResource(int resId,int alpha){
		Bitmap bm = BitmapFactory.decodeResource(getResources(), resId);
		this.setImageBitmap(getRoundedRectBitmap(bm));
	}

	public void setCircularImageBitmap(Bitmap resId,int alpha){
		this.setImageBitmap(getRoundedRectBitmap(resId));
	}


	private Bitmap getRoundedRectBitmap(Bitmap bitmap) {
		int pixels;
		Bitmap result = null;
		try {

			Bitmap resized;

			if(bitmap.getHeight()>bitmap.getWidth()){
				resized = Bitmap.createScaledBitmap(bitmap, bitmap.getHeight(), bitmap.getHeight(), true);
			}else if(bitmap.getWidth()>bitmap.getHeight()){
				resized = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(),bitmap.getWidth(), true);
			}else{
				resized = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(),bitmap.getHeight(), true);
			}

			imageWidth = bitmap.getWidth();
			imageHeight = bitmap.getHeight();

			if(imageWidth > imageHeight){
				pixels = imageWidth;
			}else if(imageHeight > imageWidth){
				pixels = imageHeight;
			}else{
				pixels = imageWidth;
			}

			result = Bitmap.createBitmap(pixels,pixels, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(result);

			int color = 0xff424242;
			Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
			Rect rect = new Rect(0,0, pixels, pixels);

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawCircle(pixels/2, pixels/2,pixels/2, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));		
			canvas.drawBitmap(resized, rect, rect, paint);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
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
			getDrawable().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0X2E2E2E));
			invalidate();
		} else {
			IS_PRESSED = false;
			getDrawable().setColorFilter(null);

			if(distance<radius){
				BUTTON_PRESSED=true;
			}else{
				BUTTON_PRESSED=false;
			}
			invalidate();
		}

		if(up && BUTTON_PRESSED){
			BUTTON_PRESSED=false;
			if(onClickListener!=null){
				onClickListener.onCircularButtonClick(this);
			}
		}

	}

	/**
	 * 
	 * @param onCircularClickListener
	 */
	public void setOnCircularClickListener(onCircularClickListener onClickListener) {
		// TODO Auto-generated method stub
		this.onClickListener = onClickListener;
	}
}