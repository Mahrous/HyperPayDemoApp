package com.oppwa.mobile.connect.demo.receiver

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.oppwa.mobile.connect.checkout.dialog.CheckoutActivity

class CheckoutBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, oldIntent: Intent?) {
        val action = oldIntent?.action

        if (CheckoutActivity.ACTION_ON_BEFORE_SUBMIT == action) {
            val paymentBrand = oldIntent.getStringExtra(CheckoutActivity.EXTRA_PAYMENT_BRAND)
            val checkoutId = oldIntent.getStringExtra(CheckoutActivity.EXTRA_CHECKOUT_ID)

            val senderComponentName: ComponentName? = oldIntent.getParcelableExtra(CheckoutActivity.EXTRA_SENDER_COMPONENT_NAME)

            /* This callback can be used to request a new checkout ID if selected payment brand requires
               some specific parameters or just send back the same checkout id to continue checkout process */
            val intent = Intent(CheckoutActivity.ACTION_ON_BEFORE_SUBMIT)
            intent.component = senderComponentName
            if (senderComponentName != null) {
                intent.setPackage(senderComponentName.packageName)
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(CheckoutActivity.EXTRA_CHECKOUT_ID, checkoutId)

            /* Also it can be used to cancel the checkout process by sending
               the CheckoutActivity.EXTRA_CANCEL_CHECKOUT */
            intent.putExtra(CheckoutActivity.EXTRA_TRANSACTION_ABORTED, false)

            context?.startActivity(intent)
        }
    }
}