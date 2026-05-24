package com.iamamin.tandemcommunity.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.iamamin.tandemcommunity.data.mapper.toCommunityUser
import com.iamamin.tandemcommunity.data.remote.CommunityApi
import com.iamamin.tandemcommunity.domain.model.CommunityUser
import com.iamamin.tandemcommunity.domain.model.error.CommunityError
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class CommunityPagingSource(
    private val api: CommunityApi
) : PagingSource<Int, CommunityUser>() {

    override fun getRefreshKey(state: PagingState<Int, CommunityUser>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommunityUser> {
        val page = params.key ?: 1

        return try {
            val response = api.getCommunity(page)
            val users = response.response

            LoadResult.Page(
                data = users.map { it.toCommunityUser() },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (users.size < 20) null else page + 1
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            LoadResult.Error(CommunityError.HttpError(e.code()))
        } catch (_: SocketTimeoutException) {
            LoadResult.Error(CommunityError.Timeout)
        } catch (_: IOException) {
            LoadResult.Error(CommunityError.NoConnectivity)
        } catch (e: Exception) {
            LoadResult.Error(CommunityError.Unknown(e))
        }
    }
}
