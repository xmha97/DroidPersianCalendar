package com.byagowi.persiancalendar.ui.calendar.times

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.withRotation
import com.byagowi.persiancalendar.entities.Jdn
import com.byagowi.persiancalendar.global.coordinates
import com.byagowi.persiancalendar.global.mainCalendar
import com.byagowi.persiancalendar.ui.shared.SolarDraw
import com.byagowi.persiancalendar.ui.utils.dp
import com.byagowi.persiancalendar.utils.Eclipse
import com.byagowi.persiancalendar.utils.calculateSunMoonPosition
import com.byagowi.persiancalendar.utils.formatDate
import com.byagowi.persiancalendar.utils.toCivilDate
import com.byagowi.persiancalendar.utils.toJavaCalendar
import com.google.android.material.slider.Slider
import java.util.*
import kotlin.math.min

// This is demo only specially as it doesn't work correctly right now
class SolarDemoView(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        addView(TextView(context).also { v ->
            v.textAlignment = View.TEXT_ALIGNMENT_CENTER
            v.text = listOf(Eclipse.Category.SOLAR, Eclipse.Category.LUNAR).joinToString("\n\n") {
                val eclipse = Eclipse(GregorianCalendar(), it, true)
                val date = Jdn(eclipse.maxPhaseDate.toJavaCalendar().toCivilDate())
                "$it\n${formatDate(date.toCalendar(mainCalendar))}\n${eclipse.type}"
            }
        })
        val solarView = object : View(context) {
            init {
                layoutParams = ViewGroup.LayoutParams(200.dp.toInt(), 200.dp.toInt())
            }

            var dayOffset = 0f

            override fun onDraw(canvas: Canvas?) {
                super.onDraw(canvas)
                canvas?.drawSolarSystem(min(width, height) / 2f, dayOffset)
            }
        }
        addView(Slider(context).apply {
            valueFrom = 0f
            valueTo = 365f
            this.addOnChangeListener { slider, value, fromUser ->
                solarView.dayOffset = value
                solarView.postInvalidate()
            }
        })
        addView(solarView)
    }

    private val earthPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.style = Paint.Style.FILL
        it.color = 0xFF6981E7.toInt()
    }
    private val solarDraw = SolarDraw(context)

    private fun Canvas.drawSolarSystem(radius: Float, dayOffset: Float) {
        val sunMoonPosition = coordinates?.calculateSunMoonPosition(
            GregorianCalendar().also { it.add(Calendar.HOUR, (dayOffset * 24f).toInt()) }
        ) ?: return
        val sunDegree = sunMoonPosition.sunEcliptic.λ.toFloat()
        val moonDegree = sunMoonPosition.moonEcliptic.λ.toFloat()
        val cr = radius / 8
        solarDraw.sun(this, radius, radius, cr)
        withRotation(pivotX = radius, pivotY = radius, degrees = -sunDegree) {
            val earthCy = cr * 3f
            drawCircle(radius, earthCy, cr / 1.2f, earthPaint)
            withRotation(pivotX = radius, pivotY = earthCy, degrees = moonDegree + sunDegree) {
                solarDraw.moon(this, sunMoonPosition, radius, earthCy - cr * 2f, cr / 1.7f)
            }
        }
    }
}