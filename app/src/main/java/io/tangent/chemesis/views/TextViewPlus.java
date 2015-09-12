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


public class TextViewPlus extends TextView {

    private static String TAG = "TextViewPlus";
    private static final Map<String, Typeface> typefaceCache = new HashMap<String, Typeface>();


    public TextViewPlus(Context context) {
        super(context);
        setCustomFont(context, "fonts/Kiro-Regular-webfont.ttf");
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
        //String typeface = "fonts/Kiro-Regular-webfont.ttf";
        try {
            setCustomFont(ctx, typeface);
        } catch( Exception ex ){
            Log.e(TAG, ex.getMessage());
            Log.e(TAG, ex.getStackTrace().toString());
        }
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = TextViewPlus.typefaceCache.get(asset);
        if( tf == null ) {
            try {
                tf = Typeface.createFromAsset(ctx.getAssets(), asset);
            } catch (Exception e) {
                Log.e(TAG, "Could not get typeface: " + e.getMessage());
                return false;
            }
            TextViewPlus.typefaceCache.put(asset, tf);
        }
        setTypeface(tf);
        return true;
    }



}
