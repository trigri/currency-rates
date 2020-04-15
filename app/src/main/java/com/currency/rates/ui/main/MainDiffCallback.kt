package com.currency.rates.ui.main

import androidx.recyclerview.widget.DiffUtil
import com.currency.rates.ui.main.MainViewModel.CurrencyItem

class MainDiffCallback(
    private val oldList: List<CurrencyItem>,
    private val newList: List<CurrencyItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].currency == newList[newItemPosition].currency
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].rate == newList[newItemPosition].rate
                && oldList[oldItemPosition].currency == newList[newItemPosition].currency
    }
}