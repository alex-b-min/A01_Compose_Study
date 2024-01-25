//package com.ftd.ivi.cerence.ui.compose
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.material3.Divider
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.testTag
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.res.dimensionResource
//import androidx.compose.ui.unit.TextUnit
//import androidx.compose.ui.unit.TextUnitType
//import androidx.compose.ui.unit.dp
//import com.ftd.ivi.cerence.R
//import com.ftd.ivi.cerence.viewmodel.ServiceViewModel
//import java.io.File
//
//@Composable
//fun PcmList(viewModel: ServiceViewModel, listItem: List<File>) {
//
//    val listState = rememberLazyListState()
//    val multipleEventsCutter = remember { MultipleEventsCutter.get() }
//    Box(
//        modifier = Modifier
//            .width(dimensionResource(id = R.dimen.agent_width))
//            .fillMaxHeight()
//            .background(Color.Black)
//    ) {
//
//        LazyColumn(
//            state = listState,
//            modifier = Modifier
//                .width(dimensionResource(id = R.dimen.agent_width))
//                .fillMaxHeight()
//                .testTag("PcmListView"),
//        ) {
//            itemsIndexed(
//                items = listItem,
//            ) { index, item ->
//
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(dimensionResource(R.dimen.dp_40))
//                        .padding(start = 20.dp)
////                .pointerInput(Unit) {
////                    detectTapGestures(
////                        onTap = {
////                            multipleEventsCutter.processEvent {
////                                viewModel.vrmwManager?.let {
////                                    it.mediaIn.setPCM(item)
////                                    it.mediaOut.playPCM(item)
////                                }
////                            }
////                        }
////                    )
////                }
//                ) {
//
//
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight()
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(dimensionResource(R.dimen.dp_40))
//                        ) {
//                            Row {
//                                Box(
//                                    modifier = Modifier
//                                        .weight(0.8f),
//                                ) {
//                                    TextButton(
//                                        modifier = Modifier
//                                            .fillMaxWidth(),
//                                        buttonText = if (item.isDirectory) "D : ${item.name}" else item.name,
//                                        fontSize = TextUnit(17.0f, TextUnitType.Sp),
//                                        clickListener = {
//                                            if (!item.isDirectory) {
//                                                viewModel.serviceManager?.vrmwManager?.let {
//                                                    viewModel.skipStore = true
//                                                    it.mediaIn.setPCM(item)
//                                                    it.mediaOut.playPCM(item)
//                                                }
//                                            }
//                                        },
//                                        isAutoSize = true,
//                                    )
//                                }
//
//                                if (item.name.first().isDigit()) {
//                                    Box(
//                                        modifier = Modifier
//                                            .weight(0.2f),
//                                    ) {
//                                        TextButton(
//                                            modifier = Modifier
//                                                .fillMaxWidth(),
//                                            buttonText = "Delete",
//                                            fontSize = TextUnit(10.0f, TextUnitType.Sp),
//                                            clickListener = {
//                                                if (!item.isDirectory) {
//                                                    item.delete()
//                                                    viewModel.getPcmList()
//                                                }
//                                            },
//                                            isAutoSize = true,
//                                        )
//                                    }
//                                }
//                            }
//
//                        }
//                        val divider = (index < listItem.size - 1)
//                        if (divider) {
//                            Divider(
//                                color = colorResource(id = R.color.white_a20),
//                                thickness = 0.4.dp
//                            )
//                        }
//                    }
//
//
//                }
//
//
//            }
//        }
//    }
//
//
//}