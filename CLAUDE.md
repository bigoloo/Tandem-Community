# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew assembleDebug

# Install on connected device/emulator
./gradlew installDebug

# Run all unit tests
./gradlew test

# Lint
./gradlew lint
```

## Architecture

Clean Architecture with three layers under `com.iamamin.tandemcommunity`:

- **`data/`** — network, local DB, paging, mappers, repository implementations
- **`domain/`** — pure interfaces, models, use cases (no Android dependencies)
- **`presentation/`** — Compose UI, ViewModels, DI wiring for UI

DI is **Koin** (not Hilt). Modules are declared as top-level `val`s (e.g., `dataModule`, `remoteModule`, `localModule`, `domainModule`, `UiModule`) and composed via `includes()`. The entry point is `TandemCommunityApplication`.

## Key Data Flow

The community list is a paginated feed that combines two sources:

1. `CommunityPagingSource` fetches pages from the Retrofit API (`CommunityApi`), mapping `UserDto` → `CommunityUser`
2. `LikedRepository` (Room/`LikedUserDao`) observes a `Flow<Set<Long>>` of liked user IDs

`GetCommunityMembersUseCase` merges these with `combine()` to produce `Flow<PagingData<CommunityMember>>`. This is the only place `CommunityMember` (the presentation model with `isLiked`) is assembled — `CommunityUser` is the network-only model.

## Error Handling Pattern

There are two distinct error paths from `CommunityPagingSource`:

- **Refresh errors** — returned as `LoadResult.Error`, surfaced via `members.loadState.refresh` in the UI
- **Append errors** (mid-scroll pagination failures) — emitted on `appendErrorFlow` (a `MutableSharedFlow`) to avoid collapsing the already-loaded list; the ViewModel forwards these as snackbar messages with a Retry action

`CommunityError` is a sealed class: `NoConnectivity`, `Timeout`, `HttpError(code)`, `Unknown`.

## Connectivity Recovery

`CommunityViewModel` observes `ConnectivityObserver.isConnected` with `.drop(1).filter { it }` to detect reconnection events (skipping the initial emission). On reconnect, `CommunityScreen` auto-refreshes or retries depending on whether the current error is a refresh or append failure.

## UI Design System

All colors, spacing, and dimensions come from the theme — never use hardcoded `dp` literals.

**Colors & typography** — always use `MaterialTheme.colorScheme.*` and `MaterialTheme.typography.*`.

**Spacing** (`presentation/theme/Spacing.kt`) — use for padding, gaps, and spacers:

| Token | Value |
|-------|-------|
| `Spacing.xxs` | 2dp |
| `Spacing.xs` | 4dp |
| `Spacing.sm` | 8dp |
| `Spacing.md` | 12dp |
| `Spacing.lg` | 16dp |
| `Spacing.xl` | 24dp |

**Dimens** (`presentation/theme/Dimens.kt`) — use for component-specific sizes:

| Token | Value | Use |
|-------|-------|-----|
| `Dimens.avatarSize` | 80dp | Profile image |
| `Dimens.avatarCornerRadius` | 8dp | Avatar clip |
| `Dimens.badgeCornerRadius` | 4dp | Language badge |
| `Dimens.likeButtonSize` | 32dp | Like button touch target |
| `Dimens.likeIconSize` | 20dp | Like icon |
| `Dimens.cardElevation` | 1dp | Member card shadow |

**Compose Previews** — wrap every `@Preview` with `@PreviewWrapper(ThemedPreviewWrapper::class)` so previews always render inside `TandemCommunityTheme`. Use `apiLevel = 34`.

```kotlin
@Preview(showBackground = true, apiLevel = 34)
@PreviewWrapper(ThemedPreviewWrapper::class)
@Composable
private fun MyComposablePreview() { ... }
```

## Tech Stack

- **Networking**: Retrofit 3 + OkHttp 5 + `kotlinx.serialization` (JSON converter)
- **Local DB**: Room 3 (alpha) with `BundledSQLiteDriver` and coroutine query context
- **Paging**: Paging 3 (`paging-compose`)
- **Images**: Coil 3 (`coil-network-okhttp`)
- **Compose**: Material3, Google Fonts (`ui-text-google-fonts`)

## Testing Conventions

**Framework**: mockk for mocking. JUnit 4 (`@Test`, `@Before`). Use `runTest` for coroutines; use `runBlocking` only for direct `suspend` calls that don't need a `TestScope` (e.g. `PagingSource.load()`).

**Test names**: backtick strings describing what + when. Example: `` `load returns NoConnectivity error when api throws IOException` ``.

**Structure**: group related tests under `// region <method>()` / `// endregion` comments. Put fake data builders in a `// region helpers` block at the bottom.

**Fakes**: each test file defines its own private `fakeXxx()` / `xFixture()` builder functions returning minimal valid objects — do not share fake builders across files.

**Paging tests** — use `paging-testing`'s `asSnapshot()` to collect a `Flow<PagingData<T>>` into a plain list. Repository tests call `scrollTo(index)` inside the lambda to trigger multi-page loads. Use case tests pass `backgroundScope` (from `runTest`) as the scope argument to the use case.

```kotlin
// Repository
val items = repository.getCommunityUsers().asSnapshot { scrollTo(20) }

// Use case (requires UnconfinedTestDispatcher)
@OptIn(ExperimentalCoroutinesApi::class)
fun `...`() = runTest(UnconfinedTestDispatcher()) {
    val result = useCase(backgroundScope).asSnapshot()
}
```

**Layer conventions**:
- *Data layer* — mock the API/DAO, test the repository/paging source logic directly (no DI container).
- *Domain layer* — mock repository interfaces, test use case logic. Simple delegation tests use `mockk(relaxed = true)` and `coVerify`.
- *Presentation layer* — no UI tests currently; ViewModel logic is covered indirectly via use case tests.
