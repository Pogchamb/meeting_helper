# Meeting Helper: On-Device AI Voice Summarizer

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![C++](https://img.shields.io/badge/c++-%2300599C.svg?style=for-the-badge&logo=c%2B%2B&logoColor=white)
![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg?style=for-the-badge)

A native Android application designed for B2B/Prosumer users to record voice, transcribe it locally (100% offline), and generate structured summaries using a Bring-Your-Own-Key (BYOK) LLM approach. 

Focused on strict data privacy, performance, and memory safety.

## 🚀 Key Features
* **Zero-Knowledge Architecture:** Voice recordings never leave the device.
* **On-Device ASR:** Offline speech-to-text powered by `whisper.cpp` running natively via JNI/NDK.
* **BYOK LLM Summarization:** Connects to external LLM providers using the user's encrypted API key.
* **Resilient Background Processing:** Utilizes `WorkManager` (Expedited Workers) and Foreground Services to ensure long transcriptions survive background restrictions.
* **Hardware-Backed Security:** API keys are secured using Android Keystore via `EncryptedSharedPreferences`.

## 🛠️ Tech Stack & Architecture
The project strictly follows **Clean Architecture** principles and a multi-module structural design to isolate the C++ core from the Android framework.

* **Language:** Kotlin, C/C++
* **UI:** Classic XML + ViewBinding
* **Concurrency:** Kotlin Coroutines & Flow (StateFlow)
* **Dependency Injection:** Dagger Hilt
* **Local Storage:** Room Database (Single Source of Truth)
* **Networking:** Retrofit 2 + OkHttp 3
* **Native / ML Core:** Android NDK, CMake, JNI, `whisper.cpp` (ggml-base)

## 📦 Project Structure (Modules)
* `:app` - Entry point, DI graphs, navigation.
* `:core:domain` - Pure Kotlin module. Interfaces, UseCases, and business entities.
* `:core:ml` - Isolated C++ core, `whisper.cpp`, AAssetManager integration, and JNI bridges.
* `:core:audio` - Low-level AudioRecord API, Foreground Service.
* `:core:network` - Retrofit implementations and DTOs.
* `:core:database` - Room DAOs, Entities, and Jetpack Security implementations.
* `:feature:recorder` - UI for the voice recorder and background process tracking.
* `:feature:history` - UI for the session list, transcriptions, and summaries.

## ⚙️ Getting Started

### Prerequisites
* Android Studio (Latest stable)
* Android NDK & CMake installed via SDK Manager

### Installation & Build
1. Clone the repository:
   ```bash
   git clone [https://github.com/Pogchamb/meeting_helper.git](https://github.com/Pogchamb/meeting_helper.git)
