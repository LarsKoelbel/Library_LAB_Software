# Library Project for the LAB Software class

This repo contains the complete Library Project for the LAB Software class.

Lars Kölbel - HOST 2025

## CLI - Documentation (AI-generates sorry...)

# 📘 Library Management CLI – Command Reference

Manage your digital media library locally or through a connected database server.

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

**Commands:**
```
connect database-server
disconnect database-server
```

**Description:**  
Connects or disconnects from the remote database server.  
Prompts for username and password when connecting.

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
