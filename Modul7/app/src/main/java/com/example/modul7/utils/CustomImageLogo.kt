package com.example.modul7.utils

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.example.modul7.R

class CustomImageLogo @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {
    private var lightModeLogo: Drawable
    private var darkModeLogo: Drawable

    init {
        lightModeLogo = ContextCompat.getDrawable(context, R.drawable.light_dicoding) as Drawable
        darkModeLogo = ContextCompat.getDrawable(context, R.drawable.dark_dicoding) as Drawable

        updateDrawable()
    }

    private fun updateDrawable() {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            setImageDrawable(darkModeLogo)
        } else {
            setImageDrawable(lightModeLogo)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        updateDrawable()
    }
}