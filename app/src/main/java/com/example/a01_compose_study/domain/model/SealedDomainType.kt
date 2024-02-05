package com.example.a01_compose_study.domain.model

sealed class SealedDomainType(val text: String) {
    object None : SealedDomainType("None")
    object Ptt : SealedDomainType("Ptt")
    object Announce : SealedDomainType("Announce")
    object MainMenu : SealedDomainType("MainMenu")
    object Call : SealedDomainType("Call")
    object Navigation : SealedDomainType("Navigation")
    object Radio : SealedDomainType("Radio")
    object Weather : SealedDomainType("Weather")
    object SendMessage : SealedDomainType("SendMessage")
    object Help : SealedDomainType("Help")
}