package com.example.demo;

import com.example.demo.controllers.AuthController;
import com.example.demo.request.SignupRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.AssertionErrors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BasmaStroreApplicationTests {

	@Autowired
	private AuthController authController;

	@Test
	public void testHomeController() {
		SignupRequest request = new SignupRequest("ihuh","erjvs@gmail.com", "passw", "0617181617");
		String result = authController.registerUser(request);
		assertEquals(result, "User registered successfully!");
	}

}
