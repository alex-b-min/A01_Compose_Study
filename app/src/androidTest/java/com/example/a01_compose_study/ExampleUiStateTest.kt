package com.example.a01_compose_study

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.screen.help.screen.HelpListWindow
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalTestApi
class ExampleUiStateTest {
    /**
    궁금증
     * 1. sealed class로 정의한 DomainUiState를 적용하면 Compose UI에 잘 적용이 되는지 테스트 하는게 가능 혹은 쉬울까?

    결과
     * 1. sealed class로 정의한 DomainUiState를 적용하면 Compose UI에 잘 적용이 되는지 테스트는 가능했지만 쉽게 할 수 있는지는 상대적인 거 같다.
     */

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testUiState() {
        // 테스트 할 Help 더미 데이터 생성
        val dummyHelpDataList = listOf(
            HelpItemData(
                domainId = SealedDomainType.Navigation,
                command = "command1", commandsDetail = listOf("detail1", "detail2", "detail3")
            ),
            HelpItemData(
                domainId = SealedDomainType.Call,
                command = "command2",
                commandsDetail = listOf("detail4", "detail5")
            ),
            HelpItemData(
                domainId = SealedDomainType.Radio,
                command = "command3",
                commandsDetail = listOf("detail6", "detail7", "detail8")
            )
        )

        // 테스트할 UI 상태 생성
        val testUiState = DomainUiState.HelpWindow(
            domainType = SealedDomainType.Help,
            screenType = ScreenType.HelpList,
            data = dummyHelpDataList,
            text = SealedDomainType.Help.text,
            screenSizeType = ScreenSizeType.Large
        )

//        composeTestRule.setContent {
//            HelpListWindow(
//                domainUiState = testUiState as DomainUiState.HelpWindow,
//                vrUiState = ,
//                backgroundColor = Color.Black,
//                onDismiss = { },
//                onBackButton = { },
//                onItemClick = { },
//            )
//        }

        // HelpListWindow UI에 표시되는 텍스트와 testUiState의 데이터를 비교하여 UI가 올바르게 표시되는지 확인하는 코드
        testUiState.data.forEachIndexed { index, helpItemData ->
            composeTestRule.onNodeWithText(helpItemData.command).isDisplayed()
            composeTestRule.onNodeWithText(helpItemData.domainId.text).isDisplayed()
        }
    }
}