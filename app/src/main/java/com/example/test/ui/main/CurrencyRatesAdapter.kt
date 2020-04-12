package com.example.test.ui.main

import android.content.Context
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.data.currency.model.CurrencyModel
import com.example.test.R
import kotlinx.android.synthetic.main.item_currency_rate.view.*
import java.util.Currency.getInstance


class CurrencyRatesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_BASE_CURRENCY = 1
    private val TYPE_CURRENCY = 2

    private var currencyRateList = mutableListOf<CurrencyModel>()
    private var listener: Listener? = null

    fun updateList(list: MutableList<CurrencyModel>?) {
        val diffResult = DiffUtil.calculateDiff(MainDiffCallback(currencyRateList, list))
        diffResult.dispatchUpdatesTo(this)
        currencyRateList.clear()
        list?.let {
            currencyRateList.addAll(it)
        }
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_BASE_CURRENCY
        } else {
            TYPE_CURRENCY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_currency_rate, parent, false
        )
        return if (viewType == TYPE_BASE_CURRENCY)
            BaseViewHolder(view)
        else
            ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BaseViewHolder -> holder.bindData(currencyRateList[position])
            is ViewHolder -> holder.bindData(currencyRateList[position])
        }
    }

    override fun getItemCount(): Int = currencyRateList.size


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindData(model: CurrencyModel) {
            setupView(model)
            setListeners()
        }

        private fun setupView(model: CurrencyModel) {
            with(itemView) {
                tv_short_currency_name.text = model.currency
                tv_currency_name.text = getInstance(model.currency).displayName
                et_rate.setText(model.rate.round())
                setImage(model.currency ?: "")
            }
        }

        private fun setImage(currency: String) {
            val imageRes = currency.getFlagResource(itemView.context)
            Glide.with(itemView.context)
                .load(imageRes)
                .placeholder(R.drawable.flag_placeholder)
                .into(itemView.img_flag)
        }

        private fun setListeners() {
            with(itemView.et_rate) {
                et_rate.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        currencyRateList.let {
                            val item = it.removeAt(adapterPosition)
                            listener?.onBaseCurrencyChanged(item.currency, item.rate)
                            it.add(0, item)
                            notifyItemMoved(adapterPosition, 0)
                        }
                    }
                    false
                }
            }
        }
    }

    inner class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(model: CurrencyModel) {
            setupView(model)
            setListener()
        }

        private fun setupView(model: CurrencyModel) {
            with(itemView) {
                tv_short_currency_name.text = model.currency
                tv_currency_name.text = getInstance(model.currency).currencyCode
                et_rate.maxLength(10)
                et_rate.setText(model.rate.round())
                setImage(model.currency ?: "")
            }
        }

        private fun setImage(currency: String) {
            val imageRes = currency.getFlagResource(itemView.context)
            Glide.with(itemView.context)
                .load(imageRes)
                .centerCrop()
                .placeholder(R.drawable.flag_placeholder)
                .into(itemView.img_flag)
        }

        private fun setListener() {
            itemView.et_rate.doOnTextChanged { text, _, _, _ ->
                val amount = if (text.isNullOrEmpty()) {
                    0f
                } else {
                    text.toString().toFloat()
                }
                listener?.onAmountEntered(amount)
            }
        }
    }

    interface Listener {
        fun onAmountEntered(amount: Float = 1f)
        fun onBaseCurrencyChanged(baseCurrency: String?, amount: Float = 1f)
    }
}

fun String?.getFlagResource(context: Context): Int {
    val flag = "flag_${this?.toLowerCase()}"
    return context.resources.getIdentifier(flag, "drawable", context.packageName)
}


fun Float?.round(decimals: Int = 2): String {
    return this?.let {
        "%.${decimals}f".format(this)
    } ?: ""
}

fun EditText.maxLength(length: Int) {
    filters = arrayOf(InputFilter.LengthFilter(length))
}