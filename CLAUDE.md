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

## Tech Stack

- **Networking**: Retrofit 3 + OkHttp 5 + `kotlinx.serialization` (JSON converter)
- **Local DB**: Room 3 (alpha) with `BundledSQLiteDriver` and coroutine query context
- **Paging**: Paging 3 (`paging-compose`)
- **Images**: Coil 3 (`coil-network-okhttp`)
- **Compose**: Material3, Google Fonts (`ui-text-google-fonts`)

## Testing Conventions

Unit tests use JUnit 4 with hand-written fakes (no mocking library). See `FakeCommunityApi` in `CommunityPagingSourceTest` as the pattern — implement the interface directly and expose mutable state (`usersToReturn`, `shouldThrow`) rather than using Mockito/MockK.
