package com.radek.tutorial;

import android.graphics.Canvas;

/**
 * Created by Radek on 2017-11-11.
 */

public interface GameObject {
    public void draw(Canvas canvas);
    public void update();
}
