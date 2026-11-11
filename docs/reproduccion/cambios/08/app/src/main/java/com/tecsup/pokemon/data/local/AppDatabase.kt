package com.tecsup.pokemon.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ItemEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun items(): ItemDao

    companion object {
        internal val Migracion1a2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE items ADD COLUMN perfilJson TEXT NOT NULL DEFAULT ''")
            }
        }

        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase = instancia ?: synchronized(this) {
            instancia ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "pokedex-planta.db",
            ).addMigrations(Migracion1a2).build().also { instancia = it }
        }
    }
}
