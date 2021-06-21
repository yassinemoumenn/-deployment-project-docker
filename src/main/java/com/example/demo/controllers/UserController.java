package com.example.demo.controllers;

import com.example.demo.Dto.UserDto;
import com.example.demo.models.User;
import com.example.demo.request.UserRequest;
import com.example.demo.response.UserResponse;
import com.example.demo.services.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping
//	@PreAuthorize("hasRole('ADMIN')")
	public List<UserResponse> allUsers(@RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "limit", defaultValue = "8") int limit) {
		List<UserResponse> userResponses = new ArrayList<>();
		List<UserDto> users = userService.getUsers(page, limit);
		for (UserDto userDto : users){
			UserResponse user = new UserResponse();
			BeanUtils.copyProperties(userDto, user);
			userResponses.add(user);
		}
		return userResponses;
	}

	@GetMapping(path = "{userId}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {
		UserDto userDto = userService.getUserByUserId(userId);
		UserResponse userResponse = new UserResponse();
		BeanUtils.copyProperties(userDto, userResponse);
		return new ResponseEntity<>(userResponse, HttpStatus.OK);
	}

	@PutMapping(path = "/update/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserRequest userRequest) {
		UserDto userDto =new UserDto();
		BeanUtils.copyProperties(userRequest, userDto);
		UserDto updateUser = userService.updateUser(userId, userDto);
		UserResponse userResponse = new UserResponse();
		BeanUtils.copyProperties(updateUser, userResponse);
		return new ResponseEntity<UserResponse>(userResponse, HttpStatus.ACCEPTED);
	}


	@DeleteMapping(path = "/delete/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Object> daleteUser(@PathVariable String userId){
		userService.deleteUser(userId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	

}
