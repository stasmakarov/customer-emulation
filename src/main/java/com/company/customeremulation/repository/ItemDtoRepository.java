package com.company.customeremulation.repository;

import com.company.customeremulation.entity.ItemDto;
import io.jmix.core.repository.JmixDataRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ItemDtoRepository extends JmixDataRepository<ItemDto, UUID> {
}