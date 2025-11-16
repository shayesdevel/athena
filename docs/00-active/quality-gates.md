# Quality Gates - Athena

**Purpose**: Mandatory checkpoints all agents must pass before committing, merging, and completing work.

**Scope**: Applies to ALL agents (Backend, Frontend, Testing, Database, DevOps, Docs, Orchestrator)

---

## Gate 0: Architecture Completeness (BEFORE First Work Session) - NEW v2.2

**When**: Once at project start, or when new agent joins project
**Purpose**: Ensure cognitive architecture is documented and accessible
**Who**: Orchestrator validates once, all agents verify on first session

### Project Documentation Exists
- [ ] Project CLAUDE.md exists and is current:
  ```bash
  ls ../../CLAUDE.md
  ```
- [ ] Contains: Project overview, agent roster, active patterns, protocol enforcement
- [ ] All placeholders (`{PLACEHOLDER}`) replaced with actual values
- [ ] Framework version documented

### Architecture Documentation Exists
- [ ] ARCHITECTURE.md exists and documents coordination model:
  ```bash
  ls ../../docs/00-active/ARCHITECTURE.md
  ```
- [ ] Contains: Multi-agent coordination model, active orchestration patterns
- [ ] Contains: Domain boundaries for all agents
- [ ] Contains: At least 1 architectural decision documented

### Decision History Initialized
- [ ] MEMORY.md exists and has initial decisions:
  ```bash
  ls ../../docs/00-active/MEMORY.md
  ```
- [ ] Contains: MEM-001 (Framework Selection) with date
- [ ] Contains: At least 2-3 setup decisions documented
- [ ] Format correct: Date, Status, Context, Consequences

### Agent Roster Matches Reality
- [ ] Agent roster in CLAUDE.md matches `.claude/agents/` directory:
  ```bash
  # List agents in CLAUDE.md
  grep -A 1 "Agent:" ../../CLAUDE.md

  # List agent contexts
  ls ../../.claude/agents/

  # Should match!
  ```
- [ ] All Tier 1 agents have context files
- [ ] All Tier 2 agents documented (even if not yet created)

### Quality Gates Documented
- [ ] This file (quality-gates.md) is customized for project
- [ ] Validation commands updated for tech stack
- [ ] All placeholders (`{PLACEHOLDER}`) replaced

**If ANY Gate 0 check fails**:
- STOP - architecture is incomplete
- See SETUP_CHECKLIST.md Steps 0.5, 5.5, 6.5
- Complete architecture documentation before starting work
- Re-run Gate 0 after completing documentation

**Why this gate matters**: Without architecture documentation, agents don't know how the project coordinates, which patterns are active, or why decisions were made. This causes 30-60 min of confusion per new agent/session.

---

## Gate 1: Pre-Flight Check (BEFORE Starting Work)

### Tool Verification
- [ ] Required tools installed and accessible:
  ```bash
  # Java 21 (required for Gradle build)
  java -version  # Should show Java 21.0.9+ LTS

  # Gradle (wrapper included, but verify)
  ./gradlew --version  # Should show Gradle 9.2.0

  # Node.js (for frontend, once implemented)
  node --version  # Should show Node.js 20.19+ or 22.12+
  ```

### Workspace Validation
- [ ] Working in correct directory (worktree for specialists, main repo for orchestrator)
- [ ] Verify location: `pwd` shows expected path
- [ ] On correct branch: `git branch --show-current`
- [ ] Clean starting state: `git status` (commit or stash existing changes)

### Documentation Access
- [ ] Can access quality gates: `ls ../../docs/00-active/quality-gates.md`
- [ ] Can access cognitive framework: `ls /home/shayesdevel/projects/cognitive-framework`
- [ ] Can access project docs: `ls ../../docs/00-active/`

### Configuration Validation
- [ ] settings.json is schema-compliant (Claude Code official schema):
  ```bash
  # Check no deprecated fields
  ! grep -qE "customShellEnv|shellIntegration" .claude/settings.json

  # Check no framework fields in settings.json (should be in framework-config.json)
  ! grep -qE "projectName|worktreeRoot|issueTracking" .claude/settings.json

  # Check hook keys are PascalCase (not lowercase)
  ! grep -qE '"(session-start|session-end|pre-tool-use)"' .claude/settings.json

  # Validate JSON syntax
  jq empty .claude/settings.json
  ```
- [ ] If settings.json invalid: See `/home/shayesdevel/projects/cognitive-framework/.claude/SETTINGS_CREATION_PROTOCOL.md`
- [ ] Compare with reference: `/home/shayesdevel/projects/cognitive-framework/examples/project-configurations/cerberus/.claude/settings.json`

**If ANY pre-flight check fails: STOP and report error before starting work**

---

## Gate 2: During Development (Continuous Monitoring)

### File Hygiene (Check with `git status` frequently)
Prevent committing:
- ❌ Binary files: `.class`, `.jar`, `.pyc`, `.exe`, `.dll`
- ❌ Generated files: `node_modules/`, `dist/`, `build/`, `target/`, `__pycache__/`, `.gradle/`
- ❌ IDE files: `.idea/`, `.vscode/` (unless project-standard), `.DS_Store`
- ❌ Secrets: `.env`, `credentials.json`, API keys, passwords
- ❌ Large files: `>1MB` (use Git LFS if needed)

**Validation command**:
```bash
# Check for Java class files, jars, and other binaries
git status --porcelain | grep -E "\\.(class|jar|war|pyc|exe|dll)$"  # Should return empty
```

### Documentation Sync
- [ ] If you modify code, update relevant README/docs
- [ ] If you add files, ensure they're documented in appropriate README
- [ ] If you remove files, remove references from docs

**Validation command**:
```bash
# Check for broken README references (manual review)
grep -r "athena-" ../../docs/
```

---

## Gate 3: Pre-Commit Validation (BEFORE `git commit`)

### Build Verification
- [ ] Build succeeds (or document why skipped):
  ```bash
  # Java/Gradle build (source ~./sdkman/bin/sdkman-init.sh && sdk use java 21.0.9-tem if needed)
  ./gradlew build --no-daemon

  # Frontend build (once implemented)
  # cd frontend && npm run build
  ```

### Test Verification
- [ ] Tests pass (or document why skipped):
  ```bash
  # Java tests
  ./gradlew test --no-daemon

  # Frontend tests (once implemented)
  # cd frontend && npm test
  ```

### Code Quality (if applicable)
- [ ] Linting passes (optional - not yet configured):
  ```bash
  # Java checkstyle (if configured)
  # ./gradlew checkstyleMain checkstyleTest

  # Frontend ESLint (once implemented)
  # cd frontend && npm run lint
  ```

### Git Hygiene
- [ ] **NO binary/generated files in `git status`**
- [ ] **NO secrets in commit** (.env, credentials, API keys)
- [ ] Meaningful commit message following project convention
- [ ] Verify changes: `git diff --cached` (review what you're committing)
- [ ] D012 attribution included: `Co-Authored-By: {AgentName} <agent@athena.project>`

### Documentation Validation
- [ ] README references to your deliverables are accurate
- [ ] No broken links in documentation you modified
- [ ] Code comments added for complex logic

### Session Journal (MANDATORY)
- [ ] **Session journal entry created**: `docs/00-active/journal/session-{N}.md`
- [ ] Journal includes: What was accomplished, decisions made, blockers encountered
- [ ] See D014 protocol: `/home/shayesdevel/projects/cognitive-framework/cognitive-core/quality-collaboration/quick-reference/D014-quick-ref.md`

**If ANY pre-commit check fails: Fix before committing**

---

## Gate 4: Pre-Merge Validation (BEFORE PR merge)

### Integration Testing
- [ ] All CI/CD checks passing (once GitHub Actions configured)
- [ ] No merge conflicts with base branch
- [ ] Branch rebased or merged with latest base
- [ ] Integration tests pass (if applicable):
  ```bash
  # Run integration tests with Testcontainers (once implemented)
  # ./gradlew integrationTest --no-daemon
  ```

### Cross-Reference Check
- [ ] Changes don't break other agents' work
- [ ] API contracts maintained (if backend changed endpoints, frontend updated)
- [ ] Database migrations applied (if schema changed):
  ```bash
  # Flyway migrations (once implemented)
  # ./gradlew flywayMigrate
  ```

### Documentation Handoff
- [ ] PR description explains what changed and why
- [ ] Links to relevant GitHub issues (e.g., "Closes #2")
- [ ] Session journal referenced in PR for context

### Quality Review
- [ ] Code review requested (orchestrator reviews all specialist PRs)
- [ ] Validation gates explicitly confirmed in PR comments
- [ ] Test coverage maintained or improved (target: 80%+)

**If ANY pre-merge check fails: Block PR merge until resolved**

---

## Gate 5: Session Close (END of Session - D014/D015)

**When**: At end of every session (triggered by "end session", "wrap up", etc.)
**Purpose**: Ensure session work is documented and GitHub issues synchronized
**Who**: Orchestrator (Nexus) executes as part of D014 End-Session Protocol

### Session Journal Published
- [ ] **Session journal created and committed**: `docs/00-active/journal/session-{N}.md`
- [ ] Journal includes: Milestones, decisions (DEC-{N}-{X}), issues (ISSUE-{N}-{X}), metrics
- [ ] Journal follows D014 template format
- [ ] Journal committed and pushed to GitHub:
  ```bash
  # Verify session journal exists and is committed
  git log --oneline --all -- docs/00-active/journal/session-*.md | head -5
  ```

### GitHub Issues Synchronized (D015)
- [ ] **All completed issues closed** with summary comments:
  ```bash
  # Example close command (orchestrator executes)
  gh issue close N --comment "✅ COMPLETED in Session {N}

  **Summary**: Brief description
  **Implementation**: PR #{M}
  **Testing**: {T} tests added, all passing
  **Session**: See docs/00-active/journal/session-{N}.md"
  ```
- [ ] **EPIC issues updated** with:
  - Checked boxes for completed phases/milestones
  - Progress percentage (e.g., "3/11 phases = 27%")
  - Test count (current total + delta from session)
  - "Recent Activity" section with session summary
- [ ] **In-progress issues commented** with session progress (if applicable)
- [ ] **New issues created** for follow-up work identified (if applicable)

### Decision Documentation
- [ ] **All decisions documented** in MEMORY.md (if architectural/technical decisions made)
- [ ] Decision format: DEC-{N}-{X} with date, context, rationale, consequences
- [ ] MEMORY.md committed if updated

### D015 Verification
```bash
# Verify GitHub is synchronized
# Check closed issues reference this session
gh issue list --state closed --search "COMPLETED in Session {N}"

# Check EPIC updated (example for EPIC #1)
gh issue view 1 | grep "Session {N}"

# Verify session journal exists
ls docs/00-active/journal/session-{N}.md
```

**See Full Protocols**:
- D014: `/home/shayesdevel/projects/cognitive-framework/cognitive-core/quality-collaboration/protocols/D014-END-SESSION-PROTOCOL.md`
- D015: `/home/shayesdevel/projects/cognitive-framework/cognitive-core/quality-collaboration/protocols/D015-GITHUB-SYNC-PROTOCOL.md`
- D015 Quick Ref: `/home/shayesdevel/projects/cognitive-framework/cognitive-core/quality-collaboration/quick-reference/d015-github-sync-quick-ref.md`

**If ANY session close check fails: Session is NOT complete - fix before ending**

---

## Gate 6: Post-Merge Verification (AFTER PR merged)

### Tracking Updates
- [ ] GitHub issue status updated (closed or commented)
- [ ] Relevant project docs updated (MEMORY.md, session journal)
- [ ] Session journal confirms task completion

### Environment Validation
- [ ] Main branch builds successfully after merge:
  ```bash
  # Validate build on main
  git checkout main && git pull && ./gradlew build --no-daemon
  ```
- [ ] Integration environment still functional (if applicable)
- [ ] Database migrations applied (if backend changes merged):
  ```bash
  # Flyway migrations (once implemented)
  # ./gradlew flywayMigrate
  ```

### Orchestrator Verification (D009b)
- [ ] Verify commits exist: `git log main --oneline -5`
- [ ] Verify PR exists and closed: `gh pr view {PR_NUMBER}`
- [ ] Worktree clean: `cd {WORKTREE} && git status`

**If post-merge verification fails: Rollback or hotfix immediately**

---

## Agent-Specific Quality Gates

### Backend Architect
**Pre-commit additions**:
- [ ] Service layer unit tests pass
- [ ] REST controllers tested (manual curl/Postman once endpoints exist)
- [ ] OpenAPI spec updated (if API changes)
- [ ] Backend validation:
  ```bash
  ./gradlew :athena-api:test :athena-core:test --no-daemon
  ```

### Data Architect
**Pre-commit additions**:
- [ ] JPA entity tests pass
- [ ] Repository tests pass (with Testcontainers)
- [ ] Flyway migrations created (if schema changed)
- [ ] Database validation:
  ```bash
  ./gradlew :athena-core:test --no-daemon
  # Flyway validate (once configured): ./gradlew flywayValidate
  ```

### Frontend Specialist
**Pre-commit additions**:
- [ ] React component renders without console errors
- [ ] Component tests pass (Vitest)
- [ ] TypeScript compiles without errors
- [ ] Frontend validation:
  ```bash
  cd frontend && npm run build && npm test
  ```

### QA Specialist
**Pre-commit additions**:
- [ ] New tests execute successfully
- [ ] Test coverage maintained or improved (target: 80%+)
- [ ] Integration tests pass (Testcontainers)
- [ ] Testing validation:
  ```bash
  ./gradlew test --no-daemon
  # Coverage report: ./gradlew jacocoTestReport
  ```

### DevOps Engineer
**Pre-commit additions**:
- [ ] Gradle build configuration valid
- [ ] Docker builds successfully (once Dockerfile created)
- [ ] GitHub Actions workflow syntax valid (once configured)
- [ ] DevOps validation:
  ```bash
  ./gradlew build --no-daemon
  # Docker: docker-compose build (once configured)
  # CI/CD: actionlint .github/workflows/*.yml (once configured)
  ```

### Scribe
**Pre-commit additions**:
- [ ] Session journal follows D014 protocol
- [ ] Markdown formatting correct
- [ ] Links in documentation validated
- [ ] Docs validation:
  ```bash
  # Markdown lint (if configured): markdownlint docs/
  # Link check: markdown-link-check docs/**/*.md
  ```

---

## Definition of Done Checklist

**Work is NOT complete until ALL items checked**:
- [ ] ✅ Pre-flight check passed (Gate 1)
- [ ] ✅ File hygiene maintained during development (Gate 2)
- [ ] ✅ Build succeeds (Gate 3)
- [ ] ✅ Tests pass (Gate 3)
- [ ] ✅ No binary/generated files committed (Gate 3)
- [ ] ✅ README accurate (Gate 3)
- [ ] ✅ **Session journal created** (Gate 3 - MANDATORY)
- [ ] ✅ D012 attribution included in commit (Gate 3)
- [ ] ✅ CI/CD passing (Gate 4 - once configured)
- [ ] ✅ No merge conflicts (Gate 4)
- [ ] ✅ PR merged (Gate 4)
- [ ] ✅ **Session journal published and pushed** (Gate 5 - MANDATORY)
- [ ] ✅ **GitHub issues synchronized** (Gate 5 - D015 protocol)
- [ ] ✅ GitHub issue updated (Gate 6)
- [ ] ✅ Post-merge verification passed (Gate 6)

**Only report work as complete after ALL checkboxes ticked**

---

## Enforcement

### Orchestrator Responsibility
- Verify ALL agents follow quality gates before merging PRs
- Run D009 verification on all "complete" reports
- Reject PRs that skip gates (binary files, missing session journals, failing tests)

### Sub-Agent Responsibility
- Review quality gates BEFORE starting work (Gate 1)
- Monitor file hygiene DURING work (Gate 2)
- Validate BEFORE committing (Gate 3)
- Confirm in PR description which gates passed

### Human Responsibility
- Review quality gates during project setup
- Enforce gates during code review
- Update gates as project evolves (add new validation commands)

---

## Tech Stack Reference

**Athena Tech Stack (as of Session 02)**:

**Backend**:
- Java 21.0.9 LTS (Temurin)
- Spring Boot 3.5.7
- Gradle 9.2.0
- PostgreSQL 17 + pgvector

**Frontend** (to be implemented):
- React 19.2.0
- TypeScript 5.9.3
- Vite 7.2.2
- Node.js 20.19+ or 22.12+

**Testing**:
- JUnit 5.11.4
- Mockito 5.15.2
- Testcontainers (for integration tests)
- Vitest (frontend, once implemented)

**Build Commands**:
- **Java build**: `./gradlew build --no-daemon`
- **Java test**: `./gradlew test --no-daemon`
- **Frontend build**: `cd frontend && npm run build` (once implemented)
- **Frontend test**: `cd frontend && npm test` (once implemented)

---

## Version

**Created**: 2025-11-15 (Session 02)
**Last Updated**: 2025-11-15 (Session 02)
**Version**: 1.0
**Customized For**: Athena project
