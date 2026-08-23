package com.quiz.famousquotequizbackend.domain.quote

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "quotes")
class Quote(
    @Column(nullable = false, unique = true, length = 1000)
    var text: String,

    @Column(nullable = false)
    var author: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
)
