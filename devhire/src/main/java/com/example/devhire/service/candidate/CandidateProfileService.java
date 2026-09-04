package com.example.devhire.service.candidate;

import com.example.devhire.dto.candidate.CandidateProfileResponse;
import com.example.devhire.dto.candidate.CreateCandidateProfileRequest;
import com.example.devhire.dto.candidate.UpdateCandidateProfileRequest;
import com.example.devhire.exception.ResourceNotFoundException;
import com.example.devhire.model.candidate.CandidateProfile;
import com.example.devhire.model.user.User;
import com.example.devhire.model.user.UserRole;
import com.example.devhire.repo.candidate.CandidateProfileRepository;
import com.example.devhire.repo.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CandidateProfileService {

        private final CandidateProfileRepository candidateProfileRepository;
        private final UserRepository userRepository;

        private CandidateProfile getCandidateByEmail(String email) {
                return candidateProfileRepository.findByUserEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
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

        public CandidateProfileResponse getCurrentProfile(String email) {
                CandidateProfile profile = candidateProfileRepository
                                .findByUserEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Profil candidat introuvable."));

                return toResponse(profile);
        }

        @Transactional
        public CandidateProfileResponse createProfile(
                        String email,
                        CreateCandidateProfileRequest request) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Utilisateur introuvable."));
                                                
                if (user.getRole() != UserRole.CANDIDATE) {
                        throw new IllegalArgumentException(
                                        "Cet utilisateur n'a pas le rôle CANDIDATE.");
                }

                if (candidateProfileRepository.existsByUserId(user.getId())) {
                        throw new IllegalArgumentException(
                                        "Ce candidat possède déjà un profil.");
                }

                CandidateProfile profile = new CandidateProfile();
                profile.setUser(user);
                profile.setPhone(request.phone());
                profile.setCity(request.city());

                CandidateProfile savedProfile = candidateProfileRepository.save(profile);

                return new CandidateProfileResponse(
                                savedProfile.getId(),
                                user.getId(),
                                savedProfile.getPhone(),
                                savedProfile.getCity());
        }

        @Transactional
        public CandidateProfileResponse updateCurrentProfile(
                        String email,
                        UpdateCandidateProfileRequest request) {
                CandidateProfile profile = getCandidateByEmail(email);

                profile.setPhone(request.phone());
                profile.setCity(request.city());

                return toResponse(candidateProfileRepository.save(profile));
        }

}
