package com.example.a01_compose_study.data


enum class HG2PStatus {
    NONE,

    /**< G2P status NONE */
    PRE_PROCESSED,

    /**< G2P status PRE_PROCESSED */
    SUCCESS,

    /**< G2P status SUCCESS */
    FAILED,

    /**< G2P status FAILED */
    CANCELLED,

    /**< G2P status CANCELLED */
    SERVER_UPDATE_SUCCESS,

    /**< G2P status SERVER_UPDATE_SUCCESS */
    SERVER_UPDATE_FAIL,

    /**< G2P status SERVER_UPDATE_FAIL */
    SERVER_DELETE_SUCCESS,

    /**< G2P status SERVER_DELETE_SUCCESS */
    SERVER_DELETE_FAIL,

    /**< G2P status SERVER_DELETE_FAIL */
    SERVER_UPDATE_FORCE,

    /**< G2P status SERVER_UPDATE_FORCE */
    MAX
    /**< G2P status MAX */
}

enum class HG2PType {
    NONE,

    /**< G2P type NONE */
    ALL,

    /**< G2P type ALL */
    EMBEDDED,

    /**< G2P type EMBEDDED */
    SERVER,

    /**< G2P type SERVER */
    MAX
    /**< G2P type MAX */
}

enum class HVRG2PMode {
    NONE,

    /**< G2P Mode NONE */
    PHONE_BOOK,

    /**< G2P Mode contact information */
    SXM,

    /**< G2P Mode North American radio stations */
    DAB,

    /**< G2P Mode european radio station */
    SETTING,

    /**< G2P Mode Setting */
    NAVIGATION,

    /**< G2P Mode Navigation Favorites */
    MAX
    /**< G2P Mode MAX */
}