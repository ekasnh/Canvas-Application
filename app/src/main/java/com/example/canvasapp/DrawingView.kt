//in this file data about the thickness of the brush, color and components are being defined
package com.example.canvasapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View


class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var mDrawPath: CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var canvas: Canvas? = null
    // starting declaration of the brush
    private var mBrushSize: Float = 0.toFloat()
    private var color = Color.BLACK
    private var mPaths = ArrayList<CustomPath>()
    private val mUndoPaths = ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }
    //for setting the initial values of the null objects above created
    private fun setUpDrawing() {
        mDrawPaint= Paint()
        mDrawPath = CustomPath(color,mBrushSize)
        mDrawPaint!!.color=color
        mDrawPaint!!.style= Paint.Style.STROKE
        mDrawPaint!!.strokeJoin= Paint.Join.ROUND
        mDrawPaint!!.strokeCap=Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        mBrushSize= 20.toFloat()

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!,0f,0f,mCanvasPaint)

        for (p in mPaths) {
            mDrawPaint!!.strokeWidth = p.brushThickness
            mDrawPaint!!.color = p.color
            canvas.drawPath(p, mDrawPaint!!)
        }

        if(!mDrawPath!!.isEmpty){
            mDrawPaint!!.strokeWidth= mDrawPath!!.brushThickness
            canvas.drawPath(mDrawPath!!,mDrawPaint!!)
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x // Touch event of X coordinate
        val touchY = event.y // touch event of Y coordinate

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize

                mDrawPath!!.reset() // Clear any lines and curves from the path, making it empty.
                mDrawPath!!.moveTo(
                    touchX,
                    touchY
                ) // Set the beginning of the next contour to the point (x,y).
            }

            MotionEvent.ACTION_MOVE -> {
                mDrawPath!!.lineTo(
                    touchX,
                    touchY
                ) // Add a line from the last point to the specified point (x,y).
            }

            MotionEvent.ACTION_UP -> {

                mPaths.add(mDrawPath!!) //Add when to stroke is drawn to canvas and added in the path arraylist

                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }

        invalidate()
        return true
    }

    fun setSizeForBrush(newSize: Float) {
        mBrushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, newSize,
            resources.displayMetrics
        )
        mDrawPaint!!.strokeWidth = mBrushSize
    }


    fun setColor(newColor: String) {
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }

    fun onClickUndo() {
        if (mPaths.size > 0) {

            mUndoPaths.add(mPaths.removeAt(mPaths.size - 1))
            invalidate() // Invalidate the whole view. If the view is visible
        }
    }
    //NOTE-: here internal inner class refers to the class that can only be used with in this file
    internal inner class CustomPath(var color: Int,
                                    var brushThickness:Float): Path(){

    }
}