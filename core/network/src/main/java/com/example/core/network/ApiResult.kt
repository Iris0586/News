package com.example.core.network

import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

/**
 * 网络调用的统一结果封装。
 *
 * 只负责「传输层」错误（超时、5xx、解析失败）；
 * 业务层错误（响应体里的 errorCode / code）仍由各 feature 自己判断，
 * 因为不同后端的响应壳格式并不一致，强行统一 DTO 反而要写适配器。
 */
sealed interface ApiResult<out T> {

    data class Success<T>(val data: T) : ApiResult<T>

    data class Failure(
        val code: Int,
        val message: String,
        val cause: Throwable? = null
    ) : ApiResult<Nothing>
}

/**
 * 把一次网络调用包成 [ApiResult]，避免每个 ViewModel 各写一遍 try/catch。
 *
 * 注意：[CancellationException] 必须重新抛出，否则协程取消会被当成「失败」吞掉，
 * 导致已取消的请求继续走后续逻辑。
 */
suspend fun <T> safeApiCall(block: suspend () -> T): ApiResult<T> = try {
    ApiResult.Success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: IOException) {
    ApiResult.Failure(code = -1, message = "网络异常，请检查连接", cause = e)
} catch (e: HttpException) {
    ApiResult.Failure(code = e.code(), message = "服务异常（${e.code()}）", cause = e)
} catch (e: Exception) {
    ApiResult.Failure(code = -2, message = "数据解析失败", cause = e)
}
