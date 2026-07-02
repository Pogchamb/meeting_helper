#include <jni.h>
#include <android/log.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <cmath>
#include <string>
#include "whisper.h"

#define LOG_TAG "WhisperJNI"
#define LOG_I(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOG_E(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jlong JNICALL
Java_pa_chan_ml_WhisperEngine_nativeInitModel(JNIEnv *env, jobject thiz, jobject asset_manager, jstring model_path, jint cpu_cores) {
    const char *path = env->GetStringUTFChars(model_path, nullptr);

    AAssetManager *mgr = AAssetManager_fromJava(env, asset_manager);

    if (!mgr) {
        LOG_E("Failed add to AAssetmanager");
        env->ReleaseStringUTFChars(model_path, path);
        return 0;
    }

    AAsset *asset = AAssetManager_open(mgr, path, AASSET_MODE_UNKNOWN);
    if (!asset) {
        LOG_E("Failed load asset %s", path);
        env->ReleaseStringUTFChars(model_path, path);
        return 0;
    }

    off_t length = AAsset_getLength(asset);
    void *buffer = malloc(length);
    if (AAsset_read(asset, buffer, length) < 0) {
        LOG_E("Failed to read asset data");
        free(buffer);
        AAsset_close(asset);
        env->ReleaseStringUTFChars(model_path, path);
        return 0;
    }

    AAsset_close(asset);
    env->ReleaseStringUTFChars(model_path, path);

    struct whisper_context *ctx = whisper_init_from_buffer(buffer, length);

    free(buffer);

    if (!ctx) {
        LOG_E("Failed to initialize whisper context");
        return  0;
    }

    LOG_I("Whisper model loaded successfully. Size %d bytes", (int)length);
    return reinterpret_cast<jlong>(ctx);
}

extern "C" JNIEXPORT jstring JNICALL
Java_pa_chan_ml_WhisperEngine_nativeTranscribe(JNIEnv *env, jobject thiz, jlong ctx_ptr, jfloatArray audio_data, jint cpu_cores) {
    struct whisper_context *ctx = reinterpret_cast<struct whisper_context *>(ctx_ptr);
    if (!ctx) return env->NewStringUTF("ERROR: context not initialized");

    jfloat *audio_ptr = env->GetFloatArrayElements(audio_data, nullptr);
    jsize audio_len = env->GetArrayLength(audio_data);

    struct whisper_full_params params = whisper_full_default_params(WHISPER_SAMPLING_GREEDY);

    int threads = std::min(4, cpu_cores - 1);
    if (threads < 1) threads = 1;
    params.n_threads = threads;

    params.print_progress = false;
    params.print_special = false;
    params.translate = false;
    params.language = "ru";

    int result = whisper_full(ctx, params, audio_ptr, audio_len);

    env->ReleaseFloatArrayElements(audio_data, audio_ptr, JNI_ABORT);

    if (result != 0) {
        LOG_E("whisper_full failed with code %d", result);
        return env->NewStringUTF("");
    }

    std::string text;
    int n_segment = whisper_full_n_segments(ctx);
    for (int i = 0; i < n_segment; ++i) {
        const char *segment_text = whisper_full_get_segment_text(ctx, i);
        if (segment_text) {
            text += segment_text;
        }
    }

    return env->NewStringUTF(text.c_str());
}

extern "C" JNIEXPORT void JNICALL
Java_pa_chan_ml_WhisperEngine_nativeFreeModel(JNIEnv *env, jobject thiz, jlong ctx_ptr) {
    struct whisper_context *ctx = reinterpret_cast<struct whisper_context *>(ctx_ptr);
    if (ctx) {
        whisper_free(ctx);
        LOG_I("Whisper context freed");
    }
}
