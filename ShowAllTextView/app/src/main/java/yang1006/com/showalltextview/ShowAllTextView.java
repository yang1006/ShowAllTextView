package yang1006.com.showalltextview;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by yang1006 on 17/7/13.
 * 一个TextView 在超出maxLines时在末尾显示 "...全文"
 */

public class ShowAllTextView extends TextView {

    /**全文按钮点击事件*/
    private ShowAllSpan.OnAllSpanClickListener onAllSpanClickListener;

    public ShowAllTextView(Context context) {
        super(context);
    }

    public ShowAllTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        /**会出现setText之后getLineCount()还是0的情况,这里延迟一点再判断添加 ...全文*/
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

    /**超过规定行数时, 在文末添加 "...全文"*/
    private void addEllipsisAndAllAtEnd(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (getMaxLines() > 0  && getLineCount() > getMaxLines()){
                try {
                    int moreWidth = PaintUtils.getTheTextNeedWidth(getPaint(), "...全文");
                    /**加上...全文 长度超过了textView的宽度, 则多减去5个字符*/
                    if (getLayout().getLineRight(getMaxLines() - 1) + moreWidth >= getLayout().getWidth()){
                        this.setText(getText().subSequence(0, getLayout().getLineEnd(getMaxLines() - 1) - 5));
                    }else {
                        this.setText(getText().subSequence(0, getLayout().getLineEnd(getMaxLines() - 1)));
                    }
                    if (getText().toString().endsWith("\n") && getText().length() >= 1){
                        this.setText(getText().subSequence(0, getText().length() - 1));
                    }
                    this.append("...");
                    SpannableString sb = new SpannableString("全文");
                    sb.setSpan(new ShowAllSpan(onAllSpanClickListener), 0, sb.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    this.append(sb);
                }catch (Exception e){}
            }
        }
    }


    public void setOnAllSpanClickListener(ShowAllSpan.OnAllSpanClickListener onAllSpanClickListener) {
        this.onAllSpanClickListener = onAllSpanClickListener;
    }

}
