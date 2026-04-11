package com.todo.auth_service.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.todo.auth_service.exception.InvalidOtpException;
import com.todo.auth_service.exception.OtpExpiredException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final StringRedisTemplate stringRedisTemplate;

    public String generateAndStoreOtp(String email) {
        // generate otp
        int otp = 100000 + new Random().nextInt(900000);
        // store in redis
        stringRedisTemplate.opsForValue().set("otp:" + email, Integer.toString(otp), 10, TimeUnit.MINUTES);
        return Integer.toString(otp);
    }

    public void validateAndDeleteOtp(String email, String otp) {
        String storedOtp = stringRedisTemplate.opsForValue().get("otp:" + email);
        if (storedOtp == null) {
            throw new OtpExpiredException("OTP expired, please try again");
        } else if (storedOtp.equals(otp)) {
            stringRedisTemplate.delete("otp:"+email);
        } else {
            throw new InvalidOtpException("Invalid OTP, try again");
        }
    }
}
