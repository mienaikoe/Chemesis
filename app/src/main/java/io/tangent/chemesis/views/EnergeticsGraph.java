package io.tangent.chemesis.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
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

    private double conversionRatioX;
    private double conversionRatioY;
    private float graphStart;


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
        textPaint.setTextSize(getResources().getDimension(R.dimen.graph_text_size));
        textPaint.setTextAlign(Paint.Align.CENTER);

        reactantsPaint.setColor(Color.CYAN);
        reactantsPaint.setStrokeWidth((int) getResources().getDimension(R.dimen.graph_line_stroke));
        reactantsPaint.setStrokeJoin(Paint.Join.ROUND);
        reactantsPaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));

        productsPaint.setColor(Color.MAGENTA);
        productsPaint.setStrokeWidth((int) getResources().getDimension(R.dimen.graph_line_stroke));
        productsPaint.setStrokeJoin(Paint.Join.ROUND);
        productsPaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));

        combinedPaint.setColor(Color.YELLOW);
        combinedPaint.setStrokeWidth((int) getResources().getDimension(R.dimen.graph_line_stroke));
        combinedPaint.setStrokeJoin(Paint.Join.ROUND);

        axisPaint.setColor(MAIN_COLOR);
        axisPaint.setStrokeWidth((int) getResources().getDimension(R.dimen.graph_axis_stroke));
        axisPaint.setStrokeJoin(Paint.Join.ROUND);
        axisPaint.setStrokeCap(Paint.Cap.ROUND);

        this.axisPadding = (int)getResources().getDimension(R.dimen.graph_axis_padding);
        this.graphStart = this.axisPadding + (int)getResources().getDimension(R.dimen.graph_axis_stroke);
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

        this.conversionRatioX = (contentWidth - graphStart) / ( maxTemp );
        float graphSpaceY = contentHeight - graphStart;
        this.conversionRatioY = (graphSpaceY) / ( maxVal - minVal );
        float xAxisY = graphSpaceY - (float)((0 - minVal) * conversionRatioY);

        // Y Axis
        canvas.drawLine(this.axisPadding, 0, this.axisPadding, maxY, this.axisPaint);
        // X Axis
        canvas.drawLine(this.axisPadding, xAxisY, contentWidth, xAxisY, this.axisPaint);


        this.graphLine(this.reactantEnergetics, xAxisY, this.reactantsPaint, false, canvas);
        this.graphLine(this.productEnergetics,  xAxisY, this.productsPaint,  false, canvas);
        this.graphLine(this.combinedEnergetics, xAxisY, this.combinedPaint,  true,  canvas);

    }



    private void graphLine( Energetics energetics, float xAxisY, Paint paint, boolean yLabel, Canvas canvas ){
        Float lastX = null, lastY = null;
        Float firstY = null;
        for( Map.Entry<Double, EnergeticsEntry> entry : energetics.getData().entrySet() ){
            Double val = entry.getValue().get(this.mode);
            if( val != null ) {
                float x = graphStart + (float) (entry.getKey() * conversionRatioX);
                float y = -(float) (val * conversionRatioY) + xAxisY;
                if (lastX != null) {
                    canvas.drawLine(lastX, lastY, x, y, paint);
                }
                if( firstY == null ){
                    firstY = y;
                }
                lastX = x;
                lastY = y;
            }
        }
        if( yLabel ) {
            canvas.save();
            canvas.rotate(-90);
            canvas.drawText(
                    energetics.getData().firstEntry().getValue().get(this.mode).toString() + " " + this.mode.getUnits(),
                    -firstY, (this.axisPadding / 2), this.textPaint);
            canvas.restore();
        }
    }


    private void calculateMaxVal(){
        double maxVal = 0, minVal = 0;
        for( EnergeticsEntry ee : this.reactantEnergetics.getData().values() ){
            Double val = ee.get(this.mode);
            if( val == null ){
                continue;
            } else if( val > maxVal ){
                maxVal = val;
            } else if( val < minVal ){
                minVal = val;
            }
        }
        for( EnergeticsEntry ee : this.productEnergetics.getData().values() ){
            Double val = ee.get(this.mode);
            if( val == null ){
                continue;
            } else if( val > maxVal ){
                maxVal = val;
            } else if( val < minVal ){
                minVal = val;
            }
        }
        for( EnergeticsEntry ee : this.combinedEnergetics.getData().values() ){
            Double val = ee.get(this.mode);
            if( val == null ){
                continue;
            } else if( val > maxVal ){
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
