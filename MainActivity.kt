package com.axiom.headlab

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.RectF
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : Activity() {
    private lateinit var preview: CharacterPreview
    private lateinit var valueText: TextView
    private var scale = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(9, 11, 19)
        window.navigationBarColor = Color.rgb(9, 11, 19)

        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(22), dp(22), dp(22), dp(18))
            setBackgroundColor(Color.rgb(9, 11, 19))
        }

        val brand = label("HEADLAB", 13f, Color.rgb(167, 139, 250), true)
        brand.letterSpacing = 0.18f
        page.addView(brand)

        val title = label("Character Lab", 27f, Color.WHITE, true)
        title.setPadding(0, dp(8), 0, dp(4))
        page.addView(title)
        page.addView(label("Kafa ölçeğini canlı önizlemede ayarla", 13f, 0xFF9CA3AF.toInt(), false))

        preview = CharacterPreview(this)
        page.addView(preview, LinearLayout.LayoutParams(-1, 0, 1f).apply {
            topMargin = dp(18); bottomMargin = dp(18)
        })

        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(17), dp(18), dp(16))
            background = rounded(0xFF151827.toInt(), 22)
        }
        val row = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
            orientation = LinearLayout.HORIZONTAL
        }
        row.addView(label("HEAD SIZE", 12f, 0xFFB8B9C8.toInt(), true),
            LinearLayout.LayoutParams(0, -2, 1f))
        valueText = label("100%", 16f, Color.WHITE, true)
        row.addView(valueText)
        panel.addView(row)

        val slider = SeekBar(this).apply {
            max = 150
            progress = 50
            progressTintList = android.content.res.ColorStateList.valueOf(0xFF9B7BFF.toInt())
            thumbTintList = android.content.res.ColorStateList.valueOf(Color.WHITE)
        }
        slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                scale = 0.5f + progress / 100f
                valueText.text = "${(scale * 100).toInt()}%"
                preview.headScale = scale
                preview.invalidate()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        panel.addView(slider)
        page.addView(panel, LinearLayout.LayoutParams(-1, -2))

        val launch = Button(this).apply {
            text = "STANDOFF 2'Yİ AÇ"
            isAllCaps = false
            textSize = 15f
            setTextColor(Color.WHITE)
            background = rounded(0xFF7654E8.toInt(), 16)
            setPadding(dp(12), dp(14), dp(12), dp(14))
            setOnClickListener {
                val intent = packageManager.getLaunchIntentForPackage("com.axlebolt.standoff2")
                if (intent != null) startActivity(intent)
                else Toast.makeText(this@MainActivity, "Standoff 2 cihazda bulunamadı.", Toast.LENGTH_LONG).show()
            }
        }
        page.addView(launch, LinearLayout.LayoutParams(-1, dp(54)).apply { topMargin = dp(16) })

        val note = label("Önizleme efekti yalnızca bu uygulamada çalışır; oyunun belleğini veya diğer oyuncuları değiştirmez.", 11f, 0xFF85899B.toInt(), false)
        note.gravity = Gravity.CENTER
        note.setPadding(dp(8), dp(12), dp(8), 0)
        page.addView(note)
        setContentView(page)
    }

    private fun label(text: String, size: Float, color: Int, bold: Boolean) =
        TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(color)
            typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }

    private fun rounded(color: Int, radius: Int) = android.graphics.drawable.GradientDrawable().apply {
        setColor(color); cornerRadius = dp(radius).toFloat()
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}

private class CharacterPreview(context: android.content.Context) : View(context) {
    var headScale = 1f
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        p.color = 0xFF111626.toInt()
        canvas.drawRoundRect(RectF(0f, 0f, w, h), 28f, 28f, p)

        // Soft floor glow
        p.color = 0x337B61E8
        canvas.drawOval(w * .18f, h * .78f, w * .82f, h * .94f, p)

        val cx = w / 2f
        val headY = h * .34f
        val headR = h * .105f * headScale

        // Body and shoulders
        p.color = 0xFF303B58.toInt()
        canvas.drawRoundRect(RectF(cx - w*.19f, headY + h*.12f, cx + w*.19f, h*.82f), 35f, 35f, p)
        p.color = 0xFF46577C.toInt()
        canvas.drawRoundRect(RectF(cx - w*.29f, headY + h*.17f, cx + w*.29f, headY + h*.43f), 35f, 35f, p)

        // Neck
        p.color = 0xFFB98268.toInt()
        canvas.drawRoundRect(RectF(cx - w*.055f, headY + headR*.65f, cx + w*.055f, headY + h*.20f), 12f, 12f, p)

        // Head
        p.color = 0xFFD6A184.toInt()
        canvas.drawCircle(cx, headY, headR, p)
        // Hair
        p.color = 0xFF28212A.toInt()
        canvas.drawArc(RectF(cx-headR, headY-headR, cx+headR, headY+headR), 180f, 180f, true, p)
        // Eyes
        p.color = 0xFF25202A.toInt()
        canvas.drawCircle(cx-headR*.36f, headY, maxOf(2f, headR*.07f), p)
        canvas.drawCircle(cx+headR*.36f, headY, maxOf(2f, headR*.07f), p)

        p.color = 0xFFE5E7EB.toInt()
        p.textAlign = Paint.Align.CENTER
        p.textSize = 13f * resources.displayMetrics.density
        canvas.drawText("LIVE PREVIEW", cx, h*.92f, p)
    }
}
