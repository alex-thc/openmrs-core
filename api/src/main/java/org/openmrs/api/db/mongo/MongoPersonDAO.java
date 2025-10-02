package org.openmrs.api.db.mongo;

import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.PersonDAO;
import org.openmrs.person.PersonMergeLog;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class MongoPersonDAO implements PersonDAO {

    private final MongoTemplate mongoTemplate;

    public MongoPersonDAO(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Set<Person> getSimilarPeople(String name, Integer birthyear, String gender) throws DAOException {
        return Collections.emptySet();
    }

    @Override
    public List<Person> getPeople(String searchPhrase, Boolean dead) throws DAOException {
        return Collections.emptyList();
    }

    @Override
    public List<Person> getPeople(String searchPhrase, Boolean dead, Boolean voided) throws DAOException {
        return Collections.emptyList();
    }

    @Override
    public PersonAttributeType savePersonAttributeType(PersonAttributeType type) throws DAOException {
        throw new UnsupportedOperationException("Not implemented in mongo shim");
    }

    @Override
    public void deletePersonAttributeType(PersonAttributeType type) throws DAOException {
        throw new UnsupportedOperationException("Not implemented in mongo shim");
    }

    @Override
    public List<PersonAttributeType> getPersonAttributeTypes(String exactName, String format, Integer foreignKey, Boolean searchable) throws DAOException {
        return Collections.emptyList();
    }

    @Override
    public List<PersonAttributeType> getAllPersonAttributeTypes(boolean includeRetired) throws DAOException {
        return Collections.emptyList();
    }

    @Override
    public PersonAttributeType getPersonAttributeType(Integer typeId) throws DAOException {
        return null;
    }

    @Override
    public PersonAttribute getPersonAttribute(Integer id) throws DAOException { return null; }

    @Override
    public Relationship getRelationship(Integer relationshipId) throws DAOException { return null; }

    @Override
    public List<Relationship> getAllRelationships(boolean includeVoided) throws DAOException { return Collections.emptyList(); }

    @Override
    public RelationshipType getRelationshipType(Integer relationshipTypeId) throws DAOException { return null; }

    @Override
    public List<RelationshipType> getRelationshipTypes(String relationshipTypeName, Boolean preferred) throws DAOException { return Collections.emptyList(); }

    @Override
    public Person savePerson(Person person) throws DAOException {
        mongoTemplate.save(person, "persons");
        return person;
    }

    @Override
    public void deletePerson(Person person) throws DAOException {
        mongoTemplate.remove(person, "persons");
    }

    @Override
    public Person getPerson(Integer personId) throws DAOException {
        return mongoTemplate.findById(personId, Person.class, "persons");
    }

    @Override
    public Relationship saveRelationship(Relationship relationship) throws DAOException { throw new UnsupportedOperationException(); }

    @Override
    public void deleteRelationship(Relationship relationship) throws DAOException { throw new UnsupportedOperationException(); }

    @Override
    public List<Relationship> getRelationships(Person fromPerson, Person toPerson, RelationshipType relType) throws DAOException { return Collections.emptyList(); }

    @Override
    public List<Relationship> getRelationships(Person fromPerson, Person toPerson, RelationshipType relType, Date startEffectiveDate, Date endEffectiveDate) throws DAOException { return Collections.emptyList(); }

    @Override
    public RelationshipType saveRelationshipType(RelationshipType relationshipType) throws DAOException { throw new UnsupportedOperationException(); }

    @Override
    public void deleteRelationshipType(RelationshipType relationshipType) throws DAOException { throw new UnsupportedOperationException(); }

    @Override
    public Person getPersonByUuid(String uuid) { return mongoTemplate.findById(uuid, Person.class, "persons"); }

    @Override
    public PersonAddress getPersonAddressByUuid(String uuid) { return null; }

    @Override
    public PersonAttribute getPersonAttributeByUuid(String uuid) { return null; }

    @Override
    public PersonName getPersonName(Integer personNameId) { return null; }

    @Override
    public PersonName getPersonNameByUuid(String uuid) { return null; }

    @Override
    public Relationship getRelationshipByUuid(String uuid) { return null; }

    @Override
    public RelationshipType getRelationshipTypeByUuid(String uuid) { return null; }

    @Override
    public PersonAttributeType getPersonAttributeTypeByUuid(String uuid) { return null; }

    @Override
    public String getSavedPersonAttributeTypeName(PersonAttributeType personAttributeType) { return null; }

    @Override
    public Boolean getSavedPersonAttributeTypeSearchable(PersonAttributeType personAttributeType) { return null; }

    @Override
    public List<RelationshipType> getAllRelationshipTypes(boolean includeRetired) { return Collections.emptyList(); }

    @Override
    public PersonMergeLog savePersonMergeLog(PersonMergeLog personMergeLog) throws DAOException { throw new UnsupportedOperationException(); }

    @Override
    public PersonMergeLog getPersonMergeLog(Integer id) throws DAOException { return null; }

    @Override
    public PersonMergeLog getPersonMergeLogByUuid(String uuid) throws DAOException { return null; }

    @Override
    public List<PersonMergeLog> getAllPersonMergeLogs() throws DAOException { return Collections.emptyList(); }

    @Override
    public List<PersonMergeLog> getWinningPersonMergeLogs(Person person) throws DAOException { return Collections.emptyList(); }

    @Override
    public PersonMergeLog getLosingPersonMergeLogs(Person person) throws DAOException { return null; }

    @Override
    public PersonName savePersonName(PersonName personName) { throw new UnsupportedOperationException(); }

    @Override
    public PersonAddress savePersonAddress(PersonAddress personAddress) { throw new UnsupportedOperationException(); }
}
