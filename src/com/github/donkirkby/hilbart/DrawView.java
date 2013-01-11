package com.github.donkirkby.hilbart;

import java.io.File;
import java.util.Dictionary;
import java.util.Hashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.os.Environment;
import android.view.View;

public class DrawView extends View {
    private Paint paint = new Paint();
    private int gap;
    private Bitmap scaled;
	private Path path;
	private Dictionary<Integer, Double> levels = 
			new Hashtable<Integer, Double>();
	private int leftLatticeMargin;
	private int topLatticeMargin;
	private int topCanvasMargin;
	private int leftCanvasMargin;
    
    public DrawView(Context context) {
        super(context);
        paint.setColor(Color.BLACK);
        paint.setStyle(Style.STROKE);
        gap = 2;
        paint.setStrokeWidth((float) (gap/2.0));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	super.onSizeChanged(w, h, oldw, oldh);
        Bitmap bitmap = BitmapFactory.decodeFile(getLatestImage());
		int latticeWidth;
		int latticeHeight;
		int imageHeight = bitmap.getHeight();
		int imageWidth = bitmap.getWidth();
		float aspect = imageHeight / (float)imageWidth;
		int canvasWidth = w;
		float potentialHeight = canvasWidth * aspect;
		int canvasHeight = h;
		if (potentialHeight > canvasHeight) {
			latticeHeight = Math.round(canvasHeight / (float)gap);
			latticeWidth = Math.round(canvasHeight / aspect / gap);
		} else {
			latticeWidth = Math.round(canvasWidth / (float)gap);
			latticeHeight = Math.round(canvasWidth * aspect / gap);
		}
		leftCanvasMargin = (canvasWidth - latticeWidth*gap)/2;
		topCanvasMargin = (canvasHeight - latticeHeight*gap)/2;
		scaled = null;
		path = new Path();
		Bitmap sized = Bitmap.createScaledBitmap(
				bitmap, 
				latticeWidth,
				latticeHeight,
				false);
		scaled = toGrayscale(sized);
		int n=10;
		int m=14;
		int pathWidth = m;
		int pathHeight = n;
		int levelCount = 1;
		int size = 1;
		
		while ((pathWidth * 2 < latticeWidth) && 
				(pathHeight * 2 < latticeHeight)) {
			pathWidth *= 2;
			pathHeight *= 2;
			size *= 2;
			levelCount++;
		}
		leftLatticeMargin = (latticeWidth - pathWidth)/2;
		topLatticeMargin = (latticeHeight - pathHeight)/2;
		calculateLevels(
				levelCount, 
				pathWidth, 
				pathHeight);
		
		movePath(0, (n/2)*size - 1);
		drawNxMCells(pathHeight, pathWidth, n, m, size);
    }

    @Override
    public void onDraw(Canvas canvas) {
    	canvas.drawPath(path, paint);
    }

	private void extendPath(int x, int y) {
		path.lineTo(
				leftCanvasMargin + (leftLatticeMargin + x) * gap, 
				topCanvasMargin + (topLatticeMargin + y) * gap);
	}
	
	private void movePath(int x, int y) {
		path.moveTo(
				leftCanvasMargin + (leftLatticeMargin + x) * gap, 
				topCanvasMargin + (topLatticeMargin + y) * gap);
	}
	
	private void drawNxMCells(
			int pathHeight, 
			int pathWidth, 
			int n, 
			int m, 
			int size) {
		for (int i = 0; i < n/2 - 1; i++)
		{
			extendPath(0, (n/2 - i)*size - 1);
			drawCell(0, (n/2 - i)*size - 1, 0, (n/2 - i - 1)*size, -1);
		}

		for (int i = 0; i < m; i++)
		{
			extendPath(i*size, size-1);
			drawCell(i*size, size - 1, (i+1)*size - 1, size - 1, 1);
		}

		for (int i = 0; i < n/2 - 1; i++)
		{
			for (int j = 0; j < m-2; j++)
			{
                extendPath((m-j)*size - 1, (2*i+1)*size);
                drawCell(
                    (m-j)*size - 1, (2*i+1)*size, 
                    (m-j-1)*size, (2*i+1)*size, 
                    1);
			}
            for (int j = 0; j < 2; j++)
            {
                extendPath(2*size - 1, (2*i+j+1)*size);
                drawCell(
                    2*size - 1, (2*i+j+1)*size, 
                    2*size - 1, (2*i+j+2)*size - 1, 
                    -1);
            }
            for (int j = 0; j < m-2; j++)
            {
                extendPath((2+j)*size, (2*i+3)*size - 1);
                drawCell(
                    (2+j)*size, (2*i+3)*size - 1, 
                    (3+j)*size-1, (2*i+3)*size - 1, 
                    1);
            }
		}
		
		for (int i = 0; i < m; i++)
		{
            extendPath((m-i)*size-1, (n-1)*size);
            drawCell(
                (m-i)*size-1, (n-1)*size, 
                (m-i-1)*size, (n-1)*size, 
                1);
		}
		
		for (int i = 0; i < n/2 - 1; i++)
		{
            extendPath(0, (n - i - 1)*size - 1);
            drawCell(0, (n - i - 1)*size - 1, 0, (n - i - 2)*size, -1);
		}
		
		path.close();
	}
		
    private Bitmap toGrayscale(Bitmap bmpOriginal)
    {        
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();    

        Bitmap bmpGrayscale = Bitmap.createBitmap(
        		width, 
        		height, 
        		Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
    
    private void drawCell(
    		int xIn, 
    		int yIn, 
    		int xOut, 
    		int yOut,
    		int sign) {
    	if (xIn == xOut && yIn == yOut)
    	{
    		return;
    	}
    	int dx = xOut-xIn;
        int dy = yOut-yIn;
        int x2 = xIn + dy*sign;
        int y2 = yIn - dx*sign;
        int x3 = xOut + dy*sign;
        int y3 = yOut - dx*sign;
        
        int size = Math.max(Math.abs(dx), Math.abs(dy)) + 1;
        int xMin = Math.min(Math.min(xIn, x2), Math.min(x3, xOut));
        int xMax = xMin + size;
        int yMin = Math.min(Math.min(yIn, y2), Math.min(y3, yOut));
        int yMax = yMin + size;
        
        int total = 0;
        for (int x = xMin; x < xMax; x++)
        {
        	for (int y = yMin; y < yMax; y++)
        	{
        		total += getIntensity(x, y);
        	}
        }
        int area = size*size;
        double intensity = total/area/255.0;
        
        Double minIntensity = levels.get(size);
        if (minIntensity == null) {
			minIntensity = 1.0;
		}
        if (intensity >= minIntensity)
    	{
        	extendPath(xOut, yOut);
    	}
        else
        {
            int halfSize = size / 2;
            int step_dx = (int) Math.signum(dx);
            int step_dy = (int) Math.signum(dy);
            int half_dx = (halfSize-1) * step_dx;
            int half_dy = (halfSize-1) * step_dy;
            int xIn1 = xIn;
            int yIn1 = yIn;
            int xOut1 = xIn + half_dy*sign;
            int yOut1 = yIn - half_dx*sign;
            int xIn2 = xOut1 + step_dy*sign*1;
            int yIn2 = yOut1 - step_dx*sign*1;
            int xOut2 = xIn2 + half_dx;
            int yOut2 = yIn2 + half_dy;
            int xIn3 = xOut2 + step_dx*1;
            int yIn3 = yOut2 + step_dy*1;
            int xOut3 = xIn3 + half_dx;
            int yOut3 = yIn3 + half_dy;
            int xIn4 = xOut3 - step_dy*sign*1;
            int yIn4 = yOut3 + step_dx*sign*1;
            int xOut4 = xOut;
            int yOut4 = yOut;
            		
            drawCell(xIn1, yIn1, xOut1, yOut1, -sign);
            extendPath(xIn2, yIn2);
            drawCell(xIn2, yIn2, xOut2, yOut2, sign);
            extendPath(xIn3, yIn3);
            drawCell(xIn3, yIn3, xOut3, yOut3, sign);
            extendPath(xIn4, yIn4);
            drawCell(xIn4, yIn4, xOut4, yOut4, -sign);
        }
    }
    
    private void calculateLevels(
    		int levelCount, 
    		int width, 
    		int height) {
        
        int[] colourCounts = new int[256];
        for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				colourCounts[getIntensity(x, y)]++;
			}
		}
        int pixelCount = width*height;
        int size = 1;
        int intensity = 0;
        int pixelsPassed = 0;
        for (int i = 0; i < levelCount; i++) {
        	int threshold = pixelCount*(i+1)/levelCount;
        	while (pixelsPassed < threshold && intensity < 256) {
				pixelsPassed += colourCounts[intensity];
				intensity++;
			}
        	levels.put(size, intensity/255.0);
			size *= 2;
		}
    }
    
    /**
     * Read the pixel intensity to display at lattice coordinates x and y.
     * We convert the grayscale colour values to a number between 0 and 
     * 255.
     * @param x lattice coordinate
     * @param y lattice coordinate
     * @return a number between 0 and 255.
     */
    private int getIntensity(int x, int y)
    {
		return scaled.getPixel(leftLatticeMargin + x, topLatticeMargin + y) & 255;
    }

	//read from sdcard
    private String getLatestImage() {
        File f = new File(
        		Environment.getExternalStorageDirectory().getPath() + 
        		"/DCIM/Camera");
        File[] files = f.listFiles();

        if (files.length > 0) {
			return files[files.length-1].getAbsolutePath();
		}
        return null;
    }
}
