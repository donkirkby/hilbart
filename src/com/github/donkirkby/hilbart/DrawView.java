package com.github.donkirkby.hilbart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class DrawView extends View {
    private Paint paint = new Paint();
    private float xScale = (float) 4.0;
    private float yScale = (float) 4.0;
    
    public DrawView(Context context) {
        super(context);
        paint.setColor(Color.BLACK);
    }

    @Override
    public void onDraw(Canvas canvas) {
    	int width = canvas.getWidth();
    	int height = canvas.getHeight();
    	drawCell(canvas, 32, 16, 48, 16, 1);
        canvas.drawLine(0, 0, 128, 128, paint);
        canvas.drawLine(128, 0, 0, 128, paint);
        canvas.drawLine(0, 128, 64, 128, paint);
        canvas.drawLine(64, 128, 128, 128, paint);
    }
    
    private void drawCell(
    		Canvas canvas, 
    		float xIn, 
    		float yIn, 
    		float xOut, 
    		float yOut,
    		int sign) {
    	if (xIn == xOut && yIn == yOut)
    	{
    		return;
    	}
    	float dx = xOut-xIn;
        float dy = yOut-yIn;
//        float x2 = xIn + dy*sign;
//        float y2 = yIn - dx*sign;
//        float x3 = xOut + dy*sign;
//        float y3 = yOut - dx*sign;
        
        float size = Math.max(Math.abs(dx), Math.abs(dy)) + 1;
//        float xMin = Math.min(Math.min(xIn, x2), Math.min(x3, xOut));
//        float xMax = xMin + size;
//        float yMin = Math.min(Math.min(yIn, y2), Math.min(y3, yOut));
//        float yMax = yMin + size;
        
//        total = 0
//        for x in range(xmin, xmax):
//            for y in range(ymin, ymax):
//                total += self.__image.getpixel((xscale*x,yscale*y))
//        area = size*size
//        intensity = total/area/255.0
//        if self.is_inverted:
//            intensity = 1.0 - intensity
//        
//        min_intensity = self.__levels.get(size, 1.0)
//        if intensity >= min_intensity:
//            self.__display.add_point(exitPoint)
//        else:
        
        if (size <= 2.00001) {
			drawLine(canvas, xIn, yIn, xOut, yOut);
		}
        else
        {
            float halfSize = size / 2;
            float step_dx = Math.signum(dx);
            float step_dy = Math.signum(dy);
            float half_dx = (halfSize-1) * step_dx;
            float half_dy = (halfSize-1) * step_dy;
            float xIn1 = xIn;
            float yIn1 = yIn;
            float xOut1 = xIn + half_dy*sign;
            float yOut1 = yIn - half_dx*sign;
            float xIn2 = xOut1 + step_dy*sign;
            float yIn2 = yOut1 - step_dx*sign;
            float xOut2 = xIn2 + half_dx;
            float yOut2 = yIn2 + half_dy;
            float xIn3 = xOut2 + step_dx;
            float yIn3 = yOut2 + step_dy;
            float xOut3 = xIn3 + half_dx;
            float yOut3 = yIn3 + half_dy;
            float xIn4 = xOut3 - step_dy*sign;
            float yIn4 = yOut3 + step_dx*sign;
            float xOut4 = xOut;
            float yOut4 = yOut;
            		
            drawCell(canvas, xIn1, yIn1, xOut1, yOut1, -sign);
            drawLine(canvas, xOut1, yOut1, xIn2, yIn2);
            drawCell(canvas, xIn2, yIn2, xOut2, yOut2, sign);
            drawLine(canvas, xOut2, yOut2, xIn3, yIn3);
            drawCell(canvas, xIn3, yIn3, xOut3, yOut3, sign);
            drawLine(canvas, xOut3, yOut3, xIn4, yIn4);
            drawCell(canvas, xIn4, yIn4, xOut4, yOut4, -sign);
        }
        
        /*
class Curve(object):
    def __init__(self, display, image):
        self.__display = display
        self.__image = image
        self.__levels = {}
        self.scale = (1, 1)
        self.is_inverted = False
        
    def draw_cell(self, entryPoint, exitPoint, sign):
    	...
    	

    def calculate_levels(self, levelCount, left, top, width, height):
        xscale, yscale = self.scale
        pixels = []
        for x in range(left, left+width):
            for y in range(top, top+height):
                intensity = self.__image.getpixel((xscale * x, yscale * y))
                if self.is_inverted:
                    intensity = 255 - intensity
                pixels.append(intensity)
        pixels.sort()
        pixelCount = len(pixels)
        self.__levels = {}
        size = 1
        for i in range(levelCount):
            pixelIndex = pixelCount*i/levelCount
            self.__levels[size] = pixels[pixelIndex] / 255.0
            size *= 2
         */
    }

	private void drawLine(Canvas canvas, float x1, float y1, float x2, float y2) {
		canvas.drawLine(
				x1*xScale, 
				y1*yScale, 
				x2*xScale, 
				y2*yScale, 
				paint);
	}
}
