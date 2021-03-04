package uz.uzdroid.weatherappwithapi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "info_table")
data class Info(

    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    @ColumnInfo(name = "speed")
    val speed: String,
    @ColumnInfo(name = "temp")
    val temp: String,
    @ColumnInfo(name = "humidity")
    val humidity: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "icon")
    val icon: String
)