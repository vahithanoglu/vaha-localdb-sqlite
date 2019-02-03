package com.vahabilisim.localdb.sqlite.example;

import com.vahabilisim.localdb.LocalDBException;
import com.vahabilisim.localdb.LocalDBTrans;
import com.vahabilisim.localdb.sqlite.SQLiteDBCore;
import java.io.File;

public class ExampleDBCore extends SQLiteDBCore {

    private static final int VERSION = 3;
    private static final int TIMEOUT_IN_SEC = 5;

    public ExampleDBCore(File appDir) throws LocalDBException {
        super(new File(appDir, "exampledb.sqlite").getAbsolutePath(), VERSION, TIMEOUT_IN_SEC);
    }

    @Override
    public void onCreate(LocalDBTrans trans) {
        // table "car" is from version 1
        trans.execSQL("CREATE TABLE car (id TEXT PRIMARY KEY, vendor TEXT, model TEXT, year TEXT)");

        // table "truck" is from version 2
        trans.execSQL("CREATE TABLE truck (id TEXT PRIMARY KEY, vendor TEXT, model TEXT, year TEXT)");

        // table "bike" is from this version = version 3
        trans.execSQL("CREATE TABLE bike (id TEXT PRIMARY KEY, vendor TEXT, model TEXT, year TEXT)");
    }

    @Override
    public void onUpgrade(LocalDBTrans trans, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            trans.execSQL("CREATE TABLE truck (id TEXT PRIMARY KEY, vendor TEXT, model TEXT, year TEXT)");
        }

        if (oldVersion < 3) {
            trans.execSQL("CREATE TABLE bike (id TEXT PRIMARY KEY, vendor TEXT, model TEXT, year TEXT)");
        }
    }
}
