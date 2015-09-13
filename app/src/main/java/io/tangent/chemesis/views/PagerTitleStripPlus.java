package io.tangent.chemesis.views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerTitleStrip;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Jesse on 9/13/2015.
 */
public class PagerTitleStripPlus extends PagerTitleStrip {

    private Typeface typeface;

    public PagerTitleStripPlus(Context context) {
        super(context);
        this.initTypeface();
    }
    public PagerTitleStripPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initTypeface();
    }
    protected void initTypeface(){
        this.typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Kiro-Regular-webfont.ttf");
    }
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        for (int i=0; i<this.getChildCount(); i++) {
            if (this.getChildAt(i) instanceof TextView) {
                ((TextView)this.getChildAt(i)).setTypeface(this.typeface);
            }
        }
    }
}
