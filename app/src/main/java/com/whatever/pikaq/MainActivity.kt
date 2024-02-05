package com.whatever.pikaq

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.whatever.frame.entities.GenerallyListEntity
import com.whatever.permission.test.TestPermissionActivity
import com.whatever.pikaq.ui.theme.PikaQTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PikaQTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView()
                }
            }
        }
        L.d("测试日志")
    }

    private fun testBlock() {
        Thread.sleep(100)
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MainView()
}

@Composable
fun MainView() {
    Column {
        Text(
            text = "Android",
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Green)
                .align(alignment = Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
        MessageList(mutableListOf<GenerallyListEntity>().apply {
            add(GenerallyListEntity("游戏", "打开游戏功能"))
            add(GenerallyListEntity("权限", "权限页面"))
        })
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageList(messages: List<GenerallyListEntity>) {
    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // 处理返回结果
    }
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .padding(10.dp, 0.dp, 10.dp, 0.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        this.items(items = messages) { item ->
            Column(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .clickable(onClick = {
                        L.el("compose", "点击了一个条目")
                        val dest = when (item.title) {
                            "权限" -> {
                                TestPermissionActivity::class.java
                            }

                            else -> GameChooseNumActivity::class.java
                        }
                        activityLauncher.launch(Intent(context, dest))
                    })
            ) {
                Text(text = item.title, Modifier.animateItemPlacement())
                Text(text = item.description)
                Divider(thickness = 0.5.dp, color = Color.Gray)
            }
        }
    }
}

