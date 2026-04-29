# 🛠️ EssentialTools

A Minecraft Paper plugin to manage coordinates, locations, and get directional assistance in-game.

---

## ✨ Features

| Feature                   | Description                            |
| ------------------------- | -------------------------------------- |
| 📍 **Save Locations**     | Save coordinates with custom names     |
| 🔍 **Retrieve Locations** | View saved coordinates instantly       |
| 📋 **List All**           | See all saved locations at once        |
| 🗑️ **Delete Locations**   | Remove saved locations                 |
| 🧭 **Player Tracking**    | Show direction to reach online players |
| 💾 **Auto-Save**          | Locations saved automatically in SQLite  |
| 🎨 **Colored UI**         | Beautiful colored messages in chat     |

---

## 📥 Installation

1. Download the latest `.jar` from the [Releases](../../releases)
2. Place the file in your server's `plugins/` folder
3. Restart the server
4. Done!

---

## 📖 Commands

### Coordinate Management

| Command             | Description              |
| ------------------- | ------------------------ |
| `/coord set <name>` | Save current position    |
| `/coord get <name>` | View saved position      |
| `/coord del <name>` | Delete saved position    |
| `/coord all`        | List all saved positions |

> Note: Location names are case-insensitive (`home` = `Home` = `HOME`)

### Utilities

| Command          | Description               |
| ---------------- | ------------------------- |
| `/coord`         | Toggle coordinate tooltip |
| `/coord tooltip` | Toggle coordinate tooltip |
| `/ping <player>` | Show direction to player  |

---

## 💡 Examples

```bash
/coord set home        # Save current position as "home"
/coord set base        # Save current position as "base"
/coord get home        # View coordinates of "home"
/coord del home        # Delete "home" location
/coord all             # Show all saved locations
/coord                 # Toggle coordinate tooltip
/ping Steve            # Show direction to Steve
```

---

## 📋 Permissions

| Permission             | Description               |
| ---------------------- | ------------------------- |
| `essentialtools.coord` | Access all coord commands |
| `essentialtools.ping`  | Access ping command       |

---

## 🔧 Configuration

Locations are stored in a SQLite database at `plugins/EssentialTools/coordinates.db`.  
The database is created automatically on first startup.

### Database Schema

```sql
CREATE TABLE IF NOT EXISTS coordinates (
    name TEXT PRIMARY KEY,
    world TEXT NOT NULL,
    uuid TEXT NOT NULL,
    x REAL NOT NULL,
    y REAL NOT NULL,
    z REAL NOT NULL
)
```

