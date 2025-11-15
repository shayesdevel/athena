# athena-core

Core domain logic for Athena platform.

## Responsibilities
- JPA entities (19 models)
- Spring Data JPA repositories
- Business logic services (35+ services)
- Flyway database migrations
- Domain events

## Key Components
- Entities: Opportunity, WorkspaceScore, User, TeamingPartner, etc.
- Repositories: OpportunityRepository, UserRepository, etc.
- Services: ScoringService, TransformerFactory, PartnerDiscoveryService
- Migrations: Flyway scripts in `src/main/resources/db/migration/`

## Database
- PostgreSQL 17 + pgvector extension
- HikariCP connection pool
- Multi-tenant support via workspace isolation
