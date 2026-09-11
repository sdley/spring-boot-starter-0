package sn.sdley.springbootstarter0.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import sn.sdley.springbootstarter0.repositories.ProfileRepository;

@AllArgsConstructor
@Service
public class UserService {

    private final ProfileRepository profileRepository;



}
