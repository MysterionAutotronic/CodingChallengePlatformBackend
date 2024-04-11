package de.amplimind.codingchallenge.service

import de.amplimind.codingchallenge.dto.UserInfoDTO
import de.amplimind.codingchallenge.dto.UserStatus
import de.amplimind.codingchallenge.exceptions.ResourceNotFoundException
import de.amplimind.codingchallenge.extensions.EnumExtensions.matchesAny
import de.amplimind.codingchallenge.model.Submission
import de.amplimind.codingchallenge.model.SubmissionStates
import de.amplimind.codingchallenge.model.User
import de.amplimind.codingchallenge.model.UserRole
import de.amplimind.codingchallenge.repository.SubmissionRepository
import de.amplimind.codingchallenge.repository.UserRepository
import org.springframework.stereotype.Service
import kotlin.jvm.Throws

/**
 * Service for managing users.
 */
@Service
class UserService(
    private val userRepository: UserRepository,
    private val submissionRepository: SubmissionRepository,
) {
    /**
     * Fetches all user infos [UserInfoDTO]
     */
    fun fetchAllUserInfos(): List<UserInfoDTO> {
        return this.userRepository.findAll().map {
            UserInfoDTO(
                email = it.email,
                role = it.role,
                status = extractUserStatus(it),
            )
        }
    }

    @Throws(ResourceNotFoundException::class)
    fun fetchUserInfosForEmail(email: String): UserInfoDTO {
        return this.userRepository.findByEmail(email)?.let { user ->
            return UserInfoDTO(
                email = user.email,
                role = user.role,
                status = extractUserStatus(user),
            )
        } ?: throw ResourceNotFoundException("User with email $email was not found")
    }

    /**
     * Extracts the [UserStatus] for a provided [User]
     * @param user the user to extract the status from
     * @return the [UserStatus]
     */
    private fun extractUserStatus(user: User): UserStatus {
        if (user.role.matchesAny(UserRole.ADMIN)) {
            // An admin should not submit anything
            return UserStatus.REGISTERED
        }

        // The user should have a submission if its not the admin

        val submission =
            this.submissionRepository.findByUserEmail(user.email)
                ?: throw IllegalStateException("User has no submission but is not in init state")

        if (user.role.matchesAny(UserRole.INIT)) {
            return UserStatus.UNREGISTERED
        }

        if (hasUserCompletedSubmission(submission)) {
            return UserStatus.SUBMITTED
        }

        if (isUserAlreadyImplementing(submission)) {
            return UserStatus.IMPLEMENTING
        }

        if (hasUserStartedImplementing(submission).not() || isUserRegistered(user))
            {
                // User did not start implementing the submission
                return UserStatus.REGISTERED
            }

        // This should never happen
        throw IllegalStateException("The userstatus does not match any criteria")
    }

    /**
     * Checks if the user has completed the submission.
     * @param submission the submission to check
     * @return true if the user has completed the submission
     */
    private fun hasUserCompletedSubmission(submission: Submission): Boolean {
        return submission.status.matchesAny(SubmissionStates.REVIEWED, SubmissionStates.IN_REVIEW, SubmissionStates.SUBMITTED)
    }

    /**
     * Checks if the user is already implementing the submission.
     * @param submission the submission to check
     * @return true if the user is already implementing the submission
     */
    private fun isUserAlreadyImplementing(submission: Submission): Boolean {
        return submission.status.matchesAny(SubmissionStates.IN_IMPLEMENTATION)
    }

    /**
     * Checks if the user has started implementing the submission.
     * @param submission the submission to check
     * @return true if the user has started implementing the submission
     */
    private fun hasUserStartedImplementing(submission: Submission): Boolean {
        return submission.status.matchesAny(SubmissionStates.INIT).not()
    }

    /**
     * Checks if the user is registered (not init anymore)
     * @param user the user to check
     * @return true if the user is registered
     */
    private fun isUserRegistered(user: User): Boolean {
        return user.role.matchesAny(UserRole.USER, UserRole.ADMIN)
    }
}
