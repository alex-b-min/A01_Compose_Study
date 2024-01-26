package com.example.a01_compose_study.data.repositoryImpl

import android.os.Bundle
import com.example.a01_compose_study.data.data_source.VMDataSource.createDummyBundle
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.repository.HelpRepository
import javax.inject.Inject

class HelpRepositoryImpl @Inject constructor() : HelpRepository {
    override fun getHelpItems(): List<HelpItemData> {
        val dummyBundle = createDummyBundle()

    // "helpItemList" 키를 사용하여 ArrayList<Bundle>의 value 가져오기
        val helpItemList: ArrayList<Bundle>? = dummyBundle.getParcelableArrayList("helpItemList")

    // helpItemList이 null이 아니라면 각 Bundle에서 데이터를 추출하여 List<HelpItemData> 추출
        val finalList: List<HelpItemData> = helpItemList?.map { bundle ->
            HelpItemData(
                domainId = bundle.getString("domainId", ""),
                command = bundle.getString("command", ""),
                commandsDetail = bundle.getStringArrayList("commandsDetail")?.filterNotNull()?.toList() ?: emptyList()
            )
        } ?: emptyList()

        return finalList
    }

//    private fun createDummyData(): List<HelpItemData> {
//        return listOf(
//            HelpItemData(
//                "Navigation",
//                "Find Filling station in London",
//                mutableListOf("command1_1", "command1_2", "command1_3")
//            ),
//            HelpItemData(
//                "Contacts",
//                "Call John Smith",
//                mutableListOf("command2_1", "command2_2", "command2_3")
//            ),
//            HelpItemData(
//                "Weather",
//                "How is the weather",
//                mutableListOf("command3_1", "command3_2", "command3_3")
//            ),
//            HelpItemData(
//                "Radio",
//                "DAB/FM List",
//                mutableListOf("command3_1", "command3_2", "command3_3")
//            ),
//        )
//    }
}