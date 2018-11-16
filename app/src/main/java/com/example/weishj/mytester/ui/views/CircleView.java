package com.example.weishj.mytester.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.weishj.mytester.R;

/**
 * 这个类演示了如何实现一个继承自View的自定义view
 *
 * 注：如果是继承自系统定义的特定View（如TextView），则不需要自己处理“wrap_content”和“padding”的问题
 */
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
		// 要是自定义属性生效，这里不能使用super(context, attrs)
//		super(context, attrs);
		this(context, attrs, 0);
		init();
	}

	public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		/**
		 * 重点3：自定义属性的实现
		 *
		 * 自定义属性的步骤：
		 * 1.创建自定义属性的xml，如attrs.xml
		 * 2.view的构造方法中解析自定义属性的值（即这里的处理）
		 * 3.layout文件中使用自定义属性
		 *   3-1. 布局文件中一定要添加schemas声明：xmlns:app="http://schemas.android.com/apk/res-auto"
		 *   3-2. 如果实现了 CircleView(Context context, AttributeSet attrs) 这个构造方法，那么其内部就不能使用 `super(context, attrs)` ，
		 *   而要改为 `this(context, attrs, 0)`，将其指向这个构造方法，因为系统创建CircleView时是走的双参数的构造方法
		 *
		 * 如何通过getResource的方式取消这里对R文件的依赖，见ShopGUI
		 */
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
		mColor = a.getColor(R.styleable.CircleView_circle_color, Color.RED);
		a.recycle();
		/** 重点3 END ================================================================= */
		init();
	}

	private void init() {
		mPaint.setColor(mColor);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		/* 以下代码是最普通的画圆，它不支持padding效果
		int width = getWidth();
		int height = getHeight();
		int radius = Math.min(width, height) / 2;
		canvas.drawCircle(width / 2, height / 2, radius, mPaint);
		*/

		/** 重点2：通过以下代码解决自定义view的padding不生效的问题 */
		final int paddingLeft = getPaddingLeft();
		final int paddingRight = getPaddingRight();
		final int paddingTop = getPaddingTop();
		final int paddingBottom = getPaddingBottom();
		int width = getWidth() - paddingLeft - paddingRight;
		int height = getHeight() - paddingTop - paddingBottom;
		int radius = Math.min(width, height) / 2;
		canvas.drawCircle(paddingLeft + width / 2, paddingTop + height / 2, radius, mPaint);
		/** 重点2 END ================================================================= */
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		/** 重点1：通过以下代码解决自定义view的wrap_content不生效（等效于match_parent）的问题 */
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
		/** 重点1 END ================================================================= */
	}
}
