package com.shubham.User.request;

import com.shubham.User.custumAnnotations.AgeLimit;
import com.shubham.User.model.User;
import com.shubham.Utils.UserIdentifier;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    @NotBlank
    private String name;
    @Email
    private String email;
    private String address;
    @NotBlank
    private String password;
    @Pattern(regexp = "^[6-9]\\d{9}$")
    private String phNo;
    @NotNull
    private UserIdentifier userIdentifier;
    @NotBlank
    private String userIdentifierValue;
    @AgeLimit
    private String dob;

    public User getUser(){
        return User.builder().
                name(this.name).
                email(this.email).
                password(this.password).
                address(this.password).
                phNo(this.phNo).
                dob(this.dob).
                userIdentifierValue(this.userIdentifierValue).
                userIdentifier(this.userIdentifier).
                build();
    }
}
