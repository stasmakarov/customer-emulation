package com.company.customeremulation.service;

import com.company.customeremulation.app.OrderDtoService;
import com.company.customeremulation.entity.ItemDto;
import com.company.customeremulation.entity.OrderDto;
import com.company.customeremulation.entity.Params;
import com.company.customeremulation.event.OrderGeneratedEvent;
import com.company.customeremulation.service.record.Customer;
import com.company.customeremulation.service.record.MapPoint;
import io.jmix.core.DataManager;
import io.jmix.core.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    public OrderDto generate() {
        Params params = getParams();
        if (params == null) {
            log.info("No parameters defined");
            return null;
        }

        OrderDto order = metadata.create(OrderDto.class);
        Customer customer = customerService.generateCustomer();
        ItemDto itemDto = randomItemDto();
        int quantity = randomQuantity();
        boolean fakeAddress = isFakeAddress();
        String address = fakeAddress ? customer.address() : getRandomAddress();
        String customerName = fakeAddress ? customer.name() : customerService.randomRussianCustomer();

        order.setCustomer(customerName);
        order.setAddress(address);
        order.setItemDto(itemDto);
        order.setQuantity(quantity);
        order.setCreated(LocalDate.now());
        orderDtoService.addOrderDto(order);
        applicationEventPublisher.publishEvent(new OrderGeneratedEvent(this, order));
        return order;
    }

    private Params getParams() {
        List<Params> params = dataManager.load(Params.class).all().list();
        if (!params.isEmpty()) return params.get(0);
        else return null;
    }

    private String getRandomAddress() {
        String address;
        do {
            MapPoint point = addressService.randomPoint();
            address = addressService.findAddress(point);
        } while (address == null);
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
        List<Params> params = dataManager.load(Params.class).all().list();
        if (!params.isEmpty()) {
            Integer maxItems = params.get(0).getMaxItems();
            if (maxItems  != null) {
                Random random = new Random();
                return random.nextInt(1, maxItems);
            }
        }
        return 1;
    }

    private boolean isFakeAddress() {
        List<Params> params = dataManager.load(Params.class).all().list();
        if (!params.isEmpty()) {
            Integer probability = params.get(0).getFakeAddressProbability();
            Random random = new Random();
            return random.nextInt(100) <= probability;
        }
        return false;
    }

    public int randomDelay() {
        List<Params> params = dataManager.load(Params.class).all().list();
        if (!params.isEmpty()) {
            Integer maxDelay = params.get(0).getMaxDelay();
            Integer minDelay = params.get(0).getMinDelay();
            Random random = new Random();
            return (random.nextInt(maxDelay - minDelay + 1) + minDelay) * 1000;
        }
        return 0;
    }
}
