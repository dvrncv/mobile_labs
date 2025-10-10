package com.example.mobile_labs.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobile_labs.R

data class Princess(
    val name: String,
    val info: String,
    val imageRes: Int
)

@Composable
fun HomeScreen() {

    val princesses = listOf(
        Princess("Белоснежка", "Самая добрая и милая принцесса", R.drawable.belosneshka),
        Princess("Золушка", "Любит танцевать на балу", R.drawable.zolushka),
        Princess("Аврора", "Спящая красавица", R.drawable.avrora),
        Princess("Белоснежка", "Самая добрая и милая принцесса", R.drawable.belosneshka),
        Princess("Золушка", "Любит танцевать на балу", R.drawable.zolushka),
        Princess("Аврора", "Спящая красавица", R.drawable.avrora)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Fon(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopBar(title = "Принцесски")

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(princesses) { princess ->
                    PrincessCard(princess = princess)
                }
            }
        }
    }
}