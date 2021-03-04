package uz.uzdroid.weatherappwithapi.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import uz.uzdroid.weatherappwithapi.model.Info


@Dao
interface InfoDao {

    @Query("SELECT * FROM info_table")
    fun queryAllInfo(): LiveData<Info>

    @Insert
    fun insertInfo(info: Info)

}