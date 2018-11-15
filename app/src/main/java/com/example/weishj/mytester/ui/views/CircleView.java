package com.example.weishj.mytester.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View {
	private static final int DEFAULT_WIDTH = 200;
	private static final int DEFAULT_HEIGHT = 200;
	private int mColor = Color.RED;
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	public CircleView(Context context) {
		super(context);
		init();
	}

	public CircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		mPaint.setColor(mColor);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = getWidth();
		int height = getHeight();
		int radius = Math.min(width, height) / 2;
		canvas.drawCircle(width / 2, height / 2, radius, mPaint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// 重点1：通过以下代码解决自定义view的wrap_content不生效（等效于match_parent）的问题
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
			setMeasuredDimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		} else if (widthSpecMode == MeasureSpec.AT_MOST) {
			setMeasuredDimension(DEFAULT_WIDTH, heightSpecSize);
		} else if (heightSpecMode == MeasureSpec.AT_MOST) {
			setMeasuredDimension(widthSpecSize, DEFAULT_HEIGHT);
		}
	}
}
