package com.example.test.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
                list: List<CurrencyModel>
            ) {
                viewModel.onBaseCurrencyChanged(baseCurrency, amount, list)
            }
        })
    }


    private fun onCurrencyRates(currencyRateModel: MutableList<CurrencyModel>?) {
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
