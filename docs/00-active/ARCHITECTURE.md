# Architecture Documentation - {PROJECT_NAME}

**Purpose**: Document how agents coordinate, which patterns are active, and why architectural decisions were made
**Audience**: AI agents, human developers, future maintainers
**Last Updated**: {DATE}

---

## Multi-Agent Coordination Model

### Overview

This project uses a **multi-agent cognitive architecture** with {N} specialist agents coordinated by an orchestrator.

**Coordination Style**: {Centralized|Distributed|Hybrid}
- **Orchestrator**: Makes high-level decisions, delegates to specialists, integrates work
- **Specialists**: Domain experts working in parallel on specific areas
- **Integration**: {How work is integrated - e.g., orchestrator merges PRs after review}

### Agent Hierarchy

```
Orchestrator (Tier 0)
├── {Agent 1} (Tier 1 - Core)
├── {Agent 2} (Tier 1 - Core)
├── {Agent 3} (Tier 1 - Core)
├── {Agent 4} (Tier 2 - On-Demand)
└── {Agent 5} (Tier 2 - On-Demand)
```

**Tier 0 (Orchestrator)**:
- Exclusive control of shared environment (main repo)
- Coordinates all specialist work
- Manages quality gates
- Integrates contributions

**Tier 1 (Core Specialists)**:
- Always active
- Work in parallel on their domains
- Own their domain's test suites
- Create PRs for orchestrator review

**Tier 2 (On-Demand Specialists)**:
- Activated for specific tasks
- Provide expertise when needed
- May share worktrees with Tier 1 agents

---

## Active Orchestration Patterns

### Pattern Selection Rationale

Document WHY each pattern was chosen for this project.

### ✅ Active Patterns

#### 1. {Pattern Name - e.g., Parallel Domain Execution}
**Status**: ACTIVE
**Source**: `{PATH_TO_COGNITIVE_FRAMEWORK}/cognitive-core/orchestration/patterns/{pattern-file}.md`
**Speedup**: {Metric - e.g., 4x}

**Description**: {Brief description}

**Why chosen**:
- {Reason 1 - e.g., Backend and frontend can develop independently}
- {Reason 2 - e.g., Clear domain boundaries minimize conflicts}
- {Reason 3 - e.g., Worktree isolation enables true parallelism}

**When used**:
- {Scenario 1 - e.g., Feature development across multiple domains}
- {Scenario 2 - e.g., Bug fixes in different components}

**Evidence**: {Link to session journal or metrics showing this pattern working}

---

#### 2. {Pattern Name}
**Status**: ACTIVE
**Source**: `{PATH_TO_COGNITIVE_FRAMEWORK}/cognitive-core/orchestration/patterns/{pattern-file}.md`
**Speedup**: {Metric}

**Description**: {Brief description}

**Why chosen**:
- {Reason 1}
- {Reason 2}

**When used**:
- {Scenario 1}
- {Scenario 2}

---

### ❌ Considered But Not Used

#### {Pattern Name - e.g., Time-Sensitive Updates}
**Status**: NOT ACTIVE
**Reason**: {Why not - e.g., No time-critical data feeds in this project}

---

## Protocol Enforcement

### Mandatory Protocols

#### D009: Commit Verification
**Status**: ENFORCED
**Coverage**: All commits by all agents
**Automation**: Quality Gate 3
**Failure Mode**: {What happens if skipped - e.g., PR blocked}

**Implementation**:
```bash
# In quality-gates.md Gate 3
./cognitive-framework/tools/verify-commit.sh
```

**Metrics**: {If tracking - e.g., 100% coverage, 0 hallucinated changes in last 50 commits}

---

#### D012: Git Attribution
**Status**: ENFORCED
**Format**: `Co-Authored-By: {AgentName} <agent@project.com>`
**Automation**: Git commit template

**Implementation**:
- All agent commits include Co-Authored-By line
- Orchestrator commits may include multiple agents
- Tracked via git log analysis

---

#### D013: Worktree Isolation
**Status**: {ENFORCED|NOT_USED}
**Rationale**: {Why enforced or not}

**If ENFORCED**:
- **Worktree Root**: `{PATH_TO_WORKTREES}`
- **Active Worktrees**: {List}
- **Shared Environment**: Main repo (orchestrator exclusive)
- **Conflict Resolution**: {Strategy}

**If NOT USED**:
- **Reason**: {Why not - e.g., project too small, agents don't overlap}
- **Alternative**: {How conflicts are prevented}

---

#### D014: Session End Protocol
**Status**: ENFORCED
**Purpose**: Structured handoff between sessions
**Location**: Session journals in `docs/00-active/journal/`

**Required elements**:
- Session summary
- Work completed
- Blockers encountered
- Next steps
- Handoff notes

---

### Optional Protocols

#### D009b: Post-PR Verification
**Status**: {ENABLED|DISABLED}
**Rationale**: {Why}

---

## Architectural Decisions (ADR-Style)

### Decision 1: {Decision Title - e.g., Use Worktree Isolation}
**Date**: {DATE}
**Status**: ACCEPTED
**Deciders**: {Who decided - e.g., Orchestrator + User}

**Context**:
{What was the situation? What problem were we solving?}

**Decision**:
{What did we decide to do?}

**Consequences**:
- **Positive**: {Benefit 1}, {Benefit 2}
- **Negative**: {Trade-off 1}, {Trade-off 2}
- **Mitigation**: {How we handle negative consequences}

**Alternatives Considered**:
1. {Alternative 1}: {Why rejected}
2. {Alternative 2}: {Why rejected}

---

### Decision 2: {Decision Title}
**Date**: {DATE}
**Status**: {PROPOSED|ACCEPTED|DEPRECATED|SUPERSEDED}

{Follow same format as Decision 1}

---

## Domain Boundaries & Responsibilities

### {Agent 1 Name} - {Domain}
**Responsibility**: {Clear statement of what this agent owns}

**Domain Boundaries**:
- **Owns**: {What files/directories - e.g., backend/api/, tests/api/}
- **Collaborates**: {With which agents, on what}
- **Never touches**: {What's off-limits}

**Quality Gates**:
- Must pass: {Specific gates for this domain}
- Validated by: {How quality is checked}

**Key Decisions**:
- {Link to relevant architectural decisions}

---

### {Agent 2 Name} - {Domain}
**Responsibility**: {Clear statement}

{Follow same format}

---

## Coordination Workflows

### Workflow 1: Feature Development (Multi-Domain)
**Scenario**: New feature requires backend + frontend + tests

**Steps**:
1. **Orchestrator**: Breaks feature into domain tasks
2. **Parallel Work**:
   - {Agent 1} implements backend API
   - {Agent 2} implements frontend UI
   - {Agent 3} writes integration tests
3. **Integration**:
   - Each agent creates PR with D012 attribution
   - Orchestrator reviews and merges in dependency order
   - D009b post-merge verification
4. **Validation**:
   - Integration tests run
   - Orchestrator validates end-to-end functionality

**Pattern Used**: Parallel Domain Execution
**Expected Speedup**: 3-4x vs sequential

---

### Workflow 2: {Workflow Name}
**Scenario**: {Description}

{Follow same format}

---

## Conflict Resolution Strategy

### Code Conflicts
**Prevention**:
- {How prevented - e.g., Worktree isolation, clear domain boundaries}
- {Communication - e.g., Agents announce PRs in session journal}

**Resolution**:
- {Who resolves - e.g., Orchestrator}
- {How resolved - e.g., Merge in dependency order, coordinate with affected agents}

### Design Conflicts
**Prevention**:
- {How prevented - e.g., Architecture decisions documented in MEMORY.md}
- {Review process - e.g., Orchestrator approves architectural changes}

**Resolution**:
- {Escalation path}
- {Decision authority - e.g., User decides on major architectural shifts}

---

## Quality Assurance Strategy

### Testing Hierarchy
**Unit Tests**:
- **Owner**: Each specialist agent for their domain
- **Coverage Target**: {Percentage}
- **Run Frequency**: Every commit (Gate 3)

**Integration Tests**:
- **Owner**: {Testing agent or orchestrator}
- **Coverage Target**: {Percentage}
- **Run Frequency**: Every PR (Gate 4)

**End-to-End Tests**:
- **Owner**: {Testing agent}
- **Coverage**: {Key user flows}
- **Run Frequency**: {When - e.g., before release}

### Code Review
**Process**:
- All specialist PRs reviewed by orchestrator
- {Additional reviewers if applicable}
- D009 verification mandatory

**Review Criteria**:
- {Criterion 1}
- {Criterion 2}

---

## Technical Constraints

### Hard Constraints
{Things that CANNOT be changed}
- {Constraint 1 - e.g., Must use PostgreSQL (client requirement)}
- {Constraint 2}

### Soft Constraints
{Things that SHOULD NOT be changed without good reason}
- {Constraint 1 - e.g., Prefer Django for consistency}
- {Constraint 2}

---

## Known Limitations

### Current Limitations
1. **{Limitation 1}**: {Description}
   - **Impact**: {How it affects the project}
   - **Workaround**: {If any}
   - **Future**: {Plans to address}

2. **{Limitation 2}**: {Description}

### Technical Debt
{Document significant technical debt}
- {Item 1}: {Why it exists, when to address}
- {Item 2}

---

## Metrics & Monitoring

### Velocity Metrics
{If tracking}
- **Baseline (single agent)**: {Metric}
- **Current (multi-agent)**: {Metric}
- **Speedup**: {Multiplier}

### Quality Metrics
- **Test Coverage**: {Percentage}
- **D009 Verification**: {Pass rate}
- **PR Cycle Time**: {Average}

### Agent Utilization
- **Parallel Sessions**: {Average concurrent agents}
- **Domain Distribution**: {Percentage by domain}

---

## Evolution & Future Considerations

### Planned Changes
{Upcoming architectural shifts}
- {Change 1}: {When, why}
- {Change 2}

### Scalability Considerations
{As project grows}
- {Consideration 1 - e.g., May need database specialist agent}
- {Consideration 2 - e.g., May split frontend into mobile + web agents}

### Framework Upgrades
**Current Framework Version**: v{VERSION}
**Upgrade Path**: {Plan for framework updates}
**Breaking Changes**: {Known issues to watch for}

---

## References

### Framework Documentation
- **Orchestration Patterns**: `{PATH_TO_COGNITIVE_FRAMEWORK}/cognitive-core/orchestration/patterns/`
- **Protocols**: `{PATH_TO_COGNITIVE_FRAMEWORK}/cognitive-core/quality-collaboration/protocols/`
- **Quick References**: `{PATH_TO_COGNITIVE_FRAMEWORK}/cognitive-core/quality-collaboration/quick-reference/`

### Project Documentation
- **Project Overview**: `../../CLAUDE.md`
- **Decision History**: `MEMORY.md`
- **Quality Gates**: `quality-gates.md`
- **Session Journals**: `journal/`

---

## Changelog

**{DATE}**: Initial architecture documentation
- Documented {N} orchestration patterns
- Documented {N} architectural decisions
- Established domain boundaries for {N} agents

{Add entries as architecture evolves}
