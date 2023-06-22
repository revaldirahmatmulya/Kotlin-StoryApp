package com.revaldi.storyapp.UI

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.revaldi.storyapp.Adapter.StoryAdapter
import com.revaldi.storyapp.Data.StoryRepository
import com.revaldi.storyapp.Models.StoriesData
import com.revaldi.storyapp.Utils.CoroutinesTestRule
import com.revaldi.storyapp.Utils.DataDummy
import com.revaldi.storyapp.Utils.PagedTestDataSource
import com.revaldi.storyapp.Utils.getOrAwaitValue
import com.revaldi.storyapp.ViewModel.MainViewModel
import junit.framework.TestCase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


@ExperimentalCoroutinesApi
class DispatcherRule : TestWatcher() {

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = DispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Story List Should Not Null and Return Success`() = runTest {
        val dummyStory = DataDummy.generateDummyListStory()
        val data: PagingData<StoriesData> = StoriesPagingSource.snapshot(dummyStory as MutableList<StoriesData>)
        val expectedQuote = MutableLiveData<PagingData<StoriesData>>()
        expectedQuote.value = data

        Mockito.`when`(storyRepository.getStoryRepo()).thenReturn(expectedQuote)
        val mainViewModel = MainViewModel(storyRepository)
        val actualQuote: PagingData<StoriesData> = mainViewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback as ListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory, differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
        Mockito.verify(storyRepository).getStoryRepo()
    }

    @Test
    fun `when Get Story List Should Be Empty and Return Success`() = runTest {
        val dummyStory = mutableListOf<StoriesData>()
        val data: PagingData<StoriesData> = StoriesPagingSource.snapshot(dummyStory)
        val expectedQuote = MutableLiveData<PagingData<StoriesData>>()
        expectedQuote.value = data

        Mockito.`when`(storyRepository.getStoryRepo()).thenReturn(expectedQuote)
        val mainViewModel = MainViewModel(storyRepository)
        val actualQuote: PagingData<StoriesData> = mainViewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory, differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertTrue(differ.snapshot().isEmpty())
        Mockito.verify(storyRepository).getStoryRepo()
    }
}

class StoriesPagingSource : PagingSource<Int, LiveData<List<StoriesData>>>() {
    companion object {
        fun snapshot(items: MutableList<StoriesData>): PagingData<StoriesData> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoriesData>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoriesData>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}