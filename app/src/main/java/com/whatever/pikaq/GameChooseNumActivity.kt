package com.whatever.pikaq

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whatever.pikaq.ui.theme.PikaQTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameChooseNumActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PikaQTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyLazyGrid()
                }
            }
        }
    }
}

@Composable
fun MyGridComposable(
    dataList: List<String>,
    clickList: MutableList<Int>,
    counts: Int,
    isRunning: MutableState<Boolean>
) {
    // 指定每行显示的列数
    LazyVerticalGrid(
        columns = GridCells.Fixed(counts),
        modifier = Modifier
            .padding(vertical = 20.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        content = {
            items(dataList.size) { index ->
                GridItem(dataList[index], clickList) { data ->
                    // 点击了某一个需要检查是否按顺序添加
                    val last = clickList.let {
                        if (it.isEmpty()) {
                            0
                        } else {
                            it.last()
                        }
                    }
                    if (last == (data.toInt() - 1)) {
                        // 正确
                        clickList.add(data.toInt())
                        if (clickList.size == dataList.size) {
                            isRunning.value = false
                        }
                        true
                    } else {
                        // 错误 误操作
                        false
                    }
                }
            }
        })
}

@Composable
fun GridItem(data: String, clickList: MutableList<Int>, onItemClick: (String) -> Boolean) {
    val bg = remember(data, clickList) {
        mutableStateOf(clickList.size > 0)
    }

    Text(
        text = data,
        fontSize = 30.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(2.dp)
            .width(50.dp)
            .height(50.dp)
            .background(
                if (bg.value) {
                    Color.Green
                } else {
                    Color.White
                }
            )
            .border(1.dp, Color.Black)
            .clickable {
                if (!bg.value) {
                    bg.value = onItemClick(data)
                    Log.v("点击了item", "点击了第${data} 的item")
                }
            }
    )
}

@Preview(showBackground = true)
@Composable
fun MyLazyGrid() {
    val columnsNumber = remember { mutableStateOf(3) }

    val nums = arrayListOf<String>()
    for (i in 1..(columnsNumber.value * columnsNumber.value)) {
        nums.add(i.toString())
    }

    val dataList = remember { nums.shuffled().toMutableStateList() }
    val clickList = remember { mutableStateListOf<Int>() }

    val isRunning = remember { mutableStateOf(true) }


    Column(Modifier.padding(20.dp), content = {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (columnsNumber.value == 3) {
                        Color.Red
                    } else {
                        Color.Blue
                    }
                ),
                onClick = {
                    columnsNumber.value = 3

                    val number = arrayListOf<String>()
                    for (i in 1..(columnsNumber.value * columnsNumber.value)) {
                        number.add(i.toString())
                    }

                    dataList.clear()
                    dataList.addAll(number.shuffled())
                    clickList.clear()
                    isRunning.value = false
//                    isRunning.value = true
                }, modifier = Modifier
                    .wrapContentWidth()
                    .weight(1f)
            ) {
                Text("1 - 9 ")
            }

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (columnsNumber.value == 4) {
                        Color.Red
                    } else {
                        Color.Blue
                    }
                ),
                onClick = {
                    columnsNumber.value = 4
                    val number = arrayListOf<String>()
                    for (i in 1..(columnsNumber.value * columnsNumber.value)) {
                        number.add(i.toString())
                    }

                    dataList.clear()
                    dataList.addAll(number.shuffled())
                    clickList.clear()
                    isRunning.value = false
//                    isRunning.value = true
                }, modifier = Modifier
                    .wrapContentWidth()
                    .weight(1f)
            ) {
                Text("1 - 16")
            }

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (columnsNumber.value == 5) {
                        Color.Red
                    } else {
                        Color.Blue
                    }
                ),
                onClick = {
                    columnsNumber.value = 5
                    val number = arrayListOf<String>()
                    for (i in 1..(columnsNumber.value * columnsNumber.value)) {
                        number.add(i.toString())
                    }

                    dataList.clear()
                    dataList.addAll(number.shuffled())
                    clickList.clear()
                    isRunning.value = false
//                    isRunning.value = true
                }, modifier = Modifier
                    .wrapContentWidth()
                    .weight(1f)
            ) {
                Text("1 - 25")
            }
        }

        Timer(isRunning)

//        Button(
//            onClick = { isRunning.value = !isRunning.value },
//            enabled = true
//        ) {
//            Text(if (isRunning.value) "停止" else "重新计时")
//        }

        MyGridComposable(dataList = dataList, clickList, columnsNumber.value, isRunning)

    })
}

@Composable
fun Timer(isRunning: MutableState<Boolean>) {
    val elapsedTime = remember { mutableStateOf(0L) }
    val coroutineScope = rememberCoroutineScope()
    val job = remember { mutableStateOf<Job?>(null) }

    val startTime = remember { mutableStateOf(System.currentTimeMillis()) }

    DisposableEffect(isRunning.value) {
        if (isRunning.value) {
            job.value = coroutineScope.launch {
                elapsedTime.value = 0L
                startTime.value = System.currentTimeMillis()
                while (true) {
                    elapsedTime.value = System.currentTimeMillis() - startTime.value
                    delay(100)
                }
            }
        } else {
            job.value?.cancel()
        }

        onDispose {
            job.value?.cancel()
        }
    }

    Text(
        text = "计时器: ${elapsedTime.value / 1000.0f} 秒",
        modifier = Modifier.padding(vertical = 20.dp)
    )

//    Button(
//        onClick = {
//            if (isRunning.value) {
//                job.value?.cancel()
//                isRunning.value = false
//            } else {
//                startTime.value = System.currentTimeMillis()
//                elapsedTime.value = 0L
//                isRunning.value = true
//            }
//        },
//        enabled = true
//    ) {
//        Text(if (isRunning.value) "停止" else "重新计时")
//    }
}

