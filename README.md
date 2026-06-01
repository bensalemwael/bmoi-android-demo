# BMOI × SIFIP — Android Demo

Application Android native (Kotlin + Jetpack Compose + Material 3) de **démonstration
bancaire** destinée à être présentée à la **BMOI (Bank of Africa Madagascar — bmoi.mg)**.

Elle simule un parcours bancaire sécurisé intégrant les APIs SIFIP **en mode mock** :

| Niveau | Étape                       | APIs SIFIP appelées (mock)                                     |
| :----- | :-------------------------- | :------------------------------------------------------------- |
| 1      | Authentification (Login)    | `Number Verify` → `SIM Swap Check` → `Device Swap`             |
| 2      | Transaction (Virement)      | `Fraud Engine` (score IA + décision APPROVE/REVIEW/REJECT)     |

Tournée sur n'importe quel device Samsung **Android 8+ (API 26)** — pas de réseau requis.

---

## 1. Pré-requis

| Outil                | Version recommandée |
| :------------------- | :------------------ |
| Android Studio       | Hedgehog (2023.1.1) ou plus récent |
| JDK                  | 17                  |
| Android SDK Platform | 34                  |
| Gradle               | 8.9 (téléchargé automatiquement par le wrapper) |

> Le device doit être en **mode développeur** + USB debugging activé.

## 2. Compiler et lancer

### Depuis Android Studio

1. **File → Open** → sélectionnez le dossier `bmoi-android-demo`.
2. Laissez Gradle finir la synchronisation (≈ 2 min la 1re fois).
3. Branchez le Samsung et sélectionnez-le dans la barre supérieure.
4. **Run ▶ app**.

### Depuis la ligne de commande

```powershell
# Windows
cd bmoi-android-demo
.\gradlew.bat installDebug
```

```bash
# Linux / macOS
cd bmoi-android-demo
./gradlew installDebug
```

> ⚠ Si le wrapper Gradle (`gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`)
> n'est pas présent dans votre clone, générez-le une fois avec un Gradle local :
> `gradle wrapper --gradle-version 8.9`

## 3. Scénarios de démo

Le **mock SIFIP** est complètement isolé dans
`app/src/main/java/com/bmoi/sifipdemo/data/mock/`. Le scénario actif définit
les réponses des 4 APIs et est commutable de **trois façons** :

### 3.1 Au runtime (recommandé pour la démo live)

Dans le bandeau bleu de l'écran de **Login**, un menu déroulant
*"Démo : Tout OK (score bas)"* permet de basculer instantanément vers :

| Scénario              | Effet                                               |
| :-------------------- | :-------------------------------------------------- |
| `ALL_OK`              | 3 checks ✅ + score fraude 12 % → tout passe        |
| `FAIL_NUMBER_VERIFY`  | Number Verify ❌ → login bloqué dès l'étape 1       |
| `FAIL_SIM_SWAP`       | SIM Swap ❌ (changement récent détecté)             |
| `FAIL_DEVICE_SWAP`    | Device Swap ❌ (appareil inconnu)                   |
| `FAIL_FRAUD`          | Login OK mais score fraude 87 % → virement bloqué  |

### 3.2 À la compilation (CI / build automatisé)

```powershell
.\gradlew.bat assembleDebug -PsifipScenario=FAIL_FRAUD
```

Le scénario par défaut au démarrage est lu dans `BuildConfig.DEFAULT_SIFIP_SCENARIO`.

### 3.3 Par code (tests, démos scriptées)

```kotlin
val app = context.applicationContext as BmoiApplication
app.sifipMock.setScenario(MockScenario.FAIL_SIM_SWAP)
```

## 4. Architecture

```
app/
├── BmoiApplication.kt          ← composition root (1 endroit pour swapper le mock)
├── MainActivity.kt
├── ViewModelFactories.kt
├── navigation/
│   └── NavGraph.kt             ← Splash → Login → Dashboard → Transfer
├── data/
│   ├── model/                  ← DTOs SIFIP + modèles compte
│   ├── mock/
│   │   ├── SifipApi.kt          ← interface — contrat unique
│   │   ├── SifipMockService.kt  ← implémentation locale (à remplacer par Retrofit)
│   │   └── MockScenario.kt
│   └── repository/
│       └── BankRepository.kt    ← données mock du dashboard
└── ui/
    ├── splash/                  ← Splash écran (logo BMOI)
    ├── login/                   ← 3 SIFIP checks animés
    ├── dashboard/               ← Solde + transactions + bouton virement
    ├── transfer/                ← Formulaire + jauge fraud score IA
    ├── components/              ← BmoiButton, CheckStepRow, FraudGauge, BmoiLogo
    └── theme/                   ← Couleurs BMOI navy/orange, Material 3
```

**Pattern** : MVVM + StateFlow (Compose). Chaque ViewModel expose un seul
`StateFlow<UiState>`, observé via `collectAsState()` côté Composable.

## 5. Identité visuelle BMOI

| Couleur          | Hex        | Usage                          |
| :--------------- | :--------- | :----------------------------- |
| BMOI Navy        | `#0A2F5C`  | Header, primary color          |
| BMOI Navy Dark   | `#061F3F`  | Gradient header / status bar   |
| BMOI Orange      | `#F39200`  | Accents, secondary color       |
| BMOI Gold        | `#D4A437`  | Tertiary, badges               |

Le logo est généré comme **vector drawable** dans
`res/drawable/bmoi_logo.xml`. Pour utiliser le logo officiel reçu de la
banque, **remplacez ce fichier** par le SVG converti via Android Studio
(*New → Vector Asset*) ou par un PNG `bmoi_logo.png` dans `res/drawable-xxhdpi/`.

## 6. Brancher la vraie API SIFIP

Le seul fichier à toucher est `BmoiApplication.kt` :

```kotlin
// avant (démo)
sifipMock = SifipMockService(initial)
val sifipApi: SifipApi = sifipMock

// après (prod)
val sifipApi: SifipApi = RetrofitSifipClient(
    baseUrl = "https://api.34-53-128-84.sslip.io",
    oauthClientId = BuildConfig.SIFIP_CLIENT_ID,
    privateKeyPem = readPemFromAssets("private.pem"),
)
```

Endpoints documentés dans
[`sifip-platform/docs/COLLEAGUE-GUIDE.md` §5](../sifip-platform/docs/COLLEAGUE-GUIDE.md) :

```
POST /number-verification/vwip/verify    scope: sifip:number-verify
POST /sim-swap/vwip/check                scope: sifip:sim-swap
POST /device-swap/vwip/check             scope: sifip:device-swap
POST /fraud-engine/score                 scope: sifip:fraud-engine
```

Les `data class` dans `data/model/SifipModels.kt` reflètent déjà ces réponses,
donc l'UI ne bouge pas.

## 7. Conseils pour la démo en clientèle

1. Préchauffer l'écran : laisser tourner une fois en `ALL_OK` pour montrer le
   parcours complet (auth → solde → virement OK).
2. Démontrer la résilience anti-fraude : passer en `FAIL_FRAUD`, refaire un
   virement → la jauge passe en rouge à 87 %, transaction bloquée.
3. Démontrer chaque check SIFIP indépendamment via `FAIL_SIM_SWAP` et
   `FAIL_DEVICE_SWAP`.
4. Le délai artificiel (≈ 500 ms par appel) est volontaire pour que les
   animations de check ✅ soient visibles à l'œil nu.

## 8. Licence

Code propriétaire — usage **démo BMOI uniquement**. Ne pas distribuer.
