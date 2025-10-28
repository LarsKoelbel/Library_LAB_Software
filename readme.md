# Library Project for the LAB Software class

This repo contains the complete Library Project for the LAB Software class.

Lars Kölbel - HOST 2025

---

## CLI - Documentation

# 📘 Library Management CLI – Command Reference

Manage your digital media library locally or through a connected database server.

---

## ⚙️ Operating Modes

The system can run in two different operating modes.

| Mode                  | Description                                                                                 |
|:----------------------|:--------------------------------------------------------------------------------------------|
| `detached`, `offline` | All changes are local. Local saving and loading allowed.                                    |
| `online`              | System connected to a server. Certain commands may be constraint <br>by database integrity. |

By default, the system will always start in `detached/ offline` mode. Even if a server is detected, it has to <br> be [connected manually](#-connect--disconnect).

---
## 🛡️ Database Integrity

Database integrity is a rule system for preventing database corruption on the server.<br>
When running the system in online mode, certain functions will be constraint or disabled by it.<br>
In this case, you will see a warning on the CLI, like this:

```
WARNING: The current session is or was connected to a server. 
         Database integrity does not allow to load local data in unclear operational state. 
         To load local data, please officially disconnect from the server using 'disconnect' command
```
The most important constrains include:
- loading from local binary file -> **disabled**
- loading from local bibtex file -> **disabled**
- loading from server when the local library is not empty -> **disabled**
- adding or dropping mediums in [unclear operating conditions](#-unclear-operating-state) -> **disabled**

For security reasons, database integrity ***can not be disabled***

---

## ⚠️ Unclear Operating State
If this message is displayed it means that [database Integrity](#-database-integrity) is uncertain about the [operating mode](#-operating-modes) of the system.
<br>This usually happens if the system was connected to a server, but the connection was lost.

To resolve this, you can officially re-/ disconnect the server.

---

## 🧾 List Mediums

**Command:**
```
list [options]
```

**Description:**  
Lists all mediums currently in the local collection.

**Options:**
| Option | Description |
|:-------|:-------------|
| `-l` | Long listing format (detailed view) |
| `-s` | Short listing format (default) |
| `-b` | Include BibTeX entry for each medium |
| `-d` | Include database representation string |

**Examples:**
```
list -l
list -s -b
list -l -d
```

---

## 🔍 Search Mediums

**Command:**
```
search <selector> [options] <query>
```

**Selectors:**
| Selector | Description |
|:----------|:-------------|
| `title` | Search by title |
| `id` | Search by inventory ID |

**Options:**
| Option | Description |
|:-------|:-------------|
| `-l` | Long listing format |
| `-s` | Short listing format |
| `-b` | Include BibTeX data |
| `-d` | Include database string |
| `-e` | Use exact match instead of substring |
| `-ignore-case` | Ignore case in exact match |

**Examples:**
```
search title -l Python
search title -e -ignore-case "Artificial Intelligence"
search id 42
```

---

## ➕ Add Medium

**Command:**
```
add <BibTeX_entry>
```

**Description:**  
Adds a new medium from a BibTeX formatted entry.

**Example:**
```
add @book{python, title={Learning Python}, author={Mark Lutz}, year={2013}}
```

---

## 💾 Save Collection

**Command:**
```
save binary <path|default>
save bibtex <path|default>
```

**Description:**  
Saves the current collection either as:
- Binary file (for internal use)
- BibTeX file (for export)

**Examples:**
```
save binary my_library.bin
save bibtex my_library.bib
```

---

## 📂 Load Collection

**Command:**
```
load binary <path|default>
load bibtex <path|default>
```

**Description:**  
Loads a collection from a saved file.  
Confirmation required if current library is not empty.

**Examples:**
```
load binary default
load bibtex my_library.bib
```

---

## 🌐 Load From Database Server

**Command:**
```
load database
```

**Description:**  
Downloads all library data from the connected server.  
Not allowed if the local collection is not empty.

---

## 🧹 Clear Local Library

**Command:**
```
clear
```

**Description:**  
Removes all mediums from the collection (after confirmation).  
Unsaved data will be lost.

---

## 🗑️ Drop Medium

**Command:**
```
drop <selector> [options] <value>
```

**Selectors:**
| Selector | Description |
|:----------|:-------------|
| `title` | Drop by title |
| `id` | Drop by inventory ID |

**Options:**
| Option | Description |
|:-------|:-------------|
| `-f` | Force deletion of all matching entries |

**Examples:**
```
drop title "Introduction to AI"
drop id 42
```

---

## 🔗 Connect / Disconnect

Commands:
```
connect database-server [scope]
disconnect database-server
```

**Description:**  
Connects or disconnects from the remote database server.  
When connecting, the CLI will prompt for your **username** and **password**.

You can specify a **server scope** to select which server address is used.

### 🔭 Scopes

| Scope(s) | Description |
|:----------|:-------------|
| `default` | Uses the default configured server. |
| `localhost` | Connects to a server running on this machine. |
| `local`, `local_network` | Both connect to a server within the **local network**. |
| `global`, `global_1` | Both connect to the **first global server**. |
| `global_2` | Connects to the **second global server**. |

### 🧩 Examples
```
connect database-server
connect database-server localhost
connect database-server local
connect database-server local_network
connect database-server global
connect database-server global_1
connect database-server global_2
disconnect database-server
```

### ⏻ Startup
On startup, the program will scan for all servers in the list and display a message <br>
listing all the available servers.

You will see something like this:
```
Library CLI version: 1.04.3-alpha

Checking server availability...
Searching on localhost
WARNING: Server not available

Searching on local network
Server available

Searching on global (internet) 1
WARNING: Server not available

Searching on global (internet) 2
WARNING: Server not available

Searching on default
Server available


Server available on: local network, default - use 'connect' to connect
Running in detached/ offline mode

Type '?' for help and command overview

> 
```
For help on operating modes see [here](#-operating-modes)

---

## ⚙️ Pipelining & Filtering

The CLI supports **command chaining** using the pipe symbol `|`.  
Multiple commands connected with `|` share the same output buffer.

That means the **output of the first command** becomes the **input of the next command**.

**Examples:**
```
list -l | grep Python
list -l | blocks with AI
search title "Machine" | grep author
list -l | blocks with Journal | grep 2020
```

---

## 🔎 Grep

**Command:**
```
grep <text>
```

**Description:**  
Filters the current process output and keeps only the lines  
that contain the specified text (case-insensitive).

**Example:**
```
list -l | grep Python
```

---

## 📦 Blocks With

**Command:**
```
blocks with <text>
```

**Description:**  
Filters complete message blocks that contain a specific text anywhere in the block.  
Useful for filtering grouped output (like detailed book entries) instead of individual lines.

**Examples:**
```
list -l | blocks with AI
search title "Data" | blocks with available
```

---

## ❓ Help

**Commands:**
```
? 
help
```

**Description:**  
Displays this help overview.

---

## 🧰 Summary

| Command | Description |
|:---------|:-------------|
| `list` | List all mediums |
| `search` | Find mediums by title or ID |
| `add` | Add new BibTeX entry |
| `save` | Save collection |
| `load` | Load collection |
| `load database` | Load from connected server |
| `clear` | Clear local library |
| `drop` | Delete a specific medium |
| `connect` / `disconnect` | Manage server connection |
| `grep` | Filter output lines |
| `blocks with` | Filter grouped blocks |
| `?` / `help` | Display command reference |

---

## 💡 Tip

You can combine commands using `|` to chain filters  
and view results dynamically.
