🚀 Modular Plugin-Based Microservice Project

📖 Overview

Welcome to the Modular Plugin-Based Microservice Project built with Java, PF4J, and Spring Boot! This architecture ensures flexibility, extensibility, and ease of integration by leveraging dynamic plugins and configurable workflows.

🌟 Features

🔌 Plugin System (PF4J): Easily add or update functionalities without downtime.

⚙️ Task Manager: Manages execution of dynamic workflows defined by external configurations.

🌐 HTTP Plugin: Enables RESTful interactions through modular steps.

📦 Shared Libraries: Provides interfaces and core definitions shared across plugins.

📂 Project Structure

.
├── 📁 library
│   ├── 📂 shared-interfaces
│   │   └── Task-related interfaces and exceptions
│   └── 📂 task-manager
│       └── Core logic for managing and executing task workflows
│
├── 📁 plugins
│   └── 📂 http-plugin
│       └── HTTP interaction steps (logging, formatting, REST calls)
│
└── 📁 services
└── 📂 example-service
└── Sample Spring Boot service demonstrating plugin integration

🛠️ Technologies Used

☕ Java (Java 21)

🍃 Spring Boot

🧩 PF4J (Plugin Framework for Java)

📋 YAML (for configuration)

🚧 Installation & Setup

Clone the repository:

git clone <repository-url>
cd <repository-directory>

Build the project:

mvn clean install

Run Example Service:

java -jar services/example-service/target/example-service.jar

⚡ Getting Started

Using Plugins

Define your workflow in flow.yaml.

Plugins will automatically be loaded and executed according to your workflow configuration.

Adding New Plugins

Create a new Maven module under the plugins directory.

Implement required interfaces from shared-interfaces.

Build and deploy your plugin without restarting the main service!

🎯 Example Workflow

Sample flow.yaml:

workflow:
- step: HelloWorldStep
- step: FormatStep
- step: LoggingStep

📋 Project Resources

🔗 PF4J Documentation

🔗 Spring Boot Documentation

🎉 Happy Coding!

