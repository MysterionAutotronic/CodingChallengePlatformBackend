package de.amplimind.codingchallenge.utils

import de.amplimind.codingchallenge.exceptions.RetryMethodException
import de.amplimind.codingchallenge.exceptions.SubmissionException
import de.amplimind.codingchallenge.submission.PushFileResponse
import kotlinx.coroutines.delay
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Utility class for api requests.
 */
object ApiRequestUtils {

    /**
     * Retries the passed method until success or the max retries are reached.
     * @return the return value of the passed method
     */
    suspend fun <T : Any> retry(
        retries: Int,
        method: suspend () -> Response<T>
    ): Response<T> {
        repeat(retries - 1) {
            val t: Response<T> = method()
            if (t.isSuccessful) {
                return t
            } else {
                println("retrying request")
                delay(500)
            }
        }
        throw RetryMethodException("Method failed after $retries retries.")
    }
}
