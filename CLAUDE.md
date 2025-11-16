# Athena

**Purpose**: AI-powered federal contract intelligence platform - replicating Cerberus feature parity with modern Java/Spring + TypeScript/React stack. Automates government contract discovery, AI scoring, teaming intelligence, and competitive analysis.
**Tech Stack**: Java 21, Spring Boot 3.2, Gradle 8.x, React 18, TypeScript 5.6, Vite 7, PostgreSQL 17 + pgvector, AWS CDK
**Framework**: cognitive-framework v2.2 (Nov 2025)
**Status**: Development

---

## Quick Orient

**What**: Greenfield rebuild of Cerberus federal contract intelligence platform using Java/Spring Boot + TypeScript/React
**Who**: Government contractors seeking contract opportunities, teaming partners, competitive intelligence
**Architecture**: Multi-agent cognitive framework with 6 Tier 1 specialist agents coordinated by Nexus orchestrator

---

## Cognitive Architecture Overview

This project uses the **cognitive-framework** for multi-agent orchestration. Multiple specialist AI agents work in parallel, each with domain expertise, coordinated by an orchestrator agent.

### Framework Version & Patterns

**Framework**: cognitive-framework v2.2
**Location**: `/home/shayesdevel/projects/cognitive-framework`
**Active Orchestration Patterns**:
- [x] Parallel domain execution (4x speedup)
- [ ] Multi-way audit (4x speedup)
- [ ] Research delegation (division of labor)
- [ ] Time-sensitive updates (2.1x speedup)
- [ ] Epic closure (2x speedup)
- [x] Wave-based execution (1.3-1.5x speedup)

**Check active patterns in**: `docs/00-active/ARCHITECTURE.md`

---

## Agent Roster

### Tier 0: Orchestrator
**Agent**: Nexus
**Context**: `.claude/agents/orchestrator.md`
**Worktree**: main (shared environment - exclusive control)
**Role**: Coordinates all specialist agents, manages quality gates, orchestrates parallel work

### Tier 1: Core Specialists (Always Active)

**Scribe**
- **Context**: `.claude/agents/scribe.md`
- **Worktree**: `/home/shayesdevel/projects/athena-worktrees/scribe/`
- **Domain**: Session documentation, architectural decision capture, D014 protocol enforcement

**Backend Architect**
- **Context**: `.claude/agents/backend-architect.md`
- **Worktree**: `/home/shayesdevel/projects/athena-worktrees/backend-architect/`
- **Domain**: Java/Spring Boot, REST APIs, service layer, business logic, external API integrations

**Data Architect**
- **Context**: `.claude/agents/data-architect.md`
- **Worktree**: `/home/shayesdevel/projects/athena-worktrees/data-architect/`
- **Domain**: JPA entities, PostgreSQL schema, Flyway migrations, database optimization

**Frontend Specialist**
- **Context**: `.claude/agents/frontend-specialist.md`
- **Worktree**: `/home/shayesdevel/projects/athena-worktrees/frontend-specialist/`
- **Domain**: React/TypeScript, Vite, MUI components, API client integration

**QA Specialist**
- **Context**: `.claude/agents/qa-specialist.md`
- **Worktree**: `/home/shayesdevel/projects/athena-worktrees/qa-specialist/`
- **Domain**: JUnit 5 tests, integration testing, Testcontainers, quality gates enforcement

**DevOps Engineer**
- **Context**: `.claude/agents/devops-engineer.md`
- **Worktree**: `/home/shayesdevel/projects/athena-worktrees/devops-engineer/`
- **Domain**: AWS CDK, Docker, GitHub Actions CI/CD, Gradle build optimization

### Tier 2: On-Demand Specialists (Activated as needed)

**Security Auditor**
- **Context**: `.claude/agents/security-auditor.md`
- **Domain**: Spring Security, JWT authentication, OWASP compliance, security testing

**Performance Engineer**
- **Context**: `.claude/agents/performance-engineer.md`
- **Domain**: Load testing, performance benchmarking vs. Cerberus, optimization

---

## Protocol Enforcement

This project enforces the following **D-series protocols** from the cognitive framework:

### Critical Protocols (Mandatory)

**D009: Commit Verification**
- **Purpose**: Prevent hallucinations, verify all changes
- **When**: After every commit, before PR
- **Quick Ref**: `/home/shayesdevel/projects/cognitive-framework/cognitive-core/quality-collaboration/quick-reference/D009-quick-ref.md`
- **Full Protocol**: `/home/shayesdevel/projects/cognitive-framework/cognitive-core/quality-collaboration/protocols/D009.md`

**D012: Git Attribution**
- **Purpose**: Track agent contributions in commit history
- **Format**: `Co-Authored-By: {AgentName} <agent@athena.project>`
- **Quick Ref**: `/home/shayesdevel/projects/cognitive-framework/cognitive-core/quality-collaboration/quick-reference/D012-quick-ref.md`

**D013: Worktree Isolation**
- **Purpose**: Enable true parallel work without conflicts
- **Status**: ENABLED (check `framework-config.json`)
- **Quick Ref**: `/home/shayesdevel/projects/cognitive-framework/cognitive-core/quality-collaboration/quick-reference/D013-quick-ref.md`

**D014: Session End Protocol**
- **Purpose**: Structured handoff between sessions
- **Quick Ref**: `/home/shayesdevel/projects/cognitive-framework/cognitive-core/quality-collaboration/quick-reference/D014-quick-ref.md`

**D015: GitHub Issue Synchronization**
- **Purpose**: Keep GitHub issues synchronized with project status
- **When**: At session end (part of D014 protocol)
- **Quick Ref**: `/home/shayesdevel/projects/cognitive-framework/cognitive-core/quality-collaboration/quick-reference/d015-github-sync-quick-ref.md`

### Optional Protocols

**D009b: Post-PR Verification**
- **Purpose**: Integration validation after merge
- **Status**: ENABLED

---

## Quality Gates

All agents must pass these gates before committing, merging, or completing work:

**Gate 0**: Architecture Completeness (BEFORE starting work)
- [ ] This CLAUDE.md is current and accurate
- [ ] ARCHITECTURE.md documents active patterns
- [ ] Agent roster matches reality

**Gate 1**: Pre-Flight Check (BEFORE starting work)
- [ ] Tools verified
- [ ] Workspace validated
- [ ] Documentation accessible
- [ ] Configuration valid

**Gate 2**: During Development (Continuous)
- [ ] File hygiene maintained
- [ ] Testing as you go
- [ ] Documentation updated

**Gate 3**: Pre-Commit Verification (BEFORE git commit)
- [ ] All tests pass
- [ ] Code reviewed
- [ ] D009 verification completed

**Gate 4**: Pre-PR Quality (BEFORE creating PR)
- [ ] Integration tests pass
- [ ] Documentation complete
- [ ] Conflicts resolved

**Full Gates**: `docs/00-active/quality-gates.md`

---

## Project Structure

```
/home/shayesdevel/projects/athena/
├── .claude/
│   ├── agents/               ← Agent context files
│   ├── hooks/                ← Session hooks
│   └── settings.json         ← Claude Code configuration
├── docs/
│   ├── 00-active/
│   │   ├── ARCHITECTURE.md   ← Architecture decisions
│   │   ├── MEMORY.md         ← Decision history
│   │   ├── quality-gates.md  ← Quality checkpoints
│   │   └── journal/          ← Session history
│   └── 01-archive/           ← Completed sessions
├── athena-api/               ← Spring Boot REST API module
├── athena-core/              ← Domain models, business logic
├── athena-tasks/             ← Scheduled jobs, async processing
├── athena-common/            ← Shared utilities
├── frontend/                 ← React/TypeScript/Vite app
├── infrastructure/           ← AWS CDK definitions
├── framework-config.json     ← Framework settings
└── CLAUDE.md                 ← This file
```

---

## Worktree Strategy

**Status**: USING_WORKTREES

**Main Repository**: `/home/shayesdevel/projects/athena`
- Used by: Nexus (orchestrator) only
- Never edited by: Specialist agents

**Worktree Root**: `/home/shayesdevel/projects/athena-worktrees`

**Active Worktrees**:
- `/home/shayesdevel/projects/athena-worktrees/scribe/` → Scribe works here
- `/home/shayesdevel/projects/athena-worktrees/backend-architect/` → Backend Architect works here
- `/home/shayesdevel/projects/athena-worktrees/data-architect/` → Data Architect works here
- `/home/shayesdevel/projects/athena-worktrees/frontend-specialist/` → Frontend Specialist works here
- `/home/shayesdevel/projects/athena-worktrees/qa-specialist/` → QA Specialist works here
- `/home/shayesdevel/projects/athena-worktrees/devops-engineer/` → DevOps Engineer works here

**Protocol**: D013 Worktree Isolation enforces safe parallel work

---

## Supporting Documentation

### Architecture & Decisions
- **ARCHITECTURE.md**: `docs/00-active/ARCHITECTURE.md` - How agents coordinate, which patterns are active
- **MEMORY.md**: `docs/00-active/MEMORY.md` - Architectural decisions, trade-offs, constraints
- **Quality Gates**: `docs/00-active/quality-gates.md` - Mandatory checkpoints

### Framework References
- **Framework Root**: `/home/shayesdevel/projects/cognitive-framework`
- **Orchestration Patterns**: `/home/shayesdevel/projects/cognitive-framework/cognitive-core/orchestration/patterns/`
- **Protocols**: `/home/shayesdevel/projects/cognitive-framework/cognitive-core/quality-collaboration/protocols/`
- **Quick References**: `/home/shayesdevel/projects/cognitive-framework/cognitive-core/quality-collaboration/quick-reference/`

### Session History
- **Active Journal**: `docs/00-active/journal/` - Current session notes
- **Archive**: `docs/01-archive/` - Completed sessions

---

## Common Workflows

### Starting New Session (Any Agent)
1. Read latest session journal: `docs/00-active/journal/session-{N}.md`
2. Check quality gates: `docs/00-active/quality-gates.md`
3. Review MEMORY for recent decisions: `docs/00-active/MEMORY.md`
4. Run Gate 0 validation (architecture completeness)
5. Run Gate 1 validation (pre-flight check)
6. Begin work

### Creating Feature (Specialist Agent)
1. Receive task from orchestrator
2. Work in assigned worktree (if using worktree isolation)
3. Follow TDD: test → code → verify
4. Run Gate 2 checks continuously
5. Run Gate 3 before commit (D009 verification)
6. Create PR with agent attribution (D012)
7. Handoff to orchestrator

### Orchestrating Multi-Agent Work (Orchestrator Only)
1. Break epic into domain-specific tasks
2. Delegate to specialist agents (parallel when possible)
3. Monitor progress via agent updates
4. Coordinate merges and integration
5. Run D009b post-PR verification
6. Update session journal

### Ending Session (Any Agent)
1. Complete current work or reach stopping point
2. Run D014 session end protocol
3. Update session journal with progress
4. Document any decisions in MEMORY.md
5. Create handoff notes for next session

---

## Onboarding New Agents

### For AI Agents
1. **Read this file first** (CLAUDE.md) - understand project architecture
2. **Read ARCHITECTURE.md** - understand coordination patterns
3. **Read MEMORY.md** - understand recent decisions
4. **Read quality-gates.md** - understand mandatory checkpoints
5. **Read latest session journal** - understand current state
6. **Review your agent context** - `.claude/agents/{your-agent}.md`
7. **Run Gate 0 and Gate 1** - verify setup before starting

### For Human Developers
1. Read CLAUDE.md (this file)
2. Review ARCHITECTURE.md for coordination model
3. Check MEMORY.md for major decisions
4. Review recent session journals for context
5. Ask orchestrator for current priorities

---

## Issue Tracking & Communication

**Platform**: GitHub
**Repository**: shayesdevel/athena
**Project Board**: https://github.com/shayesdevel/athena/projects

**Communication Channels**:
- Session journals: `docs/00-active/journal/`
- GitHub issues/PRs: https://github.com/shayesdevel/athena/issues
- EPIC tracking: GitHub issues with linked sub-issues

---

## Metrics & Velocity

**Target Setup Time**: <30 minutes (framework v2.2)
**Active Patterns**: 2 orchestration patterns (Parallel domain execution, Wave-based execution)
**Parallel Agents**: 6 Tier 1 agents (can work simultaneously)
**Expected Velocity**: 3-4x speedup from parallelization (based on wave-based + parallel execution patterns)

---

## Key Constraints & Trade-offs

Document in `docs/00-active/MEMORY.md`, but quick summary:

- **Worktree isolation**: ENABLED - 6 Tier 1 agents working in parallel require conflict-free coordination
- **Agent count**: 6 Tier 1 agents chosen for large-scale greenfield rebuild (data layer, backend, frontend, QA, DevOps, scribe)
- **Tech stack choices**: Java 21 + Spring Boot 3.2 for enterprise-ready backend, modern patterns (Gradle, MVC, Spring Batch)
- **Framework patterns**: Wave-based + parallel execution for systematic migration across 9 phases, 24-week timeline

---

## Version Info

**Project Version**: 0.1.0 (Initial setup)
**Framework Version**: v2.2
**Last Updated**: 2025-11-15
**Last Updated By**: Nexus (Orchestrator)

---

## Emergency Contacts & Escalation

**Framework Issues**: https://github.com/shayesdevel/cognitive-framework/issues
**Project Repository**: https://github.com/shayesdevel/athena
**Escalation Path**: Create GitHub issue with `blocked` label, tag orchestrator in session journal
