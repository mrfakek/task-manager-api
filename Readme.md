# task-manager-api

Этот проект — Task manager api, сервис созданный с использованием Spring Boot и базы данных PostgreSQL.
Сервис для управления задачами, позволяющий создавать, обновлять, удалять и отслеживать статус задач. 
Этот API предоставляет набор эндпоинтов для работы с задачами и их комментариями, а также поддерживает функционал для аутентификации и авторизации пользователей.

## 🚀 Локальный запуск с Docker Compose
### 💾**1. Склонируйте репозиторий на локальную машину.**
Для начала, клонируйте репозиторий на локальную машину с помощью команды:

```sh
git clone <URL-репозитория>
```
### 📦**2. Установите зависимости**
Перед запуском убедитесь, что у вас установлены:
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

Проверьте, что Docker запущен, командой:
```sh
docker --version
```
### 🌱**3. Запустите проект**
   Выполните команду:

```sh
docker-compose up --build
```
Это поднимет все необходимые сервисы:

✅ Backend (Spring Boot)

✅ Database (PostgreSQL)

✅ Документация API (Swagger UI)

⚠️ **Примечание:** Первый запуск может занять время, так как образы будут загружены и собраны.

### 🔍 **4. Проверка работы**

- Swagger UI (Документация API): http://localhost:8080/swagger-ui.html

- API сервер: http://localhost:8080


- **База данных:**
- Адрес: `localhost:5432`
- Пользователь: `postgres`
- Пароль: `root`
- База данных: `postgres`

### 🛑**5. Остановка**
Для остановки контейнеров используйте:

```sh
docker-compose down
```
### **🛠️ Состав проекта**
- Java 17
- Spring Boot 3.4.0
- Spring Security
- JWT
- PostgreSQL
- H2 (для тестов)
- Spring Data JPA
- Springdoc-openapi
- MapStruct
- Lombok
- JUnit 5
- Spring Security

### **🔧 Полезные команды**
  Просмотр запущенных контейнеров:

```sh
docker ps
```
Логи контейнера:

```sh
docker logs <container_id>
```
Остановка всех контейнеров:

```sh
docker-compose down
```

