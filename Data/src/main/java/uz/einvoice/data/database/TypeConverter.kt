package uz.einvoice.data.database

import androidx.room.ProvidedTypeConverter
import com.google.gson.Gson
import javax.inject.Singleton

@Singleton
@ProvidedTypeConverter
object TypeConverter {

  private var gson: Gson = Gson()

//  @TypeConverter
//  fun convertMenu(info: InfoEntity): String {
//    return gson.toJson(info)
//  }
//
//  @TypeConverter
//  fun getConvertMenuInfo(str: String): InfoEntity {
//    return gson.fromJson(str, object : TypeToken<InfoEntity>() {}.type)
//  }
//
//  @TypeConverter
//  fun convertToGsonItemEntityList(list: List<ItemEntity>): String {
//    return gson.toJson(list)
//  }
//
//  @TypeConverter
//  fun convertToItemEntityList(str: String): List<ItemEntity> {
//    return gson.fromJson(str, object : TypeToken<List<ItemEntity>>() {}.type)
//  }
//
//  @TypeConverter
//  fun convertToUserObject(str: String): UserEntity {
//    return gson.fromJson(str, object : TypeToken<UserEntity>() {}.type)
//  }
//
//  @TypeConverter
//  fun convertToGsonObject(str: UserEntity): String {
//    return gson.toJson(str)
//  }
}