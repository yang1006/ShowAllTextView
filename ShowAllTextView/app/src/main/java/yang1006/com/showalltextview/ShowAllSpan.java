package yang1006.com.showalltextview;

import android.text.style.ClickableSpan;
import android.view.View;


/**
 * Created by yang1006 on 17/7/13.
 * 显示全文的span
 */

public class ShowAllSpan extends ClickableSpan {

    private OnAllSpanClickListener clickListener;


    public ShowAllSpan(OnAllSpanClickListener clickListener){
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View widget) {
        if (clickListener != null){
            clickListener.onClick(widget);
        }
    }

    public interface OnAllSpanClickListener{
        void onClick(View widget);
    }
}
