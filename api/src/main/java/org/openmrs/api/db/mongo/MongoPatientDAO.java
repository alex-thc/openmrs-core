/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.api.db.mongo;

import org.openmrs.*;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.PatientDAO;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MongoPatientDAO implements PatientDAO {

    private final MongoTemplate mongoTemplate;
    private static final String COLLECTION = "persons";

    public MongoPatientDAO(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Patient savePatient(Patient patient) throws DAOException {
        // upsert by personId or uuid
        Query q = personMatchQuery(patient.getPatientId(), patient.getUuid());
        Update u = new Update();
        if (patient.getPatientId() != null) u.set("personId", patient.getPatientId());
        if (patient.getUuid() != null) u.set("uuid", patient.getUuid());
        if (patient.getAllergyStatus() != null) u.set("allergyStatus", patient.getAllergyStatus());
        // write identifiers as simplified array
        List<Map<String, Object>> ids = new ArrayList<>();
        for (PatientIdentifier pi : patient.getIdentifiers()) {
            ids.add(toIdentifierDoc(pi));
        }
        u.set("identifiers", ids);
        mongoTemplate.upsert(q, u, COLLECTION);
        return patient;
    }

    @Override
    public Patient getPatient(Integer patientId) throws DAOException {
        Query q = Query.query(Criteria.where("personId").is(patientId));
        Map d = mongoTemplate.findOne(q, Map.class, COLLECTION);
        return d == null ? null : toPatient(d);
    }

    @Override
    public void deletePatient(Patient patient) throws DAOException {
        Query q = personMatchQuery(patient.getPatientId(), patient.getUuid());
        mongoTemplate.remove(q, COLLECTION);
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
    public Patient getPatientByUuid(String uuid) {
        Query q = Query.query(Criteria.where("uuid").is(uuid));
        Map d = mongoTemplate.findOne(q, Map.class, COLLECTION);
        return d == null ? null : toPatient(d);
    }

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
    public List<Allergy> getAllergies(Patient patient) {
        Map d = mongoTemplate.findOne(personMatchQuery(patient.getPatientId(), patient.getUuid()), Map.class, COLLECTION);
        if (d == null) return Collections.emptyList();
        List<Map<String, Object>> arr = (List<Map<String, Object>>) d.getOrDefault("allergies", Collections.emptyList());
        List<Allergy> out = new ArrayList<>();
        for (Map<String, Object> ad : arr) {
            out.add(fromAllergyDoc(ad, patient));
        }
        return out;
    }

    @Override
    public String getAllergyStatus(Patient patient) {
        Map d = mongoTemplate.findOne(personMatchQuery(patient.getPatientId(), patient.getUuid()), Map.class, COLLECTION);
        if (d == null) return null;
        Object val = d.get("allergyStatus");
        return val == null ? null : String.valueOf(val);
    }

    @Override
    public Allergies saveAllergies(Patient patient, Allergies allergies) {
        Query q = personMatchQuery(patient.getPatientId(), patient.getUuid());
        Update u = new Update();
        if (allergies != null) {
            List<Map<String, Object>> arr = new ArrayList<>();
            for (Allergy a : allergies) {
                arr.add(toAllergyDoc(a));
            }
            u.set("allergies", arr);
            if (allergies.getAllergyStatus() != null) {
                u.set("allergyStatus", allergies.getAllergyStatus());
            }
        } else {
            u.unset("allergies");
        }
        mongoTemplate.updateFirst(q, u, COLLECTION);
        return allergies;
    }

    @Override
    public Allergy getAllergy(Integer allergyId) {
        Query q = Query.query(Criteria.where("allergies").elemMatch(Criteria.where("allergyId").is(allergyId)));
        Map d = mongoTemplate.findOne(q, Map.class, COLLECTION);
        if (d == null) return null;
        List<Map<String, Object>> arr = (List<Map<String, Object>>) d.get("allergies");
        if (arr == null) return null;
        for (Map<String, Object> ad : arr) {
            if (Objects.equals(allergyId, (Integer) ad.get("allergyId"))) {
                Patient p = toPatient(d);
                return fromAllergyDoc(ad, p);
            }
        }
        return null;
    }

    @Override
    public Allergy getAllergyByUuid(String uuid) {
        Query q = Query.query(Criteria.where("allergies").elemMatch(Criteria.where("uuid").is(uuid)));
        Map d = mongoTemplate.findOne(q, Map.class, COLLECTION);
        if (d == null) return null;
        List<Map<String, Object>> arr = (List<Map<String, Object>>) d.get("allergies");
        if (arr == null) return null;
        for (Map<String, Object> ad : arr) {
            if (uuid.equals(ad.get("uuid"))) {
                Patient p = toPatient(d);
                return fromAllergyDoc(ad, p);
            }
        }
        return null;
    }

    @Override
    public Allergy saveAllergy(Allergy allergy) {
        Patient patient = allergy.getPatient();
        if (patient == null) throw new IllegalArgumentException("Allergy must have patient");
        Query q = personMatchQuery(patient.getPatientId(), patient.getUuid());
        Map d = mongoTemplate.findOne(q, Map.class, COLLECTION);
        if (d == null) d = new HashMap<>();
        List<Map<String, Object>> arr = (List<Map<String, Object>>) d.get("allergies");
        if (arr == null) arr = new ArrayList<>();
        Map<String, Object> doc = toAllergyDoc(allergy);
        // replace by allergyId or uuid if present
        boolean replaced = false;
        for (int i = 0; i < arr.size(); i++) {
            Map<String, Object> e = arr.get(i);
            Integer eid = (Integer) e.get("allergyId");
            String eu = (String) e.get("uuid");
            if ((allergy.getAllergyId() != null && Objects.equals(eid, allergy.getAllergyId())) ||
                (allergy.getUuid() != null && Objects.equals(eu, allergy.getUuid()))) {
                arr.set(i, doc);
                replaced = true;
                break;
            }
        }
        if (!replaced) arr.add(doc);
        Update u = new Update().set("allergies", arr);
        if (patient.getAllergyStatus() != null) u.set("allergyStatus", patient.getAllergyStatus());
        mongoTemplate.updateFirst(q, u, COLLECTION);
        return allergy;
    }

    @Override
    public List getPatientIdentifierByProgram(PatientProgram patientProgram) { return Collections.emptyList(); }

    // --------------------- Helpers ---------------------
    private static Query personMatchQuery(Integer personId, String uuid) {
        if (personId != null) return Query.query(Criteria.where("personId").is(personId));
        if (uuid != null) return Query.query(Criteria.where("uuid").is(uuid));
        throw new IllegalArgumentException("Either personId or uuid must be provided");
    }

    private static Map<String, Object> toIdentifierDoc(PatientIdentifier pi) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (pi.getPatientIdentifierId() != null) m.put("patientIdentifierId", pi.getPatientIdentifierId());
        if (pi.getIdentifier() != null) m.put("identifier", pi.getIdentifier());
        if (pi.getPreferred() != null) m.put("preferred", pi.getPreferred());
        if (pi.getIdentifierType() != null) {
            Map<String, Object> t = new LinkedHashMap<>();
            if (pi.getIdentifierType().getPatientIdentifierTypeId() != null)
                t.put("patientIdentifierTypeId", pi.getIdentifierType().getPatientIdentifierTypeId());
            if (pi.getIdentifierType().getName() != null) t.put("name", pi.getIdentifierType().getName());
            m.put("identifierType", t);
        }
        if (pi.getLocation() != null && pi.getLocation().getLocationId() != null) {
            Map<String, Object> l = new LinkedHashMap<>();
            l.put("locationId", pi.getLocation().getLocationId());
            m.put("location", l);
        }
        return m;
    }

    private static Patient toPatient(Map d) {
        Integer personId = (Integer) d.get("personId");
        Patient p = new Patient(personId);
        Object uo = d.get("uuid");
        if (uo != null) p.setUuid(String.valueOf(uo));
        Object as = d.get("allergyStatus");
        if (as != null) p.setAllergyStatus(String.valueOf(as));
        List<Map<String, Object>> ids = (List<Map<String, Object>>) d.get("identifiers");
        if (ids != null) {
            Set<PatientIdentifier> set = new HashSet<>();
            for (Map<String, Object> im : ids) {
                PatientIdentifier pi = new PatientIdentifier();
                if (im.get("patientIdentifierId") != null) pi.setPatientIdentifierId((Integer) im.get("patientIdentifierId"));
                if (im.get("identifier") != null) pi.setIdentifier((String) im.get("identifier"));
                if (im.get("preferred") != null) pi.setPreferred(Boolean.valueOf(String.valueOf(im.get("preferred"))));
                Map<String, Object> t = (Map<String, Object>) im.get("identifierType");
                if (t != null) {
                    PatientIdentifierType pit = new PatientIdentifierType();
                    if (t.get("patientIdentifierTypeId") != null)
                        pit.setPatientIdentifierTypeId((Integer) t.get("patientIdentifierTypeId"));
                    if (t.get("name") != null) pit.setName((String) t.get("name"));
                    pi.setIdentifierType(pit);
                }
                Map<String, Object> l = (Map<String, Object>) im.get("location");
                if (l != null && l.get("locationId") != null) {
                    Location loc = new Location((Integer) l.get("locationId"));
                    pi.setLocation(loc);
                }
                pi.setPatient(p);
                set.add(pi);
            }
            p.setIdentifiers(set);
        }
        return p;
    }

    private static Map<String, Object> toAllergyDoc(Allergy a) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (a.getAllergyId() != null) m.put("allergyId", a.getAllergyId());
        if (a.getUuid() != null) m.put("uuid", a.getUuid());
        if (a.getComments() != null) m.put("comments", a.getComments());
        if (a.getAllergen() != null) {
            Map<String, Object> al = new LinkedHashMap<>();
            if (a.getAllergen().getAllergenType() != null) al.put("type", a.getAllergen().getAllergenType().name());
            if (a.getAllergen().getCodedAllergen() != null) {
                Map<String, Object> c = new LinkedHashMap<>();
                if (a.getAllergen().getCodedAllergen().getConceptId() != null)
                    c.put("conceptId", a.getAllergen().getCodedAllergen().getConceptId());
                if (a.getAllergen().getCodedAllergen().getUuid() != null)
                    c.put("uuid", a.getAllergen().getCodedAllergen().getUuid());
                al.put("coded", c);
            }
            if (a.getAllergen().getNonCodedAllergen() != null) al.put("nonCoded", a.getAllergen().getNonCodedAllergen());
            m.put("allergen", al);
        }
        if (a.getSeverity() != null) {
            Map<String, Object> s = new LinkedHashMap<>();
            if (a.getSeverity().getConceptId() != null) s.put("conceptId", a.getSeverity().getConceptId());
            if (a.getSeverity().getUuid() != null) s.put("uuid", a.getSeverity().getUuid());
            m.put("severity", s);
        }
        if (a.getReactions() != null && !a.getReactions().isEmpty()) {
            List<Map<String, Object>> rs = new ArrayList<>();
            for (AllergyReaction r : a.getReactions()) {
                Map<String, Object> rm = new LinkedHashMap<>();
                if (r.getAllergyReactionId() != null) rm.put("allergyReactionId", r.getAllergyReactionId());
                if (r.getReaction() != null) {
                    Map<String, Object> rc = new LinkedHashMap<>();
                    if (r.getReaction().getConceptId() != null) rc.put("conceptId", r.getReaction().getConceptId());
                    if (r.getReaction().getUuid() != null) rc.put("uuid", r.getReaction().getUuid());
                    rm.put("reaction", rc);
                }
                if (r.getReactionNonCoded() != null) rm.put("reactionNonCoded", r.getReactionNonCoded());
                rs.add(rm);
            }
            m.put("reactions", rs);
        }
        if (a.getEncounter() != null && a.getEncounter().getEncounterId() != null) {
            Map<String, Object> enc = new LinkedHashMap<>();
            enc.put("encounterId", a.getEncounter().getEncounterId());
            m.put("encounter", enc);
        }
        return m;
    }

    private static Allergy fromAllergyDoc(Map<String, Object> m, Patient patient) {
        Allergy a = new Allergy();
        a.setPatient(patient);
        if (m.get("allergyId") != null) a.setAllergyId((Integer) m.get("allergyId"));
        if (m.get("uuid") != null) a.setUuid((String) m.get("uuid"));
        if (m.get("comments") != null) a.setComments((String) m.get("comments"));
        Map<String, Object> al = (Map<String, Object>) m.get("allergen");
        if (al != null) {
            Allergen allergen = new Allergen();
            if (al.get("type") != null) allergen.setAllergenType(AllergenType.valueOf((String) al.get("type")));
            Map<String, Object> c = (Map<String, Object>) al.get("coded");
            if (c != null) {
                Concept cc = new Concept();
                if (c.get("conceptId") != null) cc.setConceptId((Integer) c.get("conceptId"));
                if (c.get("uuid") != null) cc.setUuid((String) c.get("uuid"));
                allergen.setCodedAllergen(cc);
            }
            if (al.get("nonCoded") != null) allergen.setNonCodedAllergen((String) al.get("nonCoded"));
            a.setAllergen(allergen);
        }
        Map<String, Object> s = (Map<String, Object>) m.get("severity");
        if (s != null) {
            Concept sc = new Concept();
            if (s.get("conceptId") != null) sc.setConceptId((Integer) s.get("conceptId"));
            if (s.get("uuid") != null) sc.setUuid((String) s.get("uuid"));
            a.setSeverity(sc);
        }
        List<Map<String, Object>> rs = (List<Map<String, Object>>) m.get("reactions");
        if (rs != null) {
            List<AllergyReaction> out = new ArrayList<>();
            for (Map<String, Object> rm : rs) {
                AllergyReaction r = new AllergyReaction();
                if (rm.get("allergyReactionId") != null) r.setAllergyReactionId((Integer) rm.get("allergyReactionId"));
                Map<String, Object> rc = (Map<String, Object>) rm.get("reaction");
                if (rc != null) {
                    Concept c = new Concept();
                    if (rc.get("conceptId") != null) c.setConceptId((Integer) rc.get("conceptId"));
                    if (rc.get("uuid") != null) c.setUuid((String) rc.get("uuid"));
                    r.setReaction(c);
                }
                if (rm.get("reactionNonCoded") != null) r.setReactionNonCoded((String) rm.get("reactionNonCoded"));
                r.setAllergy(a);
                out.add(r);
            }
            a.setReactions(out);
        }
        Map<String, Object> enc = (Map<String, Object>) m.get("encounter");
        if (enc != null && enc.get("encounterId") != null) {
            Encounter e = new Encounter();
            e.setEncounterId((Integer) enc.get("encounterId"));
            a.setEncounter(e);
        }
        return a;
    }
}
