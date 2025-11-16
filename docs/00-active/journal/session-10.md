# Session 10: Phase 6 Background Tasks (Prototype Scope)

**Date**: 2025-11-15
**Orchestrator**: Nexus
**Active Agents**: Backend Architect, QA Specialist, Scribe
**Focus**: Implement background job engine to complete backend automation

---

## Session Objectives

### Primary Goal
Implement Phase 6 (Background Tasks) with prototype-adapted scope: Spring Batch jobs for data processing and AI scoring, plus @Scheduled tasks for alerts and digests, enabling automated workflow execution.

### Success Criteria
- [ ] SAM.gov data import job (Spring Batch)
- [ ] AI opportunity scoring job (Spring Batch)
- [ ] High-score alert scheduler (@Scheduled)
- [ ] Weekly digest scheduler (@Scheduled)
- [ ] Spring Batch configuration and infrastructure
- [ ] Integration tests for all 4 background jobs
- [ ] All builds green, quality gates passed
- [ ] Phase 6 marked complete (adapted prototype scope)
- [ ] GitHub Issue #6 updated with progress/closure (via D015)

---

## Execution Plan

### Prototype Scope Adaptation (MEM-005 Context)

**Context**: Per MEM-005 architectural decision, Athena is a proof of concept demonstrating automated ELT → LLM → analysis → notification workflow. Background tasks automate the entire pipeline end-to-end.

**Adapted Scope for Phase 6**:

**1. SAM.gov Data Import Job (Spring Batch)**
- **Original Plan**: Hourly scheduled job calling SAM.gov API for incremental sync
- **Prototype Adaptation**: Batch job importing cached SAM.gov JSON files
- **Implementation**:
  - Spring Batch job reading JSON files from filesystem/S3
  - Uses SamGovDataLoader (from Phase 4) as batch reader
  - Chunk-oriented processing (chunk size: 100 opportunities)
  - Job parameters: file path, import date
  - No scheduling (@Scheduled) - manual trigger for demo
- **Rationale**: Demonstrates batch ETL pipeline, sufficient for proof of concept
- **Future**: Add @Scheduled annotation when SAM.gov API key available

**2. AI Opportunity Scoring Job (Spring Batch)**
- **Scope**: Batch job scoring unscored opportunities using Claude API
- **Implementation**:
  - Spring Batch job querying opportunities without scores
  - Uses AnthropicClaudeClient (from Phase 4) for AI analysis
  - Chunk-oriented processing (chunk size: 10 opportunities to respect API rate limits)
  - Job parameters: score threshold, max opportunities per run
  - Stores OpportunityScore entities in PostgreSQL
- **Rationale**: Core value proposition - demonstrates AI-powered batch analysis
- **Scheduling**: Manual trigger for demo (avoids unnecessary API costs)

**3. High-Score Alert Scheduler (@Scheduled)**
- **Scope**: Scheduled task detecting newly scored high-value opportunities and sending alerts
- **Implementation**:
  - @Scheduled task running every 15 minutes (configurable via cron expression)
  - Query opportunities scored > 80 in last 24 hours
  - Send Teams webhook alerts via MicrosoftTeamsClient (from Phase 4)
  - Track last alert sent (avoid duplicate notifications)
- **Rationale**: Demonstrates real-time stakeholder engagement workflow
- **Configuration**: Cron expression in application.yml

**4. Weekly Digest Scheduler (@Scheduled)**
- **Scope**: Scheduled task sending weekly summary emails
- **Implementation**:
  - @Scheduled task running every Monday at 8am (configurable)
  - Query top 10 opportunities from past week (sorted by score)
  - Generate digest email via SmtpEmailClient (from Phase 4)
  - Send to configured recipient list
- **Rationale**: Demonstrates automated reporting workflow
- **Configuration**: Cron expression + recipient list in application.yml

**Deferred to Post-Prototype**:
- ❌ SBIR.gov weekly collection (@Scheduled) - SBIR API integration deferred per DEC-09-02
- ❌ Document processing (async PDF parsing) - Complex, not critical for demo
- ❌ Real-time SAM.gov hourly sync - No API key available per MEM-005

**Why Deferred**:
- Focus on end-to-end automated workflow (import → score → alert)
- 4 jobs sufficient to demonstrate batch processing + scheduling capabilities
- SBIR and document processing add complexity without demo value

### Implementation Strategy - Wave-Based Execution

**Wave 1: Spring Batch Infrastructure**
- **Priority**: HIGHEST - Foundation for both batch jobs
- **Agent**: Backend Architect
- **Deliverables**:
  - Spring Batch configuration class (@EnableBatchProcessing)
  - JobRepository configuration (PostgreSQL-backed)
  - JobLauncher configuration
  - Job execution monitoring (JobExecutionListener)
  - Error handling and retry logic
  - Testing: Unit tests for configuration
- **Tests**: QA Specialist - Validate batch infrastructure with simple test job

**Wave 2: Data Import Batch Job**
- **Priority**: HIGH - Enables all downstream workflows
- **Agent**: Backend Architect
- **Deliverables**:
  - SamGovImportJob configuration (Job + Step definitions)
  - ItemReader: Reads JSON files using SamGovDataLoader
  - ItemProcessor: Validates and transforms opportunity data
  - ItemWriter: Batch insert via OpportunityRepository
  - Job parameters: file path, import timestamp
  - Chunk size: 100 (balance performance vs memory)
  - Skip policy: Log and skip malformed records
- **Tests**: QA Specialist - Integration tests with sample JSON files

**Wave 3: AI Scoring Batch Job**
- **Priority**: HIGH - Core AI workflow automation
- **Agent**: Backend Architect
- **Deliverables**:
  - OpportunityScoringJob configuration
  - ItemReader: Query unscored opportunities from PostgreSQL
  - ItemProcessor: Call AnthropicClaudeClient for each opportunity
  - ItemWriter: Batch insert OpportunityScore entities
  - Job parameters: score threshold, max batch size
  - Chunk size: 10 (respect Claude API rate limits)
  - Retry logic: Exponential backoff for API failures (reuse from Phase 4)
- **Tests**: QA Specialist - Integration tests with mock Claude API

**Wave 4: Scheduled Tasks (Alerts + Digest)**
- **Priority**: MEDIUM - Completes notification automation
- **Agent**: Backend Architect
- **Deliverables**:
  - @Configuration class with @EnableScheduling
  - HighScoreAlertScheduler (@Scheduled fixedRate or cron)
    - Query high-scoring opportunities (score > 80, last 24h)
    - Send Teams alerts via MicrosoftTeamsClient
    - Track last alert timestamp (avoid duplicates)
  - WeeklyDigestScheduler (@Scheduled cron: Monday 8am)
    - Query top 10 opportunities from past week
    - Generate HTML digest email
    - Send via SmtpEmailClient
  - Configuration externalized to application.yml
- **Tests**: QA Specialist - Unit tests with mocked time + manual scheduler triggers

**Wave 5: Integration & Validation**
- **Priority**: STANDARD - Quality gate enforcement
- **Agent**: QA Specialist
- **Deliverables**:
  - End-to-end integration test: Import JSON → Score opportunities → Send alert
  - Error handling tests (malformed JSON, API failures, SMTP errors)
  - Performance baseline (time to import N opportunities, score N opportunities)
  - Spring Batch job execution verification (JobExecution status, metrics)
- **Tests**: Full integration test suite with Testcontainers + WireMock

---

## Agent Delegation

### Backend Architect
- **Worktree**: `/home/shayesdevel/projects/athena-worktrees/backend-architect/`
- **Branch**: `feature/phase-6-background-tasks`
- **Tasks**:
  - Wave 1: Configure Spring Batch infrastructure (JobRepository, JobLauncher)
  - Wave 2: Implement SamGovImportJob (batch data ingestion)
  - Wave 3: Implement OpportunityScoringJob (batch AI scoring)
  - Wave 4: Implement HighScoreAlertScheduler + WeeklyDigestScheduler
  - Add Spring Batch dependencies to athena-tasks module
  - Configure job parameters in application.yml
- **Patterns**: Follow Spring Batch best practices, chunk-oriented processing
- **Quality Gates**: Gate 2 (continuous), Gate 3 (pre-commit D009)

### QA Specialist
- **Worktree**: `/home/shayesdevel/projects/athena-worktrees/qa-specialist/`
- **Branch**: `feature/phase-6-background-tests`
- **Tasks**:
  - Wave 1: Unit tests for Spring Batch configuration
  - Wave 2: Integration tests for SamGovImportJob (sample JSON files)
  - Wave 3: Integration tests for OpportunityScoringJob (mock Claude API)
  - Wave 4: Unit tests for scheduled tasks (mocked time)
  - Wave 5: End-to-end integration test (full pipeline)
- **Quality Gates**: Gate 3 (pre-commit D009), Gate 4 (integration tests)

### Scribe
- **Worktree**: `/home/shayesdevel/projects/athena-worktrees/scribe/`
- **Branch**: `feature/scribe-session-10-docs`
- **Tasks**:
  - Document session progress in this journal (session-10.md)
  - Update MEMORY.md if new architectural decisions made
  - Track scope adaptations and rationale (MEM-005 context)
  - Prepare D014 session handoff + D015 GitHub sync
- **Quality Gates**: D014 compliance, D015 GitHub issue synchronization

---

## Session Log

### 2025-11-15 - Session Start
- **Action**: Nexus initialized Session 10 - Phase 6 Background Tasks
- **Context**: Issue #6 scope adapted per MEM-005 (prototype with 4 jobs, not 6)
- **Status**: Plan approved, scope clarified (2 Spring Batch jobs, 2 @Scheduled tasks)
- **Next**: Delegate Wave 1 (Spring Batch infrastructure) to Backend Architect

---

## Decisions Made

### DEC-10-01: Spring Batch Job Triggering (Manual, Not Scheduled)
**Decision**: Spring Batch jobs triggered manually for prototype demo, not scheduled via @Scheduled
**Context**:
- Prototype goal: Demonstrate batch job capabilities, not production automation
- SamGovImportJob and OpportunityScoringJob are resource-intensive
- No need for hourly/daily triggers during development and demo
**Rationale**:
- **Cost Control**: Manual triggering avoids unnecessary Claude API costs during development
- **Demo Flexibility**: Can trigger jobs on-demand during leadership presentation
- **Configuration**: Jobs configured for scheduling (cron expressions ready) but not enabled
- **Future**: Enable @Scheduled annotations when moving to production
**Implementation**:
- Spring Batch jobs exposed via REST endpoint (manual trigger via API call)
- @Scheduled annotations commented out with // TODO: Enable for production
- Cron expressions documented in application.yml (disabled)
**Consequences**:
- ✅ **Positive**: Cost savings during development, flexible demo execution
- ✅ **Positive**: Jobs fully tested and production-ready (just add scheduling)
- ⚠️ **Negative**: Not demonstrating fully automated workflow
- 🔧 **Mitigation**: Scheduled tasks (alerts, digests) still demonstrate @Scheduled pattern
**Related**: MEM-005 (SAM.gov Data Source), DEC-09-01 (file-based loader)

---

## Issues Encountered

(Issues will be documented as they occur during implementation)

---

## Metrics

### Starting State
- Background jobs: 0/4 (0%)
- Spring Batch infrastructure: Not configured
- @Scheduled tasks: 0
- athena-tasks module: Empty
- Build status: Green (from Session 09)
- Total tests: 532 passing

### Final State
(Metrics will be populated as session progresses)

### Velocity
- **Session duration**: TBD
- **Agents active**: 3 (Backend Architect, QA Specialist, Scribe)
- **Parallelization**: Wave-based execution (Backend impl → QA tests → integration)
- **Quality gates**: TBD

---

## Next Session Preview

**Expected Focus**: Phase 7 - Frontend Integration OR Phase 8 - Authentication & Security

**Phase 7 Option (Frontend Integration)**:
- React components consuming REST API
- TypeScript API client updates for background job triggering
- Job execution monitoring UI
- Opportunity list with AI scores, alerts dashboard
- Frontend Specialist + Backend Architect collaboration

**Phase 8 Option (Authentication & Security)**:
- Spring Security configuration
- JWT authentication
- Role-based access control (RBAC)
- Security Auditor + Backend Architect collaboration

**Decision**: Backend foundation complete after Phase 6 - time to integrate frontend or add security layer

---

## Links

**GitHub**:
- Issue #6: https://github.com/shayesdevel/athena/issues/6 (Phase 6: Background Tasks)
- EPIC Issue #1: https://github.com/shayesdevel/athena/issues/1

**Architecture References**:
- MEM-005: SAM.gov Data Source (Cached JSON)
- DEC-09-01: SAM.gov File-Based Loader (Not Live API)
- Session 09: External Integrations (provides clients used by background jobs)

**Spring Batch Documentation**:
- Spring Batch Reference: https://docs.spring.io/spring-batch/docs/current/reference/html/
- Spring Scheduling Reference: https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#scheduling

---

## Session Handoff (D014)

**Status**: Session 10 IN PROGRESS
**Work Completed**: TBD
**Issues Updated**: TBD (Issue #6 to be updated via D015)
**PRs Merged**: TBD
**Next Session**: TBD
**Blockers**: None known
**Notes for Next Orchestrator**:
- Phase 6 adapted for prototype (4 jobs, not 6 - per MEM-005)
- Spring Batch jobs manual trigger (not scheduled) per DEC-10-01
- SBIR collection and document processing deferred to post-prototype
- Batch jobs reuse external clients from Phase 4 (SamGovDataLoader, AnthropicClaudeClient, etc.)
- All background tasks in athena-tasks module (multi-module structure per MEM-004)

---

## Protocol Compliance

### D014 Session End Protocol
- [ ] Session work completed or clear stopping point reached
- [ ] Session log updated with all major activities
- [ ] Decisions documented (DEC-10-XX format)
- [ ] Issues encountered documented (ISSUE-10-XX format)
- [ ] Metrics captured (starting state, final state, velocity)
- [ ] Next session preview provided
- [ ] Handoff notes prepared

### D015 GitHub Issue Synchronization
- [ ] Issue #6 status updated with session progress
- [ ] Commits and PR numbers linked in issue comments
- [ ] Blockers documented in issue (if any)
- [ ] Issue closed if Phase 6 complete (with completion summary)

### D012 Git Attribution
- [ ] All commits include agent co-authorship
- [ ] Format: `Co-Authored-By: {AgentName} <agent@athena.project>`

### D009 Commit Verification
- [ ] All tests passing before commits
- [ ] D009 verification completed before each commit
- [ ] D009b post-PR verification completed after merge
