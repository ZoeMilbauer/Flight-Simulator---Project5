package com.example.ex4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

// JoystickView : view of the joystick
public class JoystickView extends View {

    private float posX; // x position of the center of joystick
    private float posY; // y position of the center of joystick
    private int littleRadius; // radius of little circle
    private int bigRadius; // radius of big circle
    private int halfCanvasWidth = -1;
    private int halfCanvasHeight = -1;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private JoystickActivity joystickActivity;
    boolean isPlayerMoving = false;

    // constructor
    public JoystickView(Context context) {
        super(context);
        joystickActivity = (JoystickActivity)context;
    }

    // isInBigCircle : returns true if the point is inside the big circle
    public boolean isInBigCircle(float x, float y){
        return Math.pow(x- halfCanvasWidth,2) +
                Math.pow(y- halfCanvasHeight,2) <  Math.pow(bigRadius,2);
    }

    // isInLittleCircle : returns true if the point is inside the little circle
    public boolean isInLittleCircle(float x, float y){
        return Math.pow(x- halfCanvasWidth,2) +
                Math.pow(y- halfCanvasHeight,2) <  Math.pow(littleRadius,2);
    }

    // onDraw : draw the joystick
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // initialize canvas size and radius

        halfCanvasWidth = canvas.getWidth() / 2;
        halfCanvasHeight = canvas.getHeight() / 2;
        if (halfCanvasWidth <= halfCanvasHeight) {
            bigRadius = halfCanvasWidth;
            littleRadius = halfCanvasWidth / 4;
        } else {
            bigRadius = halfCanvasHeight;
            littleRadius = halfCanvasHeight / 4;
        }

        canvas.drawColor(Color.parseColor("#00b386"));
        // draw big circle
        paint.setColor(Color.parseColor("#80ffe5"));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(halfCanvasWidth, halfCanvasHeight, bigRadius, paint);
        // draw little circle
        paint.setColor(Color.parseColor("#33ffcc"));
        paint.setStyle(Paint.Style.FILL);
        // if player is not moving, move joystick to the center
        if (!isPlayerMoving) {
            posX = halfCanvasWidth;
            posY = halfCanvasHeight;
        }
        canvas.drawCircle(posX, posY, littleRadius, paint);
    }

    // onTouchEvent : the function takes care of case
    // that player is moving the joystick
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            // start of movement
            case MotionEvent.ACTION_DOWN: {
                invalidate();
                float x = event.getX();
                float y = event.getY();
                // if current position is inside little circle, move joystick
                if (isInLittleCircle(x,y)) {
                    posX = x;
                    posY = y;
                    isPlayerMoving = true;
                    invalidate();
                }
                break;
            }
            // dragging the joystick
            case MotionEvent.ACTION_MOVE: {
                // if player is not moving the joystick, return
                if (!isPlayerMoving)
                    return true;
                // get current position
                float x = event.getX();
                float y = event.getY();
                // if current position is the joystick area,
                // update position of joystick
                if (isInBigCircle(x,y)) {
                    posX = x;
                    posY = y;
                    invalidate();
                }
                break;
            }
            // end of movement
            case MotionEvent.ACTION_UP: {
                // if player is moving, find aileron and elevator values,
                // and send them to server
                if (isPlayerMoving) {
                    float aileron, elevator;
                    // if the x position is in the right side of screen,
                    // normalize value between 0 and 1
                    if (posX > halfCanvasWidth) {
                        aileron = (posX - halfCanvasWidth) / (bigRadius);
                    // if the x position is in the left side of screen,
                    // normalize value between -1 and 0
                    } else if (posX < halfCanvasWidth) {
                        aileron = -(1 -((posX - (halfCanvasWidth - bigRadius))
                                / (bigRadius)));
                    // else, aileron is 0
                    } else {
                        aileron = 0;
                    }
                    // if the y position is in the upper side of screen,
                    // normalize value between 0 and 1
                    if (posY < halfCanvasHeight) {
                        elevator = (posY - (halfCanvasHeight - bigRadius)) / (bigRadius);
                        elevator = 1 - elevator;
                    // if the y position is in the down side of screen,
                    // normalize value between -1 and 0
                    } else if (posY > halfCanvasHeight) {
                        elevator = -((posY - (halfCanvasHeight)) / (bigRadius));
                    // else, elevator is 0
                    } else {
                        elevator = 0;
                    }
                    // send aileron and elevator to server
                    joystickActivity.setAileronElevator(aileron, elevator);
                }
                isPlayerMoving = false;
                invalidate();
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                isPlayerMoving = false;
                break;
            }
        }
        return true;
    }
}
