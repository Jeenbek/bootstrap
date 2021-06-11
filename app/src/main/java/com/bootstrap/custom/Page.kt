package com.bootstrap.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.use
import androidx.core.view.children
import androidx.core.widget.TextViewCompat
import com.bootstrap.R
import com.bootstrap.databinding.LayoutPageBinding
import kotlin.properties.Delegates

class Page @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, null, defStyleAttr) {
    private val linearLayout = LinearLayout(context, attrs, 0)
    private var titleGravity = Gravity.CENTER
    private var showToolbar = true
    private var titleAppearance: Int = android.R.style.TextAppearance
    private var initialTitle: String? = null
    private var initialNavIcon: Int = R.drawable.ic_arrow_back
    private var initialNavWidth: Int = -1
    private var initialShowNavIcon = true
    private var initialTitleStartMargin = 0
    private var initialShowToolbarDivider = true
    private var wrapHeight = false

    private lateinit var binding: LayoutPageBinding

    var showNavIcon by Delegates.observable(initialShowNavIcon) { _, _, it ->
        binding.toolbar.apply {
            if (it) setNavigationIcon(initialNavIcon) else navigationIcon = null
        }
    }
    var title: String?
        get() = binding.toolbarTitle.text.toString()
        set(value) {
            binding.toolbarTitle.text = value
        }
    var navWidth: Int
        get() = binding.toolbar.children.find { it is AppCompatImageButton }?.minimumWidth
            ?: initialNavWidth
        set(value) {
            if (initialNavWidth < 0) return
            binding.toolbar.children.find { it is AppCompatImageButton }?.minimumWidth = value
        }
    var titleStartMargin: Int
        get() = binding.toolbar.contentInsetStartWithNavigation
        set(value) {
            binding.toolbar.contentInsetStartWithNavigation = value
        }
    var showToolbarDivider: Boolean
        get() = binding.toolbarDivider.isVisible
        set(value) {
            binding.toolbarDivider.isVisible = value
        }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.Page, defStyleAttr, 0)
            .use { it.use() }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val oldChildren = children.toList()
        val coordinatorChildren =
            (oldChildren.find { it.id == R.id.coordinator } as? CoordinatorLayout)?.run {
                children.toList().map {
                    removeView(it)
                    Pair(it, it.layoutParams as CoordinatorLayout.LayoutParams)
                }
            }
        oldChildren.forEach(::removeView)
        binding = LayoutPageBinding.inflate(LayoutInflater.from(context), this, true)
        id = linearLayout.id
        linearLayout.id = View.NO_ID
        val matchParent = ViewGroup.LayoutParams.MATCH_PARENT
        val wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT
        val heightParam = if (wrapHeight) wrapContent else matchParent
        val stubParams = ViewGroup.LayoutParams(matchParent, heightParam)
        if (wrapHeight) binding.nestedScrollView.updateLayoutParams<CoordinatorLayout.LayoutParams> {
            height = wrapContent
        }
        binding.nestedScrollView.addView(linearLayout, layoutParams ?: stubParams)
        oldChildren.filter { it.id != R.id.coordinator }
            .forEach { linearLayout.addView(it, it.layoutParams) }
        coordinatorChildren?.forEach { binding.coordinatorLayout.addView(it.first, it.second) }
        (context as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        binding.appbar.bringToFront()
        binding.toolbarTitle.updateLayoutParams<Toolbar.LayoutParams> { gravity = titleGravity }
        TextViewCompat.setTextAppearance(binding.toolbarTitle, titleAppearance)
        binding.toolbar.title = null
        binding.toolbar.isVisible = showToolbar
        title = initialTitle
        binding.toolbar.setNavigationIcon(initialNavIcon)
        showNavIcon = initialShowNavIcon
        navWidth = initialNavWidth
        titleStartMargin = initialTitleStartMargin
        showToolbarDivider = initialShowToolbarDivider
    }

    private fun TypedArray.use() {
        titleGravity = getInt(R.styleable.Page_titleGravity, titleGravity)
        showToolbar = getBoolean(R.styleable.Page_showToolbar, showToolbar)
        initialTitle = getString(R.styleable.Page_title)
        titleAppearance = getResourceId(R.styleable.Page_titleAppearance, titleAppearance)
        initialNavIcon = getResourceId(R.styleable.Page_navIcon, initialNavIcon)
        initialShowNavIcon = getBoolean(R.styleable.Page_showNavIcon, initialShowNavIcon)
        initialNavWidth = getDimensionPixelSize(R.styleable.Page_navWidth, initialNavWidth)
        initialShowToolbarDivider =
            getBoolean(R.styleable.Page_showToolbarDivider, initialShowToolbarDivider)
        initialTitleStartMargin =
            getDimensionPixelSize(R.styleable.Page_titleStartMargin, initialTitleStartMargin)
        wrapHeight = getBoolean(R.styleable.Page_wrapHeight, wrapHeight)
    }
}
