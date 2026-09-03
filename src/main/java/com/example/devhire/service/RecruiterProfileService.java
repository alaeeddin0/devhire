package com.example.devhire.service;

import com.example.devhire.dto.recruiter.CreateRecruiterProfileRequest;
import com.example.devhire.dto.recruiter.RecruiterProfileResponse;
import com.example.devhire.dto.recruiter.UpdateRecruiterProfileRequest;
import com.example.devhire.exception.ResourceNotFoundException;
import com.example.devhire.model.RecruiterProfile;
import com.example.devhire.model.User;
import com.example.devhire.model.UserRole;
import com.example.devhire.repo.RecruiterProfileRepository;
import com.example.devhire.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecruiterProfileService {

        private final RecruiterProfileRepository recruiterProfileRepository;
        private final UserRepository userRepository;

        public RecruiterProfileResponse getCurrentProfile(String email) {
                RecruiterProfile profile = recruiterProfileRepository
                                .findByUserEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Profil recruteur introuvable."));

                return toResponse(profile);
        }

        private RecruiterProfileResponse toResponse(
                        RecruiterProfile profile) {
                return new RecruiterProfileResponse(
                                profile.getId(),
                                profile.getUser().getId(),
                                profile.getCompanyName(),
                                profile.getCompanyDescription(),
                                profile.getCompanyWebsite());
        }

        @Transactional
        public RecruiterProfileResponse createProfile(
                        String email,
                        CreateRecruiterProfileRequest request) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Utilisateur introuvable."));

                if (user.getRole() != UserRole.RECRUITER) {
                        throw new IllegalArgumentException(
                                        "Cet utilisateur n'a pas le rôle RECRUITER.");
                }

                if (recruiterProfileRepository.existsByUserId(user.getId())) {
                        throw new IllegalArgumentException(
                                        "Ce recruteur possède déjà un profil.");
                }

                RecruiterProfile profile = new RecruiterProfile();
                profile.setUser(user);
                profile.setCompanyName(request.companyName());
                profile.setCompanyDescription(request.companyDescription());
                profile.setCompanyWebsite(request.companyWebsite());

                RecruiterProfile savedProfile = recruiterProfileRepository.save(profile);

                return new RecruiterProfileResponse(
                                savedProfile.getId(),
                                user.getId(),
                                savedProfile.getCompanyName(),
                                savedProfile.getCompanyDescription(),
                                savedProfile.getCompanyWebsite());
        }
        
        @Transactional
        public RecruiterProfileResponse updateCurrentProfile(
                        String email,
                        UpdateRecruiterProfileRequest request) {
                RecruiterProfile profile = getRecruiterByEmail(email);

                profile.setCompanyName(request.companyName());
                profile.setCompanyDescription(request.companyDescription());
                profile.setCompanyWebsite(request.companyWebsite());

                return toResponse(recruiterProfileRepository.save(profile));
        }
        
        private RecruiterProfile getRecruiterByEmail(String email) {
                return recruiterProfileRepository.findByUserEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Profil recruteur introuvable."));
        }
        
}
