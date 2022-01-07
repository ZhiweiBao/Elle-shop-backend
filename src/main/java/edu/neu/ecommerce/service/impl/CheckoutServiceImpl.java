package edu.neu.ecommerce.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import edu.neu.ecommerce.dao.CustomerRepository;
import edu.neu.ecommerce.dto.PaymentInfo;
import edu.neu.ecommerce.dto.Purchase;
import edu.neu.ecommerce.dto.PurchaseResponse;
import edu.neu.ecommerce.entity.Customer;
import edu.neu.ecommerce.entity.Order;
import edu.neu.ecommerce.entity.OrderItem;
import edu.neu.ecommerce.service.CheckoutService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckoutServiceImpl implements CheckoutService {

  private final CustomerRepository customerRepository;

  public CheckoutServiceImpl(CustomerRepository customerRepository,
      @Value("${stripe.key.secret}") String secretKey) {
    this.customerRepository = customerRepository;
    Stripe.apiKey = secretKey;
  }

  @Override
  @Transactional
  public PurchaseResponse placeOrder(Purchase purchase) {
    // retrieve the order info from dto
    Order order = purchase.getOrder();
    // generate tracking number
    String orderTrackingNumber = generateOrderTrackingNumber();
    order.setOrderTrackingNumber(orderTrackingNumber);
    // populate order with orderItems
    Set<OrderItem> orderItems = purchase.getOrderItems();
    orderItems.forEach(order::add);
    // populate order with shippingAddress and billingAddress
    order.setShippingAddress(purchase.getShippingAddress());
    order.setBillingAddress(purchase.getBillingAddress());
    // populate customer with order
    Customer customer = purchase.getCustomer();
    // check if this is the existing customer
    String email = customer.getEmail();
    Customer customerFromDB = customerRepository.findByEmail(email);
    if (customerFromDB != null) {
      customer = customerFromDB;
    }
    customer.add(order);
    // save to the database
    customerRepository.save(customer);
    // return a response
    return new PurchaseResponse(orderTrackingNumber);
  }

  private String generateOrderTrackingNumber() {
    // generate a random UUID number (UUID version-4)
    return UUID.randomUUID().toString();
  }

  @Override
  public PaymentIntent createPaymentIntent(PaymentInfo paymentInfo) throws StripeException {
    List<String> paymentMethodTypes = new ArrayList<>();
    paymentMethodTypes.add("card");

    Map<String, Object> params = new HashMap<>();
    params.put("amount", paymentInfo.getAmount());
    params.put("currency", paymentInfo.getCurrency());
    params.put("payment_method_types", paymentMethodTypes);
    params.put("description", "Elle's Shop Purchase");
    params.put("receipt_email", paymentInfo.getReceiptEmail());

    return PaymentIntent.create(params);
  }
}
