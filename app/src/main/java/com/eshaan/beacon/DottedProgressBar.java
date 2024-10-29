package com.eshaan.beacon;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.Color;

public class DottedProgressBar extends View {
    private int progress = 0;
    private int maxProgress = 100;
    private int numberOfDots = 20;
    private float dotRadius = 20f;
    private float dotSpacing = 40f;
    private int activeColor = Color.BLUE;
    private int inactiveColor = Color.GRAY;
    private boolean useAutoSpacing = true;

    private Paint paint;

    public DottedProgressBar(Context context) {
        super(context);
        init(null);
    }

    public DottedProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DottedProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.DottedProgressBar,
                    0, 0);

            try {
                progress = a.getInteger(R.styleable.DottedProgressBar_progress, progress);
                maxProgress = a.getInteger(R.styleable.DottedProgressBar_maxProgress, maxProgress);
                numberOfDots = a.getInteger(R.styleable.DottedProgressBar_numberOfDots, numberOfDots);
                dotRadius = a.getDimension(R.styleable.DottedProgressBar_dotRadius, dotRadius);
                dotSpacing = a.getDimension(R.styleable.DottedProgressBar_dotSpacing, dotSpacing);
                // If dotSpacing is explicitly set in XML, use that instead of auto-calculation
                if (a.hasValue(R.styleable.DottedProgressBar_dotSpacing)) {
                    dotSpacing = a.getDimension(R.styleable.DottedProgressBar_dotSpacing, dotSpacing);
                    useAutoSpacing = false;
                }
                activeColor = a.getColor(R.styleable.DottedProgressBar_activeColor, activeColor);
                inactiveColor = a.getColor(R.styleable.DottedProgressBar_inactiveColor, inactiveColor);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int height = (int) (2 * dotRadius);
        int width;

        // If we're using auto spacing and have a specific width constraint
        if (useAutoSpacing && (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
            width = widthSize;
            // Calculate spacing based on available width
            if (numberOfDots > 1) {
                float availableSpace = width - getPaddingLeft() - getPaddingRight() - (2 * dotRadius * numberOfDots);
                dotSpacing = availableSpace / (numberOfDots - 1);
                dotSpacing = Math.max(dotSpacing, 1); // Ensure minimum spacing
            }
        } else {
            // Use default spacing if no specific width constraint or auto spacing is disabled
            width = (int) (numberOfDots * (2 * dotRadius) + (numberOfDots - 1) * dotSpacing)
                    + getPaddingLeft() + getPaddingRight();
        }

        // Adjust for height constraints
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(height, heightSize);
        }

        height += getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int activeDots = (int) ((float) progress / maxProgress * numberOfDots);
        float startX = getPaddingLeft() + dotRadius;

        for (int i = 0; i < numberOfDots; i++) {
            float cx = startX + i * (2 * dotRadius + dotSpacing);
            float cy = getHeight() / 2f;

            paint.setColor(i < activeDots ? activeColor : inactiveColor);
            canvas.drawCircle(cx, cy, dotRadius, paint);
        }
    }

    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(progress, maxProgress));
        invalidate();
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        invalidate();
    }
}