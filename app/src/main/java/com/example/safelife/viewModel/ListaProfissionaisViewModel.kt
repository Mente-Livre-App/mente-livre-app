package com.example.safelife.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelife.model.Profissional
import com.example.safelife.repository.ProfissionalRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsável por buscar e expor a lista de profissionais disponíveis.
 */
class ListaProfissionaisViewModel(
    private val repository: ProfissionalRepository = ProfissionalRepository(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _profissionais = MutableStateFlow<List<Profissional>>(emptyList())
    val profissionais: StateFlow<List<Profissional>> = _profissionais

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    fun carregarProfissionais() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resultado = repository.getProfissionais()
                _profissionais.value = resultado
                Log.d("ListaVM", "Todos profissionais carregados: ${resultado.size}")
            } catch (e: Exception) {
                _profissionais.value = emptyList()
                Log.e("ListaVM", "Erro ao carregar profissionais: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun carregarProfissionaisPorTipo(tipo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .whereEqualTo("userType", tipo.lowercase()) // garante que compara com 'profissional'
                    .get()
                    .addOnSuccessListener { result ->
                        val lista = result.documents.mapNotNull { doc ->
                            val profissional = doc.toObject(Profissional::class.java)
                            android.util.Log.d("ListaVM", "Profissional carregado: ${profissional?.name}")
                            profissional
                        }
                        _profissionais.value = lista
                        _isLoading.value = false
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("ListaVM", "Erro ao buscar profissionais: ${e.message}")
                        _profissionais.value = emptyList()
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                android.util.Log.e("ListaVM", "Erro inesperado: ${e.message}")
                _profissionais.value = emptyList()
                _isLoading.value = false
            }
        }
    }


}

