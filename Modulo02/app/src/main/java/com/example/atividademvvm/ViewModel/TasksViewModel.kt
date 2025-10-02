package com.example.atividademvvm.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atividademvvm.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TasksViewModel : ViewModel() {

    // O StateFlow privado que pode ser modificado internamente.
    private val _uiState = MutableStateFlow<UiState<List<String>>>(UiState.Loading)

    // O StateFlow público e somente leitura que a UI irá observar.
    val uiState = _uiState.asStateFlow()

    init {
        // Inicia a busca de dados quando o ViewModel é criado.
        fetchTasks()
    }

    // Evento que a UI pode chamar para recarregar os dados.
    fun refreshTasks() {
        fetchTasks()
    }

    fun removeTask(taskToRemove: String) {
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            // Cria uma nova lista sem a tarefa a ser removida
            val updatedTasks = currentState.data.filter { it != taskToRemove }
            _uiState.value = UiState.Success(updatedTasks)
        }
    }

    private fun fetchTasks() {
        viewModelScope.launch {
            // 1. Define o estado como Loading
            _uiState.value = UiState.Loading
            try {
                // Simula uma chamada de rede com um delay
                delay(2000)

                // Simula um erro aleatório
                if (System.currentTimeMillis() % 2 == 0L) {
                    throw Exception("Falha ao conectar com o servidor.")
                }

                val tasks = listOf("Comprar pão", "Estudar MVVM", "Passear com o cachorro", "Caçar vaca")

                // 2. Em caso de sucesso, atualiza o estado com os dados
                _uiState.value = UiState.Success(tasks)

            } catch (e: Exception) {
                // 3. Em caso de erro, atualiza o estado com a mensagem
                _uiState.value = UiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }
}
