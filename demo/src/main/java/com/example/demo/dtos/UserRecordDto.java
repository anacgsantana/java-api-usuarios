package com.example.demo.dtos;
import jakarta.validation.constraints.NotBlank;

public record UserRecordDto(@NotBlank String nome,@NotBlank String email, @NotBlank String cpf,int idade) {

}
