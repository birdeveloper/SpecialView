package com.birdeveloper.specialview

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.GradientDrawable.Orientation

class Shadow(spread:Int, opacity:Int, color:String, shape:Int, radius:FloatArray, position:Position) {
    private var spread:Int = 0
    private var opacity:Int = 0
    private val color:String
    private var shape:Int = 0
    private val radius:FloatArray = radius
    lateinit var shadow:LayerDrawable
    val shadowPosition:Position
    init{
        this.spread = spread
        this.opacity = opacity
        this.color = color.replace("#", "")
        this.shape = shape
        this.shadowPosition = position
        init()
    }
    private fun init() {
        var hex = 0
        spread *= 14
        val gradientDrawables = arrayOfNulls<InsetDrawable>(spread)
        val padding = 1
        val center = shadowPosition == Position.CENTER
        val orientation = getOrientation(shadowPosition)
        var i = 0
        var step = 0
        while (i < spread)
        {
            val drawable = GradientDrawable()
            drawable.shape = shape
            drawable.gradientType = shape
            var str = Integer.toHexString(hex)
            if (hex < 16)
            {
                str = "0" + str
            }
            str += color
            val col = Color.parseColor("#" + str)
            if (!center)
            {
                drawable.orientation = orientation
                val colors = intArrayOf(col, Color.parseColor("#00ffffff"))
                drawable.colors = colors
            }
            else
            {
                drawable.setColor(col)
            }
            drawable.cornerRadii = radius
            gradientDrawables[i] = InsetDrawable(drawable, padding, padding, padding, padding)
            if (step == spread / 14)
            {
                ++hex
                step = 0
            }
            ++step
            ++i
        }
        shadow = LayerDrawable(gradientDrawables)
        shadow.alpha = opacity
    }
    private fun getOrientation(position:Position):Orientation {
        var orientation = Orientation.TOP_BOTTOM
        when (position) {
            Position.BOTTOM -> orientation = Orientation.BOTTOM_TOP
            Position.LEFT -> orientation = Orientation.LEFT_RIGHT
            Position.RIGHT -> orientation = Orientation.RIGHT_LEFT
            Position.TOP -> orientation = Orientation.TOP_BOTTOM
        }
        return orientation
    }
    internal fun getShadow():Drawable {
        return shadow
    }
    enum class Position {
        CENTER,
        RIGHT,
        LEFT,
        TOP,
        BOTTOM
    }
}