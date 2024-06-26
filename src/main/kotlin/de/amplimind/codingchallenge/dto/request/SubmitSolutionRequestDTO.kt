package de.amplimind.codingchallenge.dto.request

import org.springframework.web.multipart.MultipartFile

/**
 * Data Transfer Object for submitting a Solution
 * @param description the description the user sent
 * @param language the programming language of the submitted code
 * @param version the version of the programming language
 * @param zipFileContent the code the user wants to submit
 */
data class SubmitSolutionRequestDTO(
    val description: String? = null,
    val language: String,
    val version: String,
    val zipFileContent: MultipartFile,
)
