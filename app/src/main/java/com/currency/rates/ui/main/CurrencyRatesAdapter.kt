package com.currency.rates.ui.main

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.currency.rates.R
import com.currency.rates.ui.main.MainViewModel.CurrencyItem
import com.currency.rates.utils.clearAndAddAll
import com.currency.rates.utils.getFlagResource
import com.currency.rates.utils.maxLength
import com.currency.rates.utils.round
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_currency_rate.view.*
import java.util.Currency.getInstance
import java.util.concurrent.TimeUnit


class CurrencyRatesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_BASE_CURRENCY = 1
    private val TYPE_CURRENCY = 2

    private var currencyRateList = mutableListOf<CurrencyItem>()
    private var listener: Listener? = null
    private var useDiffUtil = true

    fun updateList(list: List<CurrencyItem>) {
        if (useDiffUtil) {
            val diffResult = DiffUtil.calculateDiff(MainDiffCallback(currencyRateList, list))
            diffResult.dispatchUpdatesTo(this)
        } else {
            notifyItemRangeChanged(1, list.size - 1)
            useDiffUtil = true
        }
        currencyRateList.clearAndAddAll(list)
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

        fun bindData(model: CurrencyItem) {
            setupView(model)
            setListeners()
        }

        private fun setupView(model: CurrencyItem) {
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
                            it.add(0, item)
                            notifyItemMoved(adapterPosition, 0)
                            listener?.onBaseCurrencyChanged(item.currency, item.rate, it)
                        }
                    }
                    false
                }
            }
        }
    }

    inner class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(model: CurrencyItem) {
            setupView(model)
            setListener()
        }

        private fun setupView(model: CurrencyItem) {
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
                Observable.just(text.toString())
                    .debounce(200, TimeUnit.MILLISECONDS)
                    .subscribe {
                        val amount = if (text.isNullOrEmpty()) {
                            useDiffUtil = false
                            0f
                        } else {
                            text.toString().toFloat()
                        }
                        listener?.onAmountEntered(amount)
                    }
            }
        }
    }

    interface Listener {
        fun onAmountEntered(amount: Float = 1f)
        fun onBaseCurrencyChanged(
            baseCurrency: String?,
            amount: Float = 1f,
            list: List<CurrencyItem>
        )
    }
}