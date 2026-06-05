# Transit Syndicate - Документация по коду

> Шпора по архитектуре и исходным файлам. Что где лежит, зачем нужно, как устроено.

---

## Содержание

1. [Архитектура](#архитектура)
2. [Точка входа и Application](#точка-входа-и-application)
3. [Core - константы, DI, утилиты](#core--константы-di-утилиты)
4. [Domain - сущности](#domain--сущности)
5. [Domain - репозитории (интерфейсы)](#domain--репозитории-интерфейсы)
6. [Domain - Use Cases (бизнес-логика)](#domain--use-cases-бизнес-логика)
7. [Data - база данных Room](#data--база-данных-room)
8. [Data - репозитории (реализации)](#data--репозитории-реализации)
9. [Presentation - экраны и ViewModel](#presentation--экраны-и-viewmodel)
10. [Карта зависимостей](#карта-зависимостей)

---

## Архитектура

Проект построен по **Clean Architecture** с тремя слоями:

```
presentation/   <- UI, Fragment, ViewModel, Adapter
    |
domain/         <- Entity, Repository (интерфейсы), UseCase
    |
data/           <- Room DB, DAO, Entity (БД), RepositoryImpl
```

**Правило направления зависимостей:** `data` знает о `domain`, `presentation` знает о `domain`. `domain` не знает ни о ком - только чистые Java-классы без Android-зависимостей.

**DI:** ручной контейнер через `AppContainer` (без Hilt/Dagger). Все зависимости создаются в `AppContainer` и пробрасываются через `GameViewModelFactory`.

**Игровой цикл:** `Handler.postDelayed` в `GameViewModel` - каждую 1000 мс вызывается `onTick()`.

---

## Точка входа и Application

---

### [TransitSyndicateApp.java](app/src/main/java/com/transitsyndicate/TransitSyndicateApp.java)

**Пакет:** `com.transitsyndicate`

Наследует `Application`. Два дела при старте приложения:

1. Создаёт `AppContainer` - инициализирует весь граф зависимостей (БД, репозитории, use cases).
2. Конфигурирует **OSMDroid** - библиотека для карт. Устанавливает user agent и директорию кэша тайлов.

```java
public AppContainer appContainer;  // доступен через ((TransitSyndicateApp) getApplication()).appContainer
```

---

### [MainActivity.java](app/src/main/java/com/transitsyndicate/presentation/game/MainActivity.java)

**Пакет:** `presentation.game`

Единственная Activity в приложении. Содержит `BottomNavigationView` и `FragmentContainerView`. Создаёт `GameViewModel` через `GameViewModelFactory` из `AppContainer`. Передаёт ViewModel во все фрагменты.

Навигация между фрагментами - простой `replace` по тапу на нижнем меню:
- Карта -> `MapFragment`
- Заказы -> `OrdersFragment`
- Флот -> `FleetFragment`
- Персонал -> `PersonnelFragment`
- Здания -> `BuildingsFragment`

---

### [SplashActivity.java](app/src/main/java/com/transitsyndicate/presentation/game/SplashActivity.java)

**Пакет:** `presentation.game`

Заставка при запуске. Проверяет первый ли это запуск через `GamePreferences`. При первом запуске инициализирует начальные данные (создаёт игрока, начального сотрудника Alex, пешего курьера, разблокирует Жилой район). Затем стартует `MainActivity`.

---

## Core - константы, DI, утилиты

---

### [GameConstants.java](app/src/main/java/com/transitsyndicate/core/constants/GameConstants.java)

**Пакет:** `core.constants`

**Главный файл баланса игры.** Все числа в одном месте. Трогай только сюда, если нужно изменить цены, скорости, дедлайны.

Константы разделены на блоки:

```java
// Тайминги
GAME_TICK_MS = 1000           // мс между тиками
ORDER_GENERATION_INTERVAL = 15 // тиков между генерацией заказов

// Цены транспорта
PRICE_SCOOTER = 500
PRICE_LARGUS = 5_000
PRICE_GAZEL = 15_000
PRICE_SEMI = 80_000
PRICE_REFRIGERATOR = 35_000
PRICE_TANKER = 40_000

// Слоты транспорта
SLOTS_WALKING = 1, SLOTS_SCOOTER = 2, SLOTS_LARGUS = 10
SLOTS_GAZEL = 5, SLOTS_SEMI = 20, SLOTS_SPECIAL = 15

// Скорости
SPEED_WALKING = 0.5, SPEED_SCOOTER = 1.5, SPEED_CAR = 1.0
SPEED_TRUCK = 0.8, SPEED_SEMI = 0.6

// Расходы топлива за доставку
FUEL_LARGUS = 20, FUEL_GAZEL = 40, FUEL_SEMI = 100, FUEL_SPECIAL = 50

// Зарплаты и найм
SALARY_COURIER = 50, SALARY_DRIVER = 200
SALARY_LOADER = 100, SALARY_DISPATCHER = 300
HIRE_WEEKS = 2  // стоимость найма = зарплата × HIRE_WEEKS

// Здания
GARAGE_BASE_COST = 2_000, GARAGE_BASE_CAPACITY = 3, GARAGE_CAPACITY_PER_LEVEL = 2
SORTING_BASE_COST = 10_000, GAS_BASE_COST = 25_000
FARM_BASE_COST = 50_000, BAKERY_BASE_COST = 40_000

// Награды и дедлайны по районам
REWARD_RESIDENTIAL = 30, DEADLINE_RESIDENTIAL = 600
REWARD_BUSINESS = 150, DEADLINE_BUSINESS = 450
REWARD_INDUSTRIAL = 300, DEADLINE_INDUSTRIAL = 900
REWARD_GLOBAL = 800, DEADLINE_GLOBAL = 1800

// Прогрессия игрока
STARTING_MONEY = 200
XP_MULTIPLIER = 0.1
NEGOTIATION_BONUS_PER_LEVEL = 0.05
```

---

### [AppContainer.java](app/src/main/java/com/transitsyndicate/core/di/AppContainer.java)

**Пакет:** `core.di`

Ручной DI-контейнер. Создаётся один раз в `TransitSyndicateApp`. Инициализирует в правильном порядке:

1. `GameDatabase` (синглтон Room)
2. Все DAO из базы
3. `GamePreferences`
4. Все `RepositoryImpl` (принимают DAO)
5. Все `UseCase` (принимают репозитории)

Поля публичные - `MainActivity` берёт нужные use cases и создаёт `GameViewModelFactory`.

---

### [BaseActivity.java](app/src/main/java/com/transitsyndicate/core/base/BaseActivity.java) / [BaseFragment.java](app/src/main/java/com/transitsyndicate/core/base/BaseFragment.java)

Пустые базовые классы. Наследуются все Activity и Fragment в проекте. Место для общей логики (пока минимальна).

---

### [GameConstants.java](app/src/main/java/com/transitsyndicate/core/constants/GameConstants.java) -> Утилиты

---

### [MoneyFormatter.java](app/src/main/java/com/transitsyndicate/core/utils/MoneyFormatter.java)

Форматирует числа для отображения в UI:
- `format(200)` -> `"200"`
- `format(5000)` -> `"5K"`
- `format(80000)` -> `"80K"`

Используется везде в адаптерах и фрагментах.

---

### [TimeUtils.java](app/src/main/java/com/transitsyndicate/core/utils/TimeUtils.java)

Переводит тики в читаемое время:
- `ticksToString(600)` -> `"10 мин"`
- `ticksToString(1800)` -> `"30 мин"`

Используется в карточках заказов для отображения дедлайна.

---

### [BadgeUtils.java](app/src/main/java/com/transitsyndicate/core/utils/BadgeUtils.java)

Управляет бейджами (красные кружки с числом) на иконках нижней навигации. Показывает количество активных заказов, ожидающих назначения.

---

### [GameNotification.java](app/src/main/java/com/transitsyndicate/core/utils/GameNotification.java)

Toast-обёртка. Показывает короткие уведомления игроку (заказ выполнен, транспорт сломан, уровень повышен).

---

## Domain - сущности

Чистые Java-классы. Никакого Android, никакого Room - только поля, геттеры, методы.

---

### Игрок

#### [Player.java](app/src/main/java/com/transitsyndicate/domain/entity/player/Player.java)

Центральная сущность. Хранит всё состояние игрока:

```java
long id
String name
int money           // текущие монеты
int experience      // накопленный опыт
int level           // вычисляется из experience
int stamina         // запас сил, макс 200
float runSpeed      // множитель скорости пешего, макс 3.0
int negotiationSkill // уровень навыка 0–10
int skillPoints     // нераспределённые очки навыков
```

Методы:
- `canAfford(int cost)` -> boolean
- `spend(int cost)` -> вычитает деньги
- `earn(int amount)` -> добавляет деньги
- `addExperience(int xp)` -> добавляет опыт, проверяет level-up, даёт skillPoints
- `getNegotiationMultiplier()` -> `1.0 + negotiationSkill * 0.05`
- `upgradeStamina()`, `upgradeRunSpeed()`, `upgradeNegotiation()` -> тратят skillPoints

Формула уровня: `level = (int)(1 + Math.sqrt(experience * 0.1))`

---

#### [PlayerSkill.java](app/src/main/java/com/transitsyndicate/domain/entity/player/PlayerSkill.java)

Enum трёх навыков: `STAMINA`, `RUN_SPEED`, `NEGOTIATION`.

---

### Транспорт

#### [Transport.java](app/src/main/java/com/transitsyndicate/domain/entity/transport/Transport.java)

Базовый абстрактный класс для всего транспорта:

```java
long id
String name
TransportType type
TransportState state  // IDLE, LOADING, DELIVERING, REFUELING, BROKEN
int maxSlots
float speedMultiplier
int fuelCostPerDelivery
int fatigue          // 0–100, только для межгорода
List<CargoType> compatibleCargo
```

Подклассы (каждый задаёт свои константы из GameConstants):

| Файл | Класс | Транспорт |
|---|---|---|
| [WalkingCourier.java](app/src/main/java/com/transitsyndicate/domain/entity/transport/WalkingCourier.java) | WalkingCourier | Пеший курьер |
| [Scooter.java](app/src/main/java/com/transitsyndicate/domain/entity/transport/Scooter.java) | Scooter | Электросамокат |
| [Largus.java](app/src/main/java/com/transitsyndicate/domain/entity/transport/Largus.java) | Largus | Лада Ларгус |
| [GazelTruck.java](app/src/main/java/com/transitsyndicate/domain/entity/transport/GazelTruck.java) | GazelTruck | Газель |
| [SemiTrailerTruck.java](app/src/main/java/com/transitsyndicate/domain/entity/transport/SemiTrailerTruck.java) | SemiTrailerTruck | Фура |
| [SpecialVehicle.java](app/src/main/java/com/transitsyndicate/domain/entity/transport/SpecialVehicle.java) | SpecialVehicle | Рефрижератор / Топливозаправщик |

`SpecialVehicle` принимает `SpecialVehicleType` (REFRIGERATOR или FUEL_TANKER) и настраивает совместимые грузы соответственно.

#### [TransportState.java](app/src/main/java/com/transitsyndicate/domain/entity/transport/TransportState.java)

Enum: `IDLE`, `LOADING`, `DELIVERING`, `REFUELING`, `BROKEN`

#### [TransportType.java](app/src/main/java/com/transitsyndicate/domain/entity/transport/TransportType.java)

Enum: `WALKING_COURIER`, `SCOOTER`, `LARGUS`, `GAZEL`, `SEMI_TRAILER`, `REFRIGERATOR`, `FUEL_TANKER`

---

### Персонал

#### [Staff.java](app/src/main/java/com/transitsyndicate/domain/entity/personnel/Staff.java)

Базовый класс сотрудника:

```java
long id
String name
StaffType type
int experienceLevel  // 1–10
float reliability    // 0.0–0.99
int salary           // монет/нед
boolean isAvailable  // false когда в рейсе
```

Метод `completeDelivery()` - увеличивает `reliability` на 0.01 (до 0.99).

Подклассы задают начальные значения salary, reliability, experienceLevel:

| Файл | Класс | Кто |
|---|---|---|
| [NoviceCourier.java](app/src/main/java/com/transitsyndicate/domain/entity/personnel/NoviceCourier.java) | NoviceCourier | Новичок-курьер |
| [ExperiencedDriver.java](app/src/main/java/com/transitsyndicate/domain/entity/personnel/ExperiencedDriver.java) | ExperiencedDriver | Опытный водитель |
| [Loader.java](app/src/main/java/com/transitsyndicate/domain/entity/personnel/Loader.java) | Loader | Грузчик |
| [Dispatcher.java](app/src/main/java/com/transitsyndicate/domain/entity/personnel/Dispatcher.java) | Dispatcher | Диспетчер |

#### [StaffType.java](app/src/main/java/com/transitsyndicate/domain/entity/personnel/StaffType.java)

Enum: `NOVICE_COURIER`, `EXPERIENCED_DRIVER`, `LOADER`, `DISPATCHER`

---

### Здания

#### [Building.java](app/src/main/java/com/transitsyndicate/domain/entity/infrastructure/Building.java)

Базовый класс:

```java
long id
BuildingType type
int level          // текущий уровень (1 = только построено)
int maxLevel
int baseCost
long districtId    // в каком районе стоит
```

Метод `getUpgradeCost()` -> `baseCost * level`

Подклассы:

| Файл | Класс | Здание |
|---|---|---|
| [Garage.java](app/src/main/java/com/transitsyndicate/domain/entity/infrastructure/Garage.java) | Garage | Гараж - метод `getCapacity()` = 3 + (level-1)*2 |
| [SortingCenter.java](app/src/main/java/com/transitsyndicate/domain/entity/infrastructure/SortingCenter.java) | SortingCenter | Сортировочный центр - `getSlots()` = 5 * level |
| [GasStation.java](app/src/main/java/com/transitsyndicate/domain/entity/infrastructure/GasStation.java) | GasStation | Заправка - `getDiscount()` = 0.2 * level |
| [Farm.java](app/src/main/java/com/transitsyndicate/domain/entity/infrastructure/Farm.java) | Farm | Ферма - `getProductionRate()` = level |
| [Bakery.java](app/src/main/java/com/transitsyndicate/domain/entity/infrastructure/Bakery.java) | Bakery | Пекарня - `getBatchSize()` = 3 * level |

#### [BuildingType.java](app/src/main/java/com/transitsyndicate/domain/entity/infrastructure/BuildingType.java)

Enum: `GARAGE`, `SORTING_CENTER`, `GAS_STATION`, `FARM`, `BAKERY`

---

### Карта

#### [District.java](app/src/main/java/com/transitsyndicate/domain/entity/map/District.java)

```java
long id
String name
DistrictType type
boolean isUnlocked
int unlockLevel      // уровень игрока для разблокировки
int trafficLevel     // 1–4, влияет на доступность транспорта
```

#### [DistrictType.java](app/src/main/java/com/transitsyndicate/domain/entity/map/DistrictType.java)

Enum: `RESIDENTIAL`, `BUSINESS`, `INDUSTRIAL`, `GLOBAL`

#### [Route.java](app/src/main/java/com/transitsyndicate/domain/entity/map/Route.java)

Описывает маршрут между двумя районами:

```java
District from
District to
float distanceKm    // используется для расчёта времени доставки
boolean truckOnly   // true = фуры и спецтранспорт, false = все
```

#### [City.java](app/src/main/java/com/transitsyndicate/domain/entity/map/City.java)

Контейнер: список `District` и список `Route`. Используется `MapRepositoryImpl` для инициализации начальных данных карты.

---

### Заказы

#### [Order.java](app/src/main/java/com/transitsyndicate/domain/entity/order/Order.java)

```java
long id
OrderType type
OrderStatus status
CargoType cargoType
int reward          // монет за выполнение
int deadline        // тиков до истечения
int ticksRemaining  // обратный отсчёт (уменьшается каждый тик пока PENDING)
int deliveryTicks   // сколько тиков займёт доставка
Long assignedTransportId
Long assignedStaffId
long districtId
```

#### [OrderStatus.java](app/src/main/java/com/transitsyndicate/domain/entity/order/OrderStatus.java)

Enum: `PENDING`, `ASSIGNED`, `IN_PROGRESS`, `COMPLETED`, `FAILED`

#### [OrderType.java](app/src/main/java/com/transitsyndicate/domain/entity/order/OrderType.java)

Enum: `DELIVERY`, `SUPPLY_CHAIN` (обычная доставка vs звено производственной цепочки)

#### [SupplyChainRoute.java](app/src/main/java/com/transitsyndicate/domain/entity/order/SupplyChainRoute.java) / [SupplyChainStep.java](app/src/main/java/com/transitsyndicate/domain/entity/order/SupplyChainStep.java)

Описывают многоэтапные производственные цепочки. `SupplyChainRoute` содержит список `SupplyChainStep`, каждый из которых - один рейс с конкретным типом груза между двумя точками.

---

### Груз

#### [CargoType.java](app/src/main/java/com/transitsyndicate/domain/entity/cargo/CargoType.java)

Enum: `FOOD`, `DOCUMENTS`, `HEAVY`, `PERISHABLE`, `FUEL`, `GRAIN`, `FLOUR`, `BREAD`

#### [Cargo.java](app/src/main/java/com/transitsyndicate/domain/entity/cargo/Cargo.java)

```java
CargoType type
float weight
boolean requiresRefrigeration  // true только для PERISHABLE
```

---

## Domain - репозитории (интерфейсы)

Каждый репозиторий - Java-интерфейс в `domain.repository`. Только сигнатуры методов, без реализации.

| Файл | Интерфейс | Что умеет |
|---|---|---|
| [PlayerRepository.java](app/src/main/java/com/transitsyndicate/domain/repository/PlayerRepository.java) | PlayerRepository | getPlayer, savePlayer, updateMoney, updateExperience |
| [TransportRepository.java](app/src/main/java/com/transitsyndicate/domain/repository/TransportRepository.java) | TransportRepository | getAllTransport, getById, save, update, delete |
| [StaffRepository.java](app/src/main/java/com/transitsyndicate/domain/repository/StaffRepository.java) | StaffRepository | getAllStaff, getById, save, update, delete |
| [OrderRepository.java](app/src/main/java/com/transitsyndicate/domain/repository/OrderRepository.java) | OrderRepository | getAllOrders, getPending, save, update, complete, fail |
| [BuildingRepository.java](app/src/main/java/com/transitsyndicate/domain/repository/BuildingRepository.java) | BuildingRepository | getAll, getByDistrict, save, upgrade |
| [MapRepository.java](app/src/main/java/com/transitsyndicate/domain/repository/MapRepository.java) | MapRepository | getDistricts, getRoutes, unlockDistrict |

---

## Domain - Use Cases (бизнес-логика)

Каждый Use Case - один класс с одним публичным методом `execute(...)`. Содержит всю игровую логику. Не знает об Android или UI.

---

### Персонал

#### [HireStaffUseCase.java](app/src/main/java/com/transitsyndicate/domain/usecase/personnel/HireStaffUseCase.java)

`execute(StaffType type, Player player)`
- Проверяет `player.canAfford(salary * HIRE_WEEKS)`
- Вычитает деньги через `player.spend()`
- Создаёт нужный подкласс Staff
- Сохраняет через `StaffRepository`

#### [FireStaffUseCase.java](app/src/main/java/com/transitsyndicate/domain/usecase/personnel/FireStaffUseCase.java)

`execute(long staffId)`
- Проверяет что сотрудник не в рейсе (`isAvailable`)
- Удаляет из `StaffRepository`
- Аванс не возвращается

#### [AutoDispatchUseCase.java](app/src/main/java/com/transitsyndicate/domain/usecase/personnel/AutoDispatchUseCase.java)

`execute(List<Order> pendingOrders, List<Transport> idleTransport, List<Staff> availableStaff)`
- Вызывается каждый тик если есть хотя бы один Dispatcher
- Для каждого PENDING заказа ищет совместимый IDLE транспорт
- Назначает доступного водителя
- Возвращает список назначенных пар (заказ, транспорт, водитель)
- `GameViewModel` применяет эти назначения

---

### Транспорт

#### [PurchaseTransportUseCase.java](app/src/main/java/com/transitsyndicate/domain/usecase/transport/PurchaseTransportUseCase.java)

`execute(TransportType type, Player player, Garage garage)`
- Проверяет деньги и свободный слот в гараже
- Создаёт экземпляр нужного транспорта
- Сохраняет, обновляет игрока

#### [AssignTransportUseCase.java](app/src/main/java/com/transitsyndicate/domain/usecase/transport/AssignTransportUseCase.java)

`execute(Order order, Transport transport, Staff driver)`
- Проверяет совместимость груза и транспорта
- Проверяет доступность сотрудника
- Переводит заказ в ASSIGNED, транспорт в LOADING
- Помечает сотрудника как недоступного

#### [RepairTransportUseCase.java](app/src/main/java/com/transitsyndicate/domain/usecase/transport/RepairTransportUseCase.java)

`execute(Transport transport, Player player, GasStation gasStation)`
- Возможен только если есть Gas Station в районе
- Стоимость ремонта зависит от уровня GasStation
- Переводит транспорт из BROKEN в IDLE

---

### Заказы

#### [GenerateOrderUseCase.java](app/src/main/java/com/transitsyndicate/domain/usecase/order/GenerateOrderUseCase.java)

`execute(List<District> unlockedDistricts)`
- Вызывается каждые 15 тиков в GameViewModel
- Для каждого разблокированного района создаёт Order
- Тип груза берётся из типа района
- Награда и дедлайн из GameConstants
- Сохраняет в OrderRepository

#### [AcceptOrderUseCase.java](app/src/main/java/com/transitsyndicate/domain/usecase/order/AcceptOrderUseCase.java)

`execute(Order order, Transport transport, Staff staff, Player player)`
- Обёртка над AssignTransportUseCase
- Применяет NavigationMultiplier к награде
- Рассчитывает deliveryTicks по расстоянию и скорости транспорта

#### [CompleteOrderUseCase.java](app/src/main/java/com/transitsyndicate/domain/usecase/order/CompleteOrderUseCase.java)

`execute(Order order, Player player, Staff driver)`
- Переводит заказ в COMPLETED
- Начисляет монеты через `player.earn(order.reward)`
- Начисляет XP через `player.addExperience(order.reward * XP_MULTIPLIER)`
- Освобождает транспорт (IDLE) и водителя (isAvailable = true)
- Вызывает `driver.completeDelivery()` для роста надёжности

#### [CreateSupplyChainUseCase.java](app/src/main/java/com/transitsyndicate/domain/usecase/order/CreateSupplyChainUseCase.java)

`execute(SupplyChainRoute route, Player player)`
- Разбивает цепочку на отдельные Order для каждого шага
- Создаёт заказы типа SUPPLY_CHAIN
- Их затем обрабатывает стандартный AssignTransport

---

### Здания

#### [ConstructBuildingUseCase.java](app/src/main/java/com/transitsyndicate/domain/usecase/infrastructure/ConstructBuildingUseCase.java)

`execute(BuildingType type, District district, Player player)`
- Проверяет деньги
- Проверяет что такого здания ещё нет в этом районе
- Создаёт Building уровня 1, сохраняет

#### [UpgradeBuildingUseCase.java](app/src/main/java/com/transitsyndicate/domain/usecase/infrastructure/UpgradeBuildingUseCase.java)

`execute(Building building, Player player)`
- Проверяет `building.level < building.maxLevel`
- Проверяет деньги: `building.getUpgradeCost()`
- Инкрементирует уровень, сохраняет

---

### Карта

#### [UnlockDistrictUseCase.java](app/src/main/java/com/transitsyndicate/domain/usecase/map/UnlockDistrictUseCase.java)

`execute(Player player, List<District> districts)`
- Проходит по всем районам
- Если `player.level >= district.unlockLevel` и район не разблокирован -> разблокирует
- Вызывается в GameViewModel при каждом level-up

---

### Игрок

#### [GetPlayerUseCase.java](app/src/main/java/com/transitsyndicate/domain/usecase/player/GetPlayerUseCase.java)

`execute()` -> `Player`  
Просто достаёт игрока из PlayerRepository.

#### [UpgradePlayerSkillUseCase.java](app/src/main/java/com/transitsyndicate/domain/usecase/player/UpgradePlayerSkillUseCase.java)

`execute(PlayerSkill skill, Player player)`
- Проверяет `player.skillPoints > 0`
- Вызывает нужный метод upgrade на Player
- Сохраняет обновлённого игрока

---

## Data - база данных Room

---

### [GameDatabase.java](app/src/main/java/com/transitsyndicate/data/local/database/GameDatabase.java)

**Пакет:** `data.local.database`

Room-база данных. Синглтон с double-checked locking.

```java
@Database(entities = {
    PlayerEntity.class, TransportEntity.class, OrderEntity.class,
    StaffEntity.class, BuildingEntity.class, DistrictEntity.class
}, version = 1)
```

Файл базы: `transit_syndicate.db`

Абстрактные геттеры DAO:
- `playerDao()`, `transportDao()`, `orderDao()`
- `staffDao()`, `buildingDao()`, `districtDao()`

---

### Entity (таблицы Room)

Каждый `*Entity` - `@Entity` класс с `@PrimaryKey`. Хранит плоские данные (enum'ы сериализуются через `@TypeConverter`).

| Файл | Таблица | Хранит |
|---|---|---|
| [PlayerEntity.java](app/src/main/java/com/transitsyndicate/data/local/database/entity/PlayerEntity.java) | `player` | money, experience, level, stamina, runSpeed, negotiationSkill, skillPoints |
| [TransportEntity.java](app/src/main/java/com/transitsyndicate/data/local/database/entity/TransportEntity.java) | `transport` | type, state, fatigue, assignedOrderId |
| [StaffEntity.java](app/src/main/java/com/transitsyndicate/data/local/database/entity/StaffEntity.java) | `staff` | type, experienceLevel, reliability, salary, isAvailable |
| [OrderEntity.java](app/src/main/java/com/transitsyndicate/data/local/database/entity/OrderEntity.java) | `orders` | type, status, cargoType, reward, deadline, ticksRemaining, districtId |
| [BuildingEntity.java](app/src/main/java/com/transitsyndicate/data/local/database/entity/BuildingEntity.java) | `building` | type, level, districtId |
| [DistrictEntity.java](app/src/main/java/com/transitsyndicate/data/local/database/entity/DistrictEntity.java) | `district` | type, isUnlocked, unlockLevel, trafficLevel |

---

### DAO

Каждый DAO - `@Dao` интерфейс с `@Query`/`@Insert`/`@Update`/`@Delete`.

| Файл | DAO | Ключевые запросы |
|---|---|---|
| [PlayerDao.java](app/src/main/java/com/transitsyndicate/data/local/database/dao/PlayerDao.java) | PlayerDao | getPlayer, insert, update |
| [TransportDao.java](app/src/main/java/com/transitsyndicate/data/local/database/dao/TransportDao.java) | TransportDao | getAll, getByState, getById, insert, update, delete |
| [StaffDao.java](app/src/main/java/com/transitsyndicate/data/local/database/dao/StaffDao.java) | StaffDao | getAll, getAvailable, getByType, insert, update, delete |
| [OrderDao.java](app/src/main/java/com/transitsyndicate/data/local/database/dao/OrderDao.java) | OrderDao | getAll, getByStatus, getByDistrict, insert, update |
| [BuildingDao.java](app/src/main/java/com/transitsyndicate/data/local/database/dao/BuildingDao.java) | BuildingDao | getAll, getByDistrict, getByType, insert, update |
| [DistrictDao.java](app/src/main/java/com/transitsyndicate/data/local/database/dao/DistrictDao.java) | DistrictDao | getAll, getUnlocked, insert, update |

---

### [GamePreferences.java](app/src/main/java/com/transitsyndicate/data/local/preferences/GamePreferences.java)

SharedPreferences-обёртка. Хранит:
- `isFirstLaunch` - флаг первого запуска (для инициализации данных)
- `currentTick` - текущий тик (для корректного резюма после паузы)
- `isAutoDispatchEnabled` - включена ли авто-диспетчеризация

---

## Data - репозитории (реализации)

Каждый `*RepositoryImpl` реализует интерфейс из `domain.repository`. Конвертирует между `*Entity` (Room) и доменными объектами (`Player`, `Transport` и т.д.).

| Файл | Реализует | Маппинг |
|---|---|---|
| [PlayerRepositoryImpl.java](app/src/main/java/com/transitsyndicate/data/repository/PlayerRepositoryImpl.java) | PlayerRepository | PlayerEntity ↔ Player |
| [TransportRepositoryImpl.java](app/src/main/java/com/transitsyndicate/data/repository/TransportRepositoryImpl.java) | TransportRepository | TransportEntity ↔ Transport (фабричный метод по TransportType) |
| [StaffRepositoryImpl.java](app/src/main/java/com/transitsyndicate/data/repository/StaffRepositoryImpl.java) | StaffRepository | StaffEntity ↔ Staff (фабричный метод по StaffType) |
| [OrderRepositoryImpl.java](app/src/main/java/com/transitsyndicate/data/repository/OrderRepositoryImpl.java) | OrderRepository | OrderEntity ↔ Order |
| [BuildingRepositoryImpl.java](app/src/main/java/com/transitsyndicate/data/repository/BuildingRepositoryImpl.java) | BuildingRepository | BuildingEntity ↔ Building (фабричный метод по BuildingType) |
| [MapRepositoryImpl.java](app/src/main/java/com/transitsyndicate/data/repository/MapRepositoryImpl.java) | MapRepository | DistrictEntity ↔ District, жёстко зашитые Route в коде |

`MapRepositoryImpl` особенный: маршруты между районами не хранятся в БД, они прописаны прямо в коде (расстояния 3, 4, 5, 50 км и флаги truckOnly).

---

## Presentation - экраны и ViewModel

---

### [GameViewModel.java](app/src/main/java/com/transitsyndicate/presentation/game/GameViewModel.java)

**Главный класс всей игры.** Оркестрирует игровой цикл и связывает UI с бизнес-логикой.

**LiveData (наблюдают фрагменты):**
```java
MutableLiveData<Player> playerLiveData
MutableLiveData<List<Order>> ordersLiveData
MutableLiveData<List<Transport>> transportLiveData
MutableLiveData<List<Staff>> staffLiveData
MutableLiveData<List<Building>> buildingsLiveData
MutableLiveData<Integer> currentTickLiveData
MutableLiveData<String> toastMessage        // уведомления игроку
MutableLiveData<Boolean> autoDispatchEnabled
MutableLiveData<Map<Long, Integer>> deliveryProgress  // orderId -> оставшиеся тики
```

**Игровой цикл:**
```java
startGame()  -> запускает Handler.postDelayed(gameLoop, GAME_TICK_MS)
stopGame()   -> отменяет handler, сохраняет тик в Preferences
onTick()     -> вызывается каждую секунду:
    1. tick++
    2. Уменьшает ticksRemaining у PENDING заказов -> FAILED если 0
    3. Уменьшает deliveryProgress у IN_PROGRESS заказов -> CompleteOrderUseCase если 0
    4. Каждые 15 тиков: GenerateOrderUseCase
    5. Если есть Dispatcher: AutoDispatchUseCase
    6. Обновляет все LiveData
```

**Публичные методы (вызываются из фрагментов):**
- `hireStaff(StaffType)` - нанять сотрудника
- `fireStaff(long staffId)` - уволить
- `purchaseTransport(TransportType)` - купить транспорт
- `assignOrder(long orderId, long transportId, long staffId)` - назначить вручную
- `constructBuilding(BuildingType, long districtId)` - построить здание
- `upgradeBuilding(long buildingId)` - улучшить здание
- `upgradeSkill(PlayerSkill)` - прокачать навык
- `repairTransport(long transportId)` - починить транспорт

---

### [GameViewModelFactory.java](app/src/main/java/com/transitsyndicate/presentation/game/GameViewModelFactory.java)

`ViewModelProvider.Factory`. Принимает все use cases из `AppContainer`, создаёт `GameViewModel`. Нужен потому что `GameViewModel` не имеет конструктора без аргументов.

---

### Фрагменты и адаптеры

| Файл | Экран | Что показывает |
|---|---|---|
| [OrdersFragment.java](app/src/main/java/com/transitsyndicate/presentation/orders/OrdersFragment.java) | Заказы | Список заказов, кнопки назначения транспорта вручную |
| [OrderAdapter.java](app/src/main/java/com/transitsyndicate/presentation/orders/OrderAdapter.java) | - | RecyclerView карточки заказов: тип, груз, награда, дедлайн, статус |
| [FleetFragment.java](app/src/main/java/com/transitsyndicate/presentation/fleet/FleetFragment.java) | Флот | Список транспорта, кнопки покупки, статус каждой единицы |
| [TransportAdapter.java](app/src/main/java/com/transitsyndicate/presentation/fleet/TransportAdapter.java) | - | Карточки транспорта: имя, статус, слоты, топливо |
| [PersonnelFragment.java](app/src/main/java/com/transitsyndicate/presentation/personnel/PersonnelFragment.java) | Персонал | Список нанятых, кнопки найма, надёжность, зарплата |
| [StaffAdapter.java](app/src/main/java/com/transitsyndicate/presentation/personnel/StaffAdapter.java) | - | Карточки сотрудников |
| [BuildingsFragment.java](app/src/main/java/com/transitsyndicate/presentation/buildings/BuildingsFragment.java) | Здания | Список построенных зданий, кнопки постройки и улучшения |
| [BuildingAdapter.java](app/src/main/java/com/transitsyndicate/presentation/buildings/BuildingAdapter.java) | - | Карточки зданий: тип, уровень, стоимость улучшения |
| [MapFragment.java](app/src/main/java/com/transitsyndicate/presentation/map/MapFragment.java) | Карта | Карта города (OSMDroid), районы, активные доставки |

---

### Карта (Map)

#### [GameView.java](app/src/main/java/com/transitsyndicate/presentation/game/GameView.java)

Custom View для визуализации игрового состояния прямо на canvas. Рисует иконки транспорта на карте, анимирует движение между районами.

#### [DistrictOverlay.java](app/src/main/java/com/transitsyndicate/presentation/map/DistrictOverlay.java)

OSMDroid Overlay. Рисует границы и названия районов поверх тайлов карты. При тапе на район показывает информацию (разблокирован/нет, уровень, активные заказы).

#### [DeliveryOverlay.java](app/src/main/java/com/transitsyndicate/presentation/map/DeliveryOverlay.java)

OSMDroid Overlay. Рисует движущиеся маркеры транспорта по маршруту доставки. Позиция интерполируется на основе `deliveryProgress` из GameViewModel.

---

## Карта зависимостей

```
TransitSyndicateApp
    └── AppContainer
            ├── GameDatabase (Room)
            │       ├── PlayerDao
            │       ├── TransportDao
            │       ├── StaffDao
            │       ├── OrderDao
            │       ├── BuildingDao
            │       └── DistrictDao
            │
            ├── GamePreferences
            │
            ├── *RepositoryImpl (используют DAO)
            │
            └── *UseCase (используют Repository)

MainActivity
    └── GameViewModelFactory (AppContainer's use cases)
            └── GameViewModel
                    ├── ordersLiveData -> OrdersFragment -> OrderAdapter
                    ├── transportLiveData -> FleetFragment -> TransportAdapter
                    ├── staffLiveData -> PersonnelFragment -> StaffAdapter
                    ├── buildingsLiveData -> BuildingsFragment -> BuildingAdapter
                    └── deliveryProgress -> MapFragment -> DeliveryOverlay
```

---

