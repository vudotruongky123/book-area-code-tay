package com.thientri.book_area.service.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.thientri.book_area.dto.request.user.UserRequest;
import com.thientri.book_area.dto.response.user.UserResponse;
import com.thientri.book_area.model.user.Address;
import com.thientri.book_area.model.user.Role;
import com.thientri.book_area.model.user.User;
import com.thientri.book_area.repository.user.AddressRepository;
import com.thientri.book_area.repository.user.RoleRepository;
import com.thientri.book_area.repository.user.UserRepository;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            AddressRepository addressRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAllUsers() {
        List<UserResponse> list = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            list.add(mapToResponse(user));
        }
        return list;
    }

    private UserResponse mapToResponse(User user) {
        Set<String> roles = new HashSet<>();
        List<Long> addressIds = new ArrayList<>();
        for (Address address : user.getAddresses()) {
            addressIds.add(address.getId());
        }
        for (Role role : user.getRoles()) {
            roles.add(role.getName());
        }

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .roles(roles)
                .addressIds(addressIds)
                .build();
    }

    public UserResponse createUser(UserRequest userRequest) {
        if (userRequest == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dữ liệu người dùng không hợp lệ.");
        }

        String email = normalize(userRequest.getEmail());
        String fullName = normalize(userRequest.getFullName());
        String rawPassword = userRequest.getPassword() == null ? "" : userRequest.getPassword();

        if (email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không được để trống.");
        }

        if (fullName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Họ tên không được để trống.");
        }

        if (rawPassword.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu phải có tối thiểu 6 ký tự.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email này đã được sử dụng.");
        }

        User newUser = new User();
        Set<Role> role = new HashSet<>();
        role.add(resolveRole("USER"));
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setFullName(fullName);
        newUser.setRoles(role);
        newUser.initCart();
        return mapToResponse(userRepository.save(newUser));
    }

    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User updatedUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản người dùng để cập nhật!"));
        updatedUser.setEmail(userRequest.getEmail());
        updatedUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        updatedUser.setFullName(userRequest.getFullName());
        userRepository.save(updatedUser);
        return mapToResponse(updatedUser);
    }

    public UserResponse deleteUser(Long id) {
        User userDeleted = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user để xóa!"));
        userRepository.deleteById(id);
        return mapToResponse(userDeleted);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user với email: " + username));
    }

    private Role resolveRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
