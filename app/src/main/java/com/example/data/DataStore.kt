package com.example.data

import androidx.compose.runtime.mutableStateListOf

data class CurriculumTopic(val name: String)
data class CurriculumLesson(val name: String, val topics: MutableList<CurriculumTopic> = mutableStateListOf())
data class SchoolClass(val name: String, val lessons: MutableList<CurriculumLesson> = mutableStateListOf())

data class PhysicalTest(val id: String, val name: String, val questionCount: Int, var answerKey: String)
data class PhysicalTopic(val name: String, val tests: MutableList<PhysicalTest> = mutableStateListOf())
data class PhysicalCourse(val name: String, val topics: MutableList<PhysicalTopic> = mutableStateListOf())
data class PhysicalBank(val id: String, val name: String, val courses: MutableList<PhysicalCourse> = mutableStateListOf())

data class JsonQuestion(val text: String, val options: List<String>, val correctAnswerIndex: Int)

data class JsonTest(
    val id: String,
    val name: String,
    val className: String,
    val lessonName: String,
    val topicName: String,
    val questions: List<JsonQuestion>
)

data class HtmlTest(
    val id: String,
    val name: String,
    val className: String,
    val lessonName: String,
    val topicName: String,
    val htmlContent: String,
    val questionCount: Int,
    var answerKey: String
)

enum class VisualTestType {
    OPEN_ENDED,
    OPTICAL
}

data class VisualTest(
    val id: String,
    val name: String,
    val className: String,
    val lessonName: String,
    val topicName: String,
    val imageUris: List<String>, 
    val type: VisualTestType,
    val questionCount: Int,
    var answerKey: String
)

data class StudentTestSubmission(
    val testId: String,
    val testName: String,
    val studentId: String,
    val answers: String, // String of chars e.g. "ABCD "
    val correctCount: Int,
    val wrongCount: Int,
    val emptyCount: Int,
    var teacherScore: Int? = null,
    var teacherFeedback: String? = null
)

data class StudentUser(
    val id: String,
    val name: String,
    val grade: String,
    val avatarUrl: String? = null,
    val badgeCount: Int = 0,
    val avgCompletionHours: Int = 0
)

object GlobalPlatformData {
    val students = mutableStateListOf<StudentUser>(
        StudentUser("std_1", "Ahmet Yılmaz", "9. Sınıf", badgeCount = 5, avgCompletionHours = 12),
        StudentUser("std_2", "Ayşe Kaya", "10. Sınıf", badgeCount = 12, avgCompletionHours = 4),
        StudentUser("std_3", "Mehmet Demir", "9. Sınıf", badgeCount = 2, avgCompletionHours = 48),
        StudentUser("std_4", "Zeynep Çelik", "11. Sınıf", badgeCount = 8, avgCompletionHours = 8),
        StudentUser("std_5", "Can Özkan", "12. Sınıf", badgeCount = 15, avgCompletionHours = 2)
    )
    val curriculum = mutableStateListOf<SchoolClass>(
        SchoolClass("9. Sınıf", mutableStateListOf(
            CurriculumLesson("Matematik", mutableStateListOf(CurriculumTopic("Kümeler"), CurriculumTopic("Mantık"))),
            CurriculumLesson("Fizik", mutableStateListOf(CurriculumTopic("Fizik Bilimine Giriş"), CurriculumTopic("Hız ve Hareket")))
        )),
        SchoolClass("10. Sınıf", mutableStateListOf(
            CurriculumLesson("Matematik", mutableStateListOf(CurriculumTopic("Polinomlar"), CurriculumTopic("Fonksiyonlar")))
        ))
    )
    
    val physicalBanks = mutableStateListOf<PhysicalBank>(
        PhysicalBank(
            id = "bank_1",
            name = "3D TYT Matematik Soru Bankası",
            courses = mutableStateListOf(
                PhysicalCourse(
                    name = "Matematik",
                    topics = mutableStateListOf(
                        PhysicalTopic(
                            name = "Kümeler",
                            tests = mutableStateListOf(
                                PhysicalTest(id = "test_1", name = "Test 1 (Kolay)", questionCount = 8, answerKey = "ABCDEABC"),
                                PhysicalTest(id = "test_2", name = "Test 2 (Orta)", questionCount = 10, answerKey = "DDEEABCDEF")
                            )
                        ),
                        PhysicalTopic(
                            name = "Mantık",
                            tests = mutableStateListOf(
                                PhysicalTest(id = "test_3", name = "Mantık Karma Test", questionCount = 5, answerKey = "AABBC")
                            )
                        )
                    )
                )
            )
        ),
        PhysicalBank(
            id = "bank_2",
            name = "Aydın Yayınları AYT Fizik",
            courses = mutableStateListOf(
                PhysicalCourse(
                    name = "Fizik",
                    topics = mutableStateListOf(
                        PhysicalTopic(
                            name = "Vektörler ve Bağıl Hareket",
                            tests = mutableStateListOf(
                                PhysicalTest(id = "test_4", name = "Vektörler Test 1", questionCount = 5, answerKey = "EDBCA")
                            )
                        )
                    )
                )
            )
        )
    )

    val trialExams = mutableStateListOf<PhysicalBank>(
        PhysicalBank(
            id = "trial_1",
            name = "Türkiye Geneli TYT Denemesi 1",
            courses = mutableStateListOf(
                PhysicalCourse(
                    name = "Türkçe",
                    topics = mutableStateListOf(
                        PhysicalTopic(
                            name = "Tüm Konular",
                            tests = mutableStateListOf(
                                PhysicalTest(id = "trial_test_1", name = "Türkçe Testi", questionCount = 40, answerKey = "ABCDEABCDEABCDEABCDEABCDEABCDEABCDEABCDE")
                            )
                        )
                    )
                ),
                PhysicalCourse(
                    name = "Matematik",
                    topics = mutableStateListOf(
                        PhysicalTopic(
                            name = "Tüm Konular",
                            tests = mutableStateListOf(
                                PhysicalTest(id = "trial_test_2", name = "Matematik Testi", questionCount = 40, answerKey = "EDCBAEDCBAEDCBAEDCBAEDCBAEDCBAEDCBAEDCBA")
                            )
                        )
                    )
                )
            )
        )
    )
    
    val jsonTests = mutableStateListOf<JsonTest>(
        JsonTest(
            id = "json_test_1",
            name = "Trigonometri Temelleri",
            className = "11. Sınıf",
            lessonName = "Matematik",
            topicName = "Trigonometri",
            questions = listOf(
                JsonQuestion("sin(30) kaçtır?", listOf("1/2", "√2/2", "√3/2", "1", "0"), 0),
                JsonQuestion("cos(60) kaçtır?", listOf("√3/2", "√2/2", "1/2", "1", "0"), 2),
                JsonQuestion("tan(45) kaçtır?", listOf("1/2", "√3/3", "1", "√3", "Tanımsız"), 2)
            )
        ),
        JsonTest(
            id = "json_test_2",
            name = "Hız ve Hareket Soruları",
            className = "9. Sınıf",
            lessonName = "Fizik",
            topicName = "Hız ve Hareket",
            questions = listOf(
                JsonQuestion("Sabit hızlı bir hareketlinin ivmesi kaçtır?", listOf("0", "1", "Artar", "Azalır", "Bilinemez"), 0),
                JsonQuestion("Hızın birimi aşağıdakilerden hangisidir?", listOf("m", "m/s", "m/s²", "N", "J"), 1),
                JsonQuestion("Yer değiştirme ile alınan yol arasındaki fark nedir?", listOf("Fark yoktur", "Yer değiştirme vektörel, yol skalerdir", "Yol vektörel, yer değiştirme skalerdir", "İkisi de türetilmiş büyüklüktür", "Biri temel boyut, diğeri türetilmiştir"), 1)
            )
        )
    )

    val htmlTests = mutableStateListOf<HtmlTest>(
        HtmlTest(
            id = "html_test_1",
            name = "Kümeler Kavrama Testi",
            className = "9. Sınıf",
            lessonName = "Matematik",
            topicName = "Kümeler",
            htmlContent = "<html><body><h1>Kümeler</h1><p><b>1. Soru:</b> A = {1, 2, 3} ve B = {3, 4, 5} olduğuna göre A ∩ B kümesi aşağıdakilerden hangisidir?</p><p>A) {1}<br>B) {2}<br>C) {3}<br>D) {4}<br>E) {5}</p><hr><p><b>2. Soru:</b> Boş kümenin eleman sayısı kaçtır?</p><p>A) 0<br>B) 1<br>C) 2<br>D) Sonsuz<br>E) Bilinemez</p><hr><p><b>3. Soru:</b> Bir kümenin kendisi hariç alt kümelerine ne ad verilir?</p><p>A) Öz alt küme<br>B) Boş küme<br>C) Ayrık küme<br>D) Evrensel küme<br>E) Kesişim kümesi</p></body></html>",
            questionCount = 3,
            answerKey = "CAA"
        ),
        HtmlTest(
            id = "html_test_2",
            name = "Fizik Bilimine Giriş",
            className = "9. Sınıf",
            lessonName = "Fizik",
            topicName = "Fizik Bilimine Giriş",
            htmlContent = "<html><body><h1>Fizik Bilimine Giriş</h1><p><b>1. Soru:</b> Aşağıdakilerden hangisi temel büyüklüktür?</p><p>A) Hız<br>B) İvme<br>C) Kuvvet<br>D) Uzunluk<br>E) Enerji</p><hr><p><b>2. Soru:</b> SI birim sisteminde kütlenin birimi nedir?</p><p>A) Gram<br>B) Kilogram<br>C) Ton<br>D) Newton<br>E) Metre</p></body></html>",
            questionCount = 2,
            answerKey = "DB"
        )
    )

    val visualTests = mutableStateListOf<VisualTest>(
        VisualTest(
            id = "visual_test_1",
            name = "Geometri Sorusu (Açık Uçlu)",
            className = "10. Sınıf",
            lessonName = "Matematik",
            topicName = "Üçgenler",
            imageUris = emptyList(),
            type = VisualTestType.OPEN_ENDED,
            questionCount = 1,
            answerKey = ""
        ),
        VisualTest(
            id = "visual_test_2",
            name = "Fizik Deneyi (Optik)",
            className = "9. Sınıf",
            lessonName = "Fizik",
            topicName = "Optik",
            imageUris = emptyList(),
            type = VisualTestType.OPTICAL,
            questionCount = 5,
            answerKey = "BCDEA"
        )
    )

    val studentSubmissions = mutableStateListOf<StudentTestSubmission>()
}
