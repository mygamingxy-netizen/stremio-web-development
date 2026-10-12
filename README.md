# UragonTv - Android TV Live Streaming Platform

**UragonTv** is a production-ready, native Android TV (Google TV / Android TV OS) live streaming application designed to play national, regional, and local live channels across the Philippines and international feeds.

---

## Key Features

- **Platform & Engine:**
  - Built natively with **Kotlin** and **Jetpack Compose for TV** (`androidx.tv.material3`, `androidx.tv.foundation`).
  - High-performance video playback with **Media3 ExoPlayer 1.5.1** (`HlsMediaSource` for `.m3u8`, MPEG-DASH `.mpd`, and progressive streams).
  - Robust automatic reconnection logic and exponential backoff retry on buffering drops.

- **TV-First Remote & D-Pad Optimization:**
  - 100% directional D-Pad navigation (Up, Down, Left, Right, Center/OK, Back).
  - **1.08x focus scale animation** with metallic gold glowing border (`2.dp #D4AF37`) and elevated shadow.

- **Visual Design:**
  - **Premium Dark Luxury Aesthetic**: Ultra-deep obsidian black (`#0B0C10`), metallic warm gold (`#D4AF37`), antique gold (`#FFD700`), and dark charcoal surfaces (`#1E1E24`).
  - High-contrast typography designed for 10-foot viewing distances.

- **Dynamic M3U / JSON Stream Source Injector:**
  - Parses live channels dynamically from local `assets/sources.m3u` or fallback `assets/channels.json`.
  - `StreamSourceRepository` allows users to inject custom remote M3U playlist URLs or toggle presets directly from Settings.

- **Navigation & Screens:**
  1. **Collapsible Navigation Sidebar**: Left-hand navigation rail (Home, Categories, Regions, Favorites, Search, Settings) that expands on focus and collapses to icon mode when browsing channels.
  2. **Home Screen**: Featured hero banner with live channel preview metadata, followed by horizontal channel rows organized by category and popularity.
  3. **Fullscreen Player**: Full-bleed live stream playback with non-intrusive On-Screen Display (OSD) overlay activated by D-Pad, quick channel picker drawer, aspect ratio toggle (Fit 16:9, Zoom, Stretch), and favorite bookmarking.
  4. **Categories Screen**: Filter channels across National Networks, Regional, News, Sports, Entertainment, and Movies.
  5. **Regions Screen**: Dedicated regional filtering covering Luzon (Bicol, Central Luzon, NCR), Visayas (Cebu, Iloilo), Mindanao (Davao, CDO), Nationwide, and International.
  6. **Favorites Screen**: User-saved bookmarks persisted via SharedPreferences/DataStore.
  7. **Search Screen**: D-Pad accessible on-screen virtual keyboard with instant channel search.
  8. **Settings Screen**: Buffer profile selector (Low Latency / Standard / High Stability), auto-reconnect toggle, playlist source switcher, and engine diagnostics.

---

## Technical Specifications

- **OS Target:** Android TV / Google TV (minSdk 26, targetSdk 36, compileSdk 36)
- **Toolchain:** AGP 9.1.1, Gradle 9.3.1, JDK 21
- **Video Engine:** `androidx.media3:media3-exoplayer:1.5.1`
- **Image Pipeline:** `io.coil-kt:coil-compose:2.7.0`
