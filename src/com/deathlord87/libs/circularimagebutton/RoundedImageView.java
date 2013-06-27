package com.deathlord87.libs.circularimagebutton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {

	private Bitmap mBitmap;
	private Paint mPaint;

	private int mSize;
	private float mShadowRadius = 0;
	private float mStrokeWidth = 1;
	private int mFrameColor = Color.DKGRAY;
	private int mHighlightColor = Color.WHITE;
	private int mFrameShadowColor = Color.WHITE;
	private float mScale = 1.0f;
	private boolean mEnableTouch;
	
	private Path mFramePath;
	private Rect mSrcRect;
	private RectF mDstRect;
	private RectF mFrameRect;
	private boolean mPressed;
	
	/**
	 * @author lalithb
	 *
	 */
	public interface onCircularClickListener {
		public void onCircularButtonClick(View v);
	}

	/**
	 * @param onClickListener
	 */
	public void setOnCircularClickListener(onCircularClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	/**
	 * 
	 */
	private onCircularClickListener onClickListener;
	/**
	 * 
	 */
	private boolean BUTTON_PRESSED;
	/**
	 * 
	 */
	private boolean IS_PRESSED;

	public RoundedImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularImageView);

		final String handlerName = a.getString(R.styleable.CircularImageView_onCircularButtonClick);
		mFrameColor = a.getColor(R.styleable.CircularImageView_FrameColor,Color.DKGRAY);
		mFrameShadowColor = a.getColor(R.styleable.CircularImageView_FrameShadowColor,Color.WHITE);
		mHighlightColor = a.getColor(R.styleable.CircularImageView_HighlightColor,Color.WHITE);
		mStrokeWidth = a.getFloat(R.styleable.CircularImageView_StrokeWidth,4.0f);
		mShadowRadius = a.getFloat(R.styleable.CircularImageView_ShadowRadius,0);
		mSize = a.getDimensionPixelOffset(R.styleable.CircularImageView_Diameter,0);
		mEnableTouch = a.getBoolean(R.styleable.CircularImageView_EnableTouch,false);
		
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
		a.recycle();
	}

	public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		Drawable drawable = getDrawable();

		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}

		Bitmap b = ((BitmapDrawable) drawable).getBitmap();
		Bitmap originalBitmap = b.copy(Bitmap.Config.ARGB_8888, true);
		Bitmap roundBitmap = getCroppedBitmap(originalBitmap, mSize, mFrameColor, mStrokeWidth, mFrameShadowColor, mShadowRadius, mHighlightColor);
		originalBitmap.recycle();
		
		if (getWidth() > getHeight()) {
			canvas.drawBitmap(roundBitmap, getHeight()/2+mSize/2, 0, null);
		}else{
			canvas.drawBitmap(roundBitmap, 0 , getHeight()/2-mSize/2, null);
		}
	}

	public Bitmap getCroppedBitmap(Bitmap bitmap, int size,
			int frameColor, float strokeWidth,
			int frameShadowColor, float shadowRadius,
			int highlightColor) {
		if (size == 0) {
			if (getWidth() > getHeight()) {
				size = getHeight();
			}else{
				size = getWidth();				
			}
		}
		mSize = size;
		mShadowRadius = shadowRadius;
		mFrameColor = frameColor;
		mFrameShadowColor = frameShadowColor;
		mStrokeWidth = strokeWidth;
		mHighlightColor = highlightColor;

		mBitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(mBitmap);

		final int width = bitmap.getWidth();
		final int height = bitmap.getHeight();
		final int square = Math.min(width, height);

		final Rect cropRect = new Rect((width - square) / 2, (height - square) / 2, square, square);
		final RectF circleRect = new RectF(0f, 0f, mSize, mSize);
		circleRect.inset(mStrokeWidth / 2f, mStrokeWidth / 2f);
		circleRect.inset(mShadowRadius, mShadowRadius);

		final Path fillPath = new Path();
		fillPath.addArc(circleRect, 0f, 360f);

		canvas.drawColor(0, PorterDuff.Mode.CLEAR);

		// opaque circle matte
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setFilterBitmap(true);
		mPaint.setColor(Color.BLACK);
		mPaint.setStyle(Paint.Style.FILL);
		canvas.drawPath(fillPath, mPaint);

		// mask in the icon where the bitmap is opaque
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
		canvas.drawBitmap(bitmap, cropRect, circleRect, mPaint);

		// prepare paint for frame drawing
		mPaint.setXfermode(null);

		mScale = 1f;

		mSrcRect = new Rect(0, 0, mSize, mSize);
		mDstRect = new RectF(0, 0, mSize, mSize);
		mFrameRect = new RectF(mDstRect);
		mFramePath = new Path();

		final float outside = Math.min(canvas.getWidth(), canvas.getHeight());
		final float inside = mScale * outside;
		final float pad = (outside - inside) / 2f;

		mDstRect.set(pad, pad, outside - pad, outside - pad);
		canvas.drawBitmap(mBitmap, mSrcRect, mDstRect, null);

		mFrameRect.set(mDstRect);
		mFrameRect.inset(mStrokeWidth / 2f, mStrokeWidth / 2f);
		mFrameRect.inset(mShadowRadius, mShadowRadius);

		mFramePath.reset();
		mFramePath.addArc(mFrameRect, 0f, 360f);

		// white frame
		if (mPressed) {
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0X2E2E2E));
			mPaint.setColor(Color.argb((int) (0.33f * 255),
					Color.red(mHighlightColor),
					Color.green(mHighlightColor),
					Color.blue(mHighlightColor)));
			canvas.drawPath(mFramePath, mPaint);
		}

		mPaint.setStrokeWidth(mStrokeWidth);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(mPressed ? mHighlightColor : mFrameColor);
		mPaint.setShadowLayer(mShadowRadius, 0f, 0f, mFrameShadowColor);
		canvas.drawPath(mFramePath, mPaint);

		return mBitmap;
	}

	/* (non-Javadoc)
	 * @see android.view.View#setOnClickListener(android.view.View.OnClickListener)
	 */
	@Override
	public void setOnClickListener(OnClickListener l) {
		try {
			throw new UnsupportedOperationException("Use onCircularClickListener() instead of OnClickListener()");
		} catch (Exception e) {
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
		case MotionEvent.ACTION_MOVE:
			up = false;
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
		float cx = mBitmap.getWidth()/2;
		float cy = mBitmap.getHeight()/2;
		float distance = (float) Math.sqrt(Math.pow((x - cx), 2) + Math.pow((y - cy), 2));
		float radius = getWidth()/2;
		if (distance < radius  && !up) {
			IS_PRESSED = true;
			this.setPressed(true);
		} else {
			IS_PRESSED = false;
			this.setPressed(false);

			if(distance<radius){
				this.setPressed(true);
				BUTTON_PRESSED=true;
			}else{
				this.setPressed(false);
				BUTTON_PRESSED=false;
			}
		}

		if(up && BUTTON_PRESSED){
			BUTTON_PRESSED=false;
			if(onClickListener!=null){
				this.setPressed(false);
				onClickListener.onCircularButtonClick(this);
			}
		}
		
		if (mEnableTouch) {
			invalidate();			
		}
		
	}

	public void setScale(float scale) {
		mScale = scale;
	}

	public float getScale() {
		return mScale;
	}

	public void setPressed(boolean pressed) {
		mPressed = pressed;
	}

	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha(int alpha) {

	}

	@Override
	public void setColorFilter(ColorFilter cf) {

	}

	/**
	 * @return the mShadowRadius
	 */
	 public float getShadowRadius() {
		 return mShadowRadius;
	 }

	 /**
	  * @param mShadowRadius the mShadowRadius to set
	  */
	 public void setShadowRadius(float mShadowRadius) {
		 this.mShadowRadius = mShadowRadius;
	 }

	 /**
	  * @return the mStrokeWidth
	  */
	 public float getStrokeWidth() {
		 return mStrokeWidth;
	 }

	 /**
	  * @param mStrokeWidth the mStrokeWidth to set
	  */
	 public void setStrokeWidth(float mStrokeWidth) {
		 this.mStrokeWidth = mStrokeWidth;
	 }

	 /**
	  * @return the mFrameColor
	  */
	 public int getFrameColor() {
		 return mFrameColor;
	 }

	 /**
	  * @param mFrameColor the mFrameColor to set
	  */
	 public void setFrameColor(int mFrameColor) {
		 this.mFrameColor = mFrameColor;
	 }

	 /**
	  * @return the mHighlightColor
	  */
	 public int getHighlightColor() {
		 return mHighlightColor;
	 }

	 /**
	  * @param mHighlightColor the mHighlightColor to set
	  */
	 public void setHighlightColor(int mHighlightColor) {
		 this.mHighlightColor = mHighlightColor;
	 }

	 /**
	  * @return the mFrameShadowColor
	  */
	 public int getFrameShadowColor() {
		 return mFrameShadowColor;
	 }

	 /**
	  * @param mFrameShadowColor the mFrameShadowColor to set
	  */
	 public void setFrameShadowColor(int mFrameShadowColor) {
		 this.mFrameShadowColor = mFrameShadowColor;
	 }
}