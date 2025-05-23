# ContactsApp

📱 Android-приложение для отображения списка контактов с устройства, совершения звонков и удаления дубликатов.

## 🧩 Функциональность

- Получение и отображение **контактов с устройства**
- Отображение: имя, номер телефона, фото (или инициал)
- Группировка по алфавиту: **русские → латиница → прочее**
- Сортировка контактов **внутри каждой группы по имени**
- **Fast Scroll** с popup-буквами для быстрой навигации
- Цветные **инициал-аватары** для контактов без фото
- Звонок по нажатию на контакт
- Удаление полностью совпадающих контактов (дубликатов)
- Централизованные уведомления (кастомный Toast)
- Обработка ошибок: доступ, сбои, пустой список
- Поддержка светлой и тёмной темы: адаптивные цвета, кнопка и ripple-эффекты

## ⚙️ Архитектура удаления дубликатов


- Реализовано как **отдельный AIDL-сервис**
- Используются `WRITE_CONTACTS` и `RAW_CONTACT_ID`
- Сравниваются: **имя, номер телефона, фото**
- Дубликат = полное совпадение всех доступных данных
- После удаления: **автоматическое обновление UI** и уведомление


## 📂 Используемые технологии

- Java 8
- Android SDK
- RecyclerView, DiffUtil, ViewBinding
- AIDL (межпроцессное взаимодействие)
- ActivityResult API, Runtime Permissions
- Glide, Custom Drawable (инициал-аватары)
- Material Components (MaterialButton, ripple, темы)
- WindowInsets API (прозрачный статус-бар, безопасная зона кнопки)
- JUnit 4, Mockito, Robolectric
- AndroidX Test (JUnit + Espresso, Instrumentation)

## 🛠 Запуск

1. Клонируй репозиторий
2. Открой проект в Android Studio
3. Подключи устройство или эмулятор
4. Собери и запусти

## ✅ Требуемые разрешения

- `READ_CONTACTS` — доступ к контактам
- `CALL_PHONE` — совершение звонков
- `WRITE_CONTACTS` — удаление контактов

## 🧪 Тестирование

### ✅ Unit-тесты (JVM)

Покрывают бизнес-логику:

- Группировка по первой букве имени
- Нормализация буквы "Ё" → "Е"
- Сортировка контактов внутри секции по алфавиту
- Обработка некорректных имён и пустого списка
- Корректный порядок секций: сначала кириллица, потом латиница

Файл: `ContactRepositoryTest.java`

---

### ✅ Android-инструментальный тест

Проверяет работу `DuplicateContactCleaner`:

- Создаёт 2 дубликата и 1 уникальный контакт
- Запускает метод удаления дубликатов
- Проверяет, что удалён **только один дубликат**
- Убедится, что уникальный контакт **сохранился**
- Использует `ContentResolver`, `CountDownLatch`, реальную базу контактов

Файл: `DuplicateContactCleanerAndroidTest.java`

> 📌 Тест выполнен на **физическом устройстве**. Разрешения (`READ_CONTACTS`, `WRITE_CONTACTS`) были выданы вручную через системные настройки перед запуском теста.

### Запуск Android-теста

Запуск на физическом устройстве:

```bash
./gradlew connectedDebugAndroidTest -x uninstallAll -x installDebug
```
## 📸 Скриншоты

###  Тёмная тема
<img src="screenshots/dark_mode.png" width="300"/>

###  Fast Scroll светлая тема
<img src="screenshots/fast_scroll_light.png" width="300"/>

###  Группировка по буквам (и без имени → #)
<img src="screenshots/grouped_contacts.png" width="300"/>

###  Fast Scroll темная тема
<img src="screenshots/fast_scroll_night.png" width="300"/>

###  Удаление дубликатов
<img src="screenshots/toast.png" width="300"/>

###  Главный экран
<img src="screenshots/main_screen.png" width="300"/>


## 🧾 Завершённые задачи

- AIDL-сервис для удаления дубликатов
- Цветные инициал-аватары
- Fast Scroll по алфавиту
- Popup-буквы при прокрутке
- Группировка и сортировка контактов
- Обработка отсутствующих данных
- Централизованные уведомления
- Работа только с локальными контактами устройства
- Поддержка Day/Night темы
- Прозрачный статус-бар с учётом системных insets
- Адаптивный стиль кнопки (фон, обводка, ripple под тему)


## © Автор

Tonny327  
[github.com/Tonny327](https://github.com/Tonny327)

