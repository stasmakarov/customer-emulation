package com.company.customeremulation.service.record;

public record OrderRecord(
        String customer,
        String address,
        String item,
        Integer quantity
) {
}
