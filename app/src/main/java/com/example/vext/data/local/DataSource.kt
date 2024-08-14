package com.example.vext.data.local

import com.example.vext.data.AppDatabase

abstract class DataSource<T> {
    protected abstract val dataBase: AppDatabase



}