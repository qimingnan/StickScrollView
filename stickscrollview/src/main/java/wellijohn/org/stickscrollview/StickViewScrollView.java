package wellijohn.org.stickscrollview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * @author: JiangWeiwei
 * @time: 2017/11/3-16:57
 * @email:
 * @desc:
 */
public class StickViewScrollView extends ScrollView {

    private boolean isChildToBottom;

    private AutoFillView mAutoFillView;

    private Runnable scrollerTask;


    private static final String TAG = "StickViewScrollView";

    private boolean mIsAutoScrollChild;

    private int initialPosition;
    private int newCheck = 50;
    private boolean mIsVisible;

    private Rect rect = new Rect();


    public StickViewScrollView(Context context) {
        this(context, null);
    }

    public StickViewScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickViewScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        post(new Runnable() {
            @Override
            public void run() {
                mAutoFillView = (AutoFillView) findChildView(StickViewScrollView.this, AutoFillView.class);
            }
        });
        setFocusableInTouchMode(false);

        scrollerTask = new Runnable() {

            public void run() {
                if (mAutoFillView == null) return;
                int newPosition = getScrollY();
                if (initialPosition - newPosition == 0) {//has stopped
                    if (!mIsVisible) return;

                    Log.d(TAG, "run: " + mIsAutoScrollChild);
                    if (mIsAutoScrollChild) {
                        ObjectAnimator.ofInt(StickViewScrollView.this, "scrollY",
                                getChildAt(0).getHeight() - mAutoFillView.getHeight()).setDuration(200).start();
                    } else {
                        ObjectAnimator.ofInt(StickViewScrollView.this, "scrollY",
                                (getChildAt(0).getHeight() - mAutoFillView.getHeight() * 2)).setDuration(200).setDuration(200).start();

                    }
                } else {
                    initialPosition = getScrollY();
                    StickViewScrollView.this.postDelayed(scrollerTask, newCheck);
                }
            }
        };

    }


    public void startScrollerTask() {
        initialPosition = getScrollY();
        StickViewScrollView.this.postDelayed(scrollerTask, newCheck);
    }

    private View findChildView(View paramView, Class<?> t) {
        View childView;
        if (paramView instanceof ViewGroup) {
            ViewGroup tempVg = (ViewGroup) paramView;
            int count = tempVg.getChildCount();
            for (int index = 0; index < count; index++) {
                View tempView = tempVg.getChildAt(index);
                if (t.isInstance(tempView)) {
                    childView = tempView;
                    return childView;
                } else if (tempView instanceof ViewGroup) {
                    View view = findChildView(tempView, t);
                    if (view != null) {
                        return view;
                    }
                }
            }
        }
        return null;
    }


    public boolean isBottom() {
        return isChildToBottom;
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mAutoFillView == null) return;
        // 滑动的距离加上本身的高度与子View的高度对比
        // ScrollView滑动到底部
        isChildToBottom = t + getHeight() >= getChildAt(0).getMeasuredHeight();

        mIsVisible = mAutoFillView.getGlobalVisibleRect(rect);

        Log.d(TAG, "mAutoFillView显示的高度: " + mIsVisible);

        if (mIsVisible) {
            mIsAutoScrollChild = rect.height() > (mAutoFillView.getHeight() / 2);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                startScrollerTask();
                break;
        }
        return super.onTouchEvent(ev);

    }

}
