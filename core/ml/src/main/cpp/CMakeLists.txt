cmake_minimum_required(VERSION 3.22.1)
project("whisper_core_ml")

# 1. Собираем ядро Whisper
add_library(
        whisper_static
        STATIC
        whisper/whisper.cpp
        whisper/ggml.c
)

target_include_directories(whisper_static PUBLIC
        ${CMAKE_CURRENT_SOURCE_DIR}/whisper
)

target_compile_definitions(whisper_static
        PRIVATE
        GGML_USE_CPU=1
        WHISPER_NO_FILE_IO=0
)

if (ANDROID_ABI STREQUAL "arm64-v8a")
    target_compile_definitions(whisper_static PRIVATE GGML_USE_ARM_NEON=1)
endif ()

# 2. Создаем JNI-обертку
add_library(
        whisper_native
        SHARED
        whisper_jni.cpp
)

# 3. ПРИВЯЗЫВАЕМ ВСЁ В ОДНОМ МЕСТЕ К ПРАВИЛЬНОЙ ЦЕЛИ (whisper_native)
target_link_libraries(whisper_native
        whisper_static  # Ядро whisper
        log             # Логи
        dl              # Для многопоточности ggml
        android         # Базовые функции NDK
        c++_shared      # Стандартная библиотека C++
)
