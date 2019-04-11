package com.example.rober.gallery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import static android.R.attr.id;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "FoodDb.db";
    public static final String TABLE_NAME = "FOOD";
    public static final String COL_1 = "Id";
    public static final String COL_2 = "name";
    public static final String COL_3 = "price";
    public static final String COL_4 = "image";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (Id INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR,price VARCHAR,image BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String name,String price,byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,price);
        contentValues.put(COL_4,image);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    public Cursor getCol() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select Id from "+TABLE_NAME,null);
        return res;
    }


    public Cursor getByID(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM catalog_db WHERE Id="+id,null);
        return res;
    }

    public void updateData(String name,String price,byte[] image,int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="UPDATE FOOD SET name=?, price=?, image=? WHERE Id=?";
        SQLiteStatement statement=db.compileStatement(sql);
        statement.bindString(1,name);
        statement.bindString(2,price);
        statement.bindBlob(3,image);
        statement.bindDouble(4,(double) id);

        statement.execute();
        db.close();

    }

    public void deleteData (int id) {
        SQLiteDatabase db = this.getWritableDatabase();
       String sql="DELETE FROM FOOD WHERE Id=?";
        SQLiteStatement statement=db.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1,(double)id);
        statement.execute();
        db.close();
    }

    public boolean empty () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        return true;
    }
}

