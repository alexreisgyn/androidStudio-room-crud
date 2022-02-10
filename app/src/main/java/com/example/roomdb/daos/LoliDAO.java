package com.example.roomdb.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.roomdb.models.Loli;

import java.util.List;

@Dao
public interface LoliDAO {
    @Query("SELECT * FROM loli")
    List<Loli> listLolis();

    @Query("SELECT * FROM loli WHERE id = :loliId")
    Loli getLoli(int loliId);

    @Insert
    void insertLoli(Loli loli);

    @Delete
    void deleteLoli(Loli loli);

    @Update
    void updateLoli(Loli loli);

    @Query("SELECT * FROM loli WHERE loliName = :loliName")
    List<Loli> getLoliByName(String loliName);

    @Query("SELECT * FROM loli WHERE loliName Like '%' || :loliName || '%'")
    List<Loli> searchLoliByName(String loliName);
}
