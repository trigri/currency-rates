package com.example.test.ui.main

import androidx.recyclerview.widget.DiffUtil
import com.example.data.currency.model.CurrencyModel

class MainDiffCallback(
    private val oldList: List<CurrencyModel>? = null,
    private val newList: List<CurrencyModel>? = null
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList?.size ?: 0
    override fun getNewListSize(): Int = newList?.size ?: 0

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList?.get(oldItemPosition)?.currency == newList?.get(newItemPosition)?.currency
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList?.get(oldItemPosition)?.rate == newList?.get(newItemPosition)?.rate
                && oldList?.get(oldItemPosition)?.currency == newList?.get(newItemPosition)?.currency
    }
}