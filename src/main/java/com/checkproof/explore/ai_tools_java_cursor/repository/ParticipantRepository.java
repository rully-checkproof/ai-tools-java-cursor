package com.checkproof.explore.ai_tools_java_cursor.repository;

import com.checkproof.explore.ai_tools_java_cursor.model.Participant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    // Find participant by email
    Optional<Participant> findByEmail(String email);

    // Find participant by email (case-insensitive)
    Optional<Participant> findByEmailIgnoreCase(String email);

    // Find participants by type
    List<Participant> findByTypeOrderByNameAsc(Participant.ParticipantType type);

    // Find participants by type with pagination
    Page<Participant> findByTypeOrderByNameAsc(Participant.ParticipantType type, Pageable pageable);

    // Find active participants
    List<Participant> findByIsActiveTrueOrderByNameAsc();

    // Find active participants with pagination
    Page<Participant> findByIsActiveTrueOrderByNameAsc(Pageable pageable);

    // Find participants by name containing (case-insensitive)
    @Query("SELECT p FROM Participant p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY p.name ASC")
    List<Participant> findByNameContainingIgnoreCase(@Param("name") String name);

    // Find participants by name containing with pagination
    @Query("SELECT p FROM Participant p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY p.name ASC")
    Page<Participant> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    // Find participants by phone number
    List<Participant> findByPhoneNumberOrderByNameAsc(String phoneNumber);

    // Find participants by phone number with pagination
    Page<Participant> findByPhoneNumberOrderByNameAsc(String phoneNumber, Pageable pageable);

    // Find participants by phone number containing
    @Query("SELECT p FROM Participant p WHERE p.phoneNumber LIKE CONCAT('%', :phoneNumber, '%') ORDER BY p.name ASC")
    List<Participant> findByPhoneNumberContaining(@Param("phoneNumber") String phoneNumber);

    // Find participants by phone number containing with pagination
    @Query("SELECT p FROM Participant p WHERE p.phoneNumber LIKE CONCAT('%', :phoneNumber, '%') ORDER BY p.name ASC")
    Page<Participant> findByPhoneNumberContaining(@Param("phoneNumber") String phoneNumber, Pageable pageable);

    // Find participants by bio containing (case-insensitive)
    @Query("SELECT p FROM Participant p WHERE LOWER(p.bio) LIKE LOWER(CONCAT('%', :bio, '%')) ORDER BY p.name ASC")
    List<Participant> findByBioContainingIgnoreCase(@Param("bio") String bio);

    // Find participants by bio containing with pagination
    @Query("SELECT p FROM Participant p WHERE LOWER(p.bio) LIKE LOWER(CONCAT('%', :bio, '%')) ORDER BY p.name ASC")
    Page<Participant> findByBioContainingIgnoreCase(@Param("bio") String bio, Pageable pageable);

    // Find participants by type and active status
    List<Participant> findByTypeAndIsActiveOrderByNameAsc(Participant.ParticipantType type, Boolean isActive);

    // Find participants by type and active status with pagination
    Page<Participant> findByTypeAndIsActiveOrderByNameAsc(Participant.ParticipantType type, Boolean isActive, Pageable pageable);

    // Count participants by type
    long countByType(Participant.ParticipantType type);

    // Count active participants
    long countByIsActiveTrue();

    // Count inactive participants
    long countByIsActiveFalse();

    // Check if email exists
    boolean existsByEmail(String email);

    // Check if email exists (case-insensitive)
    boolean existsByEmailIgnoreCase(String email);

    // Check if phone number exists
    boolean existsByPhoneNumber(String phoneNumber);
} 