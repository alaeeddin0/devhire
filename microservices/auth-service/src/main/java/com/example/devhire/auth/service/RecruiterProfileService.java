package com.example.devhire.auth.service;

import com.example.devhire.auth.dto.profile.CreateRecruiterProfileRequest;
import com.example.devhire.auth.dto.profile.RecruiterProfileResponse;
import com.example.devhire.auth.dto.profile.UpdateRecruiterProfileRequest;
import com.example.devhire.auth.model.RecruiterProfile;
import com.example.devhire.auth.model.User;
import com.example.devhire.auth.model.UserRole;
import com.example.devhire.auth.repo.RecruiterProfileRepository;
import com.example.devhire.auth.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RecruiterProfileService {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final UserRepository userRepository;

    public RecruiterProfileResponse create(
            String email,
            CreateRecruiterProfileRequest request) {

        User user = getRecruiterUser(email);

        if (recruiterProfileRepository.existsByUserId(user.getId())) {
            throw new IllegalArgumentException(
                    "Le profil recruteur existe déjà.");
        }

        RecruiterProfile profile = new RecruiterProfile();
        profile.setUser(user);
        apply(profile, request);

        return toResponse(recruiterProfileRepository.save(profile));
    }

    @Transactional(readOnly = true)
    public RecruiterProfileResponse getMyProfile(String email) {
        return toResponse(getProfileByEmail(email));
    }

    public RecruiterProfileResponse update(
            String email,
            UpdateRecruiterProfileRequest request) {

        RecruiterProfile profile = getProfileByEmail(email);
        apply(profile, request);

        return toResponse(recruiterProfileRepository.save(profile));
    }

    private User getRecruiterUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilisateur introuvable."));

        if (user.getRole() != UserRole.RECRUITER) {
            throw new IllegalArgumentException(
                    "Seul un recruteur peut posséder un profil recruteur.");
        }

        return user;
    }

    private RecruiterProfile getProfileByEmail(String email) {
        return recruiterProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Profil recruteur introuvable."));
    }

    private void apply(
            RecruiterProfile profile,
            CreateRecruiterProfileRequest request) {

        profile.setCompanyName(request.companyName().trim());
        profile.setCompanyDescription(request.companyDescription());
        profile.setCompanyWebsite(request.companyWebsite());
    }

    private void apply(
            RecruiterProfile profile,
            UpdateRecruiterProfileRequest request) {

        profile.setCompanyName(request.companyName().trim());
        profile.setCompanyDescription(request.companyDescription());
        profile.setCompanyWebsite(request.companyWebsite());
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
}