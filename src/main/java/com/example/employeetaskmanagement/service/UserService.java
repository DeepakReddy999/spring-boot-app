package com.example.employeetaskmanagement.service;

import com.example.employeetaskmanagement.dto.UserProfileResponseDto;
import com.example.employeetaskmanagement.entity.User;

public interface UserService {

    UserProfileResponseDto getCurrentUserProfile(String email);

    User getUserEntityByEmail(String email);
}
