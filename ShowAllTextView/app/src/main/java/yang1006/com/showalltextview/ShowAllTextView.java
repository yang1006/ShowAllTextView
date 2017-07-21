package yang1006.com.showalltextview;

import android.content.Context;
import android.os.Handler;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by yang1006 on 17/7/13.
 * 一个TextView 在超出maxLines时在末尾显示 "...全文"
 */

public class ShowAllTextView extends TextView {

    /**全文按钮点击事件*/
    private ShowAllSpan.OnAllSpanClickListener onAllSpanClickListener;
    private int maxShowLines = 0;  //最大显示行数

    public ShowAllTextView(Context context) {
        super(context);
    }

    public ShowAllTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**调用此方法才有效果*/
    public void setMyText(CharSequence text) {
        super.setText(text);
        /** 会出现setText之后getLineCount()还是0的情况,这里延迟一点再判断添加 ...全文*/
        if (!TextUtils.isEmpty(getText()) && getLineCount() <= 0){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addEllipsisAndAllAtEnd();
                }
            }, 50);
        }else {
            addEllipsisAndAllAtEnd();
        }
    }

    /**调用此方法才有效果*/
    public void setMyText(int resId){
        setMyText(getContext().getResources().getText(resId));
    }

    /**超过规定行数时, 在文末添加 "...全文"*/
    private void addEllipsisAndAllAtEnd(){
        if (maxShowLines > 0 && maxShowLines < getLineCount()) {
            try {
                int moreWidth = PaintUtils.getTheTextNeedWidth(getPaint(), "...全文");
                /**加上...全文 长度超过了textView的宽度, 则多减去5个字符*/
                if (getLayout().getLineRight(maxShowLines - 1) + moreWidth >= getLayout().getWidth()){
                    this.setText(getText().subSequence(0, getLayout().getLineEnd(maxShowLines - 1) - 5));
                    /**避免减5个字符后还是长度还是超出了,这里再减4个字符*/
                    if (getLayout().getLineRight(maxShowLines - 1) + moreWidth >= getLayout().getWidth()){
                        this.setText(getText().subSequence(0, getLayout().getLineEnd(maxShowLines - 1) - 4));
                    }
                }else {
                    this.setText(getText().subSequence(0, getLayout().getLineEnd(maxShowLines - 1)));
                }
                if (getText().toString().endsWith("\n") && getText().length() >= 1){
                    this.setText(getText().subSequence(0, getText().length() - 1));
                }
                this.append("...");
                SpannableString sb = new SpannableString("全文");
                sb.setSpan(new ShowAllSpan(getContext(), onAllSpanClickListener), 0, sb.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                this.append(sb);
            }catch (Exception e){}
        }
    }

    public void setOnAllSpanClickListener(ShowAllSpan.OnAllSpanClickListener onAllSpanClickListener) {
        this.onAllSpanClickListener = onAllSpanClickListener;
    }

    public int getMaxShowLines() {
        return maxShowLines;
    }

    public void setMaxShowLines(int maxShowLines) {
        this.maxShowLines = maxShowLines;
    }

    private ClickableSpan mPressedSpan = null;
    private boolean result = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        CharSequence text = getText();
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(text);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mPressedSpan = getPressedSpan(this, spannable, event);
                if (mPressedSpan != null){
                    if (mPressedSpan instanceof ShowAllSpan){
                        ((ShowAllSpan) mPressedSpan).setPressed(true);
                    }
                    Selection.setSelection(spannable, spannable.getSpanStart(mPressedSpan), spannable.getSpanEnd(mPressedSpan));
                    result = true;
                }else {
                    result = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                ClickableSpan mClickSpan = getPressedSpan(this, spannable, event);
                if (mPressedSpan != null && mPressedSpan != mClickSpan){
                    if (mPressedSpan instanceof ShowAllSpan){
                        ((ShowAllSpan) mPressedSpan).setPressed(false);
                    }
                    mPressedSpan = null;
                    Selection.removeSelection(spannable);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mPressedSpan != null){
                    if (mPressedSpan instanceof ShowAllSpan){
                        ((ShowAllSpan) mPressedSpan).setPressed(false);
                    }
                    mPressedSpan.onClick(this);
                }
                mPressedSpan = null;
                Selection.removeSelection(spannable);
                break;
        }
        return result;

    }

    private ClickableSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {

        ClickableSpan mTouchSpan = null;

        int x = (int) event.getX();
        int y = (int) event.getY();
        x -= textView.getTotalPaddingLeft();
        x += textView.getScrollX();
        y -= textView.getTotalPaddingTop();
        y += textView.getScrollY();
        Layout layout = getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);

        ShowAllSpan[] spans = spannable.getSpans(off, off, ShowAllSpan.class);
        if (spans != null && spans.length > 0){
            mTouchSpan = spans[0];
        }

        return mTouchSpan;
    }
}
