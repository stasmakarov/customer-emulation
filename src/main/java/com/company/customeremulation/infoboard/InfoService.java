package com.company.customeremulation.infoboard;

import com.company.customeremulation.entity.ItemDto;
import com.company.customeremulation.entity.OrderDto;
import com.company.customeremulation.repository.ItemDtoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("cst_InfoService")
public class InfoService {

    private static final Logger log = LoggerFactory.getLogger(InfoService.class);
    @Autowired
    private ItemDtoRepository itemDtoRepository;

    public void initItemDtos() {
        for (ItemDto itemDto : itemDtoRepository.findAll()) {
        }
        log.info("Init Item DTOs");
    }

    public void countOrder(OrderDto order) {

    }
}