package edu.neu.ecommerce.dto;

import edu.neu.ecommerce.entity.Address;
import edu.neu.ecommerce.entity.Customer;
import edu.neu.ecommerce.entity.Order;
import edu.neu.ecommerce.entity.OrderItem;
import java.util.Set;
import lombok.Data;

@Data
public class Purchase {

  private Customer customer;
  private Address shippingAddress;
  private Address billingAddress;
  private Order order;
  private Set<OrderItem> orderItems;
}
