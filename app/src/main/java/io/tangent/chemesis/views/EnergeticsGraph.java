package io.tangent.chemesis.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Set;

import io.tangent.chemesis.R;
import io.tangent.chemesis.models.Energetics;
import io.tangent.chemesis.models.EnergeticsEntry;
import io.tangent.chemesis.models.EnergeticsField;
import io.tangent.chemesis.models.EnergeticsSet;
import io.tangent.chemesis.util.TypefaceCache;

/**
 * TODO: document your custom view class.
 */
public class EnergeticsGraph extends View implements View.OnTouchListener {

    private static final int MAIN_COLOR = Color.WHITE;
    private EnergeticsSet energetics;
    private double maxTemp;
    private float maxVal;
    private float minVal;
    private EnergeticsField mode = EnergeticsField.GIBBS;


    private Paint combinedPaint = new Paint();
    private Paint fillPaint = new Paint();
    private Paint axisPaint = new Paint();
    private Paint textPaint = new Paint();
    private Paint cursorPaint = new Paint();
    private DecimalFormat labelFormat = new DecimalFormat("#.00");
    private int axisPadding;

    private double conversionRatioX;
    private double conversionRatioY;
    private float graphStart;
    private int contentWidth;

    private float cursorX;


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
    
    public void setEnergetics( EnergeticsSet energetics ){
        this.energetics = energetics;
        this.maxTemp = Math.max(
            this.energetics.getReactantEnergetics().getData().lastEntry().getKey(),
            Math.max(
                this.energetics.getProductEnergetics().getData().lastEntry().getKey(),
                this.energetics.getCombinedEnergetics().getData().lastEntry().getKey()
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

        combinedPaint.setColor(this.getResources().getColor(R.color.graph_line));
        combinedPaint.setStrokeWidth((int) getResources().getDimension(R.dimen.graph_line_stroke));
        combinedPaint.setStrokeJoin(Paint.Join.ROUND);

        fillPaint.setColor(this.getResources().getColor(R.color.graph_fill));
        fillPaint.setStyle(Paint.Style.FILL);

        axisPaint.setColor(Color.WHITE);
        axisPaint.setStrokeWidth((int) getResources().getDimension(R.dimen.graph_axis_stroke));
        axisPaint.setStrokeJoin(Paint.Join.ROUND);
        axisPaint.setStrokeCap(Paint.Cap.ROUND);

        cursorPaint.setColor(this.getResources().getColor(R.color.cursor_gray));
        cursorPaint.setStrokeWidth((int) getResources().getDimension(R.dimen.graph_line_stroke));

        this.axisPadding = (int)getResources().getDimension(R.dimen.graph_axis_padding);
        this.graphStart = this.axisPadding + (int)getResources().getDimension(R.dimen.graph_axis_stroke);
        this.cursorX = this.graphStart;

        this.setOnTouchListener(this);
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if( this.energetics == null) {
            canvas.drawText("Loading...", 15, 15, this.textPaint);
            return;
        }

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        this.contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        int maxY = contentHeight - this.axisPadding;

        this.conversionRatioX = (contentWidth - graphStart) / ( maxTemp );
        float graphSpaceY = maxY - graphStart;
        this.conversionRatioY = (graphSpaceY) / ( maxVal - minVal );
        float xAxisY = graphSpaceY - (float)((0 - minVal) * conversionRatioY) + this.axisPadding;

        // Data
        this.graphLine(this.energetics.getCombinedEnergetics(), xAxisY, this.combinedPaint, canvas);

        // Axes
        canvas.drawLine(this.axisPadding, this.axisPadding, this.axisPadding, maxY, this.axisPaint);
        canvas.drawLine(this.axisPadding, xAxisY, contentWidth, xAxisY, this.axisPaint);

        // Cursor
        this.graphCursor(canvas, xAxisY, maxY);

    }




    private void graphLine( Energetics energetics, float xAxisY, Paint paint, Canvas canvas ){
        Float lastX = null, lastY = null;
        Float firstX = null, firstY = null;
        Path fillPath = new Path();

        for( Map.Entry<Double, EnergeticsEntry> entry : energetics.getData().entrySet() ){
            Double val = entry.getValue().get(this.mode);
            if( val != null ) {
                Float x = graphStart + (float) (entry.getKey() * conversionRatioX);
                Float y = -(float) (val * conversionRatioY) + xAxisY;
                if( firstX == null ){
                    firstX = x;
                    firstY = y;
                    fillPath.moveTo(firstX, firstY);
                } else if (lastX != null) {
                    canvas.drawLine(lastX, lastY, x, y, paint);
                    fillPath.lineTo(x, y);
                }
                lastX = x;
                lastY = y;
            }
        }
        fillPath.lineTo(lastX, xAxisY);
        fillPath.lineTo(firstX, xAxisY);
        fillPath.close();
        canvas.drawPath(fillPath, this.fillPaint);
    }

    private void graphCursor(Canvas canvas, float xAxisY, float maxY){
        Double cursorTemp;
        if( this.cursorX <= this.graphStart) {
            cursorTemp = this.energetics.getCombinedEnergetics().getFirstDataKey(this.mode);
            this.cursorX = (float)(cursorTemp * conversionRatioX) + graphStart;
        } else {
            cursorTemp = ((this.cursorX - graphStart) / conversionRatioX);
        }

        Double yValue = this.energetics.getCombinedEnergetics().extrapolateValue(cursorTemp, this.mode);
        if( yValue != null ) {
            float cursorY = -(float) (yValue * conversionRatioY) + xAxisY;

            canvas.drawLine(this.cursorX, this.graphStart, this.cursorX, maxY, this.cursorPaint);
            canvas.drawLine(this.graphStart, cursorY, this.contentWidth, cursorY, this.cursorPaint);

            String tempStr = labelFormat.format(cursorTemp) + " K";
            if (yValue <= 0) {
                canvas.drawText(tempStr, this.cursorX,
                        xAxisY - this.getResources().getDimension(R.dimen.graph_label_padding),
                        this.textPaint);
            } else {
                canvas.drawText(tempStr, this.cursorX,
                        xAxisY + this.textPaint.getTextSize() + this.getResources().getDimension(R.dimen.graph_label_padding),
                        this.textPaint);
            }

            canvas.save();
            canvas.rotate(-90);
            String yValueStr = labelFormat.format(yValue) + " " + this.mode.getUnits();
            canvas.drawText(yValueStr, -cursorY, (this.axisPadding / 2), this.textPaint);
            canvas.restore();
        }
    }




    private void calculateMaxVal(){
        double maxVal = 0, minVal = 0;
        for( EnergeticsEntry ee : this.energetics.getCombinedEnergetics().getData().values() ){
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
        this.postInvalidate();
    }




    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                this.cursorX = event.getX();
                if( this.cursorX < this.graphStart ){
                    this.cursorX = this.graphStart;
                } else if( this.cursorX > this.contentWidth ){
                    this.cursorX = this.contentWidth;
                }
                this.postInvalidate();
                return true;
        }
        return false;
    }
}
