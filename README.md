# Athena

**AI-Powered Federal Contract Intelligence Platform**

> Greenfield rebuild of Cerberus using modern Java/Spring Boot + TypeScript/React stack

## Overview

Athena automates the discovery, analysis, and prioritization of federal government contract opportunities through:
- **Automated Collection**: SAM.gov, SBIR.gov, GovWin integration
- **AI-Powered Scoring**: Two-stage heuristic + LLM analysis
- **Teaming Intelligence**: Partner discovery and recommendation
- **Competitive Analysis**: Incumbent contractor analysis and go/no-bid guidance
- **19+ Dashboard Widgets**: Market intelligence, business health, strategic gaps

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.2, Spring Data JPA, Spring Batch
- **Frontend**: React 18, TypeScript 5.6, Vite 7, Material-UI v5
- **Database**: PostgreSQL 17 + pgvector
- **Build**: Gradle 8.x (multi-module)
- **Infrastructure**: AWS CDK, Docker, GitHub Actions
- **AI**: Anthropic Claude Sonnet 4.5

## Architecture

Multi-module monolith (extract microservices later):
- `athena-api`: REST API endpoints (Spring Web MVC)
- `athena-core`: Domain models, business logic
- `athena-tasks`: Scheduled jobs (Spring Batch + @Scheduled)
- `athena-common`: Shared utilities
- `frontend`: React/TypeScript/Vite app
- `infrastructure`: AWS CDK definitions

## Development Approach

This project uses the **cognitive-framework v2.2** for multi-agent orchestration:
- **6 Tier 1 Agents**: Backend, Data, Frontend, QA, DevOps, Scribe
- **Wave-based execution**: Systematic migration across 9 phases
- **Expected velocity**: 3-4x speedup from parallel execution
- **Timeline**: 24 weeks (6 months)

See [CLAUDE.md](CLAUDE.md) for full cognitive architecture details.

## Project Status

**Current Phase**: Foundation (Week 1)
- ✅ Repository created
- ✅ Cognitive framework integrated
- 🔄 GitHub repository setup
- ⏳ EPIC issue creation
- ⏳ Gradle multi-module scaffold

## Quick Start

```bash
# Clone repository
git clone https://github.com/shayesdevel/athena.git
cd athena

# Backend setup (Java 21 required)
./gradlew build

# Frontend setup (Node 18+ required)
cd frontend
npm install
npm run dev

# Infrastructure setup (AWS CDK)
cd infrastructure
npm install
npx cdk synth
```

## Documentation

- **[CLAUDE.md](CLAUDE.md)**: Cognitive framework architecture, agent roster, protocols
- **[ARCHITECTURE.md](docs/00-active/ARCHITECTURE.md)**: Architectural decisions and patterns
- **[MEMORY.md](docs/00-active/MEMORY.md)**: Decision history and trade-offs
- **[Quality Gates](docs/00-active/quality-gates.md)**: Mandatory checkpoints for all agents
- **[Session Journal](docs/00-active/journal/)**: Development history and progress

## Feature Parity with Cerberus

Replicating all Cerberus features:
- ✅ ELT Pipeline (Extract → Load → Transform → Enrich)
- ✅ Two-stage scoring (heuristic + LLM)
- ✅ Workspace isolation (multi-tenant scoring)
- ✅ 19+ dashboard widgets
- ✅ Teaming intelligence
- ✅ Competitive landscape analysis
- ✅ Alert system (Teams, Email)
- ✅ Document processing
- ✅ Search & filtering
- ✅ User management & auth
- ✅ Data export (PDF, Excel)

See [Cerberus Analysis](docs/00-active/CERBERUS_ANALYSIS.md) for detailed feature mapping.

## Contributing

This project uses D-series protocols from the cognitive framework:
- **D009**: Commit verification (prevent hallucinations)
- **D012**: Git attribution (agent co-authoring)
- **D013**: Worktree isolation (parallel work)
- **D014**: Session end protocol (structured handoff)

All agents must pass quality gates before committing. See [docs/00-active/quality-gates.md](docs/00-active/quality-gates.md).

## License

MIT License - see [LICENSE](LICENSE) file

## Links

- **Repository**: https://github.com/shayesdevel/athena
- **Issues**: https://github.com/shayesdevel/athena/issues
- **Cognitive Framework**: https://github.com/shayesdevel/cognitive-framework
- **Cerberus (original)**: Internal reference project

---

**Framework**: cognitive-framework v2.2
**Last Updated**: 2025-11-15
**Status**: Development (Phase 1: Foundation)
