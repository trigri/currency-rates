package com.currency.rates.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import com.currency.rates.R
import com.currency.rates.ui.base.BaseActivity
import com.currency.rates.ui.main.MainViewModel.*
import com.currency.rates.utils.observeViewModel
import kotlinx.android.synthetic.main.activity_main.*
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
        rc_currency_rates.itemAnimator = DefaultItemAnimator()
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
            observeViewModel(loading, ::onLoading)
            observeViewModel(error, ::onError)
        }
    }

    private fun setListeners() {
        currencyAdapter.setListener(object : CurrencyRatesAdapter.Listener {
            override fun onAmountEntered(amount: Float) {
                viewModel.onBaseAmountChanged(amount)
            }

            override fun onBaseCurrencyChanged(
                baseCurrency: String?,
                amount: Float,
                list: List<CurrencyItem>
            ) {
                viewModel.onBaseCurrencyChanged(baseCurrency, amount, list)
            }
        })
    }


    private fun onCurrencyRates(currencyRateModel: MutableList<CurrencyItem>?) {
        currencyRateModel?.let {
            currencyAdapter.updateList(it)
        }
    }

    private fun onError(error: String?) {
        Toast.makeText(this, error.orEmpty(), Toast.LENGTH_SHORT).show()
    }

    private fun onLoading(loading: Boolean?) {
        if (loading == true) {
            showProgressBar()
        } else {
            hideProgressBar()
        }
    }

    private fun showProgressBar() {
        progress_circular.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progress_circular?.visibility = View.GONE
    }

    private fun getViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }
}
