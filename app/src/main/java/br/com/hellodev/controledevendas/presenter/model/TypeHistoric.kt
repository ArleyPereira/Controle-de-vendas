package br.com.hellodev.controledevendas.presenter.model

sealed class TypeHistoric {
    object Today: TypeHistoric()
    object Week: TypeHistoric()
    object Month: TypeHistoric()
    object All: TypeHistoric()
}