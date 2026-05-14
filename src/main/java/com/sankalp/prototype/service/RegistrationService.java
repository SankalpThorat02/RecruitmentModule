package com.sankalp.prototype.service;

import com.sankalp.prototype.dto.RegistrationRequest;

import com.sankalp.prototype.util.QueryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
public class RegistrationService {

    private final EntityManager entityManager;
    public RegistrationService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    private QueryService queryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public String registerUser(RegistrationRequest requestDto) {
        String procedureName = queryService.getQuery("procedure.registerUser");
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery(procedureName);

        query.registerStoredProcedureParameter("P_FIRST_NAME", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_LAST_NAME", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_DOB", LocalDate.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_REFERENCE", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_EMAIL_ID", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_CARD_TYPE", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_CARD_NUMBER", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_MOBILE_NO", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_POSITION_APPLIED", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_QUALIFICATION", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_SPECIALIZATION", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_COLLEGE_NAME", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_ROLE_ID", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("O_MESSAGE", String.class, ParameterMode.OUT);

        query.setParameter("P_FIRST_NAME", requestDto.getFirstName());
        query.setParameter("P_LAST_NAME", requestDto.getLastName());
        query.setParameter("P_DOB", requestDto.getDob());
        query.setParameter("P_REFERENCE", requestDto.getReference());
        query.setParameter("P_EMAIL_ID", requestDto.getEmailId());
        query.setParameter("P_CARD_TYPE", requestDto.getCardType());
        query.setParameter("P_CARD_NUMBER", requestDto.getCardNumber());
        query.setParameter("P_MOBILE_NO", requestDto.getMobileNo());
        query.setParameter("P_POSITION_APPLIED", requestDto.getPositionApplied());
        query.setParameter("P_QUALIFICATION", requestDto.getQualification());
        query.setParameter("P_SPECIALIZATION", requestDto.getSpecialization());
        query.setParameter("P_COLLEGE_NAME", requestDto.getCollegeName());
        query.setParameter("P_ROLE_ID", requestDto.getRoleId());

        try {
            query.execute();

            String outputMessage = (String) query.getOutputParameterValue("O_MESSAGE");
            log.info("[AUDIT] Stored procedure completed for email={} with message={}", requestDto.getEmailId(), outputMessage);

            //Password Encryption
            Optional<User> newUser = userRepository.findByEmail(requestDto.getEmailId());
            if(newUser.isPresent()) {
                User user = newUser.get();

                String rawDbPassword = user.getPassword();
                String encryptedPassword = passwordEncoder.encode(rawDbPassword);

                user.setPassword(encryptedPassword);
                userRepository.save(user);

                log.info("[AUDIT] Successfully hashed and updated password for user Id={}", user.getId());
            } else {
                log.warn("[WARN] Registration succeeded but could not find user by email={} to hash password", requestDto.getEmailId());
            }

            return outputMessage;
        }
        catch (Exception e) {
            log.error("[ERROR] Database execution failed. Details {}", e.getMessage());
            throw new RuntimeException("Database execution failed. Details " + e.getMessage());
        }
    }
}