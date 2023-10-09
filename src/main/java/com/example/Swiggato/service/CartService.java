package com.example.Swiggato.service;

import com.example.Swiggato.dto.request.FoodRequest;
import com.example.Swiggato.dto.response.CartStatusResponse;
import com.example.Swiggato.dto.response.FoodResponse;
import com.example.Swiggato.exception.CustomerNotFoundException;
import com.example.Swiggato.exception.MenuItemNotFoundException;
import com.example.Swiggato.model.*;
import com.example.Swiggato.repository.CartRepository;
import com.example.Swiggato.repository.CustomerRepository;
import com.example.Swiggato.repository.FoodRepository;
import com.example.Swiggato.repository.MenuRepository;
import com.example.Swiggato.transformer.FoodTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    final CustomerRepository customerRepository;
    final MenuRepository menuRepository;
     final CartRepository cartRepository;
    final FoodRepository foodRepository;

    @Autowired
    public CartService(CustomerRepository customerRepository, MenuRepository menuRepository, CartRepository cartRepository, FoodRepository foodRepository) {
        this.customerRepository = customerRepository;
        this.menuRepository = menuRepository;
        this.cartRepository = cartRepository;
        this.foodRepository = foodRepository;
    }

    public CartStatusResponse addFoodItemToCart(FoodRequest foodRequest) {

        Customer customer = customerRepository.findByMobileNo(foodRequest.getCustomerMobile());
        if(customer==null){
            throw new CustomerNotFoundException("Customer doesn't exist");
        }

        Optional<MenuItem> menuItemOptional = menuRepository.findById(foodRequest.getMenuItemId());
        if(menuItemOptional.isEmpty()){
            throw new MenuItemNotFoundException("Item not available in the restaurant!!!");
        }

        MenuItem menuItem = menuItemOptional.get();
        if(!menuItem.getRestaurant().isOpened() || !menuItem.isAvailable()) {
            throw new MenuItemNotFoundException("Given dish is out of stock for now!!!");
        }

        Cart cart=customer.getCart();
        if(cart.getFoodItems().size()!=0) {
            Restaurant currRestaurant = cart.getFoodItems().get(0).getMenuItem().getRestaurant();
            Restaurant newRestaurant = menuItem.getRestaurant();

            if (!currRestaurant.equals(newRestaurant)) {
                List<FoodItem> foodItems = cart.getFoodItems();
                for (FoodItem foodItem:foodItems){
                    foodItem.setCart(null);
                    foodItem.setOrder(null);
                    foodItem.setMenuItem(null);
                }
                cart.setCartTotal(0);
                cart.getFoodItems().clear();
                foodRepository.deleteAll(foodItems);
            }
        }
       boolean alreadyExist=false;
        FoodItem savedFoodItem=new FoodItem();
        if(cart.getFoodItems().size()!=0){
            for (FoodItem foodItem:cart.getFoodItems()){
                if(foodItem.getMenuItem().getId()==menuItem.getId()){
                    savedFoodItem=foodItem;
                    int curr=foodItem.getRequiredQuantity();
                    foodItem.setRequiredQuantity(curr+foodRequest.getRequiredQuantity());
                    alreadyExist=true;
                    break;
                }
            }
        }

        if(!alreadyExist) {
            FoodItem foodItem = FoodItem.builder()
                    .menuItem(menuItem)
                    .requiredQuantity(foodRequest.getRequiredQuantity())
                    .totalCost(foodRequest.getRequiredQuantity() * menuItem.getPrice())
                    .build();

            savedFoodItem = foodRepository.save(foodItem);
            cart.getFoodItems().add(savedFoodItem);
            menuItem.getFoodItems().add(savedFoodItem);
            savedFoodItem.setCart(cart);

        }
        double cartTotal=0;
        for(FoodItem food: cart.getFoodItems()) {
            cartTotal += food.getRequiredQuantity() * food.getMenuItem().getPrice();
        }
        cart.setCartTotal(cartTotal);
        Cart savedCart=cartRepository.save(cart);
        MenuItem savedMenuItem=menuRepository.save(menuItem);


        List<FoodResponse>foodResponseList=new ArrayList<>();


        for(FoodItem food: cart.getFoodItems()){


            foodResponseList.add(FoodTransformer.FoodToFoodResponse(food));

        }
        return CartStatusResponse.builder()
                .customerName(savedCart.getCustomer().getName())
                .customerMobile(savedCart.getCustomer().getMobileNo())
                .customerAddress(savedCart.getCustomer().getAddress())
                .foodList(foodResponseList)
                .restaurantName(savedMenuItem.getRestaurant().getName())
                .cartTotal(cartTotal)
                .build();
    }

}
