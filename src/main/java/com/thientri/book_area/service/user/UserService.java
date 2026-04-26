package com.thientri.book_area.service.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.thientri.book_area.dto.request.user.UserRequest;
import com.thientri.book_area.dto.response.user.UserResponse;
import com.thientri.book_area.model.user.Address;
import com.thientri.book_area.model.user.Role;
import com.thientri.book_area.model.user.User;
import com.thientri.book_area.repository.user.AddressRepository;
import com.thientri.book_area.repository.user.RoleRepository;
import com.thientri.book_area.repository.user.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
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
        User newUser = new User();
        Set<Role> role = new HashSet<>();
        role.add(roleRepository.findByName("USER") // Gán quyền mặc định cho User
                .orElseThrow(() -> new RuntimeException("Khong tim thay quyen cua USER de tao tai khoan")));
        newUser.setEmail(userRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(userRequest.getPassword())); // mật khẩu đăng ký => mã hóa khi Save
        newUser.setFullName(userRequest.getFullName());
        newUser.setRoles(role);
        newUser.initCart(); // Thêm giỏ hàng mới khi tạo tài khoản
        return mapToResponse(userRepository.save(newUser));
    }

    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User updatedUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay tai khoan nguoi dung de cap nhat!"));
        updatedUser.setEmail(userRequest.getEmail());
        updatedUser.setPassword(userRequest.getPassword());
        updatedUser.setFullName(userRequest.getFullName());
        userRepository.save(updatedUser);
        return mapToResponse(updatedUser);
    }

    public UserResponse deleteUser(Long id) {
        User userDeleted = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay user de xoa!"));
        userRepository.deleteById(id);
        return mapToResponse(userDeleted);
    }
}
