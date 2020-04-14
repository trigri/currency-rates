package com.example.test.ui.main

import androidx.recyclerview.widget.DiffUtil
import com.example.data.currency.model.CurrencyModel

class MainDiffCallback(
    private val oldList: List<CurrencyModel>,
    private val newList: List<CurrencyModel>
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