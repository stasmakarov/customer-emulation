package com.company.customeremulation.service;

import com.company.customeremulation.app.OrderDtoService;
import com.company.customeremulation.configuration.WebinarConfig;
import com.company.customeremulation.entity.CustomerSetting;
import com.company.customeremulation.entity.ItemDto;
import com.company.customeremulation.entity.OrderDto;
import com.company.customeremulation.event.OrderGeneratedEvent;
import com.company.customeremulation.service.record.MapPoint;
import io.jmix.appsettings.AppSettings;
import io.jmix.core.DataManager;
import io.jmix.core.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderGenerator {
    private static final Logger log = LoggerFactory.getLogger(OrderGenerator.class);

    @Autowired
    private Metadata metadata;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private OrderDtoService orderDtoService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private final CustomerSetting customerSettings;

    public OrderGenerator(AppSettings appSettings) {
        customerSettings = appSettings.load(CustomerSetting.class);;
    }

    public OrderDto generate() {

        OrderDto order = metadata.create(OrderDto.class);
        order.setCustomer(customerService.generateCustomerName());
        order.setAddress(getRandomAddress());
        order.setItemDto(randomItemDto());
        order.setQuantity(randomQuantity());
        order.setCreated(LocalDate.now());
        orderDtoService.addOrderDto(order);
        applicationEventPublisher.publishEvent(new OrderGeneratedEvent(this, order));

        return order;
    }

    private String getRandomAddress() {
        String address;
        int count = customerSettings.getAttempts();
        do {
            MapPoint point = addressService.randomPoint(isValidAddress());
            address = addressService.findAddress(point).orElse(null);
        } while (address == null && count-- > 0);
        return address;
    }

    private ItemDto randomItemDto() {
        List<ItemDto> items = dataManager.load(ItemDto.class).all().list();
        if (!items.isEmpty()) {
            int size = items.size();
            Random random = new Random();
            int i = random.nextInt(size);
            return items.get(i);
        }
        return null;
    }

    private int randomQuantity() {
        int maxItems = customerSettings.getMaxItems();
        Random random = new Random();
        return random.nextInt(1, maxItems);
    }

    private boolean isValidAddress() {
        // clamp to [0..100] to avoid config mistakes
        int p = Math.max(0, Math.min(100, customerSettings.getFakeProbability()));
        // nextInt(100) -> 0..99, so:
        // p=0  -> always false
        // p=100-> always true
        return java.util.concurrent.ThreadLocalRandom.current().nextInt(100) < p;
    }


    public int randomDelay() {
        int minDelay = customerSettings.getMinDelay();
        int maxDelay = customerSettings.getMaxDelay();
        Random random = new Random();
        return (random.nextInt(maxDelay - minDelay + 1) + minDelay) * 1000;
    }
}
