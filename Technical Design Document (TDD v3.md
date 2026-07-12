\# Technical Design Document: On-Device AI Voice Summarizer (v3.0 \- Обновлено)

\*\*Описание проекта\*\*  
Нативное Android-приложение для записи голоса, локальной транскрибации (без интернета) и генерации саммари. Продукт ориентирован на профессиональную аудиторию (B2B/Prosumer), требующую строгой конфиденциальности данных и отказоустойчивости при работе с тяжелыми фоновыми процессами.

\*\*Архитектурный паттерн\*\*  
\* \*\*Архитектура:\*\* Clean Architecture \+ MVVM.  
\* \*\*Организация проекта:\*\* Многомодульная структура со строгой изоляцией. Зависимости направлены строго внутрь (к \`:core:domain\`). \`@Module\` (Hilt) лежат внутри самих модулей.  
\* \*\*Внедрение зависимостей:\*\* Dagger Hilt.  
\* \*\*Источник истины (SSOT):\*\* База данных Room. WorkManager обновляет статусы в БД, UI подписан на Flow.

\*\*Технологический стек\*\*  
\* \*\*Базовый слой:\*\* Kotlin, C/C++.  
\* \*\*UI:\*\* Классический XML \+ ViewBinding \+ Material Components.  
\* \*\*Асинхронность:\*\* Kotlin Coroutines (\`Dispatchers.IO\` для записи и транскрибации).  
\* \*\*Управление состояниями:\*\* Kotlin Flow (StateFlow / SharedFlow).  
\* \*\*База данных:\*\* Room (с TypeConverters для Enum и Date).  
\* \*\*Фоновые задачи:\*\* WorkManager \+ Hilt Work (\`androidx.hilt:hilt-work\`).  
\* \*\*Min SDK:\*\* 26 (Android 8.0).

\---

\#\#\# Многомодульная структура (Актуальный статус)

\* \*\*\`:app\`\*\* — Точка входа. Содержит \`@HiltAndroidApp\`, \`MainActivity\`. Здесь будет настроена \`HiltWorkerFactory\` и \`Configuration.Provider\` для WorkManager.  
\* \*\*\`:core:domain\`\*\* — ✅ \*\*ГОТОВО (Базовые контракты)\*\*. Чистый Kotlin. Содержит бизнес-модели (\`RecordSessionModel\`), Enum'ы (\`RecordSessionStatus\`) и интерфейсы контрактов (\`RecordRepository\`, \`TranscriptionScheduler\`).  
\* \*\*\`:core:ml\`\*\* — ✅ \*\*ГОТОВО\*\*. Изоляция C++ (Whisper v1.4.2). JNI-мост. \`WhisperEngine.kt\` обертка.  
\* \*\*\`:core:audio\`\*\* — ✅ \*\*ГОТОВО (ОБНОВЛЕНО)\*\*. \`AudioRecorder\` (уникальное имя файла, возврат пути). \`AudioRecorderService\` (Foreground, паттерн Command Intent, полевой инжект Hilt, сохранение в БД через репозиторий при остановке).  
\* \*\*\`:core:database\`\*\* — ✅ \*\*ГОТОВО (ОБНОВЛЕНО)\*\*. Entity, DAO (без vararg), Converters. \`DatabaseModule\` (Hilt). \`RecordRepositoryImpl\` (маппинг Entity \-\> Domain Model, возврат ID).  
\* \*\*\`:core:work\`\*\* — 🚧 \*\*АКТИВНО (НОВЫЙ МОДУЛЬ)\*\*. Изоляция WorkManager. Содержит \`TranscriptionWorker\` (оркестрация Whisper \+ БД) и \`TranscriptionSchedulerImpl\` (постановка задач в очередь).  
\* \*\*\`:core:network\`\*\* — Не начат.  
\* \*\*\`:feature:recorder\`\*\* — Не начат.  
\* \*\*\`:feature:history\`\*\* — Не начат.

\---

\#\#\# ЭТАПЫ РЕАЛИЗАЦИИ (Выжимка по статусу)

\#\#\#\# Этап 1 и 2: Инфраструктура и ML-Ядро — ✅ ЗАВЕРШЕНЫ  
\* Whisper.cpp v1.4.2 настроен и работает через JNI.

\#\#\#\# Этап 3: Аудио-движок — ✅ ЗАВЕРШЕН (С архитектурными правками)  
\* \*\*Сохранение файла:\*\* \`AudioRecorder\` генерирует уникальный путь \`record\_${timestamp}.pcm\` и сохраняет его в поле класса, возвращая при \`stopRecording()\`.  
\* \*\*Управление Сервисом:\*\* Использован паттерн \*\*Command Intent\*\*. UI отправляет \`ACTION\_START\` и \`ACTION\_STOP\` через \`startForegroundService()\`. Это избавило от блокировки Main-потока (\`runBlocking\`) в \`onDestroy\`.  
\* \*\*Жизненный цикл:\*\* При \`ACTION\_STOP\` сервис останавливает рекордер, в корутине (\`serviceScope\`) сохраняет запись в БД через \`RecordRepository\` и вызывает \`stopSelf()\`.  
\* \*\*DI:\*\* \`AudioRecorderService\` использует полевой инжект (\`@Inject lateinit var\`), так как Андроид-компоненты нельзя инжектить через конструктор.

\#\#\#\# Этап 4: Локальная БД и WorkManager — 🚧 В ПРОЦЕССЕ (Архитектура выстроена)  
\* \*\*Изоляция моделей:\*\* Доменная модель \`RecordSessionModel\` и \`RecordSessionStatus\` перенесены в \`:core:domain\`. Модуль БД зависит от Domain, реализуя интерфейс \`RecordRepository\`.  
\* \*\*DAO:\*\* \`insertRecordSession\` возвращает \`Long\` (ID), \`vararg\` убран.  
\* \*\*Связь Audio и Work:\*\* Модуль \`:core:audio\` не знает про WorkManager. Он вызывает интерфейс \`TranscriptionScheduler\` из \`:core:domain\`.  
\* \*\*Модуль \`:core:work\`:\*\* Создан для оркестрации. \`TranscriptionSchedulerImpl\` ставит \`OneTimeWorkRequest\` в очередь. \`TranscriptionWorker\` (создается через \`@HiltWorker\`) будет доставать путь из БД, вызывать \`WhisperEngine\` и обновлять статус.

\---

\#\#\# ⚠️ СВОДКА ПОДВОДНЫХ КАМНЕЙ (Обновлено)

1\. \*\*Инициализация сервисов:\*\* Запуск/Остановку сервиса нужно делать через \`startForegroundService(intent)\` с передачей \`action\`. Вызов \`stopService\` не подходит, так как сервис может умереть до завершения корутины сохранения в БД. \`stopSelf()\` должен вызывать сам сервис после завершения работы.  
2\. \*\*Hilt в Android-компонентах:\*\* В \`Service\`, \`Activity\`, \`Fragment\` нельзя использовать конструктор с \`@Inject\`. Только \`@AndroidEntryPoint\` и \`@Inject lateinit var\`.  
3\. \*\*Variable Shadowing:\*\* При создании файла в \`AudioRecorder\` важно не создавать локальную переменную \`val recordFile\`, а присваивать значение полю класса \`recordFile \= ...\`, иначе путь потеряется.  
4\. \*\*Room DAO возвращаемые типы:\*\* Использование \`vararg\` в \`@Insert\` заставляет Room возвращать \`LongArray\` (массив ID). Если нужен один \`Long\` (ID), нужно передавать один объект, а не \`vararg\`.  
5\. \*\*Duplicate class \`annotations\`\*\*: Если при билде вылезает ошибка дублирования классов \`org.intellij.lang.annotations...\`, в \`build.gradle.kts\` (модуля \`:app\`) нужно добавить:  
\`\`\`kotlin  
configurations.all { exclude(group \= "com.intellij", module \= "annotations") }  
\`\`\`  
6\. \*\*Manifest Service Registration\*\*: Регистрировать сервис из \`:core:audio\` в \`:app\` нужно по полному пути: \`android:name="pa.chan.audio.AudioRecorderService"\`.  
7\. \*\*Permissions для Foreground Service\*\*: Для Android 14 строго: \`FOREGROUND\_SERVICE\` \+ \`FOREGROUND\_SERVICE\_MICROPHONE\` \+ \`POST\_NOTIFICATIONS\` \+ \`RECORD\_AUDIO\`.

\---

\#\#\# ПЛАН ДЕЙСТВИЙ ДЛЯ СЛЕДУЮЩЕГО ШАГА

1\. \*\*Создать скелет \`:core:work\`:\*\* Настроить \`build.gradle.kts\` (Hilt, WorkManager, hilt-work). Создать пустой \`TranscriptionWorker\` (\`@HiltWorker\`, \`CoroutineWorker\`) и \`TranscriptionSchedulerImpl\`.  
2\. \*\*Настроить HiltWorkerFactory в \`:app\`:\*\* Реализовать \`Configuration.Provider\` в \`Application\` классе, чтобы WorkManager мог получать зависимости через Hilt.  
3\. \*\*Интегрировать Scheduler:\*\* В \`AudioRecorderService\` (в ветке \`ACTION\_STOP\`) инжектнуть \`TranscriptionScheduler\` и вызывать \`scheduleTranscription(id)\` после сохранения в БД.  
4\. \*\*Наполнить Worker:\*\* Реализовать логику в \`TranscriptionWorker.doWork()\`: обновить статус на \`IN\_PROGRESS\`, прочитать аудиофайл, вызвать \`WhisperEngine\`, обновить статус на \`COMPLETED\` (или \`ERROR\`).  
