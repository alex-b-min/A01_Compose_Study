package com.example.a01_compose_study.data

import com.example.a01_compose_study.domain.util.CustomLogger


enum class HVRConfigEvent {
    NONE,

    /**< VR Event NONE */
    DONE,

    /**< VR Event DONE */
    ERROR,

    /**< VR Event ERROR */
    LANGUAGE_SUPPORTED,

    /**< VR Event LANGUAGE_SUPPORTED */
    SUPPORTED_LANGUAGES,

    /**< VR Event SUPPORTED_LANGUAGES */
    INITIALIZE_DONE,

    /**< VR Event INITIALIZE_DONE */
    DIALOGUE_INTENT_PATH,

    /**< VR Event DIALOGUE INTENT PATH */
    MAX
    /**< VR Event MAX */
}

enum class HVRRequestEvent {
    NONE,

    /**< Requeset Event NONE */
    REQUEST_DONE,

    /**< Requeset Event REQUEST_DONE */
    REQUEST_FAILED,

    /**< Requeset Event REQUEST_FAILED */
    MAX
    /**< Requeset Event MAX */
}

enum class HVRError(val value: Int) {
    NONE(0),
    UNKNOWN(-1),
    ERROR_DIALOGUE(0x10000000),
    ERROR_AUDIO(0x20000000),
    ERROR_ARBITRATOR(0x30000000),
    ERROR_PROMPT(0x40000000),
    ERROR_ASR(0x50000000),
    ERROR_HMI(0x60000000),
    ERROR_DIALOGUE_UNINITIALIZED(ERROR_DIALOGUE.value or 1),
    ERROR_DIALOGUE_PARSE_PARAM(ERROR_DIALOGUE.value or 2),
    ERROR_DIALOGUE_RECOGNITION_START(ERROR_DIALOGUE.value or 3),
    ERROR_DIALOGUE_RECOGNITION_STOP(ERROR_DIALOGUE.value or 4),
    ERROR_DIALOGUE_RECOGNITION_PAUSE(ERROR_DIALOGUE.value or 5),
    ERROR_DIALOGUE_RECOGNITION_RESUME(ERROR_DIALOGUE.value or 6),
    ERROR_DIALOGUE_NOT_RECOGNIZING(ERROR_DIALOGUE.value or 7),
    ERROR_DIALOGUE_NOT_PAUSING(ERROR_DIALOGUE.value or 8),
    ERROR_DIALOGUE_PAUSING(ERROR_DIALOGUE.value or 9),
    ERROR_DIALOGUE_FINISHING(ERROR_DIALOGUE.value or 10),
    ERROR_DIALOGUE_TERMINATING(ERROR_DIALOGUE.value or 11),

    ERROR_DIALOGUE_AUDIO_REQUEST_ROUTE(ERROR_AUDIO.value or 1),
    ERROR_DIALOGUE_ARBITRATOR_REJECTION(ERROR_ARBITRATOR.value or 5),

    ERROR_DIALOGUE_PROMPT_UNINITIALIZED(ERROR_PROMPT.value or 1),
    ERROR_DIALOGUE_PROMPT_REQUEST_RENDER(ERROR_PROMPT.value or 2),
    ERROR_DIALOGUE_PROMPT_RENDERING(ERROR_PROMPT.value or 3),

    ERROR_DIALOGUE_AUDIO_STREAM_INITIALIZE(ERROR_ASR.value or 1),
    ERROR_DIALOGUE_AUDIO_STREAM_OPEN(ERROR_ASR.value or 2),
    ERROR_DIALOGUE_KEYWORD_SPOT_PARAMETER(ERROR_ASR.value or 3),
    ERROR_DIALOGUE_KEYWORD_SPOT_INITIALIZE(ERROR_ASR.value or 4),
    ERROR_DIALOGUE_KEYWORD_SPOT_START(ERROR_ASR.value or 5),
    ERROR_DIALOGUE_ASR_PARAMETER(ERROR_ASR.value or 6),
    ERROR_DIALOGUE_ASR_INITIALIZE(ERROR_ASR.value or 7),
    ERROR_DIALOGUE_ASR_RECOGNITION_START(ERROR_ASR.value or 8),
    ERROR_DIALOGUE_ASR_RECOGNITION_TIMEOUT(ERROR_ASR.value or 9),
    ERROR_DIALOGUE_ASR_SERVER_RESPONSE(ERROR_ASR.value or 10),
    ERROR_DIALOGUE_ASR_SERVER_UNAVAILABLE(ERROR_ASR.value or 11),
    ERROR_DIALOGUE_ASR_SERVER_CONNECTION(ERROR_ASR.value or 12),
    ERROR_DIALOGUE_ASR_SERVER_NO_RESPONSE(ERROR_ASR.value or 13),
    ERROR_DIALOGUE_ASR_NETWORK_NO_SIGNAL(ERROR_ASR.value or 14);

    companion object {
        val MAX = values().maxOf { it.value }

        init {
            CustomLogger.i("Constructor companion object Hash[${this.hashCode()}]")
        }
    }
}

enum class HVRState {
    INITIALIZING,

    /**< VR State INITIALIZING */
    IDLE,

    /**< VR State IDLE */
    PREPARING,

    /**< VR State PREPARING */
    SPOTTING,

    /**< VR State SPOTTING */
    SPEAKING,

    /**< VR State SPEAKING */
    LISTENING,

    /**< VR State LISTENING */
    THINKING,

    /**< VR State THINKING */
    FINISHING,

    /**< VR State FINISHING */
    RESTARTING,

    /**< VR State RESTARTING */
    PAUSING,

    /**< VR State PAUSING */
    PAUSED,

    /**< VR State PAUSED */
    TERMINATING,

    /**< VR State TERMINATING */
    TERMINATED,

    /**< VR State TERMINATED */
    MAX
    /**< VR State MAX */
}

enum class HVRSpeechDetection {
    BOS,

    /**< VR Speech Detection BOS */
    BOS_TIMEOUT,

    /**< VR Speech Detection BOS_TIMEOUT */
    EOS,

    /**< VR Speech Detection EOS */
    EOS_TIMEOUT,

    /**< VR Speech Detection EOS_TIMEOUT */
    MAX
    /**< VR Speech Detection MAX */
}

enum class HVRPromptStatus { NONE, PROMPT_PLAY, PROMPT_DONE, MAX }

enum class HVRDetectedPosition { DRIVER, PASSENGER, REAR_DRIVER, REAR_PASSENGER, MAX }

enum class HVRGuidanceType { PROMPT, BEEP_START, BEEP_END, NO_BEEP, MAX }