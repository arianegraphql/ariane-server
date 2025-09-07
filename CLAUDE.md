# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ArianeGraphQL is a lightweight Kotlin GraphQL server framework with code generation capabilities. The project is structured as a multi-module Gradle build with the following key modules:

- **server**: Core GraphQL server functionality with resolver interfaces and extensions
- **server-ktor**: Ktor-specific server implementation with GraphQL request/subscription handlers
- **codegen**: Gradle plugin for generating Kotlin code from GraphQL schemas
- **sample**: Example application demonstrating usage

## Key Commands

### Build and Test
- `./gradlew build` - Build all modules
- `./gradlew :server:build` - Build only the server module
- `./gradlew test` - Run all tests
- `./gradlew :server:test` - Run server module tests
- `./gradlew :codegen:test` - Run codegen module tests

### Sample Application
- `../gradlew :sample:run` - Run the sample application (note: run from sample directory or use full path)

### Code Generation
The codegen module is a Gradle plugin that generates Kotlin resolvers and types from GraphQL schemas. Configuration is done via `codegen.json` files in the resources directory.

### Publishing
- `./gradlew publish` - Publish to local repository
- `./gradlew publishToSonatype` - Publish to Sonatype (requires credentials)

## Architecture

### Core Server Module
The server module provides the foundation with:
- `Resolver<S, A, R>` interface for type-safe GraphQL resolvers
- `FunctionalResolver` for lambda-based resolvers
- Extension functions for converting resolvers to DataFetchers
- Builder DSLs for schema construction (`RootResolverBuilder`, `TypeResolverBuilder`)

### Ktor Integration
The server-ktor module provides:
- `handleGraphQLRequest()` for HTTP GraphQL endpoints
- `handleGraphQLSubscription()` for WebSocket subscriptions
- `launch()` extension for easy server startup

### Code Generation
The codegen module generates:
- Type classes from GraphQL types/inputs/enums
- Resolver interfaces for queries/mutations/subscriptions
- Argument classes for resolver parameters
- Configured via `codegen.json` with package names and scalar mappings

### Sample Structure
The sample demonstrates:
- GraphQL schema definition (`schema.graphql`)
- Generated code integration (in `build/generated/ariane/`)
- Resolver implementations
- Server setup with Ktor

## Development Notes

- Kotlin version: 2.2.10
- Java version: 17
- Uses kotlinx-coroutines for async resolver support
- GraphQL Java library version: 25.0.beta-5
- Generated code is placed in `build/generated/ariane/main/kotlin/`
- The framework uses context parameters (`-Xcontext-parameters` compiler flag)
- All modules use JUnit 5 for testing with MockK and Kotlin Faker

## Project Structure

The codebase follows a standard Gradle multi-project structure:
- Root build.gradle.kts defines common configuration and publishing setup
- Each module has its own build.gradle.kts with specific dependencies
- Generated code is automatically included in source sets
- CI/CD pipeline builds and tests the server module via GitHub Actions