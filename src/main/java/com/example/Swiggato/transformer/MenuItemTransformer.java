package com.example.Swiggato.transformer;

import com.example.Swiggato.dto.request.MenuRequest;
import com.example.Swiggato.dto.response.MenuResponse;
import com.example.Swiggato.model.MenuItem;

public class MenuItemTransformer {

    public static MenuItem MenuRequestToMenuItem(MenuRequest foodRequest){
        return MenuItem.builder()
                .dishName(foodRequest.getDishName())
                .price(foodRequest.getPrice())
                .category(foodRequest.getCategory())
                .veg(foodRequest.isVeg())
                .available(foodRequest.isAvailable())
                .build();

    }

    public static MenuResponse MenuItemToMenuResponse(MenuItem foodItem){
        return MenuResponse.builder()
                .dishName(foodItem.getDishName())
                .price(foodItem.getPrice())
                .veg(foodItem.isVeg())
                .category(foodItem.getCategory())
                .build();

    }
}
