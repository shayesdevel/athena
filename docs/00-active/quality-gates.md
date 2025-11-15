# Quality Gates - {PROJECT_NAME}

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
  {VALIDATION_COMMAND_1}  # Example: python3 --version
  {VALIDATION_COMMAND_2}  # Example: node --version
  {VALIDATION_COMMAND_3}  # Example: mvn --version
  ```

### Workspace Validation
- [ ] Working in correct directory (worktree for specialists, main repo for orchestrator)
- [ ] Verify location: `pwd` shows expected path
- [ ] On correct branch: `git branch --show-current`
- [ ] Clean starting state: `git status` (commit or stash existing changes)

### Documentation Access
- [ ] Can access quality gates: `ls ../../docs/00-active/quality-gates.md`
- [ ] Can access cognitive framework: `ls {PATH_TO_COGNITIVE_FRAMEWORK}`
- [ ] Can access project docs: `ls {PROJECT_DOCS_PATH}`

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
- [ ] If settings.json invalid: See `{PATH_TO_COGNITIVE_FRAMEWORK}/.claude/SETTINGS_CREATION_PROTOCOL.md`
- [ ] Compare with reference: `{PATH_TO_COGNITIVE_FRAMEWORK}/examples/project-configurations/cerberus/.claude/settings.json`

**If ANY pre-flight check fails: STOP and report error before starting work**

---

## Gate 2: During Development (Continuous Monitoring)

### File Hygiene (Check with `git status` frequently)
Prevent committing:
- ❌ Binary files: `.class`, `.jar`, `.pyc`, `.exe`, `.dll`
- ❌ Generated files: `node_modules/`, `dist/`, `build/`, `target/`, `__pycache__/`
- ❌ IDE files: `.idea/`, `.vscode/` (unless project-standard), `.DS_Store`
- ❌ Secrets: `.env`, `credentials.json`, API keys, passwords
- ❌ Large files: `>1MB` (use Git LFS if needed)

**Validation command**:
```bash
git status --porcelain | grep -E "{BINARY_FILE_PATTERN}"  # Should return empty
```

### Documentation Sync
- [ ] If you modify code, update relevant README/docs
- [ ] If you add files, ensure they're documented in appropriate README
- [ ] If you remove files, remove references from docs

**Validation command**:
```bash
# Check for broken README references (manual review)
grep -r "{YOUR_DELIVERABLE_PATTERN}" {PROJECT_DOCS_PATH}
```

---

## Gate 3: Pre-Commit Validation (BEFORE `git commit`)

### Build Verification
- [ ] Build succeeds (or document why skipped):
  ```bash
  {BUILD_COMMAND}  # Example: mvn clean install
  {BUILD_COMMAND}  # Example: npm run build
  {BUILD_COMMAND}  # Example: make build
  ```

### Test Verification
- [ ] Tests pass (or document why skipped):
  ```bash
  {TEST_COMMAND}  # Example: mvn test
  {TEST_COMMAND}  # Example: npm test
  {TEST_COMMAND}  # Example: pytest
  ```

### Code Quality (if applicable)
- [ ] Linting passes:
  ```bash
  {LINT_COMMAND}  # Example: eslint src/
  {LINT_COMMAND}  # Example: mvn checkstyle:check
  {LINT_COMMAND}  # Example: mypy .
  ```

### Git Hygiene
- [ ] **NO binary/generated files in `git status`**
- [ ] **NO secrets in commit** (.env, credentials, API keys)
- [ ] Meaningful commit message following project convention
- [ ] Verify changes: `git diff --cached` (review what you're committing)

### Documentation Validation
- [ ] README references to your deliverables are accurate
- [ ] No broken links in documentation you modified
- [ ] Code comments added for complex logic

### Session Journal (MANDATORY)
- [ ] **Session journal entry created**: `docs/00-active/journal/{SESSION_FILE}`
- [ ] Journal includes: What was accomplished, decisions made, blockers encountered
- [ ] See D014 protocol: `{PATH_TO_D014_QUICK_REF}`

**If ANY pre-commit check fails: Fix before committing**

---

## Gate 4: Pre-Merge Validation (BEFORE PR merge)

### Integration Testing
- [ ] All CI/CD checks passing
- [ ] No merge conflicts with base branch
- [ ] Branch rebased or merged with latest base
- [ ] Integration tests pass (if applicable):
  ```bash
  {INTEGRATION_TEST_COMMAND}
  ```

### Cross-Reference Check
- [ ] Changes don't break other agents' work
- [ ] API contracts maintained (if backend changed endpoints, frontend updated)
- [ ] Database migrations applied (if schema changed)

### Documentation Handoff
- [ ] PR description explains what changed and why
- [ ] Links to relevant GitHub issues
- [ ] Session journal referenced in PR for context

### Quality Review
- [ ] Code review requested (if multi-developer project)
- [ ] Validation gates explicitly confirmed in PR comments
- [ ] Test coverage maintained or improved

**If ANY pre-merge check fails: Block PR merge until resolved**

---

## Gate 5: Post-Merge Verification (AFTER PR merged)

### Tracking Updates
- [ ] GitHub issue status updated (closed or commented)
- [ ] Relevant project docs updated (SPRINT.md, NEXT.md, etc.)
- [ ] Session journal confirms task completion

### Environment Validation
- [ ] Main branch builds successfully after merge
- [ ] Integration environment still functional (if applicable)
- [ ] Database migrations applied (if backend changes merged):
  ```bash
  {MIGRATION_COMMAND}  # Example: alembic upgrade head
  ```

### Orchestrator Verification (D009)
- [ ] Verify commits exist: `git log {BRANCH} --oneline -5`
- [ ] Verify PR exists and closed: `gh pr view {PR_NUMBER}`
- [ ] Worktree clean: `cd {WORKTREE} && git status`

**If post-merge verification fails: Rollback or hotfix immediately**

---

## Agent-Specific Quality Gates

### Backend Agent
**Pre-commit additions**:
- [ ] API endpoints tested with manual curl/Postman
- [ ] Database migrations created (if schema changed)
- [ ] Backend validation: `{BACKEND_VALIDATION_COMMAND}`

**Example**: `mvn clean install && mvn test`

### Frontend Agent
**Pre-commit additions**:
- [ ] UI renders without console errors
- [ ] Component tests pass
- [ ] Frontend validation: `{FRONTEND_VALIDATION_COMMAND}`

**Example**: `npm run build && npm test`

### Testing Agent
**Pre-commit additions**:
- [ ] New tests execute successfully
- [ ] Coverage maintained or improved
- [ ] Testing validation: `{TESTING_VALIDATION_COMMAND}`

**Example**: `pytest --cov=. --cov-report=term`

### Database Agent
**Pre-commit additions**:
- [ ] Migrations reversible (test downgrade/upgrade)
- [ ] Schema changes documented
- [ ] Database validation: `{DATABASE_VALIDATION_COMMAND}`

**Example**: `alembic upgrade head && alembic current`

### DevOps Agent
**Pre-commit additions**:
- [ ] Docker builds successfully
- [ ] CI/CD pipeline syntax valid
- [ ] DevOps validation: `{DEVOPS_VALIDATION_COMMAND}`

**Example**: `docker-compose build && docker-compose config`

### Docs Agent
**Pre-commit additions**:
- [ ] Markdown linting passes (if configured)
- [ ] Links validated (no broken references)
- [ ] Docs validation: `{DOCS_VALIDATION_COMMAND}`

**Example**: `markdownlint docs/ && markdown-link-check docs/**/*.md`

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
- [ ] ✅ CI/CD passing (Gate 4)
- [ ] ✅ No merge conflicts (Gate 4)
- [ ] ✅ PR merged (Gate 4)
- [ ] ✅ GitHub issue updated (Gate 5)
- [ ] ✅ Post-merge verification passed (Gate 5)

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
- Customize {PLACEHOLDER} values for your tech stack
- Enforce gates during code review
- Update gates as project evolves

---

## Tech Stack Customization

**Replace these placeholders** with your project-specific commands:

- `{VALIDATION_COMMAND_1-3}`: Tool version checks
- `{PATH_TO_COGNITIVE_FRAMEWORK}`: Path to cognitive framework
- `{PROJECT_DOCS_PATH}`: Path to project documentation
- `{BINARY_FILE_PATTERN}`: Regex for binary files (e.g., `\\.class$|\\.jar$`)
- `{BUILD_COMMAND}`: Build command (e.g., `mvn clean install`, `npm run build`)
- `{TEST_COMMAND}`: Test command (e.g., `pytest`, `npm test`)
- `{LINT_COMMAND}`: Linting command (e.g., `eslint`, `checkstyle`)
- `{INTEGRATION_TEST_COMMAND}`: Integration test command
- `{MIGRATION_COMMAND}`: Database migration command (e.g., `alembic upgrade head`)
- `{BACKEND_VALIDATION_COMMAND}`: Backend-specific validation
- `{FRONTEND_VALIDATION_COMMAND}`: Frontend-specific validation
- `{TESTING_VALIDATION_COMMAND}`: Testing-specific validation
- `{DATABASE_VALIDATION_COMMAND}`: Database-specific validation
- `{DEVOPS_VALIDATION_COMMAND}`: DevOps-specific validation
- `{DOCS_VALIDATION_COMMAND}`: Docs-specific validation
- `{PATH_TO_D014_QUICK_REF}`: Path to D014 quick reference
- `{SESSION_FILE}`: Session journal filename pattern

---

## Version

**Created**: {DATE}
**Last Updated**: {DATE}
**Version**: 1.0
