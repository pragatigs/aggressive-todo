package com.todo.auth_service.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //sets getters and setters 
@NoArgsConstructor
@Entity
public class UserEntity {

    @Id
    private String email;

    @Column (nullable = false)
    private String password;

    private boolean emailVerified = false;

    private LocalDateTime lastActive;

    @CreationTimestamp
    private LocalDateTime accountCreated;

    public UserEntity(String email, String password){
        this.email = email;
        this.password = password;
    }

    // public void setUseremail (String useremail){
    //     this.useremail = useremail;
    // }
    // public void setPassword (String password){
    //     this.password = password;
    // }
    // public void setVerification (boolean isVerified){
    //     this.isVerified = isVerified;
    // }
    // public void setLastActive (LocalDateTime lastActive){
    //     this.lastActive = lastActive;
    // }
    // public void setAccountCreated (LocalDateTime accountCreated){
    //     this.accountCreated = accountCreated;
    // }

    // public String getUseremail(){
    //     return useremail;
    // }
    // public String getHashedpassword(){
    //     return password;
    // }
    // public boolean getVerification(){
    //     return isVerified;
    // }
    // public LocalDateTime getLastActive(){
    //     return lastActive;
    // }
    // public LocalDateTime getAccountCreated(){
    //     return accountCreated;
    // }

}
