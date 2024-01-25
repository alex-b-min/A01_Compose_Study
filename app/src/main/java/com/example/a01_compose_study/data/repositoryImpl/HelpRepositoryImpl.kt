package com.example.a01_compose_study.data.repositoryImpl

import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.repository.HelpRepository
import javax.inject.Inject

class HelpRepositoryImpl @Inject constructor(

) : HelpRepository {
    override fun getHelpItems(): List<HelpItemData> {
        return createDummyData()
    }

    private fun createDummyData(): List<HelpItemData> {
        return listOf(
            HelpItemData(
                "Navigation",
                "Find Filling station in London",
                mutableListOf("command1_1", "command1_2", "command1_3")
            ),
            HelpItemData(
                "Contacts",
                "Call John Smith",
                mutableListOf("command2_1", "command2_2", "command2_3")
            ),
            HelpItemData(
                "Weather",
                "How is the weather",
                mutableListOf("command3_1", "command3_2", "command3_3")
            ),
            HelpItemData(
                "Radio",
                "DAB/FM List",
                mutableListOf("command3_1", "command3_2", "command3_3")
            ),
        )
    }
}