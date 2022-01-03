package edu.neu.ecommerce.service.impl;

import edu.neu.ecommerce.dao.CustomerRepository;
import edu.neu.ecommerce.dto.Purchase;
import edu.neu.ecommerce.dto.PurchaseResponse;
import edu.neu.ecommerce.entity.Customer;
import edu.neu.ecommerce.entity.Order;
import edu.neu.ecommerce.entity.OrderItem;
import edu.neu.ecommerce.service.CheckoutService;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckoutServiceImpl implements CheckoutService {

  private final CustomerRepository customerRepository;

  public CheckoutServiceImpl(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
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
}
