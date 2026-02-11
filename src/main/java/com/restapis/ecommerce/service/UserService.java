package com.restapis.ecommerce.service;

import com.restapis.ecommerce.entity.User;
import com.restapis.ecommerce.io.UserRequest;
import com.restapis.ecommerce.io.UserResponse;
import com.restapis.ecommerce.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacadeImpl authenticationFacade;

    public UserResponse registerUser(UserRequest request) {
        User newUser = convertToEntity(request);
        newUser = userRepository.save(newUser);
        return convertToResponse(newUser);
    }

    // ✅ Correct method name to match OrderService usage
    public String getCurrentUserId() {
        String loggedInUserEmail = authenticationFacade.getAuthentication().getName();
        User loggedInUser = userRepository.findByEmail(loggedInUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return String.valueOf(loggedInUser.getId());
    }

    private User convertToEntity(UserRequest request) {
        return User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();
    }

    private UserResponse convertToResponse(User userEntity) {
        return UserResponse.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .build();
    }
}
