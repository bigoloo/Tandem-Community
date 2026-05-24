# Git Workflow — Mio (Trunk-Based Development)

> Invoke with `/project:git` in Claude Code.
> Codex / other assistants: follow the conventions in this file.

---

## Branching Strategy: Trunk-Based Development

```
main (trunk)
  │
  ├── feature/add-feature  ← short-lived, < 2 days
  ├── fix/bugfixes      ← short-lived, < 1 day
  └── release/1.2.0           ← cut from trunk at release time
```

- **`main` is the trunk** — always green, always deployable
- Every developer integrates to `main` at least once per day
- Feature branches are **short-lived** (< 2 days) — if it takes longer, use feature flags
- No long-running `develop` or `release-staging` branches
- Release branches are cut from `main` at the moment of release, then frozen (patches only)
- Delete branches immediately after merge — they are never re-used

---

## Branch Types & Naming

```
<type>/<short-description>
```

| Type        | Lifetime        | When to use                                         |
|-------------|-----------------|-----------------------------------------------------|
| `feat/`     | < 2 days        | New capability — use feature flags for large work   |
| `fix/`      | < 1 day         | Bug fix                                             |
| `refactor/` | < 1 day         | Code restructure with no behavior change            |
| `chore/`    | < 1 day         | Build, deps, config, tooling                        |
| `release/`  | Until patched   | Cut from `main` at release time (e.g. `release/1.2.0`) |
| `hotfix/`   | Hours           | Emergency patch on a live `release/` branch         |


Rules:
- **kebab-case**, lowercase
- ≤ 50 characters
- No ticket number prefix (optional: `fix/MIO-42-crash-on-launch`)

---

## Feature Flags (for large features)

When a feature takes more than 1–2 days, **do not leave a long-lived branch open**. Instead:

1. Land small incremental commits directly to `main` behind a feature flag
2. The flag disables the UI/behavior by default
3. Enable in debug/internal builds for testing
4. Remove the flag once the feature is stable and shipped

```kotlin
// Example feature flag (simple approach)
object FeatureFlags {
    val newHomeLayout: Boolean = BuildConfig.DEBUG  // enabled only in debug
    val weightChartV2: Boolean = false              // disabled for all until ready
}

// Usage in Composable
if (FeatureFlags.newHomeLayout) {
    NewHomeLayout(...)
} else {
    CurrentHomeLayout(...)
}
```

---

## Commit Message Format

Follow **Conventional Commits** (https://www.conventionalcommits.org).
In trunk-based development, each commit to `main` must be **self-contained and safe** — it should not break the build or tests.

```
<type>(<scope>): <short summary>

[optional body — wrap at 72 chars, explain WHY not WHAT]

[optional footer: BREAKING CHANGE, Closes #issue]
```

### Types

| Type       | Appears in changelog | Bumps version  | When to use                              |
|------------|----------------------|----------------|------------------------------------------|
| `feature`  | ✅ Yes                | Minor          | New user-facing capability               |
| `fix`      | ✅ Yes                | Patch          | Bug fix visible to user                  |
| `perf`     | ✅ Yes                | Patch          | Performance improvement                  |
| `refactor` | ❌ No                 | —              | Code change, no behavior change          |
| `chore`    | ❌ No                 | —              | Build, deps, tooling, config             |
| `docs`     | ❌ No                 | —              | Documentation only                       |
| `test`     | ❌ No                 | —              | Adding or fixing tests                   |
| `style`    | ❌ No                 | —              | Formatting, lint fixes                   |
| `ci`       | ❌ No                 | —              | CI/CD changes                            |
| `revert`   | ✅ Yes                | Patch          | Reverts a previous commit                |

Use `BREAKING CHANGE:` in the footer to bump the **Major** version.


## Pull Request Guidelines

### PR Rules (trunk-based style)
- PRs must be **small** — aim for < 200 lines changed
- Must be merged within **2 days** of opening — if not, rebase and reconsider scope
- **Squash or rebase merge** — no merge commits on `main`
- CI must be green before merge — no exceptions

### PR Title
Same format as a commit: `fix(paging): fixing crash for retry`

### PR Description Template
```markdown
## What
One sentence: what changed and why.

## Safety
- [ ] Behind a feature flag (if incomplete feature)
- [ ] Tests added / updated
- [ ] Manually verified on Android
- [ ] Manually verified on iOS simulator
- [ ] No other modules broken

## Checklist
- [ ] Follows MVI pattern
- [ ] No business logic in Composables
- [ ] No `!!` operators
- [ ] CI passes
- [ ] Branch will be deleted after merge
```

---

## Trunk Health Rules

`main` must **never be broken**. If it is:

1. **Revert immediately** — don't try to fix forward under pressure
   ```bash
   git revert <bad-commit-sha> --no-edit
   git push
   ```
2. Fix on a branch, then re-merge
3. Investigate why CI didn't catch it and fix the gap

---

## Useful Commands

```bash
# See what changed on your branch vs trunk
git diff origin/main...HEAD

# Check which modules were touched (helps scope test runs)
git diff --name-only origin/main | grep -oE '^[^/]+' | sort -u

# Interactive rebase to clean up commits before PR
git rebase -i origin/main

# Undo last commit but keep changes staged
git reset --soft HEAD~1

# Find the commit that introduced a bug
git bisect start
git bisect bad HEAD
git bisect good v1.1.0
```

---

## When AI is Asked to Work with Git

- **Create branch name** → Use `<type>/<short-description>`, trunk-based rules (short-lived)
- **Write commit message** → Conventional Commits; check if the change is `feature`, `fix`, or `chore` by what it does, not what was requested
- **Review a diff** → Flag: MVI violations, `!!` usage, missing tests, business logic in Composables, any change that could break trunk
- **Large feature spanning > 2 days** → Suggest feature flag approach instead of long-lived branch
- **Generate changelog** → Include `feature`, `fix`, `perf`, `revert`; group by module scope; exclude `chore`, `docs`, `test`, `style`, `ci`
- **Hotfix** → Always cherry-pick back to `main` after patching the release branch
