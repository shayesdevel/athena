# Path Reference Guide - {PROJECT_NAME}

**Purpose**: Prevent path reference errors when agents work in worktrees vs main repository

**Critical**: Path depth errors are the #1 cause of agent setup delays (30+ min debugging per agent)

---

## Directory Structure

```
/home/{USER}/projects/
├── cognitive-framework/                    # Framework repo
│   ├── cognitive-core/
│   ├── domain-specializations/
│   └── templates/
├── {PROJECT_NAME}/                         # Your main project repo
│   ├── .claude/
│   │   ├── agents/                         # Agent context files
│   │   └── settings.json
│   ├── docs/
│   │   └── 00-active/
│   │       ├── quality-gates.md
│   │       └── journal/
│   ├── {BACKEND_DIR}/                      # Backend code
│   ├── {FRONTEND_DIR}/                     # Frontend code
│   └── README.md
└── {PROJECT_NAME}-worktrees/               # Worktree root (ONE level up from main repo)
    ├── backend-agent/                      # Backend agent's isolated workspace
    │   ├── .git                            # Worktree git metadata
    │   ├── {BACKEND_DIR}/                  # Backend code (checked out)
    │   ├── docs/                           # Docs (checked out)
    │   └── .claude/                        # .claude (checked out)
    ├── frontend-agent/                     # Frontend agent's isolated workspace
    ├── testing-agent/                      # Testing agent's isolated workspace
    └── database-agent/                     # Database agent's isolated workspace
```

**Key insight**: Worktree root is at `/projects/{PROJECT_NAME}-worktrees/`, which is **ONE level up** from main repo at `/projects/{PROJECT_NAME}/`

---

## Path Patterns by Location

### From Main Repository (`~/projects/{PROJECT_NAME}/`)

**Orchestrator works here** (not in worktree)

| Target | Path from Main Repo | Explanation |
|--------|---------------------|-------------|
| Cognitive framework | `../cognitive-framework/` | One level up to /projects/, then into framework |
| Project docs | `./docs/00-active/` | Local directory (current repo) |
| Project .claude | `./.claude/` | Local directory (current repo) |
| Agent contexts | `./.claude/agents/` | Local directory (current repo) |
| Worktree root | `../{PROJECT_NAME}-worktrees/` | One level up to /projects/, then into worktrees |

**Testing from main repo**:
```bash
cd ~/projects/{PROJECT_NAME}
ls ../cognitive-framework/                  # Framework (should list cognitive-core/, etc.)
ls ./docs/00-active/                        # Project docs (should list quality-gates.md, etc.)
ls ./.claude/agents/                        # Agent contexts (should list orchestrator-context.md, etc.)
```

---

### From Agent Worktree (`~/projects/{PROJECT_NAME}-worktrees/{AGENT_NAME}/`)

**Specialist agents work here** (isolated workspace)

| Target | Path from Worktree | Explanation |
|--------|-------------------|-------------|
| Cognitive framework | `../cognitive-framework/` | One level up to /projects/, then into framework |
| Main repo docs | `../../docs/00-active/` | Two levels up to /projects/, into project, then docs |
| Main repo .claude | `../../.claude/` | Two levels up to /projects/, into project, then .claude |
| Worktree root | `../` | One level up to worktree root |
| Agent's own code | `./{CODE_DIR}/` | Local directory (checked out in worktree) |

**Testing from worktree**:
```bash
cd ~/projects/{PROJECT_NAME}-worktrees/backend-agent
ls ../cognitive-framework/                  # Framework (should list cognitive-core/, etc.)
ls ../../docs/00-active/                    # Main repo docs (should list quality-gates.md, etc.)
ls ../../.claude/settings.json              # Main repo .claude (should show settings.json)
```

---

## Common Path Mistakes

### ❌ WRONG: Two levels up to framework from worktree
```markdown
# In agent context file
- Quality gates: `../../../cognitive-framework/...`  # TOO DEEP!
```

**Why wrong**: Worktrees are only ONE level up from main repo, not two

### ✅ CORRECT: One level up to framework from worktree
```markdown
# In agent context file
- Quality gates: `../cognitive-framework/...`
```

---

### ❌ WRONG: One level to main repo docs from worktree
```markdown
# In agent context file
- Quality gates: `../docs/00-active/quality-gates.md`  # Not deep enough!
```

**Why wrong**: Need to go TWO levels up (worktree → /projects/ → project) to reach main repo

### ✅ CORRECT: Two levels to main repo docs from worktree
```markdown
# In agent context file
- Quality gates: `../../docs/00-active/quality-gates.md`
```

---

## Path Reference Templates for Agent Contexts

### For Orchestrator Agent (works in main repo)

```markdown
## Key References

**Quick References** (from main repo):
- D009 Verification: `../cognitive-framework/cognitive-core/quality-collaboration/quick-reference/d009-verification-quick-ref.md`
- D013 Worktree Isolation: `../cognitive-framework/cognitive-core/quality-collaboration/quick-reference/d013-worktree-isolation-quick-ref.md`
- Quality Gates: `./docs/00-active/quality-gates.md`
- Session Journals: `./docs/00-active/journal/`
```

### For Specialist Agent (works in worktree)

```markdown
## Key References

**Quick References** (from worktree):
- D009 Verification: `../cognitive-framework/cognitive-core/quality-collaboration/quick-reference/d009-verification-quick-ref.md`
- Quality Gates: `../../docs/00-active/quality-gates.md`
- Session Journals: `../../docs/00-active/journal/`
- Project Settings: `../../.claude/settings.json`

**Domain Patterns** (from worktree):
- Backend Patterns: `../cognitive-framework/domain-specializations/software-development/validation-workflows/backend-validation.md`
- Frontend Patterns: `../cognitive-framework/domain-specializations/software-development/validation-workflows/frontend-validation.md`
```

---

## Mandatory Path Testing (BEFORE Committing Agent Contexts)

**CRITICAL**: Test ALL paths from agent worktree location BEFORE committing agent context file

### Step 1: Create Test Worktree
```bash
cd ~/projects/{PROJECT_NAME}
git worktree add ../{PROJECT_NAME}-worktrees/test-agent
```

### Step 2: Test Paths from Worktree
```bash
cd ~/projects/{PROJECT_NAME}-worktrees/test-agent

# Test framework access (ONE level up)
ls ../cognitive-framework/ || echo "❌ ERROR: Framework path broken"

# Test main repo docs (TWO levels up)
ls ../../docs/00-active/ || echo "❌ ERROR: Docs path broken"

# Test main repo .claude (TWO levels up)
ls ../../.claude/settings.json || echo "❌ ERROR: .claude path broken"
```

### Step 3: If ANY Test Fails
1. **STOP** - Do not commit agent context yet
2. Fix paths in agent context file
3. Re-run tests until all pass
4. THEN commit agent context

### Step 4: Cleanup Test Worktree
```bash
cd ~/projects/{PROJECT_NAME}
git worktree remove ../{PROJECT_NAME}-worktrees/test-agent
```

---

## Automated Path Validation Script

**Location**: `{PROJECT_ROOT}/scripts/validate-agent-paths.sh`

```bash
#!/bin/bash
# Validate all agent context path references from worktree perspective

PROJECT_NAME="{PROJECT_NAME}"
WORKTREE_ROOT="$HOME/projects/${PROJECT_NAME}-worktrees"

echo "Testing paths for all agents..."

for agent in backend-agent frontend-agent testing-agent database-agent; do
  if [ -d "$WORKTREE_ROOT/$agent" ]; then
    cd "$WORKTREE_ROOT/$agent"

    echo ""
    echo "Testing $agent:"

    # Framework (ONE level up)
    if [ -d "../cognitive-framework" ]; then
      echo "  ✅ Framework path: ../cognitive-framework/"
    else
      echo "  ❌ Framework path broken: ../cognitive-framework/"
    fi

    # Main repo docs (TWO levels up)
    if [ -d "../../docs/00-active" ]; then
      echo "  ✅ Docs path: ../../docs/00-active/"
    else
      echo "  ❌ Docs path broken: ../../docs/00-active/"
    fi

    # Main repo .claude (TWO levels up)
    if [ -f "../../.claude/settings.json" ]; then
      echo "  ✅ .claude path: ../../.claude/"
    else
      echo "  ❌ .claude path broken: ../../.claude/"
    fi
  else
    echo "  ⚠️  Worktree not found: $WORKTREE_ROOT/$agent"
  fi
done

cd ~/projects/${PROJECT_NAME}
echo ""
echo "Path validation complete."
```

**Usage**:
```bash
chmod +x scripts/validate-agent-paths.sh
./scripts/validate-agent-paths.sh
```

---

## Quick Reference Chart

| Agent Location | Framework | Main Repo Docs | Main Repo .claude |
|---------------|-----------|----------------|-------------------|
| Main repo (orchestrator) | `../cognitive-framework/` | `./docs/00-active/` | `./.claude/` |
| Worktree (specialists) | `../cognitive-framework/` | `../../docs/00-active/` | `../../.claude/` |

**Memory trick**:
- Framework is always **ONE level up** (whether in main repo or worktree)
- Main repo resources are **local (.)** from main repo, **two levels up (../..)** from worktree

---

## Troubleshooting

### Symptom: Agent reports "cannot find quality-gates.md"

**Diagnosis**: Path depth error in agent context

**Fix**:
1. Check agent's working directory: `pwd`
2. If in worktree: Use `../../docs/00-active/quality-gates.md`
3. If in main repo: Use `./docs/00-active/quality-gates.md`

### Symptom: Agent reports "cannot find cognitive-framework"

**Diagnosis**: Framework path error

**Fix**:
1. Both main repo and worktree use **same path**: `../cognitive-framework/`
2. This assumes cognitive-framework is sibling to project at `/projects/` level

### Symptom: "No such file or directory" errors for quick-refs

**Diagnosis**: Agent trying to access framework from worktree with wrong depth

**Fix**:
1. From worktree: `../cognitive-framework/cognitive-core/...`
2. NOT `../../cognitive-framework/...` (too deep)
3. NOT `./cognitive-framework/...` (not deep enough)

---

## Version

**Created**: {DATE}
**Last Updated**: {DATE}
**Version**: 1.0

**Critical Lesson Source**: LESSONS_FROM_INTERVIEW_PREP_DEPLOYMENT.md - Critical Lesson 3
