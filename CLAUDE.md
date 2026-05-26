# KMP Currency Converter — Claude Context

## Project Overview

A Kotlin Multiplatform (KMP) currency converter that demonstrates shared business logic
across Android and iOS. The shared module contains the full data, domain, and presentation
layers. Android uses Jetpack Compose for the UI; iOS is a SwiftUI placeholder ready to
consume the shared XCFramework.

Exchange rates are fetched from [open.er-api.com](https://www.exchangerate-api.com/docs/free)
— a free, no-key-required REST API:

```
GET https://open.er-api.com/v6/latest/{BASE_CURRENCY}
```

Rates are cached in-memory per base currency for the session lifetime.

---

## Modules

| Module | Language | Role |
|---|---|---|
| `:shared` | Kotlin (KMP) | Data layer (Ktor API, repository), domain use cases, shared ViewModel + UiState |
| `:androidApp` | Kotlin + Compose | Android UI, AndroidX ViewModel wrapper, Koin DI setup |
| `iosApp/` | Swift + SwiftUI | Placeholder stub; Swift files exist but no Xcode project yet |

---

## Build Commands

```bash
# Build the Android app (debug APK)
./gradlew :androidApp:assembleDebug

# Install on connected device or emulator
./gradlew :androidApp:installDebug

# Build the shared KMP module (all targets)
./gradlew :shared:build

# Build the shared XCFramework for iOS consumption
./gradlew :shared:assembleXCFramework
# Output: shared/build/XCFrameworks/debug/shared.xcframework

# Run Android unit tests
./gradlew :shared:testDebugUnitTest
./gradlew :androidApp:testDebugUnitTest

# Lint
./gradlew :shared:lint
./gradlew :androidApp:lint
```

---

## Architecture

### Layer Diagram

```
┌────────────────────────────────────────────────────┐
│                   :shared (KMP)                    │
│                                                    │
│  data/                domain/          presentation│
│  ├─ CurrencyApi(intf)  ConvertCurrency  Converter  │
│  ├─ CurrencyApiImpl    UseCase          ViewModel  │
│  ├─ CurrencyRepository                  UiState    │
│  ├─ model/                                         │
│  │   Currency                                      │
│  │   ConversionResult                              │
│  │   ExchangeRate                                  │
│  di/ SharedModule (Koin)                           │
└──────────────────┬─────────────────┬───────────────┘
                   │                 │
       ┌───────────▼──┐      ┌───────▼──────────────┐
       │  :androidApp  │      │      iosApp/         │
       │               │      │                      │
       │  Compose UI   │      │  SwiftUI placeholder │
       │  ConverterAnd │      │  ContentView.swift   │
       │  roidViewModel│      │  (stub only)         │
       │  AndroidModule│      │                      │
       └───────────────┘      └──────────────────────┘
```

### Package Structure

```
shared/src/commonMain/kotlin/com/hendramarihot/currencyconverter/
├── data/
│   ├── CurrencyApi.kt           — Interface + CurrencyApiImpl (Ktor HTTP)
│   ├── CurrencyRepository.kt    — In-memory cache + rate lookup
│   └── model/
│       ├── Currency.kt          — Currency data class + supportedCurrencies list
│       ├── ConversionResult.kt  — Result of a conversion operation
│       └── ExchangeRate.kt      — ExchangeRateResponse (@Serializable) + ExchangeRate
├── di/
│   └── SharedModule.kt          — Koin module: HttpClient, CurrencyApi, repo, use cases
├── domain/
│   └── ConvertCurrencyUseCase.kt
├── presentation/
│   └── ConverterViewModel.kt    — KMP ViewModel; takes injected CoroutineScope

androidApp/src/main/kotlin/com/hendramarihot/currencyconverter/android/
├── CurrencyConverterApp.kt      — Application; startKoin with sharedModule + androidModule
├── MainActivity.kt
├── di/
│   └── AndroidModule.kt         — Koin viewModel { ConverterAndroidViewModel(...) }
└── ui/
    ├── ConverterAndroidViewModel.kt  — AndroidX ViewModel wrapping shared ConverterViewModel
    ├── ConverterScreen.kt
    └── components/
        ├── AmountInput.kt
        ├── ConversionResultCard.kt
        └── CurrencySelector.kt       — Flag emoji extension val on Currency (local to file)
```

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Kotlin Multiplatform | 2.1.21 | Shared business logic |
| Ktor | 3.1.3 | HTTP client (platform engines: OkHttp / Darwin) |
| kotlinx.serialization | 1.8.1 | JSON parsing |
| kotlinx.coroutines | 1.10.2 | Async / Flow-based state |
| kotlinx.datetime | 0.6.0 | Platform-agnostic timestamps |
| Koin | 4.0.0 | Dependency injection |
| Jetpack Compose BOM | 2025.05.01 | Android UI |
| SwiftUI | — | iOS UI (placeholder) |

---

## KMP Rules

These rules are non-negotiable for the shared module:

1. **No Android framework dependencies in `:shared` commonMain or androidMain.**
   `androidMain` may only have platform-specific Ktor engine (`ktor-client-okhttp`).
   `koin-android` belongs exclusively in `:androidApp`, never in `:shared`.

2. **Use Ktor for all networking** — never Retrofit or OkHttp directly. Ktor provides
   both Android (OkHttp engine) and iOS (Darwin engine) implementations transparently.

3. **Koin for DI:**
   - `koin-core` in `:shared` commonMain
   - `koin-android` + `koin-androidx-compose` in `:androidApp` only
   - Koin 4.0: use `org.koin.core.module.dsl.viewModel` (not the deprecated `org.koin.androidx.viewmodel.dsl.viewModel`)
   - Define `sharedModule` in `SharedModule.kt`; define `androidModule` in `AndroidModule.kt`

4. **kotlinx.serialization for JSON** — annotate all API response classes with
   `@Serializable`. Use `@SerialName("snake_case_key")` when the JSON field name does
   not match the Kotlin property name (camelCase preferred for Kotlin properties).

5. **kotlinx-datetime for time** — use `Clock.System.now()` instead of
   `System.currentTimeMillis()` or any platform-specific date API.

6. **Shared ViewModel pattern:**
   - `ConverterViewModel` in `:shared` accepts a `CoroutineScope` constructor parameter.
   - Never reference `viewModelScope` or any AndroidX class in `:shared`.
   - `:androidApp` wraps it in `ConverterAndroidViewModel : ViewModel()`, passing
     `viewModelScope` at construction time.

7. **Expose `StateFlow`, never `MutableStateFlow`** — use `asStateFlow()` at the
   ViewModel boundary.

---

## Code Conventions

### Kotlin / Shared

- `val` over `var`; immutable by default
- Never use `!!` — prefer safe calls `?.`, `?: throw`, `requireNotNull()`, or `checkNotNull()`
- Named arguments when calling functions with 3+ parameters
- `sealed class` / `sealed interface` for exhaustive state modeling
- `@SerialName` for JSON fields that differ from the camelCase Kotlin property name
- `update { }` (not direct `.value = .value + ...`) when mutating `MutableStateFlow`
  to avoid non-atomic read-modify-write races

### Android / Compose

- `collectAsStateWithLifecycle()` — never bare `collectAsState()` in Compose UI
- All colors via `MaterialTheme.colorScheme.*` — never hardcoded color literals
- State hoisting: composables receive state and callbacks as parameters
- One `modifier: Modifier = Modifier` parameter per composable, listed last
- `const val` for compile-time string/number constants in `companion object`

### Naming

- Files: `PascalCase.kt` matching primary class name
- Classes/objects/interfaces: `PascalCase`
- Functions, properties, variables: `lowerCamelCase`
- Constants in companion objects: `SCREAMING_SNAKE_CASE`
- JSON response data classes: camelCase Kotlin properties + `@SerialName` for mapping

---

## Testing

Tests live in `shared/src/commonTest/`. Run with `./gradlew :shared:testDebugUnitTest`.

- Use `FakeCurrencyApi` (implements `CurrencyApi` interface) for test doubles
- Use `kotlinx-coroutines-test`: `runTest`, `StandardTestDispatcher`, `advanceUntilIdle()`
- Set `Dispatchers.setMain(testDispatcher)` in `@BeforeTest` for ViewModel tests
- Dependencies: `kotlin-test` + `kotlinx-coroutines-test` in `commonTest.dependencies`

---

## CI

CI runs via `.github/workflows/ci.yml` on PRs to main and pushes to main:
`shared:build` → `shared:testDebugUnitTest` → `androidApp:assembleDebug` → `androidApp:testDebugUnitTest`
