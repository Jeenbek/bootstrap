package com.bootstrap.custom

import android.content.Context
import android.content.res.TypedArray
import android.text.InputType
import android.util.AttributeSet
import android.view.Gravity
import android.view.View.OnFocusChangeListener
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.view.contains
import androidx.core.view.updateMargins
import androidx.core.view.updatePaddingRelative
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.bootstrap.R
import com.bootstrap.extensions.*

class Input @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.textInputStyle
) : TextInputLayout(context, attrs, defStyleAttr) {
    private val labelView by lazy {
        TextView(context).apply {
            setTextAppearance(context, R.style.Text_Regular)
            val textColor = ContextCompat.getColor(context, R.color.black)
            setTextColor(textColor)
        }
    }
    private var labelBottomMargin = 0
    private var isRequired = false
    private var gravityCenter = false
    private var dropdown = false
    var label: String?
        set(value) {
            val wrap = LayoutParams.WRAP_CONTENT
            val lp = LayoutParams(wrap, wrap).apply { updateMargins(bottom = labelBottomMargin) }
            if (value != null && !contains(labelView)) addView(labelView, 0, lp)
            if (value == null && contains(labelView)) removeView(labelView)
            if (value != null && contains(labelView)) labelView.text = buildSpannedString {
                append(value)
                if (!isRequired) return@buildSpannedString
                val color = ContextCompat.getColor(context, R.color.black)
                color(color) { append(" (required)") }
            }
        }
        get() = labelView.text?.toString()

    private var hint: String?
        set(value) = editText?.setHint(value) ?: Unit
        get() = editText?.hint?.toString()

    var text: String?
        set(value) = editText?.setText(value) ?: Unit
        get() = editText?.text?.toString()

    private val hideKeyboardOnDropDownFocus = OnFocusChangeListener { v, hasFocus ->
        val dropDownView = v as? AutoCompleteTextView
        if (hasFocus && dropdown && dropDownView != null) {
            dropDownView.showDropDown()
            performClick()
            hideKeyboard()
        }
    }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.Input, defStyleAttr, 0)
            .use { it.init() }
    }

    private fun TypedArray.init() {
        dropdown = getBoolean(R.styleable.Input_dropdown, false)
        val wrap = LayoutParams.WRAP_CONTENT
        var editTextHeight = getDimensionPixelSize(R.styleable.Input_editText_height, wrap)
        val lines = getInt(R.styleable.Input_editText_lines, 1)
        if (lines > 1) editTextHeight = wrap
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, editTextHeight)
        val autoStyle by lazy { R.attr.autoCompleteTextViewStyle }
        val autoView by lazy { AutoCompleteTextView(context, null, autoStyle) }
        val editText = if (dropdown) autoView else TextInputEditText(context)
        editText.gravity = Gravity.START or Gravity.CENTER_VERTICAL

        editText.startPadding = 16.pxToDp
        gravityCenter = getBoolean(R.styleable.Input_gravityCenter, false)
        if (gravityCenter) {
            editText.gravity = Gravity.CENTER
            editText.startPadding = 0.pxToDp
        }
        editText.setLines(lines)
        editText.maxLength = getInt(R.styleable.Input_editText_maxLength, 1000000)
        addView(editText, lp)
        labelBottomMargin = getDimension(R.styleable.Input_label_margin_bottom, 0f).toInt()
        isRequired = getBoolean(R.styleable.Input_required, false)
        label = getString(R.styleable.Input_label)
        editText.textSize = getFloat(R.styleable.Input_editText_textSize, 15f)
        editText.setHintTextColor(ContextCompat.getColor(context, R.color.black))
        hint = getString(R.styleable.Input_hint)
        val inputNone = InputType.TYPE_NULL
        val inputType = getInt(R.styleable.Input_inputType, inputNone)
        this@Input.editText?.inputType = if (dropdown) inputNone else inputType
        onFocusChangeListener = hideKeyboardOnDropDownFocus
    }

    fun <T> setDropdown(
        items: List<T>,
        onClick: (T) -> Unit,
        toString: (T) -> String,
        bind: TextView.(T) -> Unit,
        bindIcon: ImageView.(T) -> Unit = {}
    ) {
        val autoCompleteTextView = editText as? AutoCompleteTextView ?: return
        val itemLayout = R.layout.item_dropdown
        val textKey = "text"
        val iconKey = "icon"
        val data = items.map { mapOf(textKey to it, iconKey to it) }
        val from = arrayOf(textKey, iconKey)
        val to = intArrayOf(android.R.id.text2, android.R.id.icon)
        var isOpen = false
        var onOpen = {}
        val adapter = object : SimpleAdapter(context, data, itemLayout, from, to) {
            override fun getItem(position: Int): Any {
                if (!isOpen) {
                    isOpen = true
                    onOpen()
                }
                return toString(items[position])
            }
        }
        adapter.setViewBinder { view, data, _ ->
            (data as? T)?.let { (view as? TextView)?.bind(it) }
            (data as? T)?.let { (view as? ImageView)?.bindIcon(it) }
            true
        }
        autoCompleteTextView.apply {
            setAdapter(adapter)
            setDropDownBackgroundResource(R.drawable.background_design_corner)
            setOnItemClickListener { _, _, position, _ -> onClick(items[position]) }
            setOnDismissListener { isOpen = false }
            onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
                val dropDownView = v as? AutoCompleteTextView
                if (hasFocus && dropdown && dropDownView != null) {
                    dropDownView.showDropDown()
                    performClick()
                    hideKeyboard()
                }
            }
            onOpen = {
                handler.post {
                    if (!isPopupShowing) return@post
                    useField<ListPopupWindow> { it.listView?.clip(R.drawable.background_design_corner) }
                }
            }
        }
    }

    override fun setError(errorText: CharSequence?) {
        val oldPaddingStart = editText?.paddingStart ?: 0
        editText?.updatePaddingRelative(start = 0)
        super.setError(errorText)
        editText?.updatePaddingRelative(start = oldPaddingStart)
    }
}