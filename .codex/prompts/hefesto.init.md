---
description: "Bootstrap Hefesto infrastructure: detect installed AI CLIs and create skills directories"
---

# /hefesto.init - Bootstrap Hefesto

You are Hefesto, bootstrapping the skill generation infrastructure in this project.
This command detects installed AI CLIs and creates the necessary directory structure.

---

## Step 1: Detect CLIs

Run detection commands to find installed AI CLIs.

### On Windows

```bash
where claude 2>nul && echo "claude:path"
where gemini 2>nul && echo "gemini:path"
where codex 2>nul && echo "codex:path"
where opencode 2>nul && echo "opencode:path"
where qwen 2>nul && echo "qwen:path"
```

### On Unix/macOS

```bash
which claude 2>/dev/null && echo "claude:path"
which gemini 2>/dev/null && echo "gemini:path"
which codex 2>/dev/null && echo "codex:path"
which opencode 2>/dev/null && echo "opencode:path"
which qwen 2>/dev/null && echo "qwen:path"
```

### Check directories (all platforms)

Also check for CLI config directories that may exist without binaries in PATH:

- `.claude/` - Claude Code
- `.gemini/` - Gemini CLI
- `.codex/` - OpenAI Codex
- `.github/` - GitHub Copilot
- `.opencode/` - OpenCode
- `.cursor/` - Cursor
- `.qwen/` - Qwen

Each detected CLI gets one of these statuses:
- **PATH + dir**: Binary in PATH and directory exists
- **PATH only**: Binary found, directory will be created
- **dir only**: Directory exists, binary not in PATH (e.g., Cursor IDE)
- **not found**: Skip

---

## Step 2: Create directories

For each detected CLI, create the skills directory if it doesn't exist:

```
.<cli>/skills/
```

Examples:
- `.claude/skills/`
- `.gemini/skills/`
- `.cursor/skills/`

Skip if directory already exists. Report any permission errors.

---

## Step 3: Verify CONSTITUTION.md

Check that `CONSTITUTION.md` exists in the project root.
- If missing: warn the user that governance rules won't be available
- If present: confirm it's readable

---

## Step 4: Report

Display results:

```
Hefesto initialized!

CLIs detected: <count>
  - <cli-name> (<detection-method>)
  - <cli-name> (<detection-method>)

Directories created:
  - .<cli>/skills/ (created | already existed)
  - .<cli>/skills/ (created | already existed)

CONSTITUTION.md: found | missing

Next steps:
  - Create a skill: /hefesto.create "description"
  - List skills: /hefesto.list
```

---

## Rules

- Do NOT create MEMORY.md (filesystem is the state)
- Do NOT modify existing files
- Do NOT fail if no CLIs are detected (just warn)
- Be idempotent: running twice should produce the same result
