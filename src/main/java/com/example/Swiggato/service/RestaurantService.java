package com.example.Swiggato.service;

import com.example.Swiggato.dto.request.MenuRequest;
import com.example.Swiggato.dto.request.RestaurantRequest;
import com.example.Swiggato.dto.response.RestaurantResponse;
import com.example.Swiggato.exception.RestaurantNotFoundException;
import com.example.Swiggato.model.MenuItem;
import com.example.Swiggato.model.Restaurant;
import com.example.Swiggato.repository.RestaurantRepository;
import com.example.Swiggato.transformer.MenuItemTransformer;
import com.example.Swiggato.transformer.RestaurantTransformer;
import com.example.Swiggato.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestaurantService {

    final RestaurantRepository restaurantRepository;

    final ValidationUtils validationUtils;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository,ValidationUtils validationUtils) {
        this.restaurantRepository = restaurantRepository;
        this.validationUtils=validationUtils;
    }

    public RestaurantResponse addRestaurant(RestaurantRequest restaurantRequest) {

        Restaurant restaurant = RestaurantTransformer.RestaurantRequestToRestaurant(restaurantRequest);

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        return RestaurantTransformer.RestaurantToRestaurantResponse(savedRestaurant);
    }


    public String changeOpenedStatus(int id) {
        if (!validationUtils.validateRestaurantId(id)) {
            throw new RestaurantNotFoundException("Restaurant doesn't exist");
        }
        Restaurant restaurant = restaurantRepository.findById(id).get();
        restaurant.setOpened(!restaurant.isOpened());
        restaurantRepository.save(restaurant);

        if (restaurant.isOpened()) {
            return "Restaurant is opened now!!";
        }
        return "Restaurant is closed";
    }

    public RestaurantResponse addMenuItemToRestaurant(MenuRequest foodRequest) {
        if(!validationUtils.validateRestaurantId(foodRequest.getRestaurantId())){
            throw new RestaurantNotFoundException("Restaurant doesn't exist");
        }
        Restaurant restaurant=restaurantRepository.findById(foodRequest.getRestaurantId()).get();

        MenuItem foodItem= MenuItemTransformer.MenuRequestToMenuItem(foodRequest);
        foodItem.setRestaurant(restaurant);
        restaurant.getAvailableFoodItems().add(foodItem);

        Restaurant savedRestaurant=restaurantRepository.save(restaurant);
        return RestaurantTransformer.RestaurantToRestaurantResponse(savedRestaurant);
}
}
