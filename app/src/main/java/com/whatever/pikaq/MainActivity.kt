package com.whatever.pikaq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.whatever.frame.entities.GenerallyListEntity
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
            add(GenerallyListEntity("1", "111"))
            add(GenerallyListEntity("2", "222"))
            add(GenerallyListEntity("3", "333"))
            add(GenerallyListEntity("4", "444"))
        })
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageList(messages: List<GenerallyListEntity>) {
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
            ) {
                Text(text = item.title, Modifier.animateItemPlacement())
                Text(text = item.description)
                Divider(thickness = 0.5.dp, color = Color.Gray)
            }
        }
    }
}

