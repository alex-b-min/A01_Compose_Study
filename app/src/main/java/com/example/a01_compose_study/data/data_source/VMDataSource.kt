//package com.example.a01_compose_study.data.data_source
//
//import android.os.Bundle
//
//object VMDataSource {
//    fun createDummyBundle(): Bundle {
//        val bundle = Bundle()
//
//        val helpItemList = ArrayList<Bundle>()
//
//        // HelpItemData 1
//        val item1Bundle = Bundle().apply {
//            putString("domainId", "Navigation")
//            putString("command", "Find Filling station in London")
//
//            val commandList1 = ArrayList<String>().apply {
//                add("command1_1")
//                add("command1_2")
//                add("command1_3")
//            }
//            putStringArrayList("commandsDetail", commandList1)
//        }
//        helpItemList.add(item1Bundle)
//
//        // HelpItemData 2
//        val item2Bundle = Bundle().apply {
//            putString("domainId", "Contacts")
//            putString("command", "Call John Smith")
//
//            val commandList2 = ArrayList<String>().apply {
//                add("command2_1")
//                add("command2_2")
//                add("command2_3")
//            }
//            putStringArrayList("commandsDetail", commandList2)
//        }
//        helpItemList.add(item2Bundle)
//
//        // HelpItemData 3
//        val item3Bundle = Bundle().apply {
//            putString("domainId", "Weather")
//            putString("command", "How is the weather")
//
//            val commandList3 = ArrayList<String>().apply {
//                add("command3_1")
//                add("command3_2")
//                add("command3_3")
//            }
//            putStringArrayList("commandsDetail", commandList3)
//        }
//        helpItemList.add(item3Bundle)
//
//        // HelpItemData 4
//        val item4Bundle = Bundle().apply {
//            putString("domainId", "Radio")
//            putString("command", "DAB/FM List")
//
//            val commandList4 = ArrayList<String>().apply {
//                add("command4_1")
//                add("command4_2")
//                add("command4_3")
//            }
//            putStringArrayList("commandsDetail", commandList4)
//        }
//        helpItemList.add(item4Bundle)
//
//        bundle.putParcelableArrayList("helpItemList", helpItemList)
//
//        return bundle
//    }
//}