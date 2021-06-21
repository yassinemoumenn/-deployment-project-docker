package com.example.demo.services;

import com.example.demo.Dto.UserDto;
import com.example.demo.models.ConfirmationToken;
import com.example.demo.models.ERole;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repository.ConfirmationTokenRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.EmailService;
import com.example.demo.util.Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailService emailService;


    @Override
    public UserDto createUser(UserDto userDto) {
        //check if username exist
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        //check if email exist
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }



        // Create new user's account
        User user = new User();
        user.setUserId(utils.generateUserId(32));
        user.setUsername(userDto.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setTelephone(userDto.getTelephone());
        Set<String> strRoles = userDto.getRole();
        Set<Role> roles = new HashSet<>();

        // default  role_user if role is null
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;

                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        User createUser = userRepository.save(user);

        //send mail

        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("zoubirtest12@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                +"http://localhost:8082/auth/confirm-account?token="+confirmationToken.getConfirmationToken());

        emailService.sendEmail(mailMessage);


        UserDto dto = new UserDto();
        BeanUtils.copyProperties(createUser, dto);
        return dto;
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        if (page > 0) page -= 1;
        List<UserDto> usersDto = new ArrayList<>();
        PageRequest pageable = PageRequest.of(page, limit);
        Page<User> userPage = userRepository.findAll(pageable);
        List<User> users = userPage.getContent();

        for (User user : users){
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(user, userDto);
            usersDto.add(userDto);
        }

        return usersDto;
    }


    @Override
    public UserDto getUserByUserId(String userId) {
        User userEntity = userRepository.findUserByUserId(userId);
        if (userEntity == null) throw  new UsernameNotFoundException(userId);
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userEntity, userDto);
        return userDto;
    }

    @Override
    public UserDto updateUser(String userId, UserDto userDto) {
        User user = userRepository.findUserByUserId(userId);
        if (user == null) throw  new UsernameNotFoundException(userId);
        user.setUsername(userDto.getUsername());
        user.setTelephone(userDto.getTelephone());
        user.setEmail(userDto.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        User updateUser = userRepository.save(user);
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(updateUser, dto);
        return dto;
    }

    @Override
    public void deleteUser(String userId) {
        User userEntity = userRepository.findUserByUserId(userId);
        if (userEntity == null) throw  new UsernameNotFoundException(userId);
        userRepository.delete(userEntity);
    }

    @Override
    public UserDto updateUserAccount(String userId, UserDto userDto) {
        User user = userRepository.findUserByUserId(userId);
        if (user == null) throw  new UsernameNotFoundException(userId);
        user.setEmailVerificationStatus(userDto.getEmailVerificationStatus());
        User updateUser = userRepository.save(user);
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(updateUser, dto);
        return dto;
    }

   

}
