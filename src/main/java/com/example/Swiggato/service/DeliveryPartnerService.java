package com.example.Swiggato.service;

import com.example.Swiggato.dto.request.DeliveryPartnerRequest;
import com.example.Swiggato.model.DeliveryPartner;
import com.example.Swiggato.repository.DeliveryPartnerRepository;
import com.example.Swiggato.transformer.PartnerTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryPartnerService {

    final DeliveryPartnerRepository deliveryPartnerRepository;

    @Autowired
    public DeliveryPartnerService(DeliveryPartnerRepository deliveryPartnerRepository) {
        this.deliveryPartnerRepository = deliveryPartnerRepository;
    }

    public String addPartner(DeliveryPartnerRequest deliveryPartnerRequest) {

        DeliveryPartner deliveryPartner= PartnerTransformer.PartnerRequestToDeliveryPartner(deliveryPartnerRequest);

        DeliveryPartner savedPartner=deliveryPartnerRepository.save(deliveryPartner);
        return "You have been successfully registered !!!";

    }
}
