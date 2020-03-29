package com.birdeveloper.specialview
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.ScaleAnimation
import android.widget.RelativeLayout
/**
 * This layout will help you a lot while designing!
 * Bu layout size tasarım yaparken çok yardımcı olacak!
 * for more: https://github.com/birdeveloper
 * @author birdeveloper - Görkem KARA
 */
class SpecialLayout:RelativeLayout, View.OnClickListener, Animation.AnimationListener {
    var amplitude:Float = 0.toFloat()
    private var pClick:OnClickListener? = null
    var frequency:Float = 0.toFloat()
    private var init = true
    private var animation: ScaleAnimation? = null
    var animationDuration:Int = 0
    var toXScale:Float = 0.toFloat()
    private var fromXScale:Float = 0.toFloat()
    var toYScale:Float = 0.toFloat()
    private var fromYScale:Float = 0.toFloat()
    private var pivotX:Float? = 0.toFloat()
    private var pivotY:Float? = 0.toFloat()
    var onclickColor:Int = 0
    private var color:Int = 0
    private var gd = GradientDrawable()
    var radius:Float = 0.toFloat()
        set(radius) {
            field = radius
            gd.cornerRadius = radius
        }
    var topRightRadius:Float = 0.toFloat()
    var topLeftRadius:Float = 0.toFloat()
    var bottomRightRadius:Float = 0.toFloat()
    var bottomLeftRadius:Float = 0.toFloat()
    private var shape:Int = 0
    private var view:View? = null
    private var animate:Boolean = false
    private var colors = IntArray(3)
    var isClickTransferable:Boolean = false
    var isClickAfterAnimation:Boolean = false
    private val main:RelativeLayout? = null
    var gradientType = "linear"
        set(gradientType) {
            var gradient = GradientDrawable.LINEAR_GRADIENT
            when (gradientType) {
                "sweep" -> gradient = GradientDrawable.SWEEP_GRADIENT
                "radial" -> gradient = GradientDrawable.RADIAL_GRADIENT
            }
            field = gradientType
            gd.gradientType = gradient
        }
    var gradientAngle = "TOP_BOTTOM"
        private set(gradientAngle) {
            field = gradientAngle
            gd.orientation = GradientDrawable.Orientation.valueOf(gradientAngle.toUpperCase())
        }
    private var interpolate:Boolean = false
    var isSelfClickable:Boolean = false
    private var fromChild:Boolean = false
    var interpolator:Interpolator? = null
    private var first = true
    var shadow:Shadow? = null
        set(shadow) {
            background = shadow!!.shadow
            field = shadow
        }
    private var shadowPosition = Shadow.Position.CENTER
    constructor(context: Context, shadow: Shadow) : super(context) {
        background = gd
        this.shadow = shadow
    }
    constructor(context:Context, attrs:AttributeSet) : super(context, attrs) {
        init(attrs)
    }
    private fun init(set:AttributeSet) {
        val ta = context.obtainStyledAttributes(set, R.styleable.SpecialLayout)
        val shadow = ta.getBoolean(R.styleable.SpecialLayout_shadow, false)
        var shadowColor = ta.getString(R.styleable.SpecialLayout_shadowColor)
        if (shadowColor == null)
        {
            shadowColor = "000000"
        }
        isClickTransferable = ta.getBoolean(R.styleable.SpecialLayout_clickTransferable, false)
        isSelfClickable = ta.getBoolean(R.styleable.SpecialLayout_selfClickable, true)
        if (ta.getString(R.styleable.SpecialLayout_gradientAngle) != null){
            gradientAngle = ta.getString(R.styleable.SpecialLayout_gradientAngle)!!
        }

        isClickAfterAnimation = ta.getBoolean(R.styleable.SpecialLayout_clickAfterAnimation, true)
        if (this.gradientAngle == null) gradientAngle = "TOP_BOTTOM"
        val gradientCenterColor = ta.getColor(R.styleable.SpecialLayout_gradientCenterColor, 0)
        val gradientEndColor = ta.getInteger(R.styleable.SpecialLayout_gradientEndColor, 0)
        val gradientStartColor = ta.getInt(R.styleable.SpecialLayout_gradientStartColor, 0)
        colors[0] = gradientStartColor
        colors[1] = gradientCenterColor
        colors[2] = gradientEndColor
        if (ta.getString(R.styleable.SpecialLayout_gradientType) != null){
            gradientType = ta.getString(R.styleable.SpecialLayout_gradientType)!!
        }
        if (this.gradientType == null)
        {
            gradientType = "linear"
        }
        val spread = ta.getInt(R.styleable.SpecialLayout_shadowSpread, 1)
        val shadowAlpha = ta.getInt(R.styleable.SpecialLayout_shadowAlpha, 255)
        animationDuration = ta.getInteger(R.styleable.SpecialLayout_animationDuration, 2000)
        toXScale = ta.getFloat(R.styleable.SpecialLayout_toXScale, 1.0f)
        fromXScale = ta.getFloat(R.styleable.SpecialLayout_fromXScale, .3f)
        toYScale = ta.getFloat(R.styleable.SpecialLayout_toYScale, 1.0f)
        fromYScale = ta.getFloat(R.styleable.SpecialLayout_fromYScale, .3f)
        pivotX = ta.getFloat(R.styleable.SpecialLayout_pX, -1f)
        pivotY = ta.getFloat(R.styleable.SpecialLayout_pY, -1f)
        animate = ta.getBoolean(R.styleable.SpecialLayout_animate, false)
        radius = ta.getDimension(R.styleable.SpecialLayout_radius, 0f)
        topRightRadius = ta.getDimension(R.styleable.SpecialLayout_topRightRadius, 0f)
        topLeftRadius = ta.getDimension(R.styleable.SpecialLayout_topLeftRadius, 0f)
        bottomRightRadius = ta.getDimension(R.styleable.SpecialLayout_bottomRightRadius, 0f)
        bottomLeftRadius = ta.getDimension(R.styleable.SpecialLayout_bottomLeftRadius, 0f)
        color = ta.getColor(R.styleable.SpecialLayout_color, android.R.attr.colorBackground)
        onclickColor = ta.getColor(R.styleable.SpecialLayout_onclickColor, -1)
        interpolate = ta.getBoolean(R.styleable.SpecialLayout_interpolate, false)
        amplitude = ta.getFloat(R.styleable.SpecialLayout_amplitude, 1.0f)
        frequency = ta.getFloat(R.styleable.SpecialLayout_frequency, .3f)
        val sh = ta.getString(R.styleable.SpecialLayout_shape)
        viewTreeObserver.addOnGlobalLayoutListener {
            if (first && animate) {
                animation = ScaleAnimation(fromXScale, toXScale, fromYScale, toYScale,
                    (if (pivotX == -1f) width as Float / 2 else pivotX)!!, (if (pivotY == -1f) height as Float / 2 else pivotY)!!
                )
                animation!!.duration = animationDuration.toLong()
                animation!!.setAnimationListener(this@SpecialLayout)
                if (interpolate) {
                    val interpolator = DefaultInterpolator()
                    animation!!.interpolator = interpolator
                }
                first = false
            }
        }
        if (sh != null)
        {
            when (sh) {
                "oval" -> shape = GradientDrawable.OVAL
                "ring" -> shape = GradientDrawable.RING
                "line" -> shape = GradientDrawable.LINE
            }
        }
        val sPosition = ta.getString(R.styleable.SpecialLayout_shadowPosition)
        if (sPosition != null)
        {
            when (sPosition) {
                "top" -> shadowPosition = Shadow.Position.TOP
                "left" -> shadowPosition = Shadow.Position.LEFT
                "right" -> shadowPosition = Shadow.Position.RIGHT
                "bottom" -> shadowPosition = Shadow.Position.BOTTOM
            }
        }
        setShape(shape)
        if (!isEmpty(colors))
        {
            gd.colors = colors
        }
        else
        {
            gd.setColor(color)
        }
        gradientType = this.gradientType
        gradientAngle = this.gradientAngle
        val initRad = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        val radius = floatArrayOf(topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius)
        val radToSet = if (this.radius == 0f) radius else initRad
        setCornerRadii(radToSet)
        if (shadow)
        {
            this.shadow = Shadow(spread, shadowAlpha, shadowColor, shape, radToSet, shadowPosition)
            background = this.shadow!!.shadow

        }
        else
            background = gd
        setOnClickListener(this)
        ta.recycle()
    }
    fun setShape(shape:Int) {
        gd.shape = shape
    }
    fun setFromXScale(fromXScale:Float) {
        this.fromXScale = fromXScale
    }
    fun setFromYScale(fromYScale:Float) {
        this.fromYScale = fromYScale
    }
    fun setGradientColor(colors:IntArray) {
        gd.colors = colors
    }
    override fun setBackgroundResource(resid:Int) {
        throw RuntimeException("setBackgroundResource not supported in SpecialLayout")
    }
    private fun isEmpty(ints:IntArray):Boolean {
        for (i in ints)
        {
            if (i != 0) return false
        }
        return true
    }
    fun setColor(color:Int) {
        gd.setColor(color)
    }
    fun setCornerRadii(radii:FloatArray) {
        gd.cornerRadii = radii
    }
    fun setInterpolate(interpolate:Boolean) {
        this.interpolate = interpolate
    }

    override fun setOnClickListener(l: OnClickListener?) {
        if (init)
        {
            super.setOnClickListener(l)
            init = false
            return
        }
        pClick = l
    }
    override fun onClick(v:View) {
        if (!isSelfClickable && !fromChild)
        {
            return
        }
        val parent = parent
        if (parent is SpecialLayout && isClickTransferable)
        {
            val SpecialLayout = parent
            SpecialLayout.fromChild = true
            SpecialLayout.onClick(SpecialLayout)
        }
        if (onclickColor != -1)
        {
            gd.setColor(onclickColor)
        }
        if (animate)
        {
            if (!isClickAfterAnimation)
            {
                if (pClick != null) pClick!!.onClick(v)
            }
            startAnimation(animation)
            return
        }
        if (pClick != null) pClick!!.onClick(v)
        fromChild = false
    }
    fun startAnimation() {
        startAnimation(animation)
    }
    override fun setBackgroundColor(color:Int) {
        throw RuntimeException("setBackgroundColor not supported!")
    }
    override fun onAnimationStart(animation:Animation) {
    }
    override fun onAnimationEnd(animation:Animation) {
        gd.setColor(color)
        if (isClickAfterAnimation && pClick != null)
        {
            pClick!!.onClick(view)
        }
    }
    override fun onAnimationRepeat(animation:Animation) {
    }
    private inner class DefaultInterpolator:Interpolator {
        override fun getInterpolation(time:Float):Float {
            return (-1.0 * Math.pow(Math.E, (-time / amplitude).toDouble()) * Math.cos((frequency * time).toDouble()) + 1).toFloat()
        }
    }
}