package com.oppwa.mobile.connect.demo.activity

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle

import com.oppwa.mobile.connect.checkout.meta.CheckoutActivityResult
import com.oppwa.mobile.connect.checkout.meta.CheckoutActivityResultContract
import com.oppwa.mobile.connect.checkout.meta.CheckoutSettings
import com.oppwa.mobile.connect.checkout.meta.CheckoutSkipCVVMode
import com.oppwa.mobile.connect.demo.R
import com.oppwa.mobile.connect.demo.common.Constants
import com.oppwa.mobile.connect.demo.receiver.CheckoutBroadcastReceiver
import com.oppwa.mobile.connect.exception.PaymentError
import com.oppwa.mobile.connect.provider.Connect
import com.oppwa.mobile.connect.provider.Transaction
import com.oppwa.mobile.connect.provider.TransactionType
import com.oppwa.mobile.connect.utils.googlepay.CardPaymentMethodJsonBuilder
import com.oppwa.mobile.connect.utils.googlepay.PaymentDataRequestJsonBuilder
import com.oppwa.mobile.connect.utils.googlepay.TransactionInfoJsonBuilder
import com.oppwa.msa.MerchantServerApplication
import com.oppwa.msa.ServerMode

import kotlinx.coroutines.ExperimentalCoroutinesApi

import org.json.JSONArray


private const val STATE_RESOURCE_PATH = "STATE_RESOURCE_PATH"

/**
 * Represents a base activity for making the payments with mobile sdk.
 * This activity handles payment callbacks.
 */
open class BasePaymentActivity : BaseActivity() {

    protected val checkoutLauncher = registerForActivityResult(
            CheckoutActivityResultContract()) {
        result: CheckoutActivityResult -> this.handleCheckoutActivityResult(result)
    }

    protected var resourcePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            resourcePath = savedInstanceState.getString(STATE_RESOURCE_PATH)
        }
    }

    @ExperimentalCoroutinesApi
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent)

        /* Check of the intent contains the callback scheme */
        if (resourcePath != null && hasCallBackScheme(intent)) {
            requestPaymentStatus(resourcePath!!)
        }
    }

    private fun handleCheckoutActivityResult(result: CheckoutActivityResult) {
        hideProgressBar()

        if (result.isCanceled) {
            return
        }

        if (result.isErrored) {
            val error: PaymentError? = result.paymentError
            error?.let { showAlertDialog(it.errorMessage) }

            return
        }

        /* Transaction completed */
        val transaction: Transaction? = result.transaction

        resourcePath = result.resourcePath

        /* Check the transaction type */
        if (transaction != null) {
            if (transaction.transactionType == TransactionType.SYNC) {
                /* Check the status of synchronous transaction */
                requestPaymentStatus(resourcePath!!)
            } else {
                /* Asynchronous transaction is processed in the onNewIntent() */
            }
        }
    }

    open fun onCheckoutIdReceived(checkoutId: String?) {
        if (checkoutId == null) {
            hideProgressBar()
            showAlertDialog(R.string.error_message)
        }
    }

    protected fun requestCheckoutId() {
        showProgressBar()

        MerchantServerApplication.requestCheckoutId(
                Constants.Config.AMOUNT,
                Constants.Config.CURRENCY,
                "PA",
                ServerMode.TEST,
                mapOf("notificationUrl" to Constants.NOTIFICATION_URL)
        ) { checkoutId, _ -> runOnUiThread { onCheckoutIdReceived(checkoutId) } }
    }

    protected fun requestPaymentStatus(resourcePath: String) {
        showProgressBar()

        MerchantServerApplication.requestPaymentStatus(
                resourcePath
        ) { isSuccessful, _ -> runOnUiThread { onPaymentStatusReceived(isSuccessful) } }
    }

    protected fun createCheckoutSettings(checkoutId: String, callbackScheme: String): CheckoutSettings {
        return CheckoutSettings(checkoutId, Constants.Config.PAYMENT_BRANDS,
                Connect.ProviderMode.TEST)
                .setSkipCVVMode(CheckoutSkipCVVMode.FOR_STORED_CARDS)
                .setShopperResultUrl("$callbackScheme://callback")
                .setGooglePayPaymentDataRequestJson(getGooglePayPaymentDataRequestJson())
                /* Set componentName if you want to receive callbacks from the checkout */
                .setComponentName(ComponentName(packageName, CheckoutBroadcastReceiver::class.java.name))
    }

    private fun hasCallBackScheme(intent: Intent): Boolean {
        return intent.scheme == getString(R.string.checkout_ui_callback_scheme) ||
                intent.scheme == getString(R.string.payment_button_callback_scheme) ||
                intent.scheme == getString(R.string.custom_ui_callback_scheme)
    }

    private fun onPaymentStatusReceived(isSuccessful: Boolean) {
        hideProgressBar()
        val message = if (isSuccessful) {
            R.string.message_successful_payment
        } else {
            R.string.message_unsuccessful_payment
        }

        showAlertDialog(message)
    }

    private fun getGooglePayPaymentDataRequestJson() : String {
        val allowedPaymentMethods = JSONArray()
                .put(CardPaymentMethodJsonBuilder()
                        .setAllowedAuthMethods(JSONArray()
                                .put("PAN_ONLY")
                                .put("CRYPTOGRAM_3DS")
                        )
                        .setAllowedCardNetworks(JSONArray()
                                .put("VISA")
                                .put("MASTERCARD")
                                .put("AMEX")
                                .put("DISCOVER")
                                .put("JCB")
                        )
                        .setGatewayMerchantId(Constants.MERCHANT_ID)
                        .toJson()
                )

        val transactionInfo = TransactionInfoJsonBuilder()
                .setCurrencyCode(Constants.Config.CURRENCY)
                .setTotalPriceStatus("FINAL")
                .setTotalPrice(Constants.Config.AMOUNT)
                .toJson()

        val paymentDataRequest = PaymentDataRequestJsonBuilder()
                .setAllowedPaymentMethods(allowedPaymentMethods)
                .setTransactionInfo(transactionInfo)
                .toJson()

        return paymentDataRequest.toString()
    }
}