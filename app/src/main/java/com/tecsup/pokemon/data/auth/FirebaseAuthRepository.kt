package com.tecsup.pokemon.data.auth

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tecsup.pokemon.domain.model.Rol
import com.tecsup.pokemon.domain.model.Sesion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(private val app: FirebaseApp?) : AuthRepository {
    private val auth get() = app?.let(FirebaseAuth::getInstance)

    override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> {
        val servicio = auth ?: return Result.failure(
            IllegalStateException("Agrega google-services.json para conectar Firebase."),
        )
        var autenticado = false
        return try {
            val usuario = servicio.signInWithEmailAndPassword(correo, clave).await().user
                ?: error("Firebase no devolvió una sesión.")
            autenticado = true
            Result.success(leerSesion(usuario))
        } catch (e: CancellationException) {
            if (autenticado) servicio.signOut()
            throw e
        } catch (e: Exception) {
            if (autenticado) servicio.signOut()
            Result.failure(errorParaUsuario(e))
        }
    }

    override suspend fun sesionActual(): Sesion? = try {
        auth?.currentUser?.let { leerSesion(it) }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        throw errorParaUsuario(e)
    }

    override fun cerrarSesion() {
        auth?.signOut()
    }

    private suspend fun leerSesion(usuario: FirebaseUser): Sesion {
        val documento = FirebaseFirestore.getInstance(checkNotNull(app))
            .collection("usuarios")
            .document(usuario.uid)
            .get()
            .await()
        val rol = when (documento.getString("rol")) {
            "admin" -> Rol.ADMIN
            "usuario" -> Rol.USUARIO
            else -> error("Tu cuenta no tiene un rol válido en Firestore.")
        }
        return Sesion(usuario.uid, usuario.email.orEmpty(), rol)
    }

    private fun errorParaUsuario(error: Exception): IllegalStateException = when (error) {
        is FirebaseNetworkException -> IllegalStateException("No se pudo conectar con Firebase.", error)
        is FirebaseAuthException -> IllegalStateException("Revisa tu correo y contraseña.", error)
        is IllegalStateException -> error
        else -> IllegalStateException("No se pudo comprobar el rol de tu cuenta.", error)
    }
}
