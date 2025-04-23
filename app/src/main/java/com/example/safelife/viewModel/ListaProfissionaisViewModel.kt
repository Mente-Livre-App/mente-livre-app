package com.example.safelife.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelife.model.Profissional
import com.example.safelife.repository.ProfissionalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsável por buscar e expor a lista de profissionais disponíveis.
 */
class ListaProfissionaisViewModel : ViewModel() {

    private val repository = ProfissionalRepository()

    private val _profissionais = MutableStateFlow<List<Profissional>>(emptyList())
    val profissionais: StateFlow<List<Profissional>> = _profissionais

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        carregarProfissionais()
    }

    private fun carregarProfissionais() {
        viewModelScope.launch {
            _isLoading.value = true
            _profissionais.value = repository.getProfissionais()
            _isLoading.value = false
            Log.d("ListaVM", "Profissionais carregados: ${_profissionais.value.size}")
        }
    }
}
