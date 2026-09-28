package com.example.util

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    PERSIAN("fa", "Persian", "فارسی")
}

object Strings {
    fun appName(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "سِروِی‌فلو" else "SurveyFlow"

    // Tabs
    fun tabSurveys(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "پرسشنامه‌ها" else "Surveys"
    fun tabArchive(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "بایگانی" else "Archive"
    fun tabPresets(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "الگوها" else "Presets"

    // Surveys Screen
    fun surveysSubtitle(lang: AppLanguage, count: Int) =
        if (lang == AppLanguage.PERSIAN) "$count پرسشنامه فعال" else "$count active questionnaires"
    fun importJsonButton(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "وارد کردن فایل JSON" else "Import JSON File"
    fun timesFilled(lang: AppLanguage, count: Int) =
        if (lang == AppLanguage.PERSIAN) "$count بار تکمیل شده" else "Filled $count times"
    fun startSurvey(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "شروع تکمیل" else "Start Survey"
    fun deleteSurvey(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "حذف" else "Delete"
    fun noSurveysTitle(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "هیچ پرسشنامه‌ای بارگذاری نشده است" else "No questionnaires available"
    fun noSurveysDesc(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "یک فایل JSON وارد کنید یا یکی از الگوهای پیش‌فرض را بارگذاری نمایید." else "Import a questionnaire JSON file or load one of the built-in presets."
    fun loadPresets(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "مشاهده الگوها" else "View Presets"
    fun questionsCount(lang: AppLanguage, count: Int) =
        if (lang == AppLanguage.PERSIAN) "$count سؤال" else "$count Questions"
    fun minutes(lang: AppLanguage, count: Int) =
        if (lang == AppLanguage.PERSIAN) "حدود $count دقیقه" else "~$count min"

    // Survey Filling Screen
    fun questionCounter(lang: AppLanguage, current: Int, total: Int) =
        if (lang == AppLanguage.PERSIAN) "سؤال $current از $total" else "Question $current of $total"
    fun answeredCount(lang: AppLanguage, answered: Int, total: Int) =
        if (lang == AppLanguage.PERSIAN) "$answered از $total پاسخ داده شد" else "$answered of $total answered"
    fun previous(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "قبلی" else "Previous"
    fun next(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "بعدی" else "Next"
    fun submitSurvey(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "ثبت نهایی پرسشنامه" else "Submit Survey"
    fun requiredField(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "اجباری" else "Required"
    fun optionalField(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "اختیاری" else "Optional"
    fun answerRequiredAlert(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "لطفاً پیش از ادامه، به این سؤال الزامی پاسخ دهید." else "Please provide an answer before continuing."
    fun exitSurveyPromptTitle(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "خروج از تکمیل پرسشنامه؟" else "Exit Survey?"
    fun exitSurveyPromptDesc(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "پاسخ‌های ثبت‌شده تا این مرحله ذخیره نخواهند شد." else "Your current progress will not be saved."
    fun keepEditing(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "ادامه تکمیل" else "Keep Editing"
    fun exit(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "خروج" else "Exit"
    fun yes(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "بله" else "Yes"
    fun no(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "خیر" else "No"
    fun selectAnOption(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "انتخاب کنید" else "Select an option"

    // Archive / History Screen
    fun archiveTitle(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "بایگانی پاسخ‌ها" else "Responses Archive"
    fun totalSavedResponses(lang: AppLanguage, count: Int) =
        if (lang == AppLanguage.PERSIAN) "$count پاسخ ذخیره‌شده محلی" else "$count local responses recorded"
    fun exportConsolidatedJson(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "خروجی کل فایل JSON" else "Export Consolidated JSON"
    fun shareJson(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "اشتراک‌گذاری" else "Share"
    fun filterAll(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "همه" else "All"
    fun noResponsesTitle(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "هیچ پاسخی ثبت نشده است" else "No responses yet"
    fun noResponsesDesc(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "پرسشنامه‌ها را تکمیل کنید تا پاسخ‌ها به صورت امن و محلی اینجا ذخیره شوند." else "Complete questionnaires to view, inspect, and export your local submissions."
    fun viewDetails(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "مشاهده جزئیات" else "View Details"
    fun durationSeconds(lang: AppLanguage, sec: Long) =
        if (lang == AppLanguage.PERSIAN) "$sec ثانیه" else "${sec}s duration"

    // Submission Detail
    fun submissionDetailTitle(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "جزئیات پاسخ" else "Submission Detail"
    fun exportSingleJson(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "خروجی فایل JSON" else "Export JSON File"
    fun copyRawJson(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "کپی متن JSON" else "Copy Raw JSON"
    fun tabResponsesSummary(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "خلاصه پاسخ‌ها" else "Responses Summary"
    fun tabRawJson(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "کد خام JSON" else "Raw JSON"
    fun deleteSubmission(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "حذف این پاسخ" else "Delete Submission"

    // Presets Screen
    fun presetsTitle(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "الگوها" else "Presets"
    fun presetsSubtitle(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "الگوهای آماده برای تست و تکمیل سریع" else "Ready-to-use questionnaire templates"
    fun sampleEnTitle(lang: AppLanguage) = "Daily Well-Being (EN)"
    fun sampleEnDesc(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "پرسشنامه ۵ گزینه‌ای لیکرت به زبان انگلیسی" else "5-point Likert scale, rating, habits checklist, and reflection (English)"
    fun sampleFaTitle(lang: AppLanguage) = "ارزیابی روزانه سلامت (FA)"
    fun sampleFaDesc(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "پرسشنامه ۵ گزینه‌ای لیکرت به زبان فارسی، وضوح ذهنی و عادات کاری" else "5-point Persian Likert scale assessing mental clarity, work satisfaction, and habits"
    fun loadIntoSurveys(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "افزودن به پرسشنامه‌ها" else "Load into Surveys"
    fun copyJson(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "کپی JSON" else "Copy JSON"
    fun exportTemplate(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "ذخیره فایل" else "Export File"
    fun customJsonHeader(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "ورود پرسشنامه دلخواه" else "Custom Questionnaire"
    fun pasteRawJsonTitle(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "ویرایشگر و چسباندن متن JSON" else "Paste & Validate Raw JSON"
    fun validateAndImport(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "اعتبارسنجی و افزودن" else "Validate & Import"
    fun schemaGuide(lang: AppLanguage) =
        if (lang == AppLanguage.PERSIAN) "راهنمای ساختار فیلدهای JSON" else "JSON Schema Guide"

    // Likert Default Labels in Persian
    val LIKERT_5_FA = listOf("کاملاً مخالف", "مخالف", "خنثی / ممتنع", "موافق", "کاملاً موافق")
    val LIKERT_7_FA = listOf("کاملاً مخالف", "مخالف", "تا حدی مخالف", "خنثی", "تا حدی موافق", "موافق", "کاملاً موافق")
    val LIKERT_5_EN = listOf("Strongly Disagree", "Disagree", "Neutral", "Agree", "Strongly Agree")
    val LIKERT_7_EN = listOf("Strongly Disagree", "Disagree", "Somewhat Disagree", "Neutral", "Somewhat Agree", "Agree", "Strongly Agree")
}
