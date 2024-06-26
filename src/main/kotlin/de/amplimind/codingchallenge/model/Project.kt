package de.amplimind.codingchallenge.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table
import org.springframework.data.annotation.Version

/**
 * Represents a project.
 * A project is a collection of tasks which a user has to fulfill.
 */
@Entity
@Table(name = "projects")
class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var title: String,
    @Lob
    val description: String,
    var active: Boolean,
    @Version
    var version: Long? = null,
)
