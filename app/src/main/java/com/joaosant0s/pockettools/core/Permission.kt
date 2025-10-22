package com.joaosant0s.pockettools.core

interface Permission {

    fun requestPermission()

    fun permissionNameId() : Int

    fun permissionIconId() : Int

}