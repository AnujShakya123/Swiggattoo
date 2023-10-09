package com.example.Swiggato.service;

import com.example.Swiggato.dto.response.OrderResponse;
import com.example.Swiggato.exception.CustomerNotFoundException;
import com.example.Swiggato.exception.EmptyCartException;
import com.example.Swiggato.model.*;
import com.example.Swiggato.repository.CustomerRepository;
import com.example.Swiggato.repository.DeliveryPartnerRepository;
import com.example.Swiggato.repository.OrderEntityRepository;
import com.example.Swiggato.repository.RestaurantRepository;
import com.example.Swiggato.transformer.OrderTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
//@Service
//public class OrderService {
//
//    final CustomerRepository customerRepository;
//
//    final OrderEntityRepository orderEntityRepository;
//
//    final DeliveryPartnerRepository deliveryPartnerRepository;
//
//   private final RestaurantRepository restaurantRepository;
//    @Autowired
//    public OrderService(CustomerRepository customerRepository, OrderEntityRepository orderEntityRepository, DeliveryPartnerRepository deliveryPartnerRepository, RestaurantRepository restaurantRepository) {
//        this.customerRepository = customerRepository;
//        this.orderEntityRepository = orderEntityRepository;
//        this.deliveryPartnerRepository = deliveryPartnerRepository;
//        this.restaurantRepository = restaurantRepository;
//    }
//
//    public OrderResponse placeOrder(String customerMobile) {
//        Customer customer=customerRepository.findByMobileNo(customerMobile);
//        if(customer==null){
//            throw new CustomerNotFoundException("Invalid Mobile Number!!!");
//        }
//        Cart cart=customer.getCart();
//        if(cart.getFoodItems().size()==0){
//            throw new EmptyCartException("Sorry! your cart is empty!!!");
//        }
//
//        DeliveryPartner partner=deliveryPartnerRepository.findRandomDeliverPartner();
//        Restaurant restaurant= cart.getFoodItems().get(0).getMenuItem().getRestaurant();
//
//        OrderEntity order= OrderTransformer.preparedOrderEntity(cart);
//
//        OrderEntity savedOrder=orderEntityRepository.save(order);
//
//        order.setCustomer(customer);
//        order.setDeliveryPartner(partner);
//        order.setRestaurant(restaurant);
//        order.setFoodItems(cart.getFoodItems());
//
//        customer.getOrders().add(savedOrder);
//        partner.getOrders().add(savedOrder);
//        restaurant.getOrders().add(savedOrder);
//
//        for(FoodItem foodItem:cart.getFoodItems()){
//            foodItem.setCart(null);
//            foodItem.setOrder(savedOrder);
//        }
//        clearCart(cart);
//
//        customerRepository.save(customer);
//        deliveryPartnerRepository.save(partner);
//        restaurantRepository.save(restaurant);
//        return OrderTransformer.OrderToOrderResponse(savedOrder);
//    }
//
//    private void clearCart(Cart cart) {
//        cart.setCartTotal(0);
//        cart.setFoodItems(new ArrayList<>());
//    }
//}


@Service
public class OrderService {

    final CustomerRepository customerRepository;
    final OrderEntityRepository orderEntityRepo;

    final DeliveryPartnerRepository deliveryPartnerRepository;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public OrderService(CustomerRepository customerRepository,
                        OrderEntityRepository orderEntityRepository,
                        DeliveryPartnerRepository deliveryPartnerRepository,
                        RestaurantRepository restaurantRepository) {
        this.customerRepository = customerRepository;
        this.orderEntityRepo = orderEntityRepository;
        this.deliveryPartnerRepository = deliveryPartnerRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public OrderResponse placeOrder(String customerMobile) {

        // verify the customer
        Customer customer = customerRepository.findByMobileNo(customerMobile);
        if(customer == null){
            throw new CustomerNotFoundException("Invalid mobile number!!!");
        }

        // verify if cart is empty or not
        Cart cart = customer.getCart();
        if(cart.getFoodItems().size()==0){
            throw new EmptyCartException("Sorry! Your cart is empty!!!");
        }

        // find a delivery partner to deliver. Randomly
        DeliveryPartner partner = deliveryPartnerRepository.findRandomDeliveryPartner();
        Restaurant restaurant = cart.getFoodItems().get(0).getMenuItem().getRestaurant();

        // prepare the order entity
        OrderEntity order = OrderTransformer.prepareOrderEntity(cart);

        OrderEntity savedOrder = orderEntityRepo.save(order);

        order.setCustomer(customer);
        order.setDeliveryPartner(partner);
        order.setRestaurant(restaurant);
        order.setFoodItems(cart.getFoodItems());

        customer.getOrders().add(savedOrder);
        partner.getOrders().add(savedOrder);
        restaurant.getOrders().add(savedOrder);

        for(FoodItem foodItem: cart.getFoodItems()){
            foodItem.setCart(null);
            foodItem.setOrder(savedOrder);
        }
        clearCart(cart);

        customerRepository.save(customer);
        deliveryPartnerRepository.save(partner);
        restaurantRepository.save(restaurant);

        // prepare orderresponse
        return OrderTransformer.OrderToOrderResponse(savedOrder);
    }

    private void clearCart(Cart cart) {
        cart.setCartTotal(0);
        cart.setFoodItems(new ArrayList<>());
    }
}