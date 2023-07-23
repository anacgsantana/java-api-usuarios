package com.example.demo.controllers;

import com.example.demo.dtos.UserRecordDto;
import com.example.demo.models.UserModel;
import com.example.demo.repositories.UserRepository;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserController {
	@Autowired
	UserRepository userRepository;

	@PostMapping("/users")
	public ResponseEntity<UserModel> saveUser(@RequestBody @Valid UserRecordDto userRecordDto) {
		var userModel = new UserModel();
		BeanUtils.copyProperties(userRecordDto, userModel);
		return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(userModel));

	}

	@GetMapping("/users")
	public ResponseEntity<List<UserModel>> getAllUsers() {
		List<UserModel> usersList = userRepository.findAll();
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
		user0.get().add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("Users List"));
		return ResponseEntity.status(HttpStatus.OK).body(user0.get());
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
