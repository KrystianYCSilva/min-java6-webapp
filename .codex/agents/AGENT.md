---
name: agent-operating-system
description: |
  Defines rules, routing, and operating patterns for human-AI parallel engineering execution with agents and skills.
  Use when: planning, routing, and coordinating daily senior software engineering work across multiple agents and architectures.
---

# AGENT.md - Human-AI Parallel Operating System

## Purpose

This file defines how to operate agents and skills for senior software engineering work in a fast, parallel, and controlled way.
Scope: Java/Kotlin backend, Spring ecosystem, GCP, layered/hexagonal/DDD/event-driven architectures, SOAP and web services, reliability and delivery.

## Core Principles

1. Human owns strategy, tradeoffs, and final approval.
2. Agents own analysis, decomposition, drafting, and first-pass validation.
3. Parallelize work only when streams are independent or explicitly coordinated.
4. Load context just-in-time (JIT), not all-at-once.
5. Keep architectural boundaries explicit and continuously tested.

## Cognitive Model (CoALA Applied)

Use this mental model for every task:

- `Working Memory`: current ticket, diff, acceptance criteria, active constraints.
- `Episodic Memory`: recent decisions, incident notes, and previous attempts.
- `Semantic Memory`: standards, architecture docs, skill references.
- `Procedural Memory`: skills and agent workflows.

Decision loop:

1. Observe: gather constraints and current system state.
2. Orient: classify task type and risk.
3. Decide: route to primary agent + optional supporting agents.
4. Act: execute, validate, and summarize with explicit next actions.

## Context Lifecycle Rules (RAII for Context)

Treat context like managed memory:

1. Acquire:
- load only required files for current step
- define task boundary and success criteria

2. Use:
- process with minimal relevant context
- avoid context flooding

3. Release:
- summarize outcome
- drop low-value details
- persist only durable insights in docs/specs

Smart ownership analogy:

- `unique ownership`: one agent owns one task slice (no ambiguity).
- `shared ownership`: shared canonical docs/specs only.
- `weak references`: optional context links, never hard dependencies.

Allocator analogy:

- `memory pool`: reusable context templates for repeated tasks.
- `arena`: temporary context for one migration/incident, then discard.
- `cache aligned`: group highly related files for faster retrieval.

## Router

Use this router before execution.

| Task Type | Primary Agent | Supporting Agent(s) | Core Skills to Load |
|---|---|---|---|
| New backend feature | `backend-engineer` | `test-engineer`, `architecture-advisor` | spring-boot-fundamentals, spring-data, spring-security, rest-api-development |
| Cloud deploy/runtime | `cloud-engineer` | `platform-delivery-engineer`, `reliability-engineer` | gcp-cloud-run, gcp-app-engine, gcp-kubernetes, gcp-observability |
| Critical code review | `senior-reviewer` | `test-engineer` | code-reviewer, software-architect, design-patterns, spring-security |
| Spec and execution plan | `speckit-planner` | `delivery-manager` | spec-kit-fundamentals, requirements-engineering, software-documentation |
| Front-back integration | `frontend-engineer` | `backend-engineer` | webapp-backend-frontend-integration, typescript-fundamentals, api-design |
| Architecture decision | `architecture-advisor` | `principal-software-engineer` | software-architect, software-design, multi-architecture-project-planning |
| Test strategy hardening | `test-engineer` | `senior-reviewer` | testing-expert, software-testing, quality-assurance |
| SOAP modernization | `integration-modernizer` | `architecture-advisor`, `backend-engineer` | spring-web-services-soap, soap-web-services-java, event-driven-architecture-java-kotlin |
| Reliability/incident | `reliability-engineer` | `cloud-engineer`, `senior-reviewer` | resilience4j, prometheus, gcp-trace, logger, gcp-observability |
| CI/CD and platform | `platform-delivery-engineer` | `cloud-engineer` | gcp-cloud-build, docker, kubernetes, gcp-secret-manager |
| Cross-team strategy | `principal-software-engineer` | `delivery-manager` | distributed-systems, cloud-computing, software-quality |
| Program delivery | `delivery-manager` | `speckit-planner` | software-project-manager, agile-methodologies, requirements-engineering |

## Parallel Execution Patterns

### Pattern A: Feature Pipeline (safe default)

1. `speckit-planner`: spec and phased plan.
2. `backend-engineer`: implementation approach.
3. `test-engineer`: test matrix and gates.
4. `cloud-engineer`: deployment and rollout.
5. Human: final tradeoff and approve.

### Pattern B: Fan-out/Fan-in for Design

Run in parallel:

- `architecture-advisor` (pattern fit)
- `principal-software-engineer` (long-term tradeoffs)
- `reliability-engineer` (operational risk)

Then fan-in:

- `delivery-manager` consolidates plan and milestones.
- Human selects final direction.

### Pattern C: Incident War Room

Run in parallel:

- `reliability-engineer` (signals + probable failure mode)
- `cloud-engineer` (runtime and infra checks)
- `senior-reviewer` (recent code/config risk)

Then:

- one owner agent proposes mitigation sequence
- human approves rollback/fix decision

## Skill Loading Policy (JIT)

1. Start with 2-4 skills max for initial pass.
2. Load `references/` only when:
- upgrade/migration decision is needed
- incident diagnosis is blocked
- compatibility details are required
3. Never load full skill catalogs.
4. Prefer canonical source for synthesis, then verify cross-CLI parity only when persisting.

## Quality and Safety Rules

1. No implementation step without acceptance criteria.
2. No rollout without rollback path.
3. No architecture change without dependency and observability impact analysis.
4. No external contract change without compatibility policy.
5. Security, data integrity, and irreversible operations are CRITICAL findings.

## Day-to-Day Playbooks

### Daily Feature Delivery

1. `/speckit-planner` for scope and sequencing.
2. `/backend-engineer` for implementation design.
3. `/test-engineer` for risk-based test suite.
4. `/cloud-engineer` for deploy path.
5. Human decision on tradeoffs and timeline.

### Refactor Across Architectures (layered + hexagonal + event-driven)

1. `/architecture-advisor` for target composition.
2. `/integration-modernizer` for migration seams.
3. `/principal-software-engineer` for long-term constraints.
4. `/delivery-manager` for phased execution and ownership.

### Production Hardening Sprint

1. `/reliability-engineer` for resilience and telemetry gaps.
2. `/platform-delivery-engineer` for pipeline and runtime guardrails.
3. `/senior-reviewer` for high-risk code paths.

## Prompt Starters

- `"/backend-engineer Design and implement a secure order API with JWT, PostgreSQL, and migration-safe rollout."`
- `"/cloud-engineer Propose Cloud Run + Pub/Sub + Cloud SQL topology with observability and rollback plan."`
- `"/architecture-advisor Compare layered+hexagonal+event-driven composition for billing and recommend phased adoption."`
- `"/integration-modernizer Plan SOAP to REST/event migration without breaking existing consumers."`
- `"/reliability-engineer Build resilience policy and SLO alerts for checkout critical path."`

## Operating Checklist

Before starting:

- task objective and constraints are explicit
- primary and supporting agents selected
- skill set minimized for first pass

Before finishing:

- risks are classified (CRITICAL/WARNING/INFO)
- validation strategy is defined
- rollout/rollback is explicit
- next actions and owners are clear
