/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.deathlord87.circularimageview;

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
import android.graphics.drawable.Drawable;

class CircleFramedDrawable extends Drawable {

    private final Bitmap mBitmap;
    private final int mSize;
    private final Paint mPaint;
    private float mShadowRadius;
    private float mStrokeWidth;
    private int mFrameColor;
    private int mHighlightColor;
    private int mFrameShadowColor;

    private float mScale;
    private Path mFramePath;
    private Rect mSrcRect;
    private RectF mDstRect;
    private RectF mFrameRect;
    private boolean mPressed;

    private boolean mClockWiseRotate;
	private float mDelta;
    
    public CircleFramedDrawable(Bitmap bitmap, int size,
            int frameColor, float strokeWidth,
            int frameShadowColor, float shadowRadius,
            int highlightColor) {
        super();
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
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
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
    }

    @Override
    public void draw(Canvas canvas) {
        // clear background
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
        
//		  TODO the rotate animation to be played.        
//        if(mClockWiseRotate)
//        	mFramePath.addArc(mFrameRect, 90f, 90f+mDelta);
//        else
//        	mFramePath.addArc(mFrameRect, 180f, 180f+mDelta);
        
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
    }

    /**
     * TODO
     * @param rotateDirection (clockwise == true /anticlockwise == false)
     * @param delta (Maximum 360f)
     * @throws Exception 
     */
    public void setRotateAnim(boolean ClockWiseRotate,float byHowmuch) throws Exception{
    	if(ClockWiseRotate && mDelta <= 180f){
    		mDelta = mDelta + byHowmuch;
    	}else if(!ClockWiseRotate && mDelta <= 360f){
    		mDelta = mDelta + byHowmuch;
    	}else{
    		throw new Exception("Error mDelta cannot be > 360f");
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

    @Override
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
