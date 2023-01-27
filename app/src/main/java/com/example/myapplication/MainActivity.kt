package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Consumer 1 of Flow Producer
        GlobalScope.launch(Dispatchers.Main) {
            produce()
                .buffer(3)
                .collect {
                    Log.d("Collecting", it.toString())
                }

        }
        // Consumer 2 of Flow Producer
        GlobalScope.launch(Dispatchers.Main) {
            produce()
                .collect {
                    Log.d("Collecting 2", it.toString())
                }
            delay(2000)
        }
        // Consumer 1 of SharedFlow Producer
        GlobalScope.launch(Dispatchers.Main) {
            produce2()
                .collect {
                    Log.d("Collecting Shared 1: ", it.toString())
                }
        }
        // Consumer 2 of SharedFlow Producer
        GlobalScope.launch(Dispatchers.Main) {
            produce2()
                .collect {
                    Log.d("Collect Sharedflow 2: ", it.toString())
                }
            delay(3000)
        }
        // Consumer 1 of StateFlow Producer
        GlobalScope.launch(Dispatchers.Main) {
            produce3()
                .collect {
                    Log.d("Collect Stateflow 1: ", it.toString())

                }

        }
    }

    //Kotlin Flow Producer
    private fun produce(): Flow<Int> {
        return flow {
            (1..10).forEach {
                emit(it)
            }

        }
            .map {
                it * 10
            }
            .filter {
                it < 80
            }
            .flowOn(Dispatchers.IO)//producer will run on IO thread
    }

    // Shared Flow Producer
    private fun produce2(): Flow<Int> {
        val mutableSharedFlow = MutableSharedFlow<Int>(3)//will replay last 3 emitted values
        GlobalScope.launch {

            (1..10).forEach {
                mutableSharedFlow.emit(it)
                delay(1000)
            }
        }

        return mutableSharedFlow
    }

    //Stateflow producer
    private fun produce3(): StateFlow<Int> {
        val mutableStateFlow = MutableStateFlow(value = 10)//set initial value to 10
        GlobalScope.launch {
            (1..10).forEach {
                mutableStateFlow.emit(it)
                delay(3000)
            }
        }

        return mutableStateFlow
    }
}