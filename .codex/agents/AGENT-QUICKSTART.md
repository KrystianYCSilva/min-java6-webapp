---
name: agent-quickstart
description: |
  Fast-start guide for selecting and running the right agents and skills with minimal context overhead.
  Use when: you need a one-page operational playbook to execute feature, review, incident, migration, or planning workflows quickly.
---

# AGENT Quickstart

## 1) Pick The Primary Route

- Feature delivery: `speckit-planner` -> `backend-engineer` -> `test-engineer` -> `cloud-engineer`
- Code review: `senior-reviewer` (+ `test-engineer` if risk is high)
- Architecture decision: `architecture-advisor` -> `principal-software-engineer`
- Incident/reliability: `reliability-engineer` -> `cloud-engineer` -> `senior-reviewer`
- Legacy/SOAP modernization: `integration-modernizer` -> `architecture-advisor`
- Program execution: `delivery-manager` (+ `speckit-planner`)

## 2) Load Context JIT

- Start with 2-4 core skills only.
- Load `references/` only when blocked by compatibility, migration, or incident diagnosis.
- Summarize and release low-value context after each major step.

## 3) Execute In Parallel Safely

- Fan-out only independent streams.
- Assign one owner per stream.
- Fan-in with one consolidation checkpoint before decisions.
- Human approves tradeoffs and final direction.

## 4) Minimum Quality Gates

- Acceptance criteria defined.
- Risk classification present (CRITICAL/WARNING/INFO).
- Test strategy present.
- Rollout and rollback strategy present.
- Observability impact considered.

## 5) Prompt Starters

- `"/backend-engineer Design and implement secure order API with JWT and PostgreSQL."`
- `"/cloud-engineer Propose Cloud Run deployment with Pub/Sub and rollback plan."`
- `"/architecture-advisor Compare layered + hexagonal + event-driven for billing."`
- `"/reliability-engineer Build resilience and SLO alert policy for checkout flow."`
- `"/delivery-manager Build quarterly execution plan with risks and milestones."`

