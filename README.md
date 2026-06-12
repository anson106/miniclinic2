# MiniClinic 社區診所掛號系統

一個以 Spring Boot 實作的社區診所掛號系統，支援醫師登入、病患掛號、
掛號狀態管理等功能。

## 線上 Demo

https://miniclinic-anson106-2nd.onrender.com


## 技術棧

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- Thymeleaf
- SQLite（開發）/ PostgreSQL（部署）
- BCrypt（密碼雜湊）

## 功能清單

- 醫師登入 / 登出
- 醫師個人 Dashboard
- 病患資料管理（CRUD）
- 線上掛號功能
- 掛號狀態變更（booked / completed / cancelled）
- RESTful API（支援第三方整合）

## 本機執行

```bash
git clone https://github.com/anson106/miniclinic2.git
cd miniclinic
./mvnw spring-boot:run
```

開啟瀏覽器訪問 http://localhost:8080

預設醫師帳密：

- D001 / pass1234
- D002 / pass1234
- （其他醫師密碼均為 pass1234）

## 資料初始化

第一次啟動時，`data.sql` 會自動插入：
- 5 位虛構醫師
- 3 位虛構病患（TEST00001, TEST00002, TEST00003）
- 3 筆示範掛號

## 專案結構

```
src/
├── main/
│   ├── java/tw/edu/fju/miniclinic/
│   │   ├── controller/     # HTTP 請求處理
│   │   ├── model/          # Entity 與 Repository
│   │   ├── interceptor/    # 登入驗證
│   │   └── config/         # Spring 配置
│   └── resources/
│       ├── templates/      # Thymeleaf 模板
│       ├── static/         # CSS、JS
│       └── application.properties
```

## 作者

2026 年 Java 程式設計課程作業

## 🌟 近期更新功能 (Recent Updates)

### 1. 醫師看診完成功能 (Dashboard)
- **功能描述**：在醫師的 Dashboard「今日掛號」清單中，為狀態為 `BOOKED` 的掛號新增「看診完成」按鈕。
- **實作細節**：
  - 點擊後觸發防呆確認視窗，避免醫師誤觸。
  - 透過 JavaScript `fetch` 非同步呼叫 `PUT /api/appointments/{id}/status`，將狀態更新為 `COMPLETED`。
  - 實作 CSRF Token 動態擷取，確保在 Spring Security 防護下能正常發送請求。
  - 狀態更新成功後自動重新整理頁面以反映最新狀態。

### 2. 系統營運統計 API (`/api/stats`)
- **功能描述**：新增免登入的開放端點，供外部驗收工具（如 AI Agent）進行系統狀態查核。
- **實作細節**：
  - 端點路徑：`GET /api/stats`
  - 整合 Spring Data JPA 的 `count()` 與自訂的 `countByStatus` 方法。
  - 回傳系統資料摘要，包含總醫師數、總病患數、總掛號數，以及依狀態 (`BOOKED`, `COMPLETED`, `CANCELLED`) 分類的掛號統計。