package com.example.test.ui.main

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.data.currency.model.CurrencyModel
import com.example.data.currency.model.CurrencyRateModel
import com.example.test.R
import com.example.test.ui.base.BaseActivity
import com.example.test.utils.observeViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel

    private val currencyAdapter = CurrencyRatesAdapter()

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getViewModel()
        setListeners()
        observerViewModel()
        rc_currency_rates.adapter = currencyAdapter
//        rc_currency_rates.itemAnimator = SlideUpItemAnimator()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onStop() {
        viewModel.onStop()
        super.onStop()
    }

    private fun observerViewModel() {
        with(viewModel) {
            observeViewModel(currencyRates, ::onCurrencyRates)
        }
    }

    private fun setListeners() {
        currencyAdapter.setListener(object : CurrencyRatesAdapter.Listener {
            override fun onAmountEntered(amount: Float) {
                viewModel.onBaseAmountChanged(amount)
            }

            override fun onBaseCurrencyChanged(baseCurrency: String?, amount: Float) {
                viewModel.onBaseCurrencyChanged(baseCurrency, amount)
            }
        })
    }


    private fun onCurrencyRates(currencyRateModel: MutableList<CurrencyModel>?) {
        currencyRateModel?.let {
            currencyAdapter.updateList(it)
        }
    }

    private fun getViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }
}
