package com.example.devhire.service;

import com.example.devhire.dto.candidate.CandidateProfileResponse;
import com.example.devhire.dto.candidate.CreateCandidateProfileRequest;
import com.example.devhire.dto.candidate.UpdateCandidateProfileRequest;
import com.example.devhire.exception.ResourceNotFoundException;
import com.example.devhire.model.CandidateProfile;
import com.example.devhire.model.User;
import com.example.devhire.model.UserRole;
import com.example.devhire.repo.CandidateProfileRepository;
import com.example.devhire.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CandidateProfileService {

        private final CandidateProfileRepository candidateProfileRepository;
        private final UserRepository userRepository;

        public CandidateProfileResponse getProfileById(Long id) {
                return toResponse(getProfileEntityById(id));
        }

        private CandidateProfile getProfileEntityById(Long id) {
                return candidateProfileRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Profil candidat introuvable avec l'id : " + id));
        }

        private CandidateProfileResponse toResponse(
                        CandidateProfile profile) {
                return new CandidateProfileResponse(
                                profile.getId(),
                                profile.getUser().getId(),
                                profile.getPhone(),
                                profile.getCity());
        }
        @Transactional
        public CandidateProfileResponse createProfile(
                        CreateCandidateProfileRequest request) {
                User user = userRepository.findById(request.userId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Utilisateur introuvable avec l'id : " + request.userId()));

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
        public CandidateProfileResponse updateProfile(
                        Long id,
                        UpdateCandidateProfileRequest request) {
                CandidateProfile profile = getProfileEntityById(id);

                profile.setPhone(request.phone());
                profile.setCity(request.city());

                return toResponse(candidateProfileRepository.save(profile));
        }


}
