/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jingcai.apps.qrcode.zxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.jingcai.apps.qrcode.R;
import com.jingcai.apps.qrcode.zxing.camera.CameraManager;

import java.util.Collection;
import java.util.HashSet;


/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 */
public final class ViewfinderView extends View {

    private static final long ANIMATION_DELAY = 20L;
    private static final int OPAQUE = 0xFF;
    public static final int LASER_LINE_HEIGHT = 5;

    private final Paint paint;
    private final int whiteColor;
    private final int yellowColor;

    private final int maskColor;
    private final int resultColor;

    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;

    private int laserPosition;  // laser up to down position
    private boolean increaseFlag = true;

    private Rect laserRect;
    private final Bitmap laserLineBitmap;
    private Bitmap resultBitmap;
    //four border bitmap
    private final Bitmap leftTopBitmap;
    private final Bitmap rightTopBitmap;
    private final Bitmap leftBottomBitmap;
    private final Bitmap rightBottomBitmap;

    //text size
    private float textSize;
    //text string
    private String text1;
    private String text2;
    //text margin top
    private float textMarginTop;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint();
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
//        resultPointColor = resources.getColor(R.color.possible_result_points);
        whiteColor = resources.getColor(R.color.white);
        yellowColor = resources.getColor(R.color.yellow);

        leftTopBitmap = BitmapFactory.decodeResource(resources, R.drawable.camera_mask_left_top);
        rightTopBitmap = BitmapFactory.decodeResource(resources, R.drawable.camera_mask_right_top);
        leftBottomBitmap = BitmapFactory.decodeResource(resources, R.drawable.camera_mask_left_bottom);
        rightBottomBitmap = BitmapFactory.decodeResource(resources,R.drawable.camera_mask_right_bottom);

        laserLineBitmap = BitmapFactory.decodeResource(resources,R.drawable.camera_laser_line);
        laserRect = new Rect();
        final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,16, displayMetrics);
        textMarginTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10, displayMetrics);
        possibleResultPoints = new HashSet<ResultPoint>(5);
        laserPosition = 0;

        text1 = resources.getText(R.string.qr_code_capture_text1).toString();
        text2 = resources.getText(R.string.qr_code_capture_text2).toString();
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {

            // Draw a two pixel solid black border inside the framing rect
//            paint.setColor(frameColor);
//            canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
//            canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
//            canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
//            canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);

            final int bitmapWidth = leftTopBitmap.getWidth();
            final float quarterWidth = bitmapWidth / 5.0f;
            final float threeQuarterWidth = bitmapWidth*4.0f / 5.0f;
            canvas.drawBitmap(leftTopBitmap,frame.left-quarterWidth,frame.top-quarterWidth,null);
            canvas.drawBitmap(rightTopBitmap,frame.right- threeQuarterWidth,frame.top-quarterWidth,null);
            canvas.drawBitmap(leftBottomBitmap,frame.left-quarterWidth,frame.bottom-threeQuarterWidth,null);
            canvas.drawBitmap(rightBottomBitmap,frame.right-threeQuarterWidth,frame.bottom-threeQuarterWidth,null);

            // Draw a red "laser scanner" line through the middle to show decoding is active
//            paint.setColor(laserColor);
            laserPosition = laserPosition < frame.top ? frame.top:laserPosition;  //if laser position out of bound.

            if(increaseFlag){
                laserPosition = laserPosition + 4;
            }else{
                laserPosition = laserPosition - 4;
            }

            if (laserPosition == (frame.top + frame.height())) {
                increaseFlag = false;
            }
            if(laserPosition == frame.top){
                increaseFlag = true;
            }
            laserRect.left = frame.left;
            laserRect.top = laserPosition;
            laserRect.right = frame.right;
            laserRect.bottom = laserPosition + LASER_LINE_HEIGHT;

            canvas.drawBitmap(laserLineBitmap,null,laserRect,null);


            //draw the text1
            paint.setAlpha(OPAQUE);
            paint.setColor(whiteColor);  //white
            paint.setTextSize(textSize);
            final float text1Width = paint.measureText(text1);
            final float text1MarginTop = frame.bottom + textMarginTop * 3;
            canvas.drawText(text1, (getWidth()-text1Width)/2, text1MarginTop,paint);

            //draw text2
            paint.setColor(yellowColor);
            final float text2Width = paint.measureText(text2);
            canvas.drawText(text2,(getWidth()-text2Width)/2,text1MarginTop+textSize+textMarginTop,paint);


//            // possible point
//            Collection<ResultPoint> currentPossible = possibleResultPoints;
//            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
//            if (currentPossible.isEmpty()) {
//                lastPossibleResultPoints = null;
//            } else {
//                possibleResultPoints = new HashSet<ResultPoint>(5);
//                lastPossibleResultPoints = currentPossible;
//                paint.setAlpha(OPAQUE);
//                paint.setColor(resultPointColor);
//                for (ResultPoint point : currentPossible) {
//                    canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
//                }
//            }
//            if (currentLast != null) {
//                paint.setAlpha(OPAQUE / 2);
//                paint.setColor(resultPointColor);
//                for (ResultPoint point : currentLast) {
//                    canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
//                }
//            }

            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

}
