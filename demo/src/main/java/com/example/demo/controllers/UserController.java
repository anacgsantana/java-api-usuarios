package com.example.demo.controllers;

import com.example.demo.dtos.UserRecordDto;
import com.example.demo.models.UserModel;
import com.example.demo.repositories.UserRepository;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired

	UserRepository userRepository;

	@PostMapping("/users")
	public ResponseEntity<?> saveUser(@RequestBody @Valid UserRecordDto userRecordDto) {
		if (userRepository.existsByEmail(userRecordDto.email())) {
			String errorMessageEmail = "O e-mail informado já está em uso.";
			logger.error(errorMessageEmail);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessageEmail);
		}

		if (userRepository.existsByCpf(userRecordDto.cpf())) {
			String errorMessageCpf = "O CPF informado já está em uso.";
			logger.error(errorMessageCpf);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessageCpf);
		}

		var userModel = new UserModel();
		BeanUtils.copyProperties(userRecordDto, userModel);
		return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(userModel));
	}

	@GetMapping("/users")
	public ResponseEntity<List<UserModel>> getAllUsers(@RequestParam(required = false) String nomeFiltro) {
		List<UserModel> usersList = userRepository.findAll();

		if (nomeFiltro != null && !nomeFiltro.isEmpty()) {
			String nomeFiltroLowerCase = nomeFiltro.toLowerCase();
			List<UserModel> filteredUsers = usersList.stream()
					.filter(user -> user.getNome().toLowerCase().contains(nomeFiltroLowerCase))
					.collect(Collectors.toList());

			if (!filteredUsers.isEmpty()) {
				for (UserModel user : filteredUsers) {
					UUID id = user.getIdUser();
					user.add(linkTo(methodOn(UserController.class).getOneUser(id)).withSelfRel());
				}
			}

			return ResponseEntity.status(HttpStatus.OK).body(filteredUsers);
		}

		if (!usersList.isEmpty()) {
			for (UserModel user : usersList) {
				UUID id = user.getIdUser();
				user.add(linkTo(methodOn(UserController.class).getOneUser(id)).withSelfRel());
			}
		}

		return ResponseEntity.status(HttpStatus.OK).body(usersList);
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<Object> getOneUser(@PathVariable(value = "id") UUID id) {
		Optional<UserModel> user0 = userRepository.findById(id);
		if (user0.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}
		UserModel user = user0.get();
		user.add(linkTo(methodOn(UserController.class).getOneUser(id)).withSelfRel());
		return ResponseEntity.status(HttpStatus.OK).body(user);
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<Object> updateUser(@PathVariable(value = "id") UUID id,
			@RequestBody @Valid UserRecordDto userRecordDto) {
		Optional<UserModel> userO = userRepository.findById(id);
		if (userO.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}
		var userModel = userO.get();
		BeanUtils.copyProperties(userRecordDto, userModel);
		return ResponseEntity.status(HttpStatus.OK).body(userRepository.save(userModel));

	}
	@PatchMapping("/users/{id}")
	public ResponseEntity<Object> patchUser(@PathVariable(value = "id") UUID id,
	                                        @RequestBody Map<String, Object> camposAtualizados) {
	    Optional<UserModel> userO = userRepository.findById(id);
	    if (userO.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	    }

	    var userModel = userO.get();

	    if (camposAtualizados.containsKey("nome")) {
	        userModel.setNome((String) camposAtualizados.get("nome"));
	    }
	    if (camposAtualizados.containsKey("email")) {
	        userModel.setEmail((String) camposAtualizados.get("email"));
	    }
	    if (camposAtualizados.containsKey("cpf")) {
	        userModel.setCpf((String) camposAtualizados.get("cpf"));
	    }    

	    return ResponseEntity.status(HttpStatus.OK).body(userRepository.save(userModel));
	}


	@DeleteMapping("/users/{id}")
	public ResponseEntity<Object> deleteUser(@PathVariable(value = "id") UUID id) {
		Optional<UserModel> userO = userRepository.findById(id);
		if (userO.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}
		userRepository.delete(userO.get());
		return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
	}
}