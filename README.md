 Java Code Health & Risk Analyzer

> A lightweight static-analysis tool built from scratch in Core Java to identify maintainability risks, dependencies, and potential change impact in Java codebases.**

CodeGuard analyzes a Java project directly from its source code and generates a developer-friendly report highlighting areas that may require attention.

Unlike simple code metrics tools, CodeGuard focuses on **relationships and risks inside the codebase** — such as complex methods, highly coupled classes, circular dependencies, duplicated code, and potentially affected classes after a change.

---

## 🎯 Why CodeGuard?

As a codebase grows, understanding its internal structure becomes harder.

A developer changing one class may not immediately know:

* Which classes depend on it?
* Could the change affect other components?
* Are there circular dependencies?
* Which methods are becoming too complex?
* Where is similar code being repeated?
* Which parts of the codebase are becoming difficult to maintain?

**CodeGuard is built to answer these questions automatically.**

---

## ✨ Features

### 🔍 Java Project Scanning

Recursively scans a Java project and discovers source files using Java NIO.

```text
Project
 ├── src/
 │   ├── User.java
 │   ├── Order.java
 │   └── service/
 │       └── OrderService.java
 └── ...
```

---

### 🧩 Source Code Analysis

CodeGuard processes Java source code using a custom tokenizer/basic parser to extract useful structural information such as:

* Classes
* Interfaces
* Enums
* Methods
* Imports
* Fields
* Class dependencies

No external parsing library is required for the core analysis.

---

### 📊 Complexity Analysis

Identifies methods containing large amounts of control flow.

The analysis considers constructs such as:

```text
if
else if
for
while
switch
catch
ternary
```

Example:

```text
HIGH COMPLEXITY

OrderService.java
└── processOrder()
    Complexity: 18
    Risk: HIGH
```

---

### 🔗 Coupling Analysis

Analyzes relationships between classes to identify highly dependent components.

Example:

```text
PaymentService
 ├── OrderService
 ├── UserService
 ├── InventoryService
 └── EmailService
```

High coupling can make a class harder to modify and maintain.

---

### 🔄 Circular Dependency Detection

Builds a directed dependency graph and detects dependency cycles.

Example:

```text
OrderService
      ↓
PaymentService
      ↓
NotificationService
      ↓
OrderService
```

CodeGuard reports the cycle instead of simply listing dependencies.

---

### 📋 Duplicate Code Detection

Identifies potentially duplicated or highly similar code blocks using normalized source/token representations.

Example:

```text
Potential Duplicate

UserService.java       line 120
AdminService.java      line 87

Similarity: 86%
```

---

### 💥 Change Impact Analysis

One of the key features of CodeGuard.

Given a changed class, CodeGuard traverses the dependency graph to identify classes that may be affected.

Example:

```text
Changed:
PaymentService.java

Potential Impact:
 ├── OrderService
 ├── OrderController
 ├── InvoiceService
 └── NotificationService
```

This helps developers understand the possible blast radius of a change.

---

# 🏗️ Architecture

CodeGuard follows a modular analysis pipeline:

```text
                  Java Project
                       │
                       ▼
               ┌───────────────┐
               │ ProjectScanner│
               └───────┬───────┘
                       │
                       ▼
               ┌───────────────┐
               │ Source Reader │
               └───────┬───────┘
                       │
                       ▼
               ┌───────────────┐
               │   Tokenizer   │
               │ /Basic Parser │
               └───────┬───────┘
                       │
                       ▼
              ┌──────────────────┐
              │    Code Model    │
              │                  │
              │ JavaClass        │
              │ JavaMethod       │
              │ JavaField        │
              │ Dependency       │
              └────────┬─────────┘
                       │
                       ▼
              ┌──────────────────┐
              │ Analysis Engine  │
              │                  │
              │ Complexity       │
              │ Coupling         │
              │ Duplication      │
              │ Dependency       │
              └────────┬─────────┘
                       │
                       ▼
              ┌──────────────────┐
              │ Dependency Graph │
              └────────┬─────────┘
                       │
                       ▼
              ┌──────────────────┐
              │ Impact / Risk    │
              │ Analysis         │
              └────────┬─────────┘
                       │
                       ▼
              ┌──────────────────┐
              │ Report Generator │
              └────────┬─────────┘
                       │
                       ▼
                  CLI Report
```

### Design Principle

Each component has a clear responsibility.

```text
Scanner   → Finds source files
Tokenizer → Breaks source into tokens
Parser    → Extracts structure
Model     → Represents the codebase
Analyzer  → Detects risks
Graph     → Represents dependencies
Reporter  → Presents findings
```

This keeps analysis logic separate from file handling and reporting.

---

# 🧠 Core Java Concepts Used

CodeGuard is intentionally built using Core Java to understand how developer tooling can be implemented without relying on large frameworks.

Key concepts include:

* Object-Oriented Programming
* Interfaces and Abstraction
* Collections Framework
* Generics
* Lambda Expressions
* Stream API
* Comparator
* Exception Handling
* Java NIO
* File I/O
* Regular Expressions
* Graph Algorithms
* CLI application design

---

# 🛠️ Tech Stack

| Technology                | Purpose                         |
| ------------------------- | ------------------------------- |
| **Java 21+**              | Application development         |
| **Java NIO**              | File and directory scanning     |
| **Collections Framework** | Code model and analysis data    |
| **Graph Algorithms**      | Dependency and cycle analysis   |
| **CLI**                   | User interaction                |
| **File I/O**              | Source processing and reporting |

### No Framework Dependency

CodeGuard's core analysis engine does **not depend on Spring Boot, databases, or external static-analysis libraries.**

The objective is to implement the important analysis logic using the Java Standard Library.

---

# 📂 Project Structure

```text
CodeGuard/
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── codeguard/
│
│                   ├── cli/
│                   ├── scanner/
│                   ├── parser/
│                   ├── model/
│                   ├── analyzer/
│                   ├── graph/
│                   ├── report/
│                   └── Main.java
│
├── test-project/
│
├── README.md
└── pom.xml
```

The exact package structure may evolve as the project develops.

---

# 🚀 Getting Started

## Prerequisites

* Java 21 or later
* Maven

Verify Java:

```bash
java -version
```

Verify Maven:

```bash
mvn -version
```

---

## Build

```bash
mvn clean package
```

---

## Run

Example:

```bash
java -jar target/codeguard.jar ./path/to/java-project
```

Or run directly from the IDE:

```text
Main → project path
```

---

# 💻 Example

Run:

```bash
codeguard ./sample-project
```

Example output:

```text
========================================
           CODEGUARD REPORT
========================================

Files Analyzed: 42

----------------------------------------
COMPLEXITY
----------------------------------------

OrderService.java
Method: processOrder()
Complexity: 18
Risk: HIGH

----------------------------------------
COUPLING
----------------------------------------

PaymentService
Dependencies: 12
Risk: HIGH

----------------------------------------
CIRCULAR DEPENDENCIES
----------------------------------------

OrderService
   ↓
PaymentService
   ↓
NotificationService
   ↓
OrderService

----------------------------------------
DUPLICATE CODE
----------------------------------------

UserService.java
AdminService.java

Similarity: 86%

----------------------------------------
CHANGE IMPACT
----------------------------------------

Changed:
PaymentService.java

Potentially affected:
 ├── OrderService
 ├── OrderController
 └── InvoiceService

========================================
```

---

# 🧪 Testing Strategy

CodeGuard is tested against small Java projects designed to reproduce specific scenarios.

```text
test-projects/
│
├── simple-project/
├── high-complexity-project/
├── circular-dependency-project/
├── duplicate-code-project/
└── impact-analysis-project/
```

Testing focuses on:

* Empty projects
* Nested directories
* Interfaces
* Enums
* Generics
* Comments containing Java syntax
* Strings containing keywords
* Circular dependencies
* Duplicate code
* Large source files
* Invalid input paths

---

# ⚙️ Design Trade-offs

CodeGuard intentionally does not attempt to become a full Java compiler.

Instead, it uses a **lightweight source-analysis approach** designed for practical code-health analysis.

### Why?

A complete Java parser/compiler would introduce significant complexity and external dependencies.

CodeGuard focuses on understanding the most useful structural information required for its analysis engine while keeping the implementation transparent and educational.

---

# 🗺️ Roadmap

### Version 1

* [x] Project scanning
* [x] Java source tokenization
* [x] Code structure extraction
* [x] Complexity analysis
* [x] Coupling analysis
* [x] Dependency graph
* [x] Circular dependency detection
* [x] Duplicate-code detection
* [x] Change-impact analysis
* [x] CLI reporting

### Future Improvements

* [ ] HTML report generation
* [ ] Configurable analysis rules
* [ ] Incremental analysis
* [ ] Parallel source scanning
* [ ] Historical trend reports
* [ ] IDE integration
* [ ] More advanced Java syntax analysis

---

# 🎓 What I Learned

Building CodeGuard focuses on more than implementing features.

The project provides practical experience with:

* Designing a modular Java application from scratch
* Working with Java source files and NIO
* Building a tokenizer and lightweight parser
* Modeling software dependencies
* Applying graph algorithms to real code
* Detecting software-maintainability risks
* Designing CLI developer tools
* Handling edge cases and malformed input
* Separating analysis, domain models, and reporting

---

# 📌 Project Goal

CodeGuard is built around a simple idea:

> **Good tooling should help developers understand the risks of changing code before those changes become problems.**

---

## 👨‍💻 Author

**Shivam Kumar Gupta**

B.Tech — Computer Science & Information Technology

Focused on **Java Backend Development, Core Java, Spring Boot, and Software Engineering**.
