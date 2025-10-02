# Call Graph for OpenMRS Core: People, Patients, Relationships

## Executive Summary
I created a comprehensive Mermaid call graph for the entities in the ERD (Person, Patient, PersonName, PersonAddress, PersonAttribute, PersonAttributeType, PatientIdentifier, PatientIdentifierType, Relationship, RelationshipType, PersonMergeLog). The graph shows:

- External API surface: service interface methods annotated with authorization privileges (exposed to modules and web layers) [1][2].
- Internal implementation and helper methods (service impl classes) [3][4].
- DAO persistence calls (direct database access) [5][6].
- Indirect/cross-service dependencies (e.g., EncounterService, ObsService, ProgramWorkflowService, UserService, SerializationService, AdministrationService) [3][4].
- Note on HTTP endpoints: OpenMRS Core does not ship REST controllers; endpoints live in the “webservices.rest” module. The diagram marks that external services are typically consumed by REST controllers there.

Diagram file (with all text labels quoted):
- people-patients-relationships-callgraph.mmd

Open it in any Mermaid renderer or in Factory’s file viewer to see the fully styled graph.

## What’s Included
- Entities from the ERD are represented and linked to the external service interface methods that operate on them.
- Service interfaces (external) include specific method names and privilege hints (e.g., GET_PERSONS, MANAGE_IDENTIFIER_TYPES) from annotations [1][2].
- Service implementations (internal) map those methods to DAOs and other services, including validation and business rules (e.g., mergePatients, processDeath) [3][4].
- DAO methods (persistence) for CRUD and queries are shown for both Person and Patient domains [5][6].
- Indirect references include Context-driven calls to AdministrationService (global properties), SerializationService (PersonMergeLog), Encounter/Visit/Program/Obs/User/Concept/Location services used by PatientServiceImpl during merges and cause-of-death workflows [3][4].

## How to Read the Diagram
- External API nodes are the service interface methods (e.g., "PersonService.savePerson") and are styled distinctly. These are the methods typically exposed to modules and REST controllers [1][2].
- Internal nodes are the implementation/helper methods in PersonServiceImpl and PatientServiceImpl. They apply validations, set preferred flags, and orchestrate domain logic [3][4].
- DAO nodes show the persistence layer that implementations delegate to for CRUD and queries [5][6].
- “Other Service” nodes represent cross-service usage (Encounter, Visit, Program, Obs, User, Concept, Location, Serialization, Administration). These are indirect references invoked by the core service implementations [3][4].
- Endpoints: The diagram includes a note that HTTP endpoints come from the REST module, which consumes these service methods. URIs are not part of openmrs-core and thus not listed here.

## Highlights and Examples
- Person attribute types lifecycle: "PersonService.savePersonAttributeType" → updates global properties when renaming and triggers search index update if "searchable" changes → DAO save → AdministrationService and SearchIndex utilities [3][5].
- Patient merging: "PatientService.mergePatients" orchestrates moving visits, encounters, programs, relationships, identifiers, names, addresses, attributes; voids the non-preferred patient; creates a PersonMergeLog via PersonService; and touches Order/Visit/Encounter/Program/Obs/User services as needed [4].
- Cause of death: "PatientService.processDeath" sets death flags, saves cause-of-death observations (through ObsService with Concept and Location services), and exits the patient from care [4].

## Limitations
- HTTP endpoints (URIs, controllers) are not in openmrs-core; they are provided by the webservices.rest module. The diagram marks their relationship but does not enumerate URIs.

## Sources
1. PersonService.java (service interface, external API, @Authorized privileges)
   - https://github.com/openmrs/openmrs-core/blob/master/api/src/main/java/org/openmrs/api/PersonService.java
2. PatientService.java (service interface, external API, @Authorized privileges)
   - https://github.com/openmrs/openmrs-core/blob/master/api/src/main/java/org/openmrs/api/PatientService.java
3. PersonServiceImpl.java (service implementation, internal logic, DAO and cross-service calls)
   - https://github.com/openmrs/openmrs-core/blob/master/api/src/main/java/org/openmrs/api/impl/PersonServiceImpl.java
4. PatientServiceImpl.java (service implementation, internal logic, DAO and cross-service calls)
   - https://github.com/openmrs/openmrs-core/blob/master/api/src/main/java/org/openmrs/api/impl/PatientServiceImpl.java
5. PersonDAO.java (persistence contract, Person/Relationship/PersonAttribute/PersonMergeLog)
   - https://github.com/openmrs/openmrs-core/blob/master/api/src/main/java/org/openmrs/api/db/PersonDAO.java
6. PatientDAO.java (persistence contract, Patient/Identifiers/Types/Allergies)
   - https://github.com/openmrs/openmrs-core/blob/master/api/src/main/java/org/openmrs/api/db/PatientDAO.java
