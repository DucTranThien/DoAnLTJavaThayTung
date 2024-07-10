package com.hutech.tranthienducpro.service;

import com.hutech.tranthienducpro.model.Role;
import com.hutech.tranthienducpro.model.User;
import com.hutech.tranthienducpro.Repository.IRoleRepository;
import com.hutech.tranthienducpro.Repository.IUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    public void save(@NotNull User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void setDefaultRole(String username) {
        userRepository.findByUsername(username).ifPresentOrElse(
                user -> {
                    Role defaultRole = roleRepository.findByName("USER");
                    if (defaultRole != null) {
                        user.getRoles().add(defaultRole);
                        userRepository.save(user);
                    }
                },
                () -> { throw new UsernameNotFoundException("User not found"); }
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !isMaster(user))
                .collect(Collectors.toList());
    }

    private boolean isMaster(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("MASTER"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    public void updateUserRole(Long userId, String role) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Lấy thông tin về người dùng hiện tại
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getName() != null) {
                String currentUsername = authentication.getName();

                // Kiểm tra nếu người dùng hiện tại là MASTER
                if (currentUsername.equals(user.getUsername())) {
                    throw new IllegalArgumentException("Bạn không thể tự thay đổi quyền của chính mình.");
                }

                // Kiểm tra nếu người dùng không phải là MASTER
                boolean isMaster = user.getRoles().stream()
                        .anyMatch(r -> r.getName().equals("MASTER"));
                if (!isMaster) {
                    Role newRole = roleRepository.findByName(role);
                    if (newRole != null) {
                        System.out.println("Changing role of user " + user.getUsername() + " to " + role);
                        user.getRoles().clear();
                        user.getRoles().add(newRole);
                        userRepository.save(user);
                        System.out.println("Role changed successfully.");
                    } else {
                        throw new IllegalArgumentException("Role " + role + " not found.");
                    }
                } else {
                    throw new IllegalArgumentException("Không thể thay đổi quyền của người dùng có vai trò MASTER.");
                }
            } else {
                throw new IllegalArgumentException("Authentication không hợp lệ.");
            }
        } else {
            throw new UsernameNotFoundException("User with ID " + userId + " not found.");
        }
    }
    public boolean checkOldPassword(User user, String oldPassword) {
        if(new BCryptPasswordEncoder().matches(oldPassword, user.getPassword())) {
            return true;
        }
        return false;
    }
    public void UpdatePassword(User user, String newPassword) {
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userRepository.save(user);
    }
    public void UpdateFailCount(User user){
        if (user.isEnabled()) {
            int count = userRepository.countFailByUsername(user.getUsername());
            count += 1;
            user.setCountFail(count);
            if (count == 3) {
                user.setEnabled(false);
                user.setCountFail(0);
                user.setLockExpired(new Date(System.currentTimeMillis() + 60 * 5 * 1000));
            }
        } else {
            if (user.getLockExpired() != null) {
                if (user.getLockExpired().getTime() < System.currentTimeMillis()) {
                    user.setLockExpired(null);
                    user.setEnabled(true);
                }
            }
        }
        userRepository.save(user);
    }
    public void ResetLoginFail(User user){
        user.setEnabled(true);
        user.setLockExpired(null);
        user.setCountFail(0);
        userRepository.save(user);
    }
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public String GenTokenResetPassword(User user){
        user.setTokenResetPassword(GenToken(45));
        user.setTokenResetPasswordExpired(new Date(System.currentTimeMillis()+1000*60*10));
        userRepository.save(user);
        return user.getTokenResetPassword();
    }
    public String GenToken(int Length){
        String source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghiklmnopqrstuvwxyz0123456789";
        StringBuilder result= new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < Length; i++) {
            int index = random.nextInt(source.length());
            result.append(source.charAt(index));
        }
        return result.toString();
    }
    public User getUserByToken(String token){
        return userRepository.findByToken(token);
    }
    public void ResetDateForgotPassword(User user){
        user.setTokenResetPasswordExpired(null);
        user.setTokenResetPassword(null);
        userRepository.save(user);
    }

    public User getUserById(Long id){
        return  userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}


