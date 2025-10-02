package org.openmrs.api.db.mongo;

import org.openmrs.*;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.PatientDAO;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository("patientDAO")
@Profile("mongo")
public class MongoPatientDAO implements PatientDAO {

    private final MongoTemplate mongoTemplate;

    public MongoPatientDAO(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Patient savePatient(Patient patient) throws DAOException {
        mongoTemplate.save(patient, "persons");
        return patient;
    }

    @Override
    public Patient getPatient(Integer patientId) throws DAOException {
        return mongoTemplate.findById(patientId, Patient.class, "persons");
    }

    @Override
    public void deletePatient(Patient patient) throws DAOException {
        mongoTemplate.remove(patient, "persons");
    }

    @Override
    public List<Patient> getAllPatients(boolean includeVoided) throws DAOException { return Collections.emptyList(); }

    @Override
    public List<Patient> getPatients(String query, Integer start, Integer length) throws DAOException { return Collections.emptyList(); }

    @Override
    public List<Patient> getPatients(String query, boolean includeVoided, Integer start, Integer length) throws DAOException { return Collections.emptyList(); }

    @Override
    public List<Patient> getPatients(String name, List<PatientIdentifierType> identifierTypes, boolean matchIdentifierExactly, Integer start, Integer length) throws DAOException { return Collections.emptyList(); }

    @Override
    public List<PatientIdentifier> getPatientIdentifiers(String identifier, List<PatientIdentifierType> patientIdentifierTypes, List<Location> locations, List<Patient> patients, Boolean isPreferred) throws DAOException { return Collections.emptyList(); }

    @Override
    public PatientIdentifierType savePatientIdentifierType(PatientIdentifierType patientIdentifierType) throws DAOException { throw new UnsupportedOperationException(); }

    @Override
    public List<PatientIdentifierType> getAllPatientIdentifierTypes(boolean includeRetired) throws DAOException { return Collections.emptyList(); }

    @Override
    public List<PatientIdentifierType> getPatientIdentifierTypes(String name, String format, Boolean required, Boolean hasCheckDigit) throws DAOException { return Collections.emptyList(); }

    @Override
    public PatientIdentifierType getPatientIdentifierType(Integer patientIdentifierTypeId) throws DAOException { return null; }

    @Override
    public void deletePatientIdentifierType(PatientIdentifierType patientIdentifierType) throws DAOException { throw new UnsupportedOperationException(); }

    @Override
    public List<Patient> getDuplicatePatientsByAttributes(List<String> attributes) throws DAOException { return Collections.emptyList(); }

    @Override
    public boolean isIdentifierInUseByAnotherPatient(PatientIdentifier patientIdentifier) { return false; }

    @Override
    public Patient getPatientByUuid(String uuid) { return mongoTemplate.findById(uuid, Patient.class, "persons"); }

    @Override
    public PatientIdentifier getPatientIdentifierByUuid(String uuid) { return null; }

    @Override
    public PatientIdentifierType getPatientIdentifierTypeByUuid(String uuid) { return null; }

    @Override
    public PatientIdentifier getPatientIdentifier(Integer patientIdentifierId) throws DAOException { return null; }

    @Override
    public PatientIdentifier savePatientIdentifier(PatientIdentifier patientIdentifier) { throw new UnsupportedOperationException(); }

    @Override
    public void deletePatientIdentifier(PatientIdentifier patientIdentifier) throws DAOException { throw new UnsupportedOperationException(); }

    @Override
    public Long getCountOfPatients(String query) { return 0L; }

    @Override
    public Long getCountOfPatients(String query, boolean includeVoided) { return 0L; }

    @Override
    public List<Allergy> getAllergies(Patient patient) { return Collections.emptyList(); }

    @Override
    public String getAllergyStatus(Patient patient) { return null; }

    @Override
    public Allergies saveAllergies(Patient patient, Allergies allergies) { return allergies; }

    @Override
    public Allergy getAllergy(Integer allergyId) { return null; }

    @Override
    public Allergy getAllergyByUuid(String uuid) { return null; }

    @Override
    public Allergy saveAllergy(Allergy allergy) { throw new UnsupportedOperationException(); }

    @Override
    public List getPatientIdentifierByProgram(PatientProgram patientProgram) { return Collections.emptyList(); }
}
