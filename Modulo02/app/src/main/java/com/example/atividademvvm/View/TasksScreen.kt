package com.example.atividademvvm.View

// TasksScreen.kt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atividademvvm.ViewModel.TasksViewModel
import com.example.atividademvvm.UiState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme

@Composable
fun TasksScreen(tasksViewModel: TasksViewModel = viewModel()) {
    // Coleta o estado do ViewModel. O Composable será recomposto sempre que o estado mudar.
    val uiState by tasksViewModel.uiState.collectAsState()

    // O `when` garante que todos os estados da sealed class sejam tratados.
    when (val state = uiState) {
        is UiState.Loading -> {
            LoadingComponent()
        }
        is UiState.Success -> {
            TasksListComponent(tasks = state.data,
                onRefreshClick = { tasksViewModel.refreshTasks() },
                onRemoveTask = { task -> tasksViewModel.removeTask(task) })
        }
        is UiState.Error -> {
            ErrorComponent(message = state.message) {
                // Envia um evento para o ViewModel quando o botão é clicado
                tasksViewModel.refreshTasks()
            }
        }
    }
}

@Composable
fun LoadingComponent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun TasksListComponent(tasks: List<String>, onRefreshClick: () -> Unit, onRemoveTask: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Minhas Tarefas", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.weight(1f)){
            items(tasks) { task ->
            TaskItem(task = task, onRemoveClick = { onRemoveTask(task) }) // Corrigido aqui
            Divider()
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onRefreshClick) {
            Text("Recarregar")
        }
    }
}

@Composable
fun TaskItem(task: String, onRemoveClick: () -> Unit){
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(task, modifier = Modifier.weight(1f))
        IconButton(onClick = onRemoveClick) {
            Icon(Icons.Filled.Close, contentDescription = "Remover Tarefa")
        }
    }
}



@Composable fun ErrorComponent(message: String, onRetryClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ocorreu um erro:", color = MaterialTheme.colorScheme.error)
        Text(message, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetryClick) {
            Text("Tentar Novamente")
        }
    }
}
