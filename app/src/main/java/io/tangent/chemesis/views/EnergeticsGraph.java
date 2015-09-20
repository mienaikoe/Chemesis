package io.tangent.chemesis.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Map;
import java.util.Set;

import io.tangent.chemesis.R;
import io.tangent.chemesis.models.Energetics;
import io.tangent.chemesis.models.EnergeticsEntry;
import io.tangent.chemesis.models.EnergeticsField;
import io.tangent.chemesis.util.TypefaceCache;

/**
 * TODO: document your custom view class.
 */
public class EnergeticsGraph extends View {

    private static final int MAIN_COLOR = Color.WHITE;
    private Energetics reactantEnergetics;
    private Energetics productEnergetics;
    private Energetics combinedEnergetics;
    private double maxTemp;
    private float maxVal;
    private float minVal;
    private EnergeticsField mode = EnergeticsField.GIBBS;


    private Paint reactantsPaint = new Paint();
    private Paint productsPaint = new Paint();
    private Paint combinedPaint = new Paint();
    private Paint axisPaint = new Paint();
    private Paint textPaint = new Paint();
    private int axisPadding;


    public EnergeticsGraph(Context context) {
        super(context);
        init(null, 0);
    }

    public EnergeticsGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public EnergeticsGraph(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }
    
    public void setEnergetics( Energetics reactantEnergetics, Energetics productEnergetics, Energetics combinedEnergetics ){
        this.reactantEnergetics = reactantEnergetics;
        this.productEnergetics = productEnergetics;
        this.combinedEnergetics = combinedEnergetics;
        this.maxTemp = Math.max(
            this.reactantEnergetics.getData().lastEntry().getKey(),
            Math.max(
                this.productEnergetics.getData().lastEntry().getKey(),
                this.combinedEnergetics.getData().lastEntry().getKey()
            )
        );
        this.calculateMaxVal();
        this.invalidate();
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes

        // Set up a default TextPaint object

        textPaint.setColor(MAIN_COLOR);
        textPaint.setTypeface(TypefaceCache.KIRO.get(this.getContext()));
        textPaint.setTextSize(R.dimen.graph_text_size);

        reactantsPaint.setColor(Color.CYAN);
        reactantsPaint.setStrokeWidth((int) getResources().getDimension(R.dimen.graph_line_stroke));
        reactantsPaint.setStrokeJoin(Paint.Join.ROUND);

        productsPaint.setColor(Color.MAGENTA);
        productsPaint.setStrokeWidth((int) getResources().getDimension(R.dimen.graph_line_stroke));
        productsPaint.setStrokeJoin(Paint.Join.ROUND);

        combinedPaint.setColor(Color.YELLOW);
        combinedPaint.setStrokeWidth((int) getResources().getDimension(R.dimen.graph_line_stroke));
        combinedPaint.setStrokeJoin(Paint.Join.ROUND);

        axisPaint.setColor(MAIN_COLOR);
        axisPaint.setStrokeWidth((int) getResources().getDimension(R.dimen.graph_axis_stroke));
        axisPaint.setStrokeJoin(Paint.Join.ROUND);
        axisPaint.setStrokeCap(Paint.Cap.ROUND);

        this.axisPadding = (int)getResources().getDimension(R.dimen.graph_axis_padding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if( this.reactantEnergetics == null) {
            canvas.drawText("Loading...", 15, 15, this.textPaint);
            return;
        }

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        int maxY = contentHeight - this.axisPadding;
        int graphStart = this.axisPadding + (int)getResources().getDimension(R.dimen.graph_axis_stroke);

        double conversionRatioX = (contentWidth - graphStart) / ( maxTemp );
        float graphSpaceY = contentHeight - graphStart;
        double conversionRatioY = (graphSpaceY) / ( maxVal - minVal );
        float xAxisY = graphSpaceY - (float)((0 - minVal) * conversionRatioY);

        // Y Axis
        canvas.drawLine(this.axisPadding, 0, this.axisPadding, maxY, this.axisPaint);
        // X Axis
        canvas.drawLine(this.axisPadding, xAxisY, contentWidth, xAxisY, this.axisPaint );

        Float lastX = null;
        Float lastY = null;
        /*
        for( Map.Entry<Double, EnergeticsEntry> entry : this.reactantEnergetics.getData().entrySet() ){
            float x = graphStart + (float)(entry.getKey() * conversionRatioX);
            float y = -(float)(entry.getValue().get(this.mode) * conversionRatioY) + xAxisY;
            if( lastX != null ){
                canvas.drawLine( lastX, lastY, x, y, this.reactantsPaint );
            }
            lastX = x;
            lastY = y;
        }

        lastX = null;
        lastY = null;
        for( Map.Entry<Double, EnergeticsEntry> entry : this.productEnergetics.getData().entrySet() ){
            float x = graphStart + (float)(entry.getKey() * conversionRatioX);
            float y = -(float)(entry.getValue().get(this.mode) * conversionRatioY) + xAxisY;
            if( lastX != null ){
                canvas.drawLine( lastX, lastY, x, y, this.productsPaint );
            }
            lastX = x;
            lastY = y;
        }
        */

        lastX = null;
        lastY = null;
        for( Map.Entry<Double, EnergeticsEntry> entry : this.combinedEnergetics.getData().entrySet() ){
            float x = graphStart + (float)(entry.getKey() * conversionRatioX);
            float y = -(float)(entry.getValue().get(this.mode) * conversionRatioY) + xAxisY;
            if( lastX != null ){
                canvas.drawLine( lastX, lastY, x, y, this.combinedPaint );
            }
            lastX = x;
            lastY = y;
        }


    }

    private void calculateMaxVal(){
        double maxVal = 0, minVal = 0;
        for( EnergeticsEntry ee : this.reactantEnergetics.getData().values() ){
            double val = ee.get(this.mode);
            if( val > maxVal ){
                maxVal = val;
            } else if( val < minVal ){
                minVal = val;
            }
        }
        for( EnergeticsEntry ee : this.productEnergetics.getData().values() ){
            double val = ee.get(this.mode);
            if( val > maxVal ){
                maxVal = val;
            } else if( val < minVal ){
                minVal = val;
            }
        }
        for( EnergeticsEntry ee : this.combinedEnergetics.getData().values() ){
            double val = ee.get(this.mode);
            if( val > maxVal ){
                maxVal = val;
            } else if( val < minVal ){
                minVal = val;
            }
        }
        this.maxVal = (float)maxVal;
        this.minVal = (float)minVal;
    }

    public void setMode(EnergeticsField mode) {
        this.mode = mode;
        this.calculateMaxVal();
    }

}
