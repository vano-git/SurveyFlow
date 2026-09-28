# SurveyFlow 📋

> **Secluded, Offline Android Survey & Likert Questionnaire Vault with JSON Import/Export**  
> Supports repeated daily survey completions, multi-point Likert scales, offline Room persistence, and full bilingual support (English & فارسی).

---

## 🌟 Key Features

- **📊 Comprehensive Likert & Question Engine:**
  - **Likert Scales:** 5-point, 7-point, or custom points with custom localized anchor labels (*Strongly Disagree* to *Strongly Agree* / *کاملاً مخالف* تا *کاملاً موافق*).
  - **Single & Multi-Choice:** Radio lists and checkbox group selections.
  - **Star Rating:** 1–5 or 1–10 star ratings.
  - **Sliders:** Continuous numerical sliders with custom step sizes and unit badges.
  - **Boolean:** Binary Yes/No decision chips.
  - **Open Text:** Free-form reflection text inputs.

- **🌐 Full Persian (فارسی) & English Bilingual Support:**
  - One-click in-app language switcher (`[FA | EN]`).
  - Dynamic Right-to-Left (RTL) layout direction provider for Persian.
  - Fully translated UI elements, buttons, dialogues, tabs, and Likert anchor labels.

- **🔒 100% Secluded Offline Architecture:**
  - Zero external tracking or network requirements. All responses are stored locally on your device in an encrypted Room SQLite database.
  - Users can complete surveys as many times as needed throughout the day without limits.

- **📁 Standardized JSON Import & Export:**
  - **Import:** Import custom questionnaires via Android Storage Access Framework (SAF) document picker or direct JSON code editor.
  - **Batch Export:** Export all historical submissions into a consolidated, formatted JSON file (`survey_responses_YYYYMMDD_HHmm.json`).
  - **Individual Export & Share:** Inspect individual responses, copy formatted JSON to clipboard, or share directly via the Android native share sheet.

- **📱 Polished Edge-to-Edge M3 UI:**
  - Material 3 design system with adaptive dark/light theming.
  - System navigation bar insets protection (`navigationBarsPadding`): buttons never get hidden behind Android 3-button navigation or gesture navigation pills.
  - Jump-dot indicator for fast question navigation.

---

## 📂 Repository Structure

```text
├── app/                              # Android application module
│   ├── src/main/java/com/example/    # Kotlin Clean Architecture source code
│   │   ├── data/                     # Room Database, DAO & Repository
│   │   ├── model/                    # Data models, Likert definitions & JSON parser
│   │   ├── ui/                       # Jetpack Compose UI (Screens, Components, Theme)
│   │   └── util/                     # Localization & translation dictionaries
│   └── src/main/res/                 # Adaptive icons, values (en) & values-fa (Persian)
├── samples/                          # Sample Questionnaire JSONs ready for import
│   ├── daily_wellbeing_en.json       # English 5-Point Likert Daily Well-Being
│   ├── daily_wellbeing_fa.json       # Persian 5-Point Likert ارزیابی روزانه سلامت
│   └── ergonomics_health_fa.json     # Persian 7-Point Likert ارزیابی ارگونومی محیط کار
├── .github/workflows/
│   └── build-apk.yml                 # Automated CI/CD workflow building and publishing APK
└── README.md                         # Project documentation
```

---

## 📥 Sample Questionnaires Included in `/samples/`

This repository includes 3 production-ready sample questionnaire JSON files located in the `samples/` directory:

1. **`samples/daily_wellbeing_en.json` (English)**:
   - 5-Point Likert scale on mental clarity, work focus, and ergonomics.
   - Screen break boolean, satisfaction rating, habits checklist, and reflection notes.

2. **`samples/daily_wellbeing_fa.json` (فارسی - Persian)**:
   - پرسشنامه ۵ گزینه‌ای ارزیابی روزانه انرژی، تمرکز و رضایت کاری به زبان فارسی.

3. **`samples/ergonomics_health_fa.json` (فارسی - Persian)**:
   - پرسشنامه تخصصی ۷ گزینه‌ای ارگونومی محیط کار و ساعات نشستن پشت میز.

---

## 📋 JSON Specifications

### 1. Questionnaire Import JSON Schema

```json
{
  "id": "survey_daily_wellbeing_en",
  "title": "Daily Well-Being & Focus Check-in",
  "description": "Daily self-reflection measuring mental clarity, ergonomic comfort, and productivity.",
  "category": "Daily Routine",
  "version": "1.0",
  "estimatedMinutes": 2,
  "questions": [
    {
      "id": "q1_energy",
      "text": "I felt energetic and motivated throughout my day.",
      "type": "likert",
      "required": true,
      "scale": {
        "points": 5,
        "labels": ["Strongly Disagree", "Disagree", "Neutral", "Agree", "Strongly Agree"]
      }
    },
    {
      "id": "q2_breaks",
      "text": "Did you take intentional screen breaks away from your device today?",
      "type": "boolean",
      "required": true
    },
    {
      "id": "q3_rating",
      "text": "Overall rating of your day's work satisfaction:",
      "type": "rating",
      "required": true,
      "minRating": 1,
      "maxRating": 5
    }
  ]
}
```

### 2. Exported Response Record JSON Schema

```json
{
  "submissionId": "sub_a9b1c2d3-4567-890a-bcde-f1234567890a",
  "surveyId": "survey_daily_wellbeing_en",
  "surveyTitle": "Daily Well-Being & Focus Check-in",
  "surveyCategory": "Daily Routine",
  "surveyVersion": "1.0",
  "startedAt": "2026-09-28T12:00:00Z",
  "completedAt": "2026-09-28T12:02:15Z",
  "durationSeconds": 135,
  "timestamp": 1790623335000,
  "respondentTag": "Offline User",
  "deviceEnvironment": "Secluded Offline Android Vault",
  "totalQuestions": 3,
  "answeredQuestions": 3,
  "responses": [
    {
      "questionId": "q1_energy",
      "questionText": "I felt energetic and motivated throughout my day.",
      "questionType": "likert",
      "answerDisplay": "Agree (4)",
      "rawValue": 4,
      "scalePoints": 5
    },
    {
      "questionId": "q2_breaks",
      "questionText": "Did you take intentional screen breaks away from your device today?",
      "questionType": "boolean",
      "answerDisplay": "Yes",
      "rawValue": true,
      "scalePoints": null
    }
  ]
}
```

---

## 🛡️ Sideloading & Google Play Protect Note

When installing any APK downloaded directly from GitHub Releases (outside the official Google Play Store), Android's **Google Play Protect** may display a prompt stating:  
> *"Unrecognized Developer / Play Protect doesn't recognize this app's developer. Install anyway?"*

### Why does this happen?
- This is standard Android security behavior for all sideloaded APKs that are not hosted on Google Play Console servers.
- **SurveyFlow is completely safe and privacy-friendly:**
  - It requests **0 dangerous permissions** (no microphone, no contacts, no camera, no location).
  - It does not require internet permissions (`android.permission.INTERNET` is not used).
  - All responses remain encrypted inside local app storage.
- **To Install:** Tap **"More details"** ➔ **"Install anyway"**.

---

## 🚀 Automated Builds via GitHub Actions

Every commit or release tag pushed to `main` automatically triggers the GitHub Actions workflow (`.github/workflows/build-apk.yml`):

1. Sets up JDK 17 (Eclipse Temurin) on Ubuntu.
2. Accepts Android SDK licenses and restores signing keys.
3. Compiles the app using Kotlin Symbol Processing (KSP 2.3.6).
4. Runs local unit and Robolectric test suites.
5. Builds and packages **`SurveyFlow-universal-app.apk`**.
6. Publishes the APK as a downloadable asset directly under the **Releases** tab on GitHub!
