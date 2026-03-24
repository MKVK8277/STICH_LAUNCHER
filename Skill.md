# Skill: Modern Android Launcher Development

## Overview
Guidelines and patterns for building high-performance, customizable Android launchers integrated with Stitch AI "vibe-designs".

## Core Principles
1.  **Performance First**: Launchers are the most used app. Optimize RecyclerViews, avoid overdraw, and ensure 90Hz/120Hz support.
2.  **Dynamic Configuration**: Use a central design system (via `StitchConfigManager`) to allow real-time UI updates without recompilation.
3.  **Minimal Friction**: App discovery (search, categories) should be accessible in < 1 second.
4.  **Edge-to-Edge**: Always implement full-screen immersive layouts.

## Architecture Pattern
*   **ViewModel + LiveData**: Use for all UI states (apps, time, notifications).
*   **Repository**: Generic interface for fetching apps (PM API) and widgets.
*   **Stitch Sync**: A background service or observer that polls/receives UI JSON and updates `SharedPreferences` / `Theme` constants.

## Feature Roadmap
1.  **App Grid**: Flexible 4x4, 5x5, 6x6 layouts.
2.  **Icon Packs**: Support for standard icon pack APIs.
3.  **Widgets**: Configurable clock, weather, and calendar widgets that follow the "Stitch Vibe".
4.  **Gestures**: Comprehensive swipe support (Up, Down, Double Tap).

## Maintenance
*   Keep `AppRepository` updated with `PACKAGE_REPLACED` / `PACKAGE_ADDED` receivers to refresh app lists automatically.
*   Monitor `BlurView` performance on mid-range devices.
