# AGENTS.md

## Scope

This repository is a small Spring Boot backend with two main areas:
- `com.example.customer`: customer-facing business module
- `com.example.employeetaskmanagement`: shared app infrastructure, auth, security, config, and exceptions

Codex should optimize for safe, reviewable changes and avoid widening the scope of a task.

## Branch Discipline

- Work on a dedicated branch. Prefer `codex/<short-task-name>` for Codex-created branches.
- Do not commit directly to `main` or combine unrelated fixes in one branch.
- Keep each branch focused on one ticket, bug, or small feature.

## Minimal Diffs

- Change only the files required for the request.
- Preserve existing package structure, naming, and layering unless the task explicitly requires otherwise.
- Do not reformat large files, rename classes, or move code across packages as part of an unrelated change.
- Avoid dependency, plugin, or configuration churn unless it is necessary to complete the task.

## Test Expectations

- Run targeted tests for the area you touched when possible.
- For changes affecting runtime wiring, security, controllers, or persistence, run `./mvnw test` (Windows: `.\mvnw.cmd test`) before finishing if the environment allows it.
- If tests are not run, say so clearly in the final handoff.
- Add or update tests when behavior changes, especially for auth flows, controller contracts, validation, and customer persistence logic.

## Customer Module Changes

- Keep customer logic inside the `com.example.customer` module unless there is a clear shared-infrastructure reason not to.
- Preserve API compatibility for existing customer endpoints unless the task explicitly includes a contract change.
- Be careful with DTO, entity, and repository changes: verify validation, serialization, pagination, and database impact together.
- Do not mix customer feature work with auth, security, or unrelated infrastructure cleanup.

## Auth And Security Changes

- Treat changes under security, auth controllers, JWT handling, seeded users, and endpoint authorization rules as high risk.
- Keep the default security posture intact: do not broaden access, disable checks, or expose debug behavior unless explicitly requested.
- Verify both successful and failing auth paths when modifying login, token, or authorization behavior.
- Do not hardcode secrets, weaken passwords, or commit environment-specific credentials.

## Avoid Unrelated Refactors

- Do not "clean up" nearby code unless it is required to make the requested change safe or understandable.
- Do not introduce framework migrations, architectural rewrites, or large naming refactors during feature or bug-fix work.
- If you notice a separate issue, mention it in the handoff instead of folding it into the same diff.
