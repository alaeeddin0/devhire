package com.example.devhire.auth.service;

import com.example.devhire.auth.dto.profile.CandidateProfileResponse;
import com.example.devhire.auth.dto.profile.CreateCandidateProfileRequest;
import com.example.devhire.auth.dto.profile.UpdateCandidateProfileRequest;
import com.example.devhire.auth.model.CandidateProfile;
import com.example.devhire.auth.model.User;
import com.example.devhire.auth.model.UserRole;
import com.example.devhire.auth.repo.CandidateProfileRepository;
import com.example.devhire.auth.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateProfileService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;

    public CandidateProfileResponse create(
            String email,
            CreateCandidateProfileRequest request) {

        User user = getCandidateUser(email);

        if (candidateProfileRepository.existsByUserId(user.getId())) {
            throw new IllegalArgumentException(
                    "Le profil candidat existe déjà.");
        }

        CandidateProfile profile = new CandidateProfile();
        profile.setUser(user);
        profile.setPhone(request.phone());
        profile.setCity(request.city());

        return toResponse(candidateProfileRepository.save(profile));
    }

    @Transactional(readOnly = true)
    public CandidateProfileResponse getMyProfile(String email) {
        return toResponse(getProfileByEmail(email));
    }

    public CandidateProfileResponse update(
            String email,
            UpdateCandidateProfileRequest request) {

        CandidateProfile profile = getProfileByEmail(email);
        profile.setPhone(request.phone());
        profile.setCity(request.city());

        return toResponse(candidateProfileRepository.save(profile));
    }

    private User getCandidateUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilisateur introuvable."));

        if (user.getRole() != UserRole.CANDIDATE) {
            throw new IllegalArgumentException(
                    "Seul un candidat peut posséder un profil candidat.");
        }

        return user;
    }

    private CandidateProfile getProfileByEmail(String email) {
        return candidateProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Profil candidat introuvable."));
    }

    private CandidateProfileResponse toResponse(
            CandidateProfile profile) {

        return new CandidateProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getPhone(),
                profile.getCity());
    }
}