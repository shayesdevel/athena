# Project Memory - Athena

**Purpose**: Track architectural decisions, trade-offs, and constraints so future agents/developers understand WHY things are the way they are
**Format**: Lightweight decision log (ADR-style)
**Audience**: All agents, future maintainers
**Last Updated**: 2025-11-15

---

## How to Use This File

**For AI Agents**:
- Read this file when starting a new session
- Add new decisions when making significant architectural choices
- Update status when revisiting past decisions

**For Humans**:
- Document major decisions here
- Link to detailed discussions (PRs, issues, session journals)
- Keep it current - archive old decisions to ARCHITECTURE.md

---

## Decision Log

### MEM-001: Framework Selection
**Date**: 2025-11-15
**Status**: ACCEPTED
**Deciders**: User + Nexus (Orchestrator)
**Category**: [ARCHITECTURE]

**Decision**: Use cognitive-framework v2.2 for multi-agent orchestration

**Context**:
- Large project scope: 65K LOC Cerberus migration to Java/Spring over 24 weeks
- User explicitly requested "full framework setup" with multi-agent orchestration
- Need parallelization to meet timeline (11 phases, systematic migration)
- Framework v2.2 provides setup improvements (<30 min setup time)

**Consequences**:
- ✅ **Positive**:
  - Expected 3-4x velocity from parallel domain execution (6 agents)
  - D-series protocols prevent hallucinations (D009), ensure quality (Gates 0-4)
  - Wave-based + parallel execution patterns provide structure
  - Session continuity via D014 protocol
- ⚠️ **Negative**:
  - Learning curve for framework patterns and protocols
  - Coordination overhead (orchestrator must manage 6 concurrent agents)
  - Dependency on framework stability and updates
- 🔧 **Mitigation**:
  - Framework has comprehensive documentation and quick-reference guides
  - CLAUDE.md documents all patterns and protocols for agent reference
  - Scribe agent handles documentation burden automatically

**Alternatives Considered**:
1. **Single agent approach**: Rejected - 24-week timeline too long for sequential work
2. **Manual multi-agent coordination**: Rejected - No quality protocols, high risk of conflicts
3. **Smaller agent team (3-4 agents)**: Rejected - 6 clear domains justify full team

**Related**:
- Framework: `/home/shayesdevel/projects/cognitive-framework`
- Setup session: `docs/00-active/journal/session-01-foundation-setup.md`
- Architecture: See ARCHITECTURE.md "Multi-Agent Coordination Model"

---

### MEM-002: Technology Stack - Java 21 + Spring Boot 3.5.7
**Date**: 2025-11-15 (Updated: 2025-11-15 for latest versions)
**Status**: ACCEPTED
**Deciders**: User + Nexus
**Category**: [TECH_STACK]

**Decision**: Use Java 21, Spring Boot 3.5.7, Gradle 9.2.0 for backend; PostgreSQL 17 + pgvector for database

**Context**:
- Greenfield rebuild of Cerberus (currently FastAPI + Celery + React)
- User requirement: "canonical Nov 2025 tech stack" with modern Java/Spring patterns
- User explicitly chose: Gradle (not Maven), MVC (not WebFlux), Spring Batch (not Celery)
- PostgreSQL schema unchanged from Cerberus (19 entities, relationships preserved)

**Why These Technologies**:

**Java 21**:
- Modern LTS version with latest features (records, pattern matching, virtual threads)
- Enterprise-ready with strong Spring Boot ecosystem
- Performance suitable for federal contract intelligence workload

**Spring Boot 3.5.7**:
- Latest stable release (Oct 23, 2025) - 69 bug fixes, documentation improvements, dependency upgrades
- Upgraded to Spring Framework 6.2.12, Hibernate 6.6.33.Final, Tomcat 10.1.48
- Comprehensive ecosystem (Data JPA, Security, Batch, Actuator)
- Production-ready patterns (externalized config, health checks, metrics)

**Gradle 9.2.0**:
- Latest stable release (Oct 29, 2025) - ARM64 support, faster work graph building
- Modern build features (version catalogs, configuration cache, incremental builds)
- Stable daemon toolchain (no longer incubating)
- User preference over Maven
- Multi-module support for clean architecture boundaries

**Spring MVC (not WebFlux)**:
- User preference for blocking model (simpler mental model)
- Suitable for I/O patterns (database queries, external API calls)
- Easier migration from FastAPI blocking patterns

**Spring Batch + @Scheduled (not Celery)**:
- User preference - simpler deployment than multi-container Celery setup
- Native Spring Boot integration
- Suitable for background tasks (opportunity sync, AI scoring, PDF generation)

**PostgreSQL 17 + pgvector**:
- Hard constraint - schema unchanged from Cerberus
- pgvector extension required for AI similarity search
- 19 entities, relationships preserved

**Consequences**:
- ✅ **Positive**:
  - Modern Java features improve code quality
  - Simpler deployment (monolith vs FastAPI + Celery multi-container)
  - Strong Spring Boot ecosystem and community
  - Gradle 8.x modern build features
- ⚠️ **Negative**:
  - Monolith may require microservices extraction later (planned)
  - FastAPI async → Spring MVC blocking migration requires performance testing
  - Gradle learning curve for Maven-familiar developers
- 🔧 **Mitigation**:
  - Multi-module structure (MEM-004) enables future microservices extraction
  - Performance Engineer (Tier 2) will validate async → blocking migration
  - DevOps Engineer provides Gradle expertise

**Alternatives Evaluated**:

| Technology | Pros | Cons | Why Not Chosen |
|------------|------|------|----------------|
| Maven | Industry standard, familiar | Less modern features than Gradle 8.x | User preference for Gradle |
| Spring WebFlux | Reactive, async | Complex mental model, harder migration | User preference for MVC blocking |
| Celery (keep it) | Existing knowledge | Multi-container complexity | User preference for Spring Batch |
| MySQL/MariaDB | Popular alternatives | No pgvector support | Hard constraint: PostgreSQL 17 |

**Related**:
- ARCHITECTURE.md: "Decision 1: Technology Stack"
- Session 01: Lines 75-99
- Module structure: `build.gradle.kts`, `settings.gradle.kts`

---

### MEM-003: Multi-Agent Setup - 6 Tier 1 + 2 Tier 2
**Date**: 2025-11-15
**Status**: ACCEPTED
**Deciders**: User + Nexus
**Category**: [ARCHITECTURE]

**Decision**: 6 Tier 1 agents (always active) + 2 Tier 2 agents (on-demand) with D013 worktree isolation

**Agent Roster**:

**Tier 0 (Orchestrator)**:
- Nexus: Coordination, quality gates, PR integration

**Tier 1 (Core Specialists - Always Active)**:
- Scribe: Documentation, session journals, D014 enforcement
- Data Architect: JPA entities, repositories, Flyway migrations
- Backend Architect: Services, REST controllers, external API clients
- Frontend Specialist: React/TypeScript, Vite, API client integration
- QA Specialist: Testing (unit, integration, E2E), quality gate enforcement
- DevOps Engineer: Gradle build, GitHub Actions, AWS CDK, Docker

**Tier 2 (On-Demand Specialists)**:
- Security Auditor: Spring Security audits, OWASP compliance, penetration testing
- Performance Engineer: Load testing, benchmarking vs Cerberus, optimization

**Context**:
- Large project scope: 65K LOC migration, 11 phases, 24-week timeline
- Clear domain boundaries enable parallel work (data vs backend vs frontend vs tests vs DevOps)
- User requested "full framework setup" - not minimal agent count
- 6 domains justify 6 specialist agents

**Why D013 Worktree Isolation**:
- 6 parallel agents require conflict-free coordination
- Each agent works in dedicated worktree on feature branch
- Orchestrator (Nexus) coordinates merge order (dependency-aware)
- Prevents merge chaos, enables true parallelism

**Consequences**:
- ✅ **Positive**:
  - Expected 3-4x velocity from parallelization (6 agents working simultaneously)
  - Clear domain boundaries prevent conflicts and confusion
  - Scribe ensures session continuity (D014 compliance)
  - QA Specialist enforces quality throughout (not just at end)
- ⚠️ **Negative**:
  - Worktree management overhead (6 separate environments + main repo)
  - Coordination complexity (orchestrator must manage 6 concurrent work streams)
  - Scribe adds documentation overhead
- 🔧 **Mitigation**:
  - D013 protocol provides standardized worktree management
  - PATH_REFERENCE_GUIDE.md documents all worktree paths
  - Scribe handles documentation automatically (removes burden from other agents)
  - Framework v2.2 improvements reduce setup time

**Alternatives Considered**:
1. **Single agent**: Rejected - 24-week timeline too long, parallelization required
2. **3-4 agents**: Rejected - 6 clear domains justify full team, velocity gains worth coordination cost
3. **No worktree isolation**: Rejected - 6 parallel agents would create merge chaos

**Related**:
- CLAUDE.md: "Agent Roster"
- ARCHITECTURE.md: "Agent Hierarchy", "Domain Boundaries"
- Session 01: Lines 100-121
- Worktrees: `/home/shayesdevel/projects/athena-worktrees/` (to be created in Session 02)

---

### MEM-004: Multi-Module Gradle Project
**Date**: 2025-11-15
**Status**: ACCEPTED
**Deciders**: Nexus (Orchestrator)
**Category**: [ARCHITECTURE]

**Decision**: 4 Gradle modules (athena-api, athena-core, athena-tasks, athena-common) + frontend + infrastructure directories

**Module Structure**:
- **athena-api**: Spring Boot REST API, controllers, OpenAPI spec, main application class
- **athena-core**: Domain models (JPA entities), repositories, business services, external API clients
- **athena-tasks**: Background jobs (Spring Batch jobs, @Scheduled tasks, async processors)
- **athena-common**: Shared utilities (Jackson configs, validation, common DTOs, constants)
- **frontend/**: React/TypeScript/Vite app (not a Gradle module)
- **infrastructure/**: AWS CDK definitions (not a Gradle module)

**Dependency Rules**:
- athena-api → athena-core (API layer depends on domain)
- athena-tasks → athena-core (background tasks depend on domain)
- athena-common → standalone (no dependencies)
- athena-core → athena-common (domain uses shared utilities)

**Context**:
- Cerberus is complex: 65K LOC across multiple concerns (API, domain, background tasks)
- Need clean architecture boundaries for multi-agent work
- Future microservices extraction planned (post-MVP)
- Spring Boot best practices recommend separation of concerns

**Why Multi-Module**:
- **Separation of concerns**: API vs domain vs background tasks clearly separated
- **Future-proof**: athena-core can become shared library when extracting microservices
- **Testability**: Easier to test modules in isolation (athena-core tests without Spring MVC)
- **Agent clarity**: Clear ownership (Backend Architect owns api + core/service, Data Architect owns core/entity)

**Consequences**:
- ✅ **Positive**:
  - Clean architecture boundaries prevent domain confusion
  - Enables future microservices extraction (athena-core → shared library)
  - Easier to test modules in isolation
  - Follows Spring Boot best practices
  - Agent domain boundaries clearer (Data Architect owns athena-core/entity, Backend Architect owns athena-core/service + athena-api)
- ⚠️ **Negative**:
  - Slightly more complex build configuration than single module
  - Dependency management across modules requires discipline
  - Circular dependency risk if not careful
- 🔧 **Mitigation**:
  - DevOps Engineer owns build configuration and dependency management
  - Dependency rules documented and enforced (api → core, tasks → core, common standalone)
  - Gradle multi-project configuration prevents circular dependencies

**Alternatives Considered**:
1. **Single module**: Rejected - Poor separation of concerns, harder to extract microservices later
2. **More modules (per entity/feature)**: Rejected - Premature complexity, over-engineering
3. **Separate repos per module**: Rejected - Overkill for monolith, complicates local development

**Related**:
- ARCHITECTURE.md: "Decision 4: Multi-Module Gradle Project"
- Session 01: Lines 141-159
- Build files: `build.gradle.kts`, `settings.gradle.kts`, module READMEs

---

### MEM-005: SAM.gov Data Source - Cached JSON (No API Key)
**Date**: 2025-11-15
**Status**: ACCEPTED
**Deciders**: User
**Category**: [CONSTRAINTS]

**Decision**: Use cached SAM.gov JSON data instead of live API integration. No SAM.gov API key available.

**Context**:
- Athena is a proof of concept to demonstrate ELT → LLM → analysis workflow to leadership
- SAM.gov API key not available for prototype development
- User has cached JSON data from previous Cerberus system
- Goal: Validate technical approach, not production-ready integration

**What This Means**:

**Data Source**:
- Use cached/static SAM.gov JSON files (opportunities, awards, vendor data)
- No live API calls to SAM.gov during prototype phase
- Data may be stale but sufficient for proof of concept

**Implementation Impact**:
- Backend Architect: Implement file-based data loaders instead of HTTP API clients
- Data Architect: Schema unchanged (same entities as Cerberus)
- No SAM.gov API client in athena-core/client/ (deferred to post-prototype)

**Prototype Scope**:
- Demonstrate: Data ingestion (JSON → PostgreSQL) → AI analysis (LLM scoring) → insights
- Validate: Architecture, multi-agent workflow, AI scoring logic
- NOT validating: Real-time SAM.gov sync, API error handling, rate limiting

**Future Migration Path** (Post-Prototype):
- Add SAM.gov API client when API key available
- Implement rate limiting, retry logic, error handling
- Add scheduled jobs for incremental sync (@Scheduled + Spring Batch)
- Migrate from static JSON loading to live API calls

**Consequences**:
- ✅ **Positive**:
  - Faster prototype development (no API key procurement, no rate limiting complexity)
  - Focus on core value: ELT → LLM → analysis workflow
  - Sufficient for leadership demo and proof of concept
  - Reduces external dependencies during development
- ⚠️ **Negative**:
  - Stale data (not current opportunities from SAM.gov)
  - Cannot validate real-time sync workflows
  - API integration work deferred to later phase
- 🔧 **Mitigation**:
  - Cached data recent enough for meaningful demo
  - Architecture supports future API integration (service layer abstraction)
  - Post-prototype, add SAM.gov API client with minimal refactoring

**Alternatives Considered**:
1. **Request SAM.gov API key**: Rejected - Procurement timeline too long for prototype
2. **Use SAM.gov public data downloads**: Considered - Similar to cached JSON approach
3. **Mock data generation**: Rejected - Real Cerberus data more convincing for leadership

**Related**:
- GitHub Issue #4: External Integrations (SAM.gov API client deferred)
- Backend Architect: Will implement JSON file loaders instead of HTTP clients
- Data Architect: Schema unchanged, entities support both static and live data

---

## Decision Status Definitions

**PROPOSED**: Decision suggested but not yet accepted
**ACCEPTED**: Decision made and active
**DEPRECATED**: Decision no longer valid but kept for history
**SUPERSEDED**: Decision replaced by newer decision (link to new one)

---

## Decision Categories

Tag decisions with categories for easier navigation:

**[ARCHITECTURE]**: System structure, patterns, coordination
**[TECH_STACK]**: Technology choices (languages, frameworks, tools)
**[PROCESS]**: Development workflows, quality gates, protocols
**[CONSTRAINTS]**: Hard/soft constraints, non-negotiables
**[TRADE-OFFS]**: Explicit trade-offs accepted

---

## Quick Reference: Recent Decisions

| ID | Date | Title | Status | Category |
|----|------|-------|--------|----------|
| MEM-001 | 2025-11-15 | Framework Selection | ACCEPTED | [ARCHITECTURE] |
| MEM-002 | 2025-11-15 | Technology Stack | ACCEPTED | [TECH_STACK] |
| MEM-003 | 2025-11-15 | Multi-Agent Setup | ACCEPTED | [ARCHITECTURE] |
| MEM-004 | 2025-11-15 | Multi-Module Gradle Project | ACCEPTED | [ARCHITECTURE] |
| MEM-005 | 2025-11-15 | SAM.gov Data Source (Cached JSON) | ACCEPTED | [CONSTRAINTS] |

---

## Archived Decisions

(When MEMORY.md grows too large, move old ACCEPTED decisions to ARCHITECTURE.md)

None yet - all decisions recent and active.

---

## Lessons Learned

### Lesson 1: Foundation Setup Before Agent Work
**Date**: 2025-11-15 (Session 02)
**Context**: Session 01 created scaffolding but deferred worktree setup and agent context customization to Session 02
**Lesson**: Cannot delegate to specialist agents until foundation is complete (worktrees created, agent contexts customized, Gate 0 passes)
**Action**: Session 02 focuses on completing foundation before delegating Issue #2 to Data Architect
**Related**: Session 02 journal (this session)

### Lesson 2: Orchestrator Must Delegate and Parallelize (CRITICAL)
**Date**: 2025-11-15 (Session 02 - End of session directive)
**Context**: User emphasized "moving forward -- you must be delegating and parallelizing when appropriate"
**Lesson**:
- **Nexus (orchestrator) primary role is COORDINATION, not implementation**
- **Default behavior**: Delegate all implementation work to specialist agents
- **Exception only**: Cross-cutting architecture, PR merging, conflict resolution, quality gate enforcement
- **Parallelization**: Use parallel domain execution pattern whenever agents have independent work
- **Expected velocity**: 3-4x speedup through parallel agent work

**Action Going Forward**:
- Session 03+: Delegate Issue #2 to Data Architect immediately
- Launch Scribe for ongoing documentation (reduce orchestrator burden)
- Coordinate parallel work streams (data + tests + docs simultaneously)
- Use Task tool to launch specialist agents, not implement directly
- Monitor, merge, validate - don't build

**Why Critical**: Framework designed for parallel velocity gains. Orchestrator doing all work defeats the purpose and loses 3-4x multiplier.

**Related**: Session 02 journal, ARCHITECTURE.md (Parallel Domain Execution pattern), MEM-003 (Multi-Agent Setup)

---

## Anti-Patterns to Avoid

### ❌ Delegate Work Before Gate 0 Passes
**Tried**: N/A (prevented by explicit Gate 0 check)
**Problem**: Agents cannot work effectively without:
  - ARCHITECTURE.md (no coordination model)
  - MEMORY.md (no decision context)
  - Worktrees (D013 violations, merge conflicts)
  - Customized agent contexts (agents don't know their paths/tools)
**Never Again**: Always validate Gate 0 (Architecture Completeness) before delegating specialist work
**Instead Do**: Complete foundation setup first (ARCHITECTURE.md, MEMORY.md, worktrees, agent contexts, quality gates)

---

## Future Considerations

### Future-001: Microservices Extraction
**Why Deferred**: Starting with monolith (MEM-002) for simplicity, plan extraction post-MVP
**Revisit When**: After feature parity achieved (Issue #12 complete) OR if monolith performance issues arise
**Context**:
  - Multi-module structure (MEM-004) enables extraction
  - Likely candidates: AI scoring service (CPU-intensive), background task workers (Spring Batch jobs)
  - athena-core becomes shared library
**Options**:
  - Extract athena-tasks → separate deployment (Spring Batch workers)
  - Extract AI scoring → separate service (dedicated resources)
  - Keep athena-api + athena-core as monolith

### Future-002: SAM.gov API Integration
**Why Deferred**: No API key for prototype (MEM-005)
**Revisit When**: Leadership approves production development, API key procurement begins
**Context**:
  - Prototype uses cached JSON data
  - Service layer abstraction supports future API client integration
  - Will need: Rate limiting, retry logic, incremental sync, error handling
**Options**:
  - Implement HTTP client in athena-core/client/
  - Use Spring Batch for scheduled sync jobs
  - Add Redis caching to reduce API calls

### Future-003: Multi-Way Audit Pattern Activation
**Why Deferred**: Not needed until Phase 9 (Testing Migration)
**Revisit When**: Issue #9 begins (278 JUnit tests to migrate)
**Context**:
  - 278 tests require parallel validation by QA + Backend + Data agents
  - Multi-way audit pattern provides 4x speedup
**Options**:
  - Activate pattern as documented in cognitive-framework
  - QA Specialist coordinates, Backend + Data validate domain-specific tests

---

## Changelog

**2025-11-15 (Session 01)**: Project initialized with cognitive-framework v2.2
- Created MEM-001: Framework Selection
- Created MEM-002: Technology Stack
- Created MEM-003: Multi-Agent Setup
- Created MEM-004: Multi-Module Gradle Project
- Established 6 Tier 1 + 2 Tier 2 agent roster
- Configured D013 worktree isolation (worktrees to be created in Session 02)

**2025-11-15 (Session 02)**: Foundation completion
- Populated MEMORY.md with session 01 decisions
- Created MEM-005: SAM.gov Data Source constraint (cached JSON, no API key)
- Lesson 1: Complete foundation before delegating work
- Lesson 2: **CRITICAL - Orchestrator must delegate and parallelize (user directive)**
- Anti-pattern documented: Never delegate before Gate 0 passes
- Customized quality-gates.md (411 lines, 54+ placeholders replaced)
- Created 6 worktrees per D013 protocol
- Foundation 80% complete, ready for multi-agent work
