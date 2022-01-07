package edu.neu.ecommerce.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import edu.neu.ecommerce.dto.PaymentInfo;
import edu.neu.ecommerce.dto.Purchase;
import edu.neu.ecommerce.dto.PurchaseResponse;

public interface CheckoutService {

  PurchaseResponse placeOrder(Purchase purchase);

  PaymentIntent createPaymentIntent(PaymentInfo paymentInfo) throws StripeException;
}
