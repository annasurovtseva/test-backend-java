package ru.surovtseva.hw5.dto.positive;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class Product <I, T, P> {
    @JsonProperty("id")
    private I id;
    @JsonProperty("title")
    private T title;
    @JsonProperty("price")
    private P price;
    @JsonProperty("categoryTitle")
    private String categoryTitle;
}
