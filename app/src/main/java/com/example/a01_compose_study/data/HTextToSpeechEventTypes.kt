package com.example.a01_compose_study.data

import com.example.a01_compose_study.domain.util.CustomLogger


enum class HTextToSpeechState {
    NONE,

    /**< TTS processing state by NONE */
    INITIALIZING,

    /**< TTS processing state by INITIALIZING */
    IDLE,

    /**< TTS processing state by IDLE */
    PREPARING,

    /**< TTS processing state by PREPARING */
    SYNTHESZING,

    /**< TTS processing state by SYNTHESZING */
    PLAYING,

    /**< TTS processing state by PLAYING */
    RESTARTING,

    /**< TTS processing state by RESTARTING */
    PAUSED,

    /**< TTS processing state by PAUSED */
    TERMINATING,

    /**< TTS processing state by TERMINATING */
    TERMINATED,

    /**< TTS processing state by TERMINATED */
    MAX
    /**< TTS processing state MAX */
}

enum class HTextToSpeechEvent {
    NONE,

    /**< TTS event by NONE */
    STATE_CHANGED,

    /**< event used in IHTextToSpeechListener */
    RELOAD_DONE,

    /**< event used in IHTextToSpeechConfigListener */
    RELOAD_FAILED,

    /**< event not used */
    PLAY_DONE,

    /**< event used in IHTextToSpeechListener */
    ERROR,

    /**< event used in IHTextToSpeechListener */
    STOPPED,

    /**< event used in IHTextToSpeechListener */
    VOICE_STYLE_CHANGED,

    /**< event used in IHTextToSpeechConfigListener */
    LANGUAGE_SUPPORTED,

    /**< event used in IHTextToSpeechConfigListener */
    SUPPORTED_LANGUAGES,

    /**< event used in IHTextToSpeechConfigListener */
    AUDIO_SOURCE,

    /**< event used in IHTextToSpeechListener */
    INITIALIZE_DONE,

    /**< event used in IHTextToSpeechConfigListener */
    SYNTHESIZING_DONE,

    /**< event used in IHTextToSpeechListener */
    SYNTHESIZING_FAILED,

    /**< event used in IHTextToSpeechListener */
    MAX
    /**< TTS event MAX */
}

enum class HTextToSpeechError(val value: Int) {
    ERROR_SPEECH(0x10000000),
    ERROR_PLAYER(0x20000000),
    ERROR_SYNTHESIZER(0x40000000),

    ERROR_SPEECH_UNINITIALIZED(ERROR_SPEECH.value or 1),
    ERROR_SPEECH_PARSE_PARAM(ERROR_SPEECH.value or 2),
    ERROR_SPEECH_SPEAK(ERROR_SPEECH.value or 3),
    ERROR_SPEECH_PLAY_FILE(ERROR_SPEECH.value or 4),
    ERROR_SPEECH_PLAY_EARCON(ERROR_SPEECH.value or 5),
    ERROR_SPEECH_STOP(ERROR_SPEECH.value or 6),
    ERROR_SPEECH_PAUSE(ERROR_SPEECH.value or 7),
    ERROR_SPEECH_RESUME(ERROR_SPEECH.value or 8),
    ERROR_SPEECH_PROMPT_RENDER(ERROR_SPEECH.value or 9),
    ERROR_SPEECH_ANOTHER_REQUEST_CAME(ERROR_SPEECH.value or 10),
    ERROR_SPEECH_NO_CHANNEL_ID(ERROR_SPEECH.value or 11),
    ERROR_SPEECH_VOICE_STYLE_CHANGED(ERROR_SPEECH.value or 12),

    ERROR_PLAYER_AUDIO_SOURCE_TYPE(ERROR_PLAYER.value or 1),
    ERROR_PLAYER_AUDIO_STREAM_OPEN(ERROR_PLAYER.value or 2),
    ERROR_PLAYER_AUDIO_ROUTE_FAIL(ERROR_PLAYER.value or 3),
    ERROR_PLAYER_NO_FRAME_INFO(ERROR_PLAYER.value or 4),
    ERROR_PLAYER_WRITE_FRAME_INFO(ERROR_PLAYER.value or 5),
    ERROR_PLAYER_WRITE_PREFIX_PADDING(ERROR_PLAYER.value or 6),
    ERROR_PLAYER_WRITE_POSTFIX_PADDING(ERROR_PLAYER.value or 7),

    ERROR_SYNTHESIZER_INITIALIZE(ERROR_SYNTHESIZER.value or 1),
    ERROR_SYNTHESIZER_RELEASE(ERROR_SYNTHESIZER.value or 2),
    ERROR_SYNTHESIZER_SYNTHESIZE(ERROR_SYNTHESIZER.value or 3),
    ERROR_SYNTHESIZER_CANCEL(ERROR_SYNTHESIZER.value or 4);

    companion object {
        val MAX = values().maxOf { it.value }

        init {
            CustomLogger.i("Constructor companion object Hash[${this.hashCode()}]")
        }
    }
}