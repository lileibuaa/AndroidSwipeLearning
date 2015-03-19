package lilei.androidswipelearning;

import android.content.Context;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * Created by lei on 3/10/15.
 */
public class SwipeLayout extends FrameLayout {
    private Context context;
    private View mContentView;
    private View mMenuView;
    private boolean isOpen = false;
    private ScrollerCompat scroller;
    public static String LOG_TAG = "Swipe_TEST";
    private GestureDetector gestureDetector;

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
        scroller = ScrollerCompat.create(context, new DecelerateInterpolator());
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    public SwipeLayout(Context context) {
        this(context, null);
    }

    private void init() {
        mContentView = LayoutInflater.from(context).inflate(R.layout.view_content, null);
        addView(mContentView);
        mMenuView = LayoutInflater.from(context).inflate(R.layout.view_menu, null);
        addView(mMenuView);
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (isOpen) {
                    swipe((int) (e2.getX() - e1.getX() - mMenuView.getWidth()));
                } else {
                    swipe((int) (e2.getX() - e1.getX()));
                }
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(e1.getX() - e2.getX()) > 20 && Math.abs(velocityX) > 500) {
                    if (velocityX > 0) {
                        smoothCloseMenu();
                    } else {
                        smoothOpenMenu();
                    }
                } else {
                    if (velocityX > 0) {
                        smoothOpenMenu();
                    } else {
                        smoothCloseMenu();
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), getResources().getDimensionPixelOffset(R.dimen.view_width));
        mMenuView.measure(MeasureSpec.makeMeasureSpec(getResources().getDimensionPixelOffset(R.dimen.view_width), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getResources().getDimensionPixelOffset(R.dimen.view_width), MeasureSpec.EXACTLY));
        mContentView.measure(widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(getResources().getDimensionPixelOffset(R.dimen.view_width), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mContentView.layout(0, 0, mContentView.getMeasuredWidth(), mContentView.getMeasuredHeight());
        mMenuView.layout(mContentView.getMeasuredWidth(), 0, mContentView.getMeasuredWidth() + mMenuView.getMeasuredWidth(), mMenuView.getMeasuredHeight());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(LOG_TAG, "action down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(LOG_TAG, "action move");
                break;
            case MotionEvent.ACTION_UP:
                Log.d(LOG_TAG, "action up");
                if (-mContentView.getLeft() > mMenuView.getWidth() * 1.0 / 2) {
                    smoothOpenMenu();
                } else {
                    smoothCloseMenu();
                    break;
                }
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (isOpen) {
            if (scroller.computeScrollOffset()) {
                swipe(scroller.getCurrX());
                postInvalidate();
            }
        } else {
            if (scroller.computeScrollOffset()) {
                swipe(scroller.getCurrX());
                postInvalidate();
            }
        }
    }

    private void smoothCloseMenu() {
        isOpen = false;
        scroller.startScroll(mContentView.getLeft(), 0, -mContentView.getLeft(), 0, 350);
        postInvalidate();
    }

    private void smoothOpenMenu() {
        isOpen = true;
        scroller.startScroll(mContentView.getLeft(), 0, -mMenuView.getWidth() - mContentView.getLeft(), 0, 350);
        postInvalidate();
    }

    private void swipe(int value) {
        if (value > 0) {
            value = 0;
        }
        if (value < -mMenuView.getWidth()) {
            value = -mMenuView.getWidth();
        }
        mContentView.layout(value, mContentView.getTop(), mContentView.getWidth() + value, getMeasuredHeight());
        mMenuView.layout(mContentView.getWidth() + value, mMenuView.getTop(), mMenuView.getWidth() + mContentView.getWidth() + value, mMenuView.getBottom());
    }
}