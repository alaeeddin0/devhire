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

        public RecruiterProfileResponse getProfileById(Long id) {
                return toResponse(getProfileEntityById(id));
        }

        private RecruiterProfile getProfileEntityById(Long id) {
                return recruiterProfileRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Profil recruteur introuvable avec l'id : " + id));
        }

        @Transactional
        public RecruiterProfileResponse updateProfile(
                        Long id,
                        UpdateRecruiterProfileRequest request) {
                RecruiterProfile profile = getProfileEntityById(id);

                profile.setCompanyName(request.companyName());
                profile.setCompanyDescription(request.companyDescription());
                profile.setCompanyWebsite(request.companyWebsite());

                return toResponse(recruiterProfileRepository.save(profile));
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
                        CreateRecruiterProfileRequest request) {
                User user = userRepository.findById(request.userId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Utilisateur introuvable avec l'id : " + request.userId()));

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
}
