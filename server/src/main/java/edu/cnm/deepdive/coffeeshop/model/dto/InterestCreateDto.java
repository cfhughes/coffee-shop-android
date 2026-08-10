package edu.cnm.deepdive.coffeeshop.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InterestCreateDto (
    @NotNull
    @NotBlank
    String category) {

}
