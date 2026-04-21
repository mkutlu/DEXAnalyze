# Testing Guide for DEXAnalyze

This document outlines the testing strategy, structure, and best practices for the DEXAnalyze project.

## Test Structure

```
app/src/test/java/com/aarw/dexanalyze/
├── data/
│   ├── auth/
│   │   └── AuthRepositoryTest.kt
│   ├── api/
│   │   └── ApiClientTest.kt (future)
│   └── repository/
│       └── ScanRepositoryTest.kt
├── ui/
│   └── screens/
│       ├── login/
│       │   └── LoginViewModelTest.kt
│       └── dashboard/
│           └── DashboardViewModelTest.kt
└── util/
    ├── LoggerTest.kt
    └── TestDispatchers.kt
```

## Testing Stack

- **JUnit 4**: Core testing framework
- **Mockk**: Mocking and verification library
- **Turbine**: Flow and StateFlow testing
- **Coroutines Test**: Coroutine testing utilities

## Running Tests

### Run all unit tests
```bash
./gradlew test
```

### Run specific test class
```bash
./gradlew test --tests LoggerTest
```

### Run tests with coverage
```bash
./gradlew test --tests "*" --gradle-debug
```

## Test Categories

### 1. **Utility Tests** (`util/`)

Test helper functions, logging, and shared utilities.

**Example: LoggerTest**
- Verifies secure data redaction
- Tests all log levels (debug, info, warn, error)
- Ensures bearer tokens and sensitive fields are masked

### 2. **Data Layer Tests** (`data/`)

#### AuthRepository
- OAuth URL generation and code exchange
- Token refresh logic
- Login/logout state management
- Error handling

#### ScanRepository
- Demo mode vs. API mode
- Data fetching and enrichment
- Error handling (HTTP errors, network failures)
- Empty response handling

#### ApiClient (future)
- Request/response logging without sensitive data
- Authentication header injection
- HTTP status code handling

### 3. **UI Layer Tests** (`ui/`)

#### ViewModels
- UI state initialization
- State updates on data loading
- Error state management
- User action handling

**Testing Pattern:**
```kotlin
@Test
fun `feature works correctly`() = runTest {
    // Arrange
    val mock = mockk<Service>()
    coEvery { mock.action() } returns Result.success(data)
    val viewModel = ViewModel(mock)

    // Act
    viewModel.trigger()

    // Assert
    viewModel.state.test {
        val state = awaitItem()
        assertEquals(expected, state.value)
        cancelAndConsumeRemainingEvents()
    }
}
```

## Best Practices

### 1. **Use `runTest` for Coroutines**
Always wrap coroutine tests with `runTest` from `kotlinx.coroutines.test`:
```kotlin
@Test
fun `async operation works`() = runTest {
    // Coroutine code here
}
```

### 2. **Mock External Dependencies**
Use Mockk to mock:
- API services
- Repositories
- External data sources
- System dependencies

```kotlin
val service = mockk<ApiService>()
coEvery { service.fetchData() } returns Result.success(data)
```

### 3. **Test StateFlow with Turbine**
For testing Compose states and flows:
```kotlin
viewModel.uiState.test {
    val state = awaitItem()
    assertEquals(expected, state)
    cancelAndConsumeRemainingEvents()
}
```

### 4. **Test Error Cases**
Always test failure scenarios:
- Network errors
- Invalid input
- Empty responses
- Permission failures

### 5. **Verify Logging**
For security-sensitive operations, verify that sensitive data is not logged:
```kotlin
verify { Log.d(any(), not(contains("token"))) }
```

## Coverage Goals

| Component | Target Coverage |
|-----------|-----------------|
| Utilities | 100% |
| Data Layer | 80%+ |
| UI Layer (ViewModels) | 70%+ |
| UI Components | 30%+ (focus on logic) |

## Adding New Tests

When implementing a new feature:

1. **Create test file** in `src/test/java` matching the source structure
2. **Name it** `[ComponentName]Test.kt`
3. **Cover happy path** and main error cases
4. **Use descriptive test names** in natural language

Example:
```kotlin
class MyRepositoryTest {
    @Test
    fun `getData returns success when API responds`() { ... }
    
    @Test
    fun `getData returns error when network fails`() { ... }
    
    @Test
    fun `getData masks sensitive fields in logs`() { ... }
}
```

## Continuous Integration

Tests run automatically on:
- Local development (`./gradlew test`)
- Pre-commit hooks (when configured)
- CI/CD pipelines

Always run tests before pushing code:
```bash
./gradlew test && git push
```

## Troubleshooting

### Tests timeout
- Increase test timeout in build.gradle.kts
- Check for infinite loops or blocking calls

### Mockk not mocking static methods
- Use `mockkStatic()` for static methods
- Remember to set up every() calls

### StateFlow tests hang
- Always call `cancelAndConsumeRemainingEvents()` in Turbine
- Don't forget `awaitItem()` before assertions

## Future Improvements

- [ ] Integration tests with test containers
- [ ] Instrumented UI tests with Compose testing
- [ ] Performance benchmarks
- [ ] Contract testing with API
