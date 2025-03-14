# Voyager Project

## Overview
Voyager is a heavily abstracted and modular pathfinding library that aims to provide a simple and efficient way to navigate a vehicle through a series of waypoints. It is designed to be flexible, allowing users to easily integrate it into their projects and customize it to suit their needs.
## Project Structure
- **Core Module**: Contains the main source code and specific dependencies.

## Installation
To use Voyager in your project, you can include it as a repository and a dependency in your `build.gradle` file. Here’s an example of how to do that:

Replace the `repositories` and `dependencies` sections in your `build.gradle` file with the following:
```groovy
repositories {
    maven {url = 'https://git.devmello.xyz/api/packages/devmello/maven'}
}
```

```groovy
dependencies {
    implementation 'xyz.devmello.voyager:core:ADD_YOUR_VERSION_HERE'
}
```


## Requirements
- Java 11 or higher
- Gradle

## Setup for Contributing
1. **Clone the repository**:
    ```sh
    git clone https://git.devmello.xyz/devmello/voyager.git
    ```
    
    ```shell
    cd voyager
    ```

2. **Build the project**:
    ```sh
    ./gradlew build
    ```

3. **Run tests**:
    ```sh
    ./gradlew test
    ```

## Configuration
### Gradle Properties
To configure the project for publishing and other settings, you need to set the following properties in your `gradle.properties` file:
- `gitea.token`: Your Gitea personal access token.

These properties can be set in the `gradle.properties` file.

### Code Formatting
The project uses Spotless for code formatting. Configuration is defined in the `build.gradle` file.

### Publishing
The project is configured to publish artifacts to a Maven repository hosted on Gitea. Ensure your credentials are set in the `gradle.properties` file.

## Dependencies
- **JUnit**: For testing.
- **Apache Commons Math**: For mathematical operations.
- **EJML**: For linear algebra operations.

## License
This project is licensed under the MIT License. See the `LICENSE` file for details.