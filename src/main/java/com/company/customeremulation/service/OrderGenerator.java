package com.company.customeremulation.service;

import com.company.customeremulation.app.OrderDtoService;
import com.company.customeremulation.entity.CustomerSetting;
import com.company.customeremulation.entity.ItemDto;
import com.company.customeremulation.entity.OrderDto;
import com.company.customeremulation.event.OrderGeneratedEvent;
import com.company.customeremulation.service.record.Customer;
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

    private final AppSettings appSettings;
    private final CustomerSetting customerSettings;

    public OrderGenerator(AppSettings appSettings) {
        this.appSettings = appSettings;
        customerSettings = appSettings.load(CustomerSetting.class);;
    }

    public OrderDto generate() {

        String address;
        String customerName;
        String randomAddress = getRandomAddress();

        if (isFakeAddress()) {      //Address outside boundaries
            Customer customer = customerService.generateCustomer();
            if (customer == null) {
                log.error("Customer is null");
                return null;
            }
            address = customer.address();
            customerName = customer.name();
        } else {
            address = randomAddress;
            customerName = customerService.randomRussianCustomer();
        }

        OrderDto order = metadata.create(OrderDto.class);
        order.setCustomer(customerName);
        order.setAddress(address);
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
            MapPoint point = addressService.randomPoint();
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

    private boolean isFakeAddress() {
        int probability = customerSettings.getFakeProbability();
        Random random = new Random();
        return random.nextInt(100) <= probability;
    }

    public int randomDelay() {
        int minDelay = customerSettings.getMinDelay();
        int maxDelay = customerSettings.getMaxDelay();
        Random random = new Random();
        return (random.nextInt(maxDelay - minDelay + 1) + minDelay) * 1000;
    }
}
