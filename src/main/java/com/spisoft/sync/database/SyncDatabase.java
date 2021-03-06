package com.spisoft.sync.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.spisoft.sync.account.DBAccountHelper;
import com.spisoft.sync.wrappers.Wrapper;
import com.spisoft.sync.wrappers.WrapperFactory;


/**
 * Created by alexandre on 27/04/16.
 */
public class SyncDatabase {
    public static final String DATABASE_NAME = "DBAccount";
    public static final int DATABASE_VERSION = 3;
    public static SyncDatabase sSyncDatabase =null;
    private final Context mContext;
    public static Object lock = new Object();
    private DatabaseHelper mDatabaseHelper;

    public SyncDatabase(Context context) {
        mContext = context;
    }

    public SQLiteDatabase open(){
        if(mDatabaseHelper == null)
            mDatabaseHelper = new DatabaseHelper(mContext);
        return mDatabaseHelper.getWritableDatabase();
    }

    public void close(){
        mDatabaseHelper.close();
    }
    public static SyncDatabase getInstance(Context context){
        if(sSyncDatabase ==null)
            sSyncDatabase = new SyncDatabase(context);
        return sSyncDatabase;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, SyncDatabase.DATABASE_NAME, null, SyncDatabase.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // This method is only called once when the database is created for the first time
            db.execSQL(DBAccountHelper.CREATE_DATABASE);
            db.execSQL(SyncedFolderDBHelper.CREATE_DATABASE);
            for(Wrapper wrapper : WrapperFactory.getWrapperList(mContext)){
                wrapper.initDB(db);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            for(Wrapper wrapper : WrapperFactory.getWrapperList(mContext)){
                wrapper.updateDB(db, oldVersion, newVersion);
            }
            SyncedFolderDBHelper.onUpgrade(db, oldVersion, newVersion);
        }
    }
}
