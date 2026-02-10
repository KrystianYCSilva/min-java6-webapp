# CLI Compatibility Rules

> Rules for adapting skills across multiple AI CLI tools.
> Used by `/hefesto.create` (Phase 6) and `/hefesto.init` for multi-CLI deployment.

---

## Supported CLIs

| CLI        | Skills Directory       | Commands Directory       | Variable Syntax | Config Format | Detection          |
|------------|------------------------|--------------------------|-----------------|---------------|--------------------|
| Claude     | `.claude/skills/`      | `.claude/commands/`      | `$ARGUMENTS`    | Markdown      | `where claude`     |
| Gemini     | `.gemini/skills/`      | `.gemini/commands/`      | `{{args}}`      | Markdown+TOML | `where gemini`     |
| Codex      | `.codex/skills/`       | `.codex/prompts/`        | `$ARGUMENTS`    | Markdown      | `where codex`      |
| Copilot    | `.github/skills/`      | `.github/commands/`      | `$ARGUMENTS`    | Markdown      | `where github-copilot` |
| OpenCode   | `.opencode/skills/`    | `.opencode/command/`     | `$ARGUMENTS`    | Markdown      | `where opencode`   |
| Cursor     | `.cursor/skills/`      | `.cursor/commands/`      | `$ARGUMENTS`    | Markdown      | dir `.cursor/`     |
| Qwen       | `.qwen/skills/`        | `.qwen/commands/`        | `{{args}}`      | Markdown      | `where qwen`       |

---

## Variable Syntax Adaptation

### Group 1: `$ARGUMENTS` (Claude, Codex, Copilot, OpenCode, Cursor)

Skills use `$ARGUMENTS` directly. No transformation needed.

```markdown
Invoke: /skill my-skill $ARGUMENTS
```

### Group 2: `{{args}}` (Gemini, Qwen)

Skills must replace `$ARGUMENTS` with `{{args}}`.

```markdown
Invoke: /skill my-skill {{args}}
```

### Transformation Rule

When persisting a skill to multiple CLIs:
1. Generate SKILL.md using canonical format (no variable references in body)
2. Only the invocation/usage line needs adaptation
3. Skill body content remains identical across CLIs

---

## Command Format Adaptation

### Claude / Codex / Copilot / OpenCode / Cursor

Plain Markdown files in commands directory.

```markdown
# Command description and instructions
$ARGUMENTS contains the user input.
```

### Gemini

Markdown with optional TOML wrapper for metadata.

```markdown
# Command description and instructions
{{args}} contains the user input.
```

### Qwen

Similar to Gemini format.

```markdown
# Command description and instructions
{{args}} contains the user input.
```

---

## Detection Strategy

### Phase 1: Check PATH

```bash
# Windows
where claude 2>nul && echo "claude:found"
where gemini 2>nul && echo "gemini:found"
where codex 2>nul && echo "codex:found"
where opencode 2>nul && echo "opencode:found"
where qwen 2>nul && echo "qwen:found"

# Unix
which claude 2>/dev/null && echo "claude:found"
which gemini 2>/dev/null && echo "gemini:found"
which codex 2>/dev/null && echo "codex:found"
which opencode 2>/dev/null && echo "opencode:found"
which qwen 2>/dev/null && echo "qwen:found"
```

### Phase 2: Check Directories

```bash
# Check for CLI config directories (may exist without binary in PATH)
test -d .claude && echo "claude:dir"
test -d .gemini && echo "gemini:dir"
test -d .codex && echo "codex:dir"
test -d .github && echo "copilot:dir"
test -d .opencode && echo "opencode:dir"
test -d .cursor && echo "cursor:dir"
test -d .qwen && echo "qwen:dir"
```

### Detection Result Format

Report each CLI as:
- **PATH + dir**: Full support (binary in PATH and directory exists)
- **PATH only**: Binary found, will create directory
- **dir only**: Directory exists, binary not in PATH (IDE-based like Cursor)
- **not found**: Skip this CLI

---

## Persistence Rules

1. **Always persist to all detected CLIs** (unless user specifies `--target`)
2. **SKILL.md content is identical** across CLIs (body does not change)
3. **Only adapt**: usage/invocation lines for variable syntax
4. **Create directory structure**: `.<cli>/skills/<name>/SKILL.md`
5. **Report what was created**: list all paths after persistence

---

## CLI-Specific Notes

### Claude Code
- Most mature integration
- Supports `/skill` command natively
- Skills auto-discovered from `.claude/skills/`

### Gemini CLI
- Uses `{{args}}` instead of `$ARGUMENTS`
- Commands may need TOML frontmatter wrapper
- Skills directory: `.gemini/skills/`

### Codex (OpenAI)
- Uses `.codex/prompts/` for commands (not `commands/`)
- Variable syntax: `$ARGUMENTS`
- Skills in `.codex/skills/`

### GitHub Copilot
- Uses `.github/` namespace (shared with other GitHub configs)
- Skills in `.github/skills/`
- Variable syntax: `$ARGUMENTS`

### OpenCode
- Uses `.opencode/command/` for commands (singular, not `commands/`)
- Skills in `.opencode/skills/`
- Variable syntax: `$ARGUMENTS`

### Cursor
- IDE-based, may not have binary in PATH
- Detect via `.cursor/` directory existence
- Skills in `.cursor/skills/`

### Qwen
- Uses `{{args}}` like Gemini
- Skills in `.qwen/skills/`


---

## Agent-Specific Notes

When generating agent commands (not skills), agents are saved as command files (not in skills/ directories).

### Agent Command Locations

| CLI | Agent Location | Format |
|-----|---------------|--------|
| Claude | `.claude/commands/<name>.md` | Markdown |
| Gemini | `.gemini/commands/<name>.toml` | TOML wrapper |
| Codex | `.codex/prompts/<name>.md` | Markdown |
| GitHub | `.github/agents/<name>.md` + `.github/prompts/<name>.prompt.md` | Dual files |
| OpenCode | `.opencode/command/<name>.md` | Markdown |
| Cursor | `.cursor/commands/<name>.md` | Markdown |
| Qwen | `.qwen/commands/<name>.md` | Markdown |

**Copilot Note**: GitHub Copilot requires dual files - full agent in agents/, stub in prompts/.

---