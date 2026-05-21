package com.iamamin.tandemcommunity.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.iamamin.tandemcommunity.data.remote.CommunityApi
import com.iamamin.tandemcommunity.data.remote.model.UserDto

class CommunityPagingSource(
    private val api: CommunityApi
) : PagingSource<Int, UserDto>() {

    override fun getRefreshKey(state: PagingState<Int, UserDto>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserDto> {
        val page = params.key ?: 1

        return try {
            val response = api.getCommunity(page)
            val users = response.response

            LoadResult.Page(
                data = users,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (users.size < 20) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}