package com.example.safelife.repository

import android.util.Log
import com.example.safelife.model.Profissional
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repositório responsável por buscar profissionais no Firestore
 * da coleção "usuarios" onde tipoConta = "profissional".
 */
class ProfissionalRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getProfissionais(): List<Profissional> {
        return try {
            val querySnapshot = db.collection("usuarios")
                .whereEqualTo("userType", "profissional")
                .get()
                .await()

            Log.d("ProfissionalRepo", "Documentos encontrados: ${querySnapshot.size()}")

            querySnapshot.documents.mapNotNull { doc ->
                Log.d("ProfissionalRepo", "Doc: ${doc.data}")
                doc.toObject(Profissional::class.java)
            }
        } catch (e: Exception) {
            Log.e("ProfissionalRepo", "Erro ao buscar profissionais: ${e.message}")
            emptyList()
        }
    }

}
