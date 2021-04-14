package com.bootstrap.custom

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.bootstrap.R
import com.bootstrap.extensions.hide
import com.bootstrap.extensions.onClick
import com.bootstrap.extensions.show

class LoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val progress_bar by lazy { findViewById<ProgressBar>(R.id.progress_bar) }

    init {
        inflate(context, R.layout.loading_view, this)
        onClick {}
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        when (visibility) {
            VISIBLE -> {
                progress_bar.hide()
                Handler().postDelayed({
                    progress_bar.show()
                }, 1500)
            }
            GONE -> {
                progress_bar.hide()
            }
        }
    }
}