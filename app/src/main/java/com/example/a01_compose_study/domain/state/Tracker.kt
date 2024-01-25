package com.example.a01_compose_study.domain.state

import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.DomainType
import com.example.a01_compose_study.domain.model.NoticeModel
import com.example.a01_compose_study.data.ScreenType


// 해당 data class는 생각하는건.. 화면천이 액션 등등 데이터 흐름을 디버깅용으로 추적하기위한 데이터클래스
data class Tracker(
    var domainType: DomainType?,
    var screenType: ScreenType?,
    var dialogueMode: DialogueMode?
) {
    var additionalInfo = AdditionalInfo()
    var cnt = ScenarioCount(0, false)

    override fun toString(): String {
        return "\n--domainType:[$domainType]\n" +
                "--screenType:[$screenType]\n" +
                "--dialogueMode:[$dialogueMode]\n" +
                "--cnt:[$cnt]\n" +
                "--additionalInfo[" +
                "$additionalInfo" +
                "\n--]"
    }

    override fun equals(other: Any?): Boolean {
        val check = other as? Tracker
        if (check == null) {
            return false
        }
        if (domainType != check.domainType) {
            return false
        }
        if (screenType != check.screenType) {
            return false
        }
        if (dialogueMode != check.dialogueMode) {
            return false
        }
        if (additionalInfo != check.additionalInfo) {
            return false
        }
        if (cnt != check.cnt) {
            return false
        }
        return true
    }
}

data class ScenarioCount(var currentIdx: Int, var isCurrentScenarioData: Boolean) {
    override fun equals(other: Any?): Boolean {
        val check = other as? ScenarioCount
        if (check == null) {
            return false
        }
        if (isCurrentScenarioData == false || other.isCurrentScenarioData == false) {
            // 비교하는 데이터가 앱 내부에서 비교하는것이라면 (collectLatest) 해당 조건 안타고 아래로 감.
            // 테스트에서 비교하는것과 앱에서 비교하는것이라면 return true로 넘어감
            return true
        }
        return super.equals(other)
    }
}

class AdditionalInfo {
    var m_isHideWindow = false
    var m_isRejection = false
    var m_playBeep = false
    var m_uiText = "" // 화면에 출력해야하는 데이터를 담는 데이터
    var m_expecetedEvent = false
    var noticeModel: NoticeModel? = null
    var isNext = false
    var isPrev = false
    var rejectionPid = ""

    override fun equals(other: Any?): Boolean {
        val check = other as? AdditionalInfo
        if (check == null) {
            return false
        }
        if (m_isHideWindow != check.m_isHideWindow) {
            return false
        }
        if (m_isRejection != check.m_isRejection) {
            return false
        }
        if (m_playBeep != check.m_playBeep) {
            return false
        }
        if ((m_uiText.isEmpty() && m_expecetedEvent) || (check.m_uiText.isEmpty() && check.m_expecetedEvent)) {
            return true
        }
        if (m_uiText != check.m_uiText) {
            return false
        }
        if (isNext != check.isNext) {
            return false
        }
        if (isPrev != check.isPrev) {
            return false
        }
        if (rejectionPid != check.rejectionPid) {
            return false
        }
        return true
    }

    override fun toString(): String {
        return "\n" +
                "----isHideWindow [$m_isHideWindow]\n" +
                "----m_isRejection [$m_isRejection]\n" +
                "----playBeep [$m_playBeep]\n" +
                "----uiText [$m_uiText]\n" +
                "----isNext [$isNext]\n" +
                "----isPrev [$isPrev]\n" +
                "----rejectionPid [$rejectionPid]\n" +
                "----expectedEvent[$m_expecetedEvent]" +
                if (noticeModel != null) {
                    "\n----noticePrompt:[${noticeModel?.noticePromptId}]" +
                            "\n----noticeString:[${noticeModel?.noticeString}]"
                } else {
                    ""
                }
    }
}