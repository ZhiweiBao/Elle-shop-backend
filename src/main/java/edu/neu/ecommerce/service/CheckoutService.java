package edu.neu.ecommerce.service;

import edu.neu.ecommerce.dto.Purchase;
import edu.neu.ecommerce.dto.PurchaseResponse;

public interface CheckoutService {

  PurchaseResponse placeOrder(Purchase purchase);
}
