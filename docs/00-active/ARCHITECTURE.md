# Architecture Documentation - Athena

**Purpose**: Document how agents coordinate, which patterns are active, and why architectural decisions were made
**Audience**: AI agents, human developers, future maintainers
**Last Updated**: 2025-11-15

---

## Multi-Agent Coordination Model

### Overview

This project uses a **multi-agent cognitive architecture** with 8 specialist agents coordinated by an orchestrator.

**Coordination Style**: Centralized
- **Orchestrator (Nexus)**: Makes high-level decisions, delegates to specialists, integrates work
- **Specialists**: Domain experts working in parallel on specific areas
- **Integration**: Orchestrator merges PRs after review, validates integration via D009b

### Agent Hierarchy

```
Nexus (Orchestrator - Tier 0)
├── Scribe (Tier 1 - Core)
├── Backend Architect (Tier 1 - Core)
├── Data Architect (Tier 1 - Core)
├── Frontend Specialist (Tier 1 - Core)
├── QA Specialist (Tier 1 - Core)
├── DevOps Engineer (Tier 1 - Core)
├── Security Auditor (Tier 2 - On-Demand)
└── Performance Engineer (Tier 2 - On-Demand)
```

**Tier 0 (Orchestrator - Nexus)**:
- Exclusive control of shared environment (main repo at `/home/shayesdevel/projects/athena`)
- Coordinates all specialist work
- Manages quality gates
- Integrates contributions via PR merge

**Tier 1 (Core Specialists - 6 agents)**:
- Always active throughout project lifecycle
- Work in parallel on their domains using dedicated worktrees
- Own their domain's test suites
- Create PRs for orchestrator review

**Tier 2 (On-Demand Specialists - 2 agents)**:
- Activated for specific tasks (security audits, performance testing)
- Provide expertise when needed
- May share worktrees with Tier 1 agents

---

## Active Orchestration Patterns

### Pattern Selection Rationale

Athena is a large-scale greenfield rebuild (65K LOC Cerberus → Java/Spring migration) requiring systematic migration across 9 phases over 24 weeks. Two patterns were chosen to maximize velocity while maintaining quality.

### ✅ Active Patterns

#### 1. Parallel Domain Execution
**Status**: ACTIVE
**Source**: `/home/shayesdevel/projects/cognitive-framework/cognitive-core/orchestration/patterns/parallel-domain-execution.md`
**Speedup**: 4x (theoretical maximum with 6 parallel agents)

**Description**: Multiple specialist agents work simultaneously on independent domains (data layer, backend services, frontend, tests) with minimal coordination overhead.

**Why chosen**:
- Clear domain boundaries minimize conflicts (data vs backend vs frontend vs tests)
- Worktree isolation (D013) enables true parallelism without merge conflicts
- Large project scope benefits from concurrent work streams
- 6 Tier 1 agents can work independently on their specialized areas

**When used**:
- Feature development spanning multiple domains (e.g., new entity → JPA model, REST endpoint, React UI)
- Bug fixes in different modules (e.g., backend service bug + frontend UI fix in parallel)
- Phase execution (e.g., Phase 2 data layer + Phase 5 REST API development simultaneously)

**Evidence**: Pattern configured in session 01, worktree isolation to be validated in session 02

---

#### 2. Wave-Based Execution
**Status**: ACTIVE
**Source**: `/home/shayesdevel/projects/cognitive-framework/cognitive-core/orchestration/patterns/wave-based-execution.md`
**Speedup**: 1.3-1.5x

**Description**: Break large migration into systematic waves (phases), complete each wave before proceeding to next, enabling incremental validation and risk mitigation.

**Why chosen**:
- 65K LOC migration too large for single sprint
- 11 GitHub issues (phases 2-12) provide natural wave boundaries
- Enables incremental testing and validation (complete data layer before service layer)
- Reduces risk of cascading failures across domains

**When used**:
- EPIC execution (Issue #1 broken into Issues #2-#12)
- Phase transitions (complete Phase 2 data layer → validate → begin Phase 3 service layer)
- Major architectural milestones (foundatio → implementation → testing → deployment)

**Evidence**: Session 01 created 11-phase roadmap, foundation wave (Phase 1) complete

---

### ❌ Considered But Not Used

#### Time-Sensitive Updates
**Status**: NOT ACTIVE
**Reason**: Athena is a greenfield rebuild, not a production system with real-time data feeds. No time-critical data integration required during development phase.

#### Multi-Way Audit
**Status**: NOT ACTIVE (Yet)
**Reason**: Quality audits will be handled by QA Specialist during Phase 9 (testing migration). May activate this pattern when migrating 278 JUnit tests across multiple domains.

---

## Protocol Enforcement

### Mandatory Protocols

#### D009: Commit Verification
**Status**: ENFORCED
**Coverage**: All commits by all agents
**Automation**: Quality Gate 3
**Failure Mode**: PR blocked until verification passes

**Implementation**:
```bash
# In quality-gates.md Gate 3
# After commit, before PR:
git diff HEAD~1 HEAD  # Review actual changes
./gradlew build       # Validate build succeeds
./gradlew test        # Validate tests pass
```

**Metrics**: Not yet tracking (project in foundation setup)

---

#### D012: Git Attribution
**Status**: ENFORCED
**Format**: `Co-Authored-By: {AgentName} <agent@athena.project>`
**Automation**: Manual enforcement during commit creation

**Implementation**:
- All agent commits include Co-Authored-By line
- Orchestrator commits may include multiple agents for collaborative work
- Tracked via git log analysis

**Example**:
```
feat: Add Opportunity JPA entity and repository

Implements Spring Data JPA entity for SAM.gov opportunities

Co-Authored-By: Data Architect <agent@athena.project>
```

---

#### D013: Worktree Isolation
**Status**: ENFORCED
**Rationale**: 6 Tier 1 agents working in parallel require conflict-free coordination. Worktree isolation prevents merge conflicts and enables true parallelism.

**Worktree Configuration**:
- **Worktree Root**: `/home/shayesdevel/projects/athena-worktrees/`
- **Shared Environment**: `/home/shayesdevel/projects/athena` (Nexus exclusive)
- **Active Worktrees**:
  - `/home/shayesdevel/projects/athena-worktrees/scribe/` → Scribe
  - `/home/shayesdevel/projects/athena-worktrees/backend-architect/` → Backend Architect
  - `/home/shayesdevel/projects/athena-worktrees/data-architect/` → Data Architect
  - `/home/shayesdevel/projects/athena-worktrees/frontend-specialist/` → Frontend Specialist
  - `/home/shayesdevel/projects/athena-worktrees/qa-specialist/` → QA Specialist
  - `/home/shayesdevel/projects/athena-worktrees/devops-engineer/` → DevOps Engineer

**Conflict Resolution Strategy**:
- Each agent works in dedicated worktree on feature branch
- Orchestrator coordinates merge order (dependency-aware)
- Agents communicate via session journal and GitHub PR comments

---

#### D014: Session End Protocol
**Status**: ENFORCED
**Purpose**: Structured handoff between sessions
**Location**: Session journals in `docs/00-active/journal/`

**Required elements**:
- Session summary (goals, accomplishments, status)
- Work completed (with links to commits/PRs)
- Blockers encountered (and mitigation strategies)
- Next steps (immediate actions for next session)
- Handoff notes (for agents and human developers)

**Compliance**: Session 01 journal fully compliant (344 lines, comprehensive documentation)

---

### Optional Protocols

#### D009b: Post-PR Verification
**Status**: ENABLED
**Rationale**: Multi-agent work requires validation that merged PRs integrate correctly (no broken tests, no runtime failures)

**Implementation**:
- After PR merge, orchestrator runs full build + test suite
- Integration tests validate cross-domain functionality
- Failures trigger rollback and agent re-work

---

## Architectural Decisions (ADR-Style)

### Decision 1: Technology Stack - Java 21 + Spring Boot 3.2
**Date**: 2025-11-15
**Status**: ACCEPTED
**Deciders**: User + Nexus (Orchestrator)

**Context**:
Greenfield rebuild of Cerberus (FastAPI + Celery + React) federal contract intelligence platform. User requirement: "canonical Nov 2025 tech stack" with modern Java/Spring patterns.

**Decision**:
- **Backend**: Java 21, Spring Boot 3.2, Gradle 8.5
- **Database**: PostgreSQL 17 + pgvector (schema unchanged)
- **Build**: Gradle (not Maven)
- **Architecture**: Monolith with Spring MVC (not WebFlux)
- **Background Tasks**: Spring Batch + @Scheduled (not Celery workers)
- **Infrastructure**: AWS CDK (user requirement)

**Consequences**:
- **Positive**:
  - Simpler deployment than Celery multi-container setup
  - Modern Java features (records, pattern matching, virtual threads)
  - Enterprise-ready Spring Boot ecosystem
  - Gradle 8.x modern build features
- **Negative**:
  - Monolith may require microservices extraction later (planned in roadmap)
  - Migration from FastAPI async → Spring MVC blocking requires performance testing
  - Gradle learning curve for Maven-familiar developers
- **Mitigation**:
  - Multi-module Gradle structure (Decision 4) enables future microservices extraction
  - Performance Engineer (Tier 2) will validate async → blocking migration
  - DevOps Engineer provides Gradle expertise

**Alternatives Considered**:
1. **Maven**: Rejected - User preference for Gradle 8.x modern features
2. **Spring WebFlux (reactive)**: Rejected - User preference for MVC blocking model
3. **Microservices from start**: Rejected - Premature complexity, plan for future extraction

---

### Decision 2: Full Multi-Agent Setup (6 Tier 1 + 2 Tier 2)
**Date**: 2025-11-15
**Status**: ACCEPTED
**Deciders**: User + Nexus

**Context**:
User requested "full framework setup" with multi-agent orchestration. Project scope: 65K LOC migration over 24 weeks with 11 phases.

**Decision**:
- 6 Tier 1 agents (always active): Scribe, Backend Architect, Data Architect, Frontend Specialist, QA Specialist, DevOps Engineer
- 2 Tier 2 agents (on-demand): Security Auditor, Performance Engineer
- D013 worktree isolation ENABLED
- Wave-based + parallel domain execution patterns

**Consequences**:
- **Positive**:
  - Expected 3-4x velocity speedup from parallelization
  - Clear domain boundaries prevent conflicts
  - Scribe agent ensures session continuity (D014 compliance)
  - QA Specialist enforces quality gates throughout
- **Negative**:
  - Worktree management overhead (6 separate environments)
  - Coordination complexity (orchestrator must manage 6 concurrent work streams)
  - Scribe agent adds overhead (documentation work)
- **Mitigation**:
  - D013 protocol provides standardized worktree management
  - Framework v2.2 improvements reduce setup time to <30 min
  - Scribe handles documentation automatically (removes burden from other agents)

**Alternatives Considered**:
1. **Single agent**: Rejected - 24-week timeline too long, parallelization required
2. **3-4 agents (smaller team)**: Rejected - 6 clear domains (data, backend, frontend, QA, DevOps, scribe) justify full team
3. **No worktree isolation**: Rejected - 6 parallel agents would create merge chaos

---

### Decision 3: GitHub-First Workflow
**Date**: 2025-11-15
**Status**: ACCEPTED
**Deciders**: User + Nexus

**Context**:
User requested EPIC issue creation as first technical task. Need organizational visibility into 24-week project plan.

**Decision**:
- Create GitHub repository + EPIC with 11 sub-issues before starting implementation
- Link all commits to GitHub issues
- Track progress via issue status and PR reviews

**Consequences**:
- **Positive**:
  - Clear project roadmap visible to stakeholders
  - Progress tracking built-in (issue status, commit history)
  - Commit history linked to issues for context
  - Enable collaboration and review via GitHub UI
- **Negative**:
  - Requires discipline to update issues
  - GitHub dependencies (offline work affected)
- **Mitigation**:
  - Scribe agent responsible for updating issue status in session journals
  - Local git workflow works offline, sync to GitHub when available

**Alternatives Considered**:
1. **Linear/Jira**: Rejected - User preference for GitHub, lower barrier to entry
2. **No issue tracking**: Rejected - 24-week project needs visibility

---

### Decision 4: Multi-Module Gradle Project
**Date**: 2025-11-15
**Status**: ACCEPTED
**Deciders**: Nexus

**Context**:
Cerberus is a complex system with 65K LOC across multiple concerns (API, domain, background tasks). Need clean architecture boundaries.

**Decision**:
- 4 Gradle modules: `athena-api`, `athena-core`, `athena-tasks`, `athena-common`
- Frontend and infrastructure as separate directories (not Gradle modules)

**Module Responsibilities**:
- **athena-api**: Spring Boot REST API, controllers, OpenAPI spec
- **athena-core**: Domain models (JPA entities), repositories, business services
- **athena-tasks**: Background jobs (Spring Batch jobs, @Scheduled tasks)
- **athena-common**: Shared utilities (Jackson configs, validation, common DTOs)

**Consequences**:
- **Positive**:
  - Clean architecture boundaries (API vs domain vs background tasks)
  - Enables future microservices extraction (athena-core → shared library)
  - Easier to test modules in isolation
  - Follows Spring Boot best practices
- **Negative**:
  - Slightly more complex build configuration
  - Dependency management across modules requires discipline
- **Mitigation**:
  - DevOps Engineer owns build configuration
  - Clear dependency rules: api → core, tasks → core, common → standalone

**Alternatives Considered**:
1. **Single module**: Rejected - Poor separation of concerns
2. **More modules (per entity/feature)**: Rejected - Premature complexity

---

## Domain Boundaries & Responsibilities

### Scribe - Documentation & Session History
**Responsibility**: Document all sessions, architectural decisions, and project evolution. Enforce D014 session end protocol.

**Domain Boundaries**:
- **Owns**: `docs/00-active/journal/`, session journals, MEMORY.md updates, ARCHITECTURE.md evolution
- **Collaborates**: With all agents (documents their work in session journals)
- **Never touches**: Source code (`.java`, `.ts`, `.tsx`), build files

**Quality Gates**:
- Must pass: Gate 0 (architecture completeness), D014 compliance
- Validated by: Orchestrator review of session journals

**Key Decisions**:
- Decision 2 (Multi-agent setup) - Scribe mandatory for session continuity

---

### Backend Architect - Service Layer & REST API
**Responsibility**: Design and implement Spring Boot service layer, REST controllers, OpenAPI specifications, external API integrations.

**Domain Boundaries**:
- **Owns**:
  - `athena-api/src/main/java/**/*Controller.java` (REST endpoints)
  - `athena-core/src/main/java/**/service/` (business services)
  - `athena-core/src/main/java/**/client/` (external API clients)
  - OpenAPI specification files
- **Collaborates**:
  - With Data Architect on repository interfaces
  - With Frontend Specialist on API contracts
  - With QA Specialist on service tests
- **Never touches**: JPA entities (Data Architect domain), React components (Frontend domain)

**Quality Gates**:
- Must pass: Gate 3 (unit tests for all services), Gate 4 (integration tests)
- Validated by: `./gradlew :athena-core:test :athena-api:test`

**Key Decisions**:
- Decision 1 (Spring MVC not WebFlux) - Affects all controller design
- Decision 4 (Multi-module) - Service layer in athena-core, controllers in athena-api

---

### Data Architect - Database Schema & JPA Entities
**Responsibility**: Design and implement JPA entities, Spring Data JPA repositories, Flyway migrations, database optimization.

**Domain Boundaries**:
- **Owns**:
  - `athena-core/src/main/java/**/entity/` (JPA entities)
  - `athena-core/src/main/java/**/repository/` (Spring Data JPA repositories)
  - `athena-core/src/main/resources/db/migration/` (Flyway migrations)
  - Database schema design and optimization
- **Collaborates**:
  - With Backend Architect on repository interfaces
  - With QA Specialist on entity tests and Testcontainers
- **Never touches**: Service layer (Backend Architect domain), controllers

**Quality Gates**:
- Must pass: Gate 3 (entity tests + repository tests), Flyway migration validation
- Validated by: `./gradlew :athena-core:test`, PostgreSQL integration tests

**Key Decisions**:
- Decision 1 (PostgreSQL 17 + pgvector) - Affects entity design
- Decision 4 (Multi-module) - Entities and repositories in athena-core

**First Assignment**: Issue #2 (Phase 2: Data Layer) - 19 JPA entities + 32 Flyway migrations

---

### Frontend Specialist - React UI & TypeScript
**Responsibility**: Design and implement React components, TypeScript types, API client integration, frontend build pipeline.

**Domain Boundaries**:
- **Owns**:
  - `frontend/src/**/*.tsx` (React components)
  - `frontend/src/**/*.ts` (TypeScript utilities, types, API clients)
  - `frontend/vite.config.ts` (Vite build configuration)
  - Frontend testing (Vitest, React Testing Library)
- **Collaborates**:
  - With Backend Architect on API contracts
  - With QA Specialist on frontend tests
- **Never touches**: Java code, database migrations

**Quality Gates**:
- Must pass: Gate 3 (component tests), Gate 4 (E2E tests for critical flows)
- Validated by: `npm test` (Vitest), `npm run build` (Vite)

**Key Decisions**:
- Decision 1 (React 18 + TypeScript 5.6 + Vite 7) - Modern frontend stack

---

### QA Specialist - Testing & Quality Assurance
**Responsibility**: Enforce quality gates, implement test suites (unit, integration, E2E), configure Testcontainers, validate all PRs.

**Domain Boundaries**:
- **Owns**:
  - `athena-*/src/test/java/**/*.java` (JUnit 5 tests)
  - `frontend/src/**/*.test.ts(x)` (Vitest tests)
  - Testcontainers configuration
  - Quality gate validation scripts
- **Collaborates**:
  - With all agents (validates their tests)
  - With DevOps Engineer on CI/CD test execution
- **Never touches**: Production code (except when writing tests)

**Quality Gates**:
- Must pass: All gates (Gate 0-4) - QA Specialist is gate enforcer
- Validated by: `./gradlew test`, `npm test`, coverage reports

**Key Decisions**:
- Decision 2 (Multi-agent setup) - QA Specialist enforces quality throughout

**Future Assignment**: Issue #9 (Phase 9: Testing Migration) - 278 JUnit tests

---

### DevOps Engineer - Build, CI/CD, Infrastructure
**Responsibility**: Maintain Gradle build, configure GitHub Actions CI/CD, implement AWS CDK infrastructure, Docker containerization.

**Domain Boundaries**:
- **Owns**:
  - `build.gradle.kts`, `settings.gradle.kts`, module build files
  - `.github/workflows/` (CI/CD pipelines)
  - `infrastructure/` (AWS CDK definitions)
  - `Dockerfile`, `docker-compose.yml`
- **Collaborates**:
  - With all agents (provides build and deployment support)
  - With QA Specialist on CI/CD test execution
- **Never touches**: Application code (unless build-related)

**Quality Gates**:
- Must pass: Gate 1 (build validation), Gate 4 (CI/CD pipeline success)
- Validated by: `./gradlew build`, GitHub Actions workflow status

**Key Decisions**:
- Decision 1 (Gradle 8.5, AWS CDK) - DevOps Engineer implements
- Decision 4 (Multi-module) - DevOps Engineer maintains module dependencies

**Future Assignment**: Issue #10 (Phase 10: Docker & CI/CD)

---

## Coordination Workflows

### Workflow 1: Feature Development (Multi-Domain)
**Scenario**: New feature requires data model + service + REST endpoint + React UI + tests

**Steps**:
1. **Orchestrator (Nexus)**:
   - Breaks feature into domain tasks
   - Creates GitHub issue with task breakdown
   - Delegates to specialist agents

2. **Parallel Work** (Pattern: Parallel Domain Execution):
   - **Data Architect**: Implements JPA entity + repository in `data-architect` worktree
   - **Backend Architect**: Implements service + controller in `backend-architect` worktree
   - **Frontend Specialist**: Implements React component + API client in `frontend-specialist` worktree
   - **QA Specialist**: Writes unit tests + integration tests in `qa-specialist` worktree

3. **Integration**:
   - Each agent creates PR with D012 attribution
   - Orchestrator reviews PRs in dependency order:
     1. Data Architect (foundation)
     2. Backend Architect (depends on entities)
     3. Frontend Specialist (depends on API)
     4. QA Specialist (depends on all)
   - D009 verification for each PR
   - Orchestrator merges in order

4. **Validation**:
   - D009b post-merge verification (full build + test suite)
   - Integration tests validate end-to-end functionality
   - Orchestrator validates feature works as expected

**Pattern Used**: Parallel Domain Execution + D013 Worktree Isolation
**Expected Speedup**: 3-4x vs sequential (4 agents working in parallel)

---

### Workflow 2: Phase Execution (Wave-Based)
**Scenario**: Complete GitHub issue phase (e.g., Issue #2: Data Layer)

**Steps**:
1. **Orchestrator (Nexus)**:
   - Activates wave (e.g., Phase 2: Data Layer)
   - Delegates to Data Architect
   - Scribe begins session journal documentation

2. **Phase Execution**:
   - **Data Architect**: Implements 19 JPA entities + 32 Flyway migrations
   - **QA Specialist**: Writes entity tests + repository tests
   - **Scribe**: Documents progress in session journals

3. **Phase Completion**:
   - Data Architect creates PR(s) for phase work
   - Orchestrator reviews + D009 verification
   - Merge to main
   - D009b post-merge verification

4. **Wave Transition**:
   - Orchestrator validates phase complete (all tests pass, entities functional)
   - Update GitHub issue #2 status to closed
   - Scribe documents phase completion in session journal
   - Begin next wave (Phase 3: Service Layer)

**Pattern Used**: Wave-Based Execution
**Expected Speedup**: 1.3-1.5x vs ad-hoc approach (systematic completion reduces rework)

---

### Workflow 3: Session Handoff (D014 Protocol)
**Scenario**: End of coding session, need to hand off to next session

**Steps**:
1. **All Active Agents**:
   - Complete current work or reach stopping point
   - Commit changes with D012 attribution
   - Run D009 verification

2. **Scribe Agent**:
   - Creates session journal in `docs/00-active/journal/session-{N}.md`
   - Documents:
     - Session goals and accomplishments
     - Work completed by each agent
     - Blockers encountered
     - Next steps for next session
     - Handoff notes

3. **Orchestrator (Nexus)**:
   - Reviews session journal for completeness
   - Validates all agents have committed work
   - Updates MEMORY.md if architectural decisions were made
   - Commits session journal

4. **Next Session**:
   - New session reads latest journal
   - Understands context and blockers
   - Continues work from handoff notes

**Pattern Used**: D014 Session End Protocol
**Benefit**: Seamless handoff between sessions, no loss of context

---

## Conflict Resolution Strategy

### Code Conflicts
**Prevention**:
- D013 worktree isolation - Each agent works in dedicated worktree on feature branch
- Clear domain boundaries - Agents don't overlap on file ownership
- Orchestrator coordinates merge order - Dependency-aware integration

**Resolution**:
- **Who resolves**: Orchestrator (Nexus)
- **How resolved**:
  1. Identify conflict source (usually domain boundary violation)
  2. Coordinate with affected agents via GitHub PR comments
  3. Merge in dependency order (data → backend → frontend)
  4. If conflict persists, orchestrator manually resolves

### Design Conflicts
**Prevention**:
- Architecture decisions documented in MEMORY.md and ARCHITECTURE.md
- Orchestrator approves all architectural changes before implementation
- Session journals capture design discussions

**Resolution**:
- **Escalation path**: Agent → Orchestrator → User (for major decisions)
- **Decision authority**: User decides on major architectural shifts, Orchestrator decides on implementation details

---

## Quality Assurance Strategy

### Testing Hierarchy
**Unit Tests**:
- **Owner**: Each specialist agent for their domain
- **Coverage Target**: 80%+ for critical paths (entities, services, repositories)
- **Run Frequency**: Every commit (Gate 3)
- **Validation**: `./gradlew test`, `npm test`

**Integration Tests**:
- **Owner**: QA Specialist
- **Coverage Target**: Critical flows (opportunity search, capture team management, AI scoring)
- **Run Frequency**: Every PR (Gate 4)
- **Validation**: Testcontainers with PostgreSQL, Spring Boot integration tests

**End-to-End Tests**:
- **Owner**: QA Specialist
- **Coverage**: Key user workflows (login → search opportunities → add to capture team → generate AI score)
- **Run Frequency**: Pre-release, nightly CI/CD builds

### Code Review
**Process**:
- All specialist PRs reviewed by Orchestrator (Nexus)
- D009 verification mandatory (build + tests + diff review)
- QA Specialist validates test coverage

**Review Criteria**:
- Tests pass (`./gradlew test` or `npm test`)
- No hallucinated changes (D009 diff review)
- Code follows domain boundaries
- D012 attribution included

---

## Technical Constraints

### Hard Constraints
(Things that CANNOT be changed)

- **PostgreSQL 17**: Client requirement, schema unchanged from Cerberus
- **pgvector extension**: Required for AI similarity search features
- **AWS deployment**: Infrastructure must use AWS CDK
- **Feature parity with Cerberus**: All 65K LOC Cerberus features must be replicated

### Soft Constraints
(Things that SHOULD NOT be changed without good reason)

- **Java 21**: Modern LTS version, prefer this unless performance issues arise
- **Spring Boot 3.2**: Latest stable, prefer this unless major bugs discovered
- **Gradle 8.5**: User preference, prefer over Maven unless critical blocker
- **Monolith architecture**: Decision 1 accepts future microservices extraction if needed

---

## Known Limitations

### Current Limitations

1. **Frontend/Infrastructure Scaffolding Only**:
   - **Impact**: Frontend and infrastructure modules are empty placeholders (no package.json, no CDK bootstrap)
   - **Workaround**: Will be implemented in Phase 7 (Frontend Integration) and Phase 10 (Docker & CI/CD)
   - **Future**: Frontend Specialist and DevOps Engineer will populate in later waves

2. **No CI/CD Pipeline Yet**:
   - **Impact**: Quality Gate 4 relies on local validation only
   - **Workaround**: Manual enforcement of `./gradlew build` and `./gradlew test` before PR
   - **Future**: Issue #10 (Phase 10) will implement GitHub Actions workflows

3. **No Pre-Commit Hooks**:
   - **Impact**: D009 verification is manual
   - **Workaround**: Quality gates document manual verification steps
   - **Future**: DevOps Engineer may implement git pre-commit hooks for automation

### Technical Debt
(None yet - project in foundation setup)

---

## Metrics & Monitoring

### Velocity Metrics
(Baseline to be established in Session 02 after worktree setup)

- **Baseline (single agent)**: TBD
- **Current (multi-agent)**: TBD
- **Target Speedup**: 3-4x (6 parallel agents, accounting for coordination overhead)

### Quality Metrics
- **Test Coverage**: Target 80%+ (not yet tracking)
- **D009 Verification**: Target 100% coverage (manual enforcement)
- **PR Cycle Time**: TBD (will track after first PRs)

### Agent Utilization
- **Parallel Sessions**: Target 3-4 concurrent agents during feature development
- **Domain Distribution**: Data (30%), Backend (30%), Frontend (20%), QA (10%), DevOps (5%), Scribe (5%)

---

## Evolution & Future Considerations

### Planned Changes

1. **Microservices Extraction (Post-MVP)**:
   - **When**: After feature parity achieved (Issue #12 complete)
   - **Why**: Monolith may need scaling (separate AI scoring service, separate background task workers)
   - **How**: athena-core becomes shared library, extract athena-api and athena-tasks to separate deployments

2. **Activate Multi-Way Audit Pattern (Phase 9)**:
   - **When**: Issue #9 (Testing Migration) begins
   - **Why**: 278 JUnit tests to migrate require parallel validation by QA + Backend + Data agents
   - **How**: Activate multi-way-audit.md pattern, 4x speedup on test migration

### Scalability Considerations

- **Agent Scaling**: May add Performance Engineer (Tier 2) as always-active if load testing reveals issues
- **Database Specialist**: May split Data Architect into separate schema design + optimization roles if database complexity grows
- **Mobile Frontend**: May add Mobile Specialist if iOS/Android apps required (currently web-only)

### Framework Upgrades
**Current Framework Version**: v2.2
**Upgrade Path**: Monitor `/home/shayesdevel/projects/cognitive-framework` for v2.3+ releases
**Breaking Changes**: None anticipated (v2.2 is stable)

---

## References

### Framework Documentation
- **Orchestration Patterns**: `/home/shayesdevel/projects/cognitive-framework/cognitive-core/orchestration/patterns/`
- **Protocols**: `/home/shayesdevel/projects/cognitive-framework/cognitive-core/quality-collaboration/protocols/`
- **Quick References**: `/home/shayesdevel/projects/cognitive-framework/cognitive-core/quality-collaboration/quick-reference/`

### Project Documentation
- **Project Overview**: `../../CLAUDE.md`
- **Decision History**: `MEMORY.md`
- **Quality Gates**: `quality-gates.md`
- **Session Journals**: `journal/`

### External References
- **GitHub Repository**: https://github.com/shayesdevel/athena
- **EPIC Issue**: https://github.com/shayesdevel/athena/issues/1
- **Cerberus Codebase** (reference): User's private repository (65K LOC FastAPI + Celery + React)

---

## Changelog

**2025-11-15 (Session 02)**: Initial architecture documentation
- Documented 2 orchestration patterns (parallel domain execution, wave-based execution)
- Documented 4 architectural decisions (tech stack, multi-agent setup, GitHub workflow, multi-module)
- Established domain boundaries for 6 Tier 1 agents
- Documented 3 coordination workflows (feature development, phase execution, session handoff)
- D013 worktree isolation configured (worktrees to be created in Session 02)
