package io.tangent.chemesis.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import io.tangent.chemesis.views.TextViewPlus;

/**
 * Created by Jesse on 9/19/2015.
 */
public enum TypefaceCache {

    KIRO("fonts/Kiro-Regular-webfont.ttf");


    private String location;
    private Typeface typeface;
    TypefaceCache(String location){
        this.location = location;
    }




    public Typeface get( Context context ){
        if( this.typeface == null ) {
            this.typeface = Typeface.createFromAsset(context.getAssets(), this.location);
        }
        return this.typeface;
    }


}
