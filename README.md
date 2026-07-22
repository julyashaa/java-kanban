# 📋 Java Kanban

<p align="center">

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![OOP](https://img.shields.io/badge/OOP-Object_Oriented_Programming-blue?style=for-the-badge)
![JUnit 5](https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![CSV](https://img.shields.io/badge/CSV-File_Storage-4CAF50?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

</p>

---

# 📖 О проекте

**Java Kanban** — консольное приложение для управления задачами.

Проект реализует систему хранения обычных задач, эпиков и подзадач с поддержкой истории просмотров и сохранением данных в файл.

Во время разработки основной акцент был сделан на проектировании архитектуры, применении принципов ООП и работе с коллекциями Java.

---

# ✨ Возможности

- ✅ создание задач
- 📚 создание эпиков
- ✔️ создание подзадач
- ✏️ редактирование задач
- 🗑 удаление задач
- 📋 получение списка задач
- 📜 история просмотров без дубликатов
- 💾 сохранение данных в CSV
- 📂 загрузка данных из файла
- 🔄 автоматическое обновление статуса эпиков

---

# 🏗 Архитектура

```text
Task
 ├── Epic
 └── Subtask

          │
          ▼

TaskManager
      │
      ▼
InMemoryTaskManager
      │
      ▼
FileBackedTaskManager

HistoryManager
      │
      ▼
InMemoryHistoryManager
```

---

# 🛠 Используемые технологии

### Язык

- Java

### Основные концепции

- ООП
- Наследование
- Полиморфизм
- Инкапсуляция
- Интерфейсы
- Коллекции Java

### Работа с данными

- CSV
- File I/O

### Тестирование

- JUnit 5

---

# 📂 Структура проекта

```text
src
├── app
├── comparator
├── exceptions
├── manage
└── tasks
```

---

# 📌 Основные классы

| Класс | Назначение |
|--------|------------|
| Task | Базовая задача |
| Epic | Эпик |
| Subtask | Подзадача |
| InMemoryTaskManager | Управление задачами в памяти |
| FileBackedTaskManager | Хранение задач в CSV |
| InMemoryHistoryManager | История просмотров |

---

# 💾 Хранение данных

Проект поддерживает:

- сохранение задач в CSV-файл;
- восстановление состояния после перезапуска;
- сохранение истории просмотра задач.

---

# 🧪 Тестирование

Проект покрыт тестами на JUnit 5.

Проверяются:

- создание задач;
- работа эпиков;
- работа подзадач;
- сохранение и загрузка данных;
- история просмотров;
- менеджеры задач.

---

# 📚 Что демонстрирует проект

- применение принципов ООП;
- проектирование архитектуры приложения;
- работу с коллекциями Java;
- использование собственных менеджеров;
- файловое хранение данных;
- сериализацию в CSV;
- написание модульных тестов.

---

# 👩‍💻 Автор

**Юлия Родионова**

GitHub: https://github.com/julyashaa
