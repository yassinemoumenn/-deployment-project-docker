package com.example.demo.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class Utils {
    private final Random random = new SecureRandom();
    private final String alpha = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabsdefghijklmnopqrstuvwz";

    public String generateUserId(int lenght){
        StringBuilder returnValue = new StringBuilder(lenght);

        for (int i = 0; i < lenght; i++){
            returnValue.append(alpha.charAt(random.nextInt(alpha.length())));
        }
        return new String(returnValue);
    }
}
