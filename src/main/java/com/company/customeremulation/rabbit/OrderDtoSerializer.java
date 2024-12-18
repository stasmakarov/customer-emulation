package com.company.customeremulation.rabbit;

import com.company.customeremulation.entity.OrderDto;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class OrderDtoSerializer extends JsonSerializer<OrderDto> {
    @Override
    public void serialize(OrderDto order,
                          JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("customer", order.getCustomer());
        gen.writeStringField("address", order.getAddress());
        if (order.getItemDto() != null) {
            gen.writeStringField("item", order.getItemDto().getName()); // Only serialize itemDto.name
        }
        gen.writeNumberField("quantity", order.getQuantity());
        gen.writeEndObject();
    }
}
