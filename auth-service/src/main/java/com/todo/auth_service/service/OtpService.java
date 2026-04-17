package com.todo.auth_service.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.todo.auth_service.exception.InvalidOtpException;
import com.todo.auth_service.exception.OtpBlockedException;
import com.todo.auth_service.exception.OtpCooldownException;
import com.todo.auth_service.exception.OtpExpiredException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final StringRedisTemplate stringRedisTemplate;

    public void checkRateLimit(String email){
        if (stringRedisTemplate.hasKey("otp:blocked:" + email)){
            throw new OtpBlockedException("Too many attempts, sorry.");
        }
        else if (stringRedisTemplate.hasKey("otp:cooldown:" + email)){
            throw new OtpCooldownException("Retry after sometime");
        }
        else{
            Long count = stringRedisTemplate.opsForValue().increment("otp:attempts:" + email);
            if (count == 1) {
                stringRedisTemplate.expire("otp:attempts:" + email, 1, TimeUnit.HOURS);
            }
            if (count >= 3) {
            stringRedisTemplate.opsForValue().set("otp:blocked:" + email, "1", 15, TimeUnit.MINUTES);
            throw new OtpBlockedException("Too many attempts. Try again after 15 minutes.");
            }
        }
    }

    public String generateAndStoreOtp(String email) {

        checkRateLimit(email);
        // generate otp
        int otp = 100000 + new Random().nextInt(900000);
        // set cooldown
        stringRedisTemplate.opsForValue().set("otp:cooldown:" + email, "1", 30, TimeUnit.SECONDS);
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
            stringRedisTemplate.delete("otp:cooldown"+email);
            stringRedisTemplate.delete("otp:blocked"+email);
            stringRedisTemplate.delete("otp:attempts:" + email);
        } else {
            throw new InvalidOtpException("Invalid OTP, try again");
        }
    }
}
