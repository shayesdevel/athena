# Project Memory - {PROJECT_NAME}

**Purpose**: Track architectural decisions, trade-offs, and constraints so future agents/developers understand WHY things are the way they are
**Format**: Lightweight decision log (ADR-style)
**Audience**: All agents, future maintainers
**Last Updated**: {DATE}

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
**Date**: {SETUP_DATE}
**Status**: ACCEPTED
**Deciders**: {USER_NAME|Team}

**Decision**: Use cognitive-framework v{VERSION} for multi-agent orchestration

**Context**:
- {Why multi-agent approach - e.g., Project complexity requires domain specialization}
- {Why this framework - e.g., Proven patterns, D-series protocols, token budget support}
- {Alternatives considered - e.g., Single agent, manual coordination}

**Consequences**:
- ✅ **Positive**: 2-4x velocity from parallel work, quality protocols prevent errors
- ⚠️ **Negative**: Learning curve for framework, dependency on framework updates
- 🔧 **Mitigation**: Framework documentation, setup checklist, quality gates

**Related**:
- Framework: `{PATH_TO_COGNITIVE_FRAMEWORK}`
- Setup session: `docs/00-active/journal/session-01.md`

---

### MEM-002: {Decision Title}
**Date**: {DATE}
**Status**: {PROPOSED|ACCEPTED|DEPRECATED|SUPERSEDED}
**Deciders**: {Who made this decision}

**Decision**: {Clear statement of what was decided}

**Context**:
{What was the situation? What problem were we solving? What constraints existed?}

**Consequences**:
- ✅ **Positive**: {Benefits}
- ⚠️ **Negative**: {Trade-offs}
- 🔧 **Mitigation**: {How we handle the trade-offs}

**Alternatives Considered**:
1. **{Alternative 1}**: {Why not chosen}
2. **{Alternative 2}**: {Why not chosen}

**Related**:
- {Links to code, PRs, issues, session journals, ARCHITECTURE.md sections}

---

### MEM-003: {Decision Title}
**Date**: {DATE}
**Status**: {STATUS}

{Follow same format}

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

{Update this section when adding new decisions - keep last 5-10}

| ID | Date | Title | Status | Category |
|----|------|-------|--------|----------|
| MEM-001 | {DATE} | Framework Selection | ACCEPTED | [ARCHITECTURE] |
| MEM-002 | {DATE} | {Title} | {STATUS} | [{CATEGORY}] |
| MEM-003 | {DATE} | {Title} | {STATUS} | [{CATEGORY}] |

---

## Archived Decisions

{When MEMORY.md grows too large, move old ACCEPTED decisions to ARCHITECTURE.md}

Moved to ARCHITECTURE.md:
- MEM-XXX: {Title} ({DATE}) - See ARCHITECTURE.md section {REFERENCE}

---

## Templates for Common Decision Types

### Architecture Decision Template
```markdown
### MEM-XXX: {Decision Title}
**Date**: {DATE}
**Status**: ACCEPTED
**Deciders**: {WHO}
**Category**: [ARCHITECTURE]

**Decision**: {What we decided}

**Context**: {Why we needed to decide}

**Consequences**:
- ✅ Positive: {Benefits}
- ⚠️ Negative: {Trade-offs}
- 🔧 Mitigation: {How we handle negatives}

**Alternatives**:
1. {Alt 1}: {Why not}
2. {Alt 2}: {Why not}

**Related**: {Links}
```

### Technology Choice Template
```markdown
### MEM-XXX: {Tech Choice}
**Date**: {DATE}
**Status**: ACCEPTED
**Category**: [TECH_STACK]

**Decision**: Use {TECHNOLOGY} for {PURPOSE}

**Context**:
- Problem: {What needed to be solved}
- Requirements: {Must-haves}
- Constraints: {Limitations}

**Why {TECHNOLOGY}**:
- {Reason 1}
- {Reason 2}
- {Reason 3}

**Alternatives Evaluated**:
| Technology | Pros | Cons | Why Not Chosen |
|------------|------|------|----------------|
| {Alt 1} | {Pros} | {Cons} | {Reason} |
| {Alt 2} | {Pros} | {Cons} | {Reason} |

**Migration Path**: {If replacing existing tech, how to migrate}
```

### Process Decision Template
```markdown
### MEM-XXX: {Process Change}
**Date**: {DATE}
**Status**: ACCEPTED
**Category**: [PROCESS]

**Decision**: {New process or workflow}

**Context**: {Why change was needed}

**Implementation**:
- {Step 1}
- {Step 2}
- {Step 3}

**Success Metrics**: {How we know it's working}

**Rollback Plan**: {If it doesn't work}
```

---

## Lessons Learned

{Document significant lessons that aren't full decisions but worth remembering}

### Lesson 1: {Title}
**Date**: {DATE}
**Context**: {What happened}
**Lesson**: {What we learned}
**Action**: {What we changed}
**Related**: {Link to session journal or issue}

---

## Anti-Patterns to Avoid

{Document things we tried that didn't work}

### ❌ {Anti-Pattern Name}
**Tried**: {When}
**Problem**: {What went wrong}
**Never Again**: {Why}
**Instead Do**: {Correct approach}

---

## Future Considerations

{Things we're not deciding now but should revisit}

### Future-001: {Consideration}
**Why Deferred**: {Reason}
**Revisit When**: {Condition - e.g., "When we reach 10 agents"}
**Context**: {What we know now}
**Options**: {Potential approaches when we revisit}

---

## Changelog

**{SETUP_DATE}**: Project initialized with cognitive-framework v{VERSION}
- Created MEM-001: Framework Selection
- Established agent roster
- Configured worktree isolation

{Add entries as significant events occur}
