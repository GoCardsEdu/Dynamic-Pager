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

import pl.gocards.dynamic_pager.DynamicPagerViewModel

data class PagerCommandHandler(
    val onClickAddGoPrevious: (page: Int) -> Unit,
    val onClickAddGoNext: (page: Int) -> Unit,
    val onClickDeleteGoPrevious: (page: Int, item: SampleItem) -> Unit,
    val onClickDeleteGoNext: (page: Int, item: SampleItem) -> Unit,
)


object PagerCommandHandlerFactory {

    fun getInstance(
        viewModel: DynamicPagerViewModel<SampleItem>
    ): PagerCommandHandler {
        var nextId = viewModel.getItems().size

        return PagerCommandHandler(
            onClickAddGoPrevious = { page ->
                viewModel.insertPageBefore(
                    page,
                    SampleItem("Previous Item ${++nextId}")
                )
            },
            onClickAddGoNext = { page ->
                viewModel.insertPageAfter(
                    page,
                    SampleItem("Next Item ${++nextId}")
                )
            },
            onClickDeleteGoPrevious = { page, item ->
                viewModel.deleteAndSlideToPrevious(page, item)
            },
            onClickDeleteGoNext = { page, item ->
                viewModel.deleteAndSlideToNext(page, item)
            }
        )
    }
}