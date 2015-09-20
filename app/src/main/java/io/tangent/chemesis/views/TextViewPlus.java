package io.tangent.chemesis.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.tangent.chemesis.R;
import io.tangent.chemesis.util.TypefaceCache;


public class TextViewPlus extends TextView {

    private static String TAG = "TextViewPlus";


    public TextViewPlus(Context context) {
        super(context);
        setCustomFont(context, TypefaceCache.KIRO);
    }

    public TextViewPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public TextViewPlus(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.TextViewPlus);
        String typeface = a.getString(R.styleable.TextViewPlus_typeface);
        TypefaceCache t = TypefaceCache.valueOf(typeface);
        setCustomFont(ctx, t);
        a.recycle();
    }

    private void setCustomFont(Context ctx, TypefaceCache type){
        try {
            setTypeface(type.get(ctx));
        } catch( Exception ex ){
            Log.e(TAG, ex.getMessage());
            Log.e(TAG, ex.getStackTrace().toString());
        }
    }



}
