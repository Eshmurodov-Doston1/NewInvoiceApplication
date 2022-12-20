package uz.idea.domain.repositories.pagingRepository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState

private const val DEFAULT_PAGE_SIZE = 10
fun <T : Any> createPager(pageSize: Int = DEFAULT_PAGE_SIZE, enablePlaceholders: Boolean = false,
                          block: suspend (Int) -> List<T>): Pager<Int, T> = Pager(
    config = PagingConfig(enablePlaceholders = enablePlaceholders, pageSize = pageSize),
    pagingSourceFactory = { BasePagingSource(block) })


private const val STARTING_PAGE_INDEX = 1
class BasePagingSource<T : Any>(
    private val block: suspend (Int) -> List<T>
): PagingSource<Int, T>() {
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            val response = block(page)
            LoadResult.Page(
                data = response,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page.minus(1),
                nextKey = if (response.isEmpty()) null else page.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}