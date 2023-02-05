package com.oppwa.mobile.connect.demo.activity

import android.os.Bundle

import com.oppwa.mobile.connect.demo.R
import com.oppwa.mobile.connect.demo.common.Constants

import kotlinx.android.synthetic.main.activity_checkout_ui.*
import kotlinx.coroutines.ExperimentalCoroutinesApi


/**
 * Represents an activity for making payments via {@link CheckoutActivity}.
 */
class CheckoutUIActivity : BasePaymentActivity() {

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_checkout_ui)

        val amount = Constants.Config.AMOUNT + " " + Constants.Config.CURRENCY

        amount_text_view.text = amount
        progressBar = progress_bar_checkout_ui

        button_proceed_to_checkout.setOnClickListener {
            requestCheckoutId()
        }
    }

    override fun onCheckoutIdReceived(checkoutId: String?) {
        super.onCheckoutIdReceived(checkoutId)
        if (checkoutId != null) {
            openCheckoutUI(checkoutId)
        }
    }

    private fun openCheckoutUI(checkoutId: String) {
        val checkoutSettings = createCheckoutSettings(
                checkoutId,
                getString(R.string.checkout_ui_callback_scheme)
        )

        /* Start the checkout activity */
        checkoutLauncher.launch(checkoutSettings)
    }
}