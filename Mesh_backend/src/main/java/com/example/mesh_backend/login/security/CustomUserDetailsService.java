package com.example.mesh_backend.login.security;

import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        Long id = Long.valueOf(userId);

        User user = userRepository.findByUserId(id)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다. : " + userId));

        return new CustomUserDetails(user);
    }

}