# VideoEditor

App de edição de vídeo em **Kotlin** com **Jetpack Compose**, adotando o design system **Material You**, **Bento Grid** e **Liquid Glass**.

## Arquitetura

Camadas limpas (Clean Architecture) com **MVVM** e injeção de dependência **Hilt**:

```
presentation/   → UI (Composable) + ViewModels (@HiltViewModel)
    ├─ home/     → HomeScreen + HomeViewModel
    └─ editor/   → EditorScreen + EditorViewModel
domain/         → Modelos de domínio + interfaces de repositório + use cases
    ├─ model/
    ├─ repository/
    └─ usecase/
data/           → Implementação: Room (SQLite), mappers, repositórios
    ├─ local/    → entities, DAOs, DatabaseProvider
    ├─ repository/ → implementações
    └─ DataModule.kt → Hilt
core/           → design system (theme, LiquidGlass, BentoGrid, AppTopBar)
```

## Design System

- **Material You**: cores dinâmicas via `dynamicColorScheme` (Android 12+) com fallback escuro customizado em `VideoEditorTheme`.
- **Liquid Glass**: `LiquidGlassCard` / `LiquidGlassSurface` com blur, contorno e brilho de borda.
- **Bento Grid**: `BentoGrid` com células de tamanhos variados (`Small`, `Medium`, `Large`, `Square`) e feedback de elevação na seleção.

## Como buildar

Requer **Android Studio** (ou `AGP` + `JDK 17` + Android SDK 35):

```bash
./gradlew :app:assembleDebug
```

ou em Android Studio: `Build → Make Project`.

## Dependências principais

- Jetpack Compose (Material 3, Navigation)
- Hilt + KSP
- Room (SQLite)
- Media3 (reprodução/exportação)
- Coil (imagens)
- kotlinx.serialization