package com.example.demo.dtos;
import jakarta.validation.constraints.NotBlank;

public record UserRecordDto(@NotBlank String nome,@NotBlank String email, @NotBlank String cpf,int idade) {

	public String nome() {
		return nome;
	}

	public String email() {
		return email;
	}

	public String cpf() {
		return cpf;
	}

	public int idade() {
		return idade;
	}

	
}


