package com.sankalp.prototype.services;

import com.sankalp.prototype.dtos.RegistrationRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RegistrationService {

    private final EntityManager entityManager;
    public RegistrationService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public String registerUser(RegistrationRequest requestDto) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("USP_RECRUT_USER_REGISTER_LOGIN");

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

            return (String) query.getOutputParameterValue("O_MESSAGE");
        }
        catch (Exception e) {
            throw new RuntimeException("Database execution failed. Details " + e.getMessage());
        }
    }
}