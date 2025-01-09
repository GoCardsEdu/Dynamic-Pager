/*
 * MIT License
 *
 * Copyright (c) 2025 GoCards Grzegorz Ziemski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * Source: https://github.com/GoCardsEdu/Dynamic-Pager
 * @author Grzegorz Ziemski
 * @version 1.0
 */

package pl.gocards.dynamic_pager.sample_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.launch
import pl.gocards.dynamic_pager.DynamicPagerStateBinder
import pl.gocards.dynamic_pager.DynamicPagerUIMediator
import pl.gocards.dynamic_pager.DynamicPagerViewModel
import pl.gocards.dynamic_pager.sample_app.ui.theme.SampleDynamicPagerTheme

/**
 * The sample app demonstrates how to use the DynamicPager.
 *
 * @author Grzegorz Ziemski
 * @version 1.0
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize a DynamicPagerViewModel or extend it to include your custom features.
        val viewModel: DynamicPagerViewModel<SampleItem> by viewModels()
        if (viewModel.getItems().isEmpty()) {
            this.lifecycle.coroutineScope.launch {
                viewModel.setItems(List(3) { index -> SampleItem("Sample Item ${index + 1}") })
            }
        }

        val commands = PagerCommandHandlerFactory.getInstance(viewModel)

        setContent {
            val pagerMediator = DynamicPagerUIMediator.create(viewModel)
            MainScreen(pagerMediator, commands)
        }
    }
}

@Composable
private fun MainScreen(
    pagerMediator: DynamicPagerUIMediator<SampleItem>,
    commands: PagerCommandHandler
) {
    val items = pagerMediator.items.value ?: emptyList()

    SampleDynamicPagerTheme {

        // 2. Wrap your Pager with DynamicPagerStateBinder to manage Pager state seamlessly.
        DynamicPagerStateBinder(pagerMediator) { pagerState ->
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = { MainTopBar() }
            ) { innerPadding ->
                Column(
                    Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    val page = pagerState.currentPage
                    val item = items.getOrNull(page)
                    if (item != null) {

                        // 3. Use any Jetpack Compose Pager (e.g., HorizontalPager) as usual.
                        HorizontalPager(
                            state = pagerState,
                            // Disable user scrolling while the page is being updated through ViewModel methods.
                            userScrollEnabled = pagerMediator.scrollByUserEnabled.value
                        ) { page ->
                            PagerCard {
                                Text(
                                    text = item.text,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    ActionButtons(page, item, commands)
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainTopBar() {
    TopAppBar(
        title = {
            Text(text = "Dynamic Pager")
        }
    )
}

@Composable
private fun PagerCard(
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
private fun ActionButtons(
    page: Int,
    item: SampleItem?,
    input: PagerCommandHandler
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        Column(
            modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 0.dp),
            horizontalAlignment = Alignment.End
        ) {
            PagerActionButton(
                "Add & Go Previous",
                onClick = { input.onClickAddGoPrevious(page) }
            )
            item?.let {
                PagerActionButton(
                    "Delete & Go Previous",
                    onClick = { input.onClickDeleteGoPrevious(page, item) }
                )
            }
        }
        Column(
            modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp),
        ) {
            PagerActionButton(
                "Add & Go Next",
                onClick = { input.onClickAddGoNext(page) }
            )
            item?.let {
                PagerActionButton(
                    "Delete & Go Next",
                    onClick = { input.onClickDeleteGoNext(page, item) }
                )
            }
        }
    }
}

@Composable
private fun PagerActionButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .width(180.dp)
            .padding(0.dp, 8.dp),
        onClick = onClick
    ) {
        Text(text, Modifier.padding(0.dp, 10.dp))
    }
}