package com.example.a01_compose_study.data.custom

import com.example.a01_compose_study.data.HLanguageType
import com.example.a01_compose_study.domain.util.CustomLogger

object NormalizeUtils {
    private const val BUF_TN_MAX = 160
    private var m_language: HLanguageType = HLanguageType.US_ENGLISH
    private val spaceUnicode: UShort = 0x0020u
    private val nullUnicode: UShort = 0x0000u
    private val unicode00D7: UShort = 0x00D7u           // ×
    private val unicode00F7: UShort = 0x00F7u           // ÷

    // Turkish
    private val unicode00D0: UShort = 0x00D0u
    private val unicode00DD: UShort = 0x00DDu
    private val unicode00DE: UShort = 0x00DEu
    private val unicode00F0: UShort = 0x00F0u
    private val unicode00FD: UShort = 0x00FDu
    private val unicode00FE: UShort = 0x00FEu
    private val unicode011E: UShort = 0x011Eu
    private val unicode011F: UShort = 0x011Fu
    private val unicode0130: UShort = 0x0130u
    private val unicode015E: UShort = 0x015Eu
    private val unicode0131: UShort = 0x0131u
    private val unicode015F: UShort = 0x015Fu

    // except Special Character
    private val unicode0026: UShort = 0x0026u   // Ampersand "&"
    private val unicode0027: UShort = 0x0027u   // Apostrophe "'"
    private val unicode002C: UShort = 0x002Cu   // Comma ","

    // private val unicode002D: UShort = 0x002Du  // Hyphen "-"
    private val unicode002E: UShort = 0x002Eu   //  Full stop "."

    init {
        CustomLogger.i(" NormalizeUtils Hash[${this.hashCode()}]")
    }

    /**
     * @brief convert Text Normalize
     *
     * @param[in] language      language
     * @param[in] szKeyword     string of Keyword
     * @return converted Text
     */
    fun convertTextNormalize(language: HLanguageType, szKeyword: String): String {
        if (szKeyword.isEmpty()) {
            return szKeyword
        } else {
            m_language = language
//            CustomLogger.i("convertTextNormal START m_language : $m_language")
            var szData = szKeyword
            var nomalizedString = ""
            val tzUTF8ToUnicodeName = UShortArray(BUF_TN_MAX) // UShort 배열을 생성합니다.

            // 코틀린은 자동 메모리 관리를 지원하므로 메모리 초기화 및 해제를 직접 처리할 필요가 없습니다.
            // String을 Char 배열로 변환. 코틀린 Char는 UTF-16으로 인코딩 되어 저장된다.
            val unicodeChars = szData.toCharArray()
            val charCount = unicodeChars.size

            // Char 배열을 UShort 배열로 복사합니다.
            for (i in 0 until charCount) {
//                CustomLogger.i("convertTextNormal unicodeChars[i] : [${unicodeChars[i]}]")
                tzUTF8ToUnicodeName[i] = unicodeChars[i].code.toUShort()
//                CustomLogger.i("convertTextNormal tzUTF8ToUnicodeName[i] : [${tzUTF8ToUnicodeName[i]}]")
            }

            // convertUnicode 함수 호출
            nomalizedString = convertUnicode(tzUTF8ToUnicodeName, BUF_TN_MAX)

//            CustomLogger.i("convertTextNormal END szKeyword : [$szKeyword], nomalizedString : [$nomalizedString]")
            return nomalizedString
        }
    }

    /**
     * @brief convert Unicode
     *
     * @param[in] unicodeName    UShortArray of UniCode
     * @return normalize size
     */
    private fun convertUnicode(unicodeName: UShortArray, iSize: Int): String {
        var j = 0
        var iSpaceCnt = 0x01
        var unicodeWord: UShort
        var convertBuff = UShortArray(BUF_TN_MAX)
        var convertedString = ""

        // 코틀린은 자동 메모리 관리를 지원하므로 메모리 초기화 및 해제를 직접 처리 X
        for (i in 0 until iSize) {

            // uniCodeName => 넘어온 이름
            unicodeWord = unicodeName[i]
//            CustomLogger.i("convertUnicode unicodeWord:[$unicodeWord], iSize : [$iSize]")

            // 현재 언어에 따라 지원하는 Unicode인지를 확인
            if (checkSupportUnicode(unicodeWord) && j < BUF_TN_MAX) {
                // space check
                if (unicodeWord == spaceUnicode) {
                    if (m_language != HLanguageType.CHINESE && iSpaceCnt == 0x00) {
                        convertBuff[j++] = spaceUnicode
                    }
                    iSpaceCnt++
                } else {
//                    CustomLogger.i("convertUnicode unicodeWord add :[$unicodeWord]")
                    convertBuff[j++] = unicodeWord
                    iSpaceCnt = 0x00
                }
            } else {
                // exit
                if (unicodeWord == nullUnicode) {
                    // Remove last space
                    if (j != 0 && (convertBuff[j - 1] == spaceUnicode)) {
                        j--
                    }
                    break
                }
                // Check the condition for the double space
                else if (j != 0 && (iSpaceCnt == 0x00) && m_language != HLanguageType.CHINESE) {
                    if (convertBuff[j - 1] != spaceUnicode && j < BUF_TN_MAX) {
                        convertBuff[j++] = spaceUnicode
                    }
                }
                iSpaceCnt++
            }
        }

        if (j < BUF_TN_MAX) {
            convertBuff[j++] = nullUnicode
        }
//        CustomLogger.i("convertUnicode convertBuff size: [${convertBuff.size}]")

        // take(n) : 0부터 'n-1'까지의 요소를 취함
        convertedString = String(convertBuff.take(j - 1).map {
//            CustomLogger.i("convertUnicode String it : [$it]")
            it.toInt().toChar()
        }.toCharArray())

        for (i in 0 until j - 1) {
            if (!checkUnicodeSpecialCharacter(convertBuff[i])) {
                // check isEmpty
                if (convertBuff[i] != nullUnicode) {
//                    CustomLogger.i("convertUnicode convertedString : $convertedString")
                    return convertedString
                }
            }
        }
        return ""
    }

    /**
     * @brief check Unicode Basic Latin
     *
     * @param[in] unicodeWord    UniCode
     * @return true/false     whether to exist Unicode
     */
    private fun checkUnicodeBasicLatin(unicodeWord: UShort): Boolean {
        return if ((0x0030u <= unicodeWord) && (unicodeWord <= 0x0039u)) { // 0 to 9
//            CustomLogger.i("checkUnicodeBasicLatin 0 to 9")
            true
        } else if ((0x0041u <= unicodeWord) && (unicodeWord <= 0x005au)) {  // A to Z
//            CustomLogger.i("checkUnicodeBasicLatin A to Z")
            true
        } else if ((0x0061u <= unicodeWord) && (unicodeWord <= 0x007au)) {  // a to z
//            CustomLogger.i("checkUnicodeBasicLatin a to z")
            true
        } else checkUnicodeSpecialCharacter(unicodeWord)
    }

    /**
     * @brief check Unicode For Special Character
     *
     * @param[in] unicodeWord    UniCode
     * @return true/false     whether to
     */
    private fun checkUnicodeSpecialCharacter(unicodeWord: UShort): Boolean {
        // WhiteSpase " "
        if (unicodeWord == spaceUnicode) {
//            CustomLogger.i("checkUnicodeSpecialCharacter space")
            return true
        } else if (m_language != HLanguageType.KOREAN) {
            when (unicodeWord) {
                unicode0026,        // Ampersand "&"
                unicode0027,        // Apostrophe "'"
                unicode002C,        // Comma ","
                    // unicode002D: // Hyphen "-"
                unicode002E -> {    //  Full stop "."
//                    CustomLogger.i("checkUnicodeSpecialCharacter true")
                    return true
                }

                else -> {
                }
            }
            return false
        } else {
            return false
        }
    }


// Korean Unicode    : 0xAC00 ~ 0xD7AF : 0xAC00 ~ 0xD7A3
// Basic Latin       : 0x0000 ~ 0x007F : 0x0041 ~ 0x005a(A~Z)
//                     0x0030 ~ 0x0039(0~9)
//                     0x0020(space)
//                     0x0026(ampersand)
//                     0x0027(apostrophe)
//                     0x002C(comma)
//                     0x002D(hyphen-minus)
//                     0x002E(full stop)
//                     0x0061 ~ 0x007a(a~z)
// Depended-a Latin  : 0x0080 ~ 0x00FF : 0x00C0 ~ 0x00FF
// ISO 8859-1 West Europe
// ISO 8859-2 East Europe 0x0100 ~ 0x017F (Capital letter, odd Small letter)
// ISO 8859-5 Cyrilic: 0x0400 ~ 0x045F (0x0400 ~ 0x042F Capital letter, 0x0430 ~ 0x045F Small letter)

    /**
     * @brief check Support Unicode
     *
     * @param[in] unicodeWord      Unicode
     * @return true/false       whether to support unicode
     */
    private fun checkSupportUnicode(unicodeWord: UShort): Boolean {
        // Basic Latin을 먼저 확인
        if (checkUnicodeBasicLatin(unicodeWord)) {
//            CustomLogger.i("checkSupportUnicode checkUnicodeBasicLatin")
            return true
        } else {
//            CustomLogger.i("checkSupportUnicode m_language : $m_language")
            when (m_language) {
                HLanguageType.KOREAN -> {
                    if ((0xAC00u <= unicodeWord) && (unicodeWord <= 0xD7A3u))
                        return true
                }

                HLanguageType.JAPANESE -> {
                    if ((0x3040u <= unicodeWord) && (unicodeWord <= 0x309Fu))  // Hiragana
                        return true
                    else if ((0x30A0u <= unicodeWord) && (unicodeWord <= 0x30FFu))  // Katakana
                        return true
                    else if ((0x4E00u <= unicodeWord) && (unicodeWord <= 0x9FFFu))  // CJK Unified Ideographs
                        return true
                }

                HLanguageType.US_ENGLISH,
                HLanguageType.CA_FRENCH,
                HLanguageType.AM_SPANISH,
//                HLanguageType.RA_PORTUGUESE,
                HLanguageType.AU_ENGLISH -> {
                    // Need converting this range words to base latin
                    if ((0x00C0u <= unicodeWord) && (unicodeWord <= 0x00FFu)) {  // ISO 8859-1
                        return when (unicodeWord) {
                            unicode00D7,
                            unicode00F7 -> {
                                false
                            }

                            else -> {
                                true
                            }
                        }
                    }
                }

                HLanguageType.UK_ENGLISH,                    // ISO 8859-1
                HLanguageType.GERMAN,                        // ISO 8859-1
                HLanguageType.EU_FRENCH,                     // ISO 8859-1 + 2
                HLanguageType.ITALIAN,                       // ISO 8859-1
                HLanguageType.EU_SPANISH,                    // ISO 8859-1
                HLanguageType.EU_PORTUGUESE,                 // ISO 8859-1
                HLanguageType.DUTCH,                         // ISO 8859-1
                HLanguageType.DANISH,                        // ISO 8859-1
                HLanguageType.SWEDISH,                       // ISO 8859-1
                HLanguageType.POLISH,                        // ISO 8859-2 Latin 2
                HLanguageType.CZECH,                         // ISO 8859-2
                HLanguageType.SLOVAK,                        // ISO 8859-2
                HLanguageType.NORWEGIAN,                     // ISO 8859-1
                HLanguageType.FINNISH,                       // ISO 8859-1 + 2
                HLanguageType.BULGARIAN,                     // ISO 8859-5 Cyrilic
                HLanguageType.RUSSIAN -> {                   // ISO 8859-5 Cyrilic
                    if ((0x00C0u <= unicodeWord) && (unicodeWord <= 0x00FFu)) {  // ISO 8859-1
                        return when (unicodeWord) {
                            unicode00D7,
                            unicode00F7 -> {
                                false
                            }

                            else -> {
                                true
                            }
                        }
                    } else if ((0x0100u <= unicodeWord) && (unicodeWord <= 0x017Fu)) {  // ISO 8859-2
                        return true
                    } else if ((0x0400u <= unicodeWord) && (unicodeWord <= 0x045Fu)) {  // ISO 8859-5
                        return true
                    }
                }

                HLanguageType.TURKISH -> {                                       // ISO 8859-9
                    if ((0x00C0u <= unicodeWord) && (unicodeWord <= 0x00FFu)) {  // ISO 8859-1
                        when (unicodeWord) {
                            unicode00D7,
                            unicode00F7,
                            unicode00D0,
                            unicode00DD,  // Only ISO 8859-1
                            unicode00DE,
                            unicode00F0,
                            unicode00FD,
                            unicode00FE -> {
                                return false
                            }

                            else -> {
                                return true
                            }
                        }
                    }
                    // Turkish
                    else if (unicodeWord == unicode011E || unicodeWord == unicode0130 ||
                        unicodeWord == unicode015E || unicodeWord == unicode011F ||
                        unicodeWord == unicode0131 || unicodeWord == unicode015F
                    ) {
                        return true
                    }
                }

                HLanguageType.GREEK -> {                         // ISO 8859-7
                    // greek & goptic alphabet
                    if ((0x0370u <= unicodeWord) && (unicodeWord <= 0x03FFu)) {
                        return true
                    }
                    // colorado state highway
                    else if ((0x1F00u <= unicodeWord) && (unicodeWord <= 0x1FFFu)) {
                        return true
                    }
                }

                HLanguageType.ARABIC -> {
                    // Arabic
                    if ((0x0600u <= unicodeWord) && (unicodeWord <= 0x06FFu)) {
                        return true
                    }
                }

                HLanguageType.CHINESE -> {
                    if ((0x4E00u <= unicodeWord) && (unicodeWord <= 0x9FFFu)) {  // CJK Unified Ideographs
                        return true
                        // ((0x3400u <= unicodeWord) && (unicodeWord <= 0x4DBFu)) {  // CJK Extension A (GB-2312)
                        // ((0x4E00u <= unicodeWord) && (unicodeWord <= 0x9FFFu)) {  // CJK Unified Ideographs
                        // ((0xF900u <= unicodeWord) && (unicodeWord <= 0xFAFFu)) {  // CJK Compatibility Ideographs
                    }
                }

                HLanguageType.HUNGARIAN,
                HLanguageType.UKRAINIAN,
                HLanguageType.PERSIAN,
                HLanguageType.HEBREW,
                HLanguageType.HINDI,
                HLanguageType.BENGALI,
                HLanguageType.MARATHI,
                HLanguageType.TELUGU,
                HLanguageType.TAMIL,
                HLanguageType.SLOVENE,
                HLanguageType.RUMANIAN,
                HLanguageType.CROATIAN,
                HLanguageType.GUJARATI,
                HLanguageType.KANNADA,
                HLanguageType.ODIA,
                HLanguageType.MALAYALAM,
                HLanguageType.PUNJABI,
                HLanguageType.MALAY,
                HLanguageType.INDONESIAN -> {
                    if ((0x00C0u <= unicodeWord) && (unicodeWord <= 0x00FFu)) {  // ISO 8859-1
                        return when (unicodeWord) {
                            unicode00D7,
                            unicode00F7 -> {
                                false
                            }

                            else -> {
                                true
                            }
                        }
                    } else if ((0x0100u <= unicodeWord) && (unicodeWord <= 0x017Fu)) {  // ISO 8859-2
                        return true
                    }
                }

                else -> {
                    if ((0x00C0u <= unicodeWord) && (unicodeWord <= 0x00FFu)) {  // ISO 8859-1
                        return when (unicodeWord) {
                            unicode00D7,
                            unicode00F7 -> {
                                false
                            }

                            else -> {
                                true
                            }
                        }
                    } else if ((0x0100u <= unicodeWord) && (unicodeWord <= 0x017Fu)) {  // ISO 8859-2
                        return true
                    }
                }
            }
            return false
        }
    }

    fun replaceUnsupportedCharacters(input: String): String {
        // 해당 API는 세렌스에서 지원하지 않는 문자에 대해서 없애는 작업.
        val excludedHexValues = setOf(0x84, 0x93) // 여기에 제외할 16진수 값을 추가하세요.

        return input.map { char ->
            val hexValue = char.code
            if (hexValue in excludedHexValues) ' ' else char
        }.joinToString("")
    }
}