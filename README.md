# vaha-localdb-sqlite

#### What is it?
An implementation (dependent to [org.xerial.sqlite-jdbc](https://github.com/xerial/sqlite-jdbc)) of [vaha-localdb](https://github.com/vahithanoglu/vaha-localdb) interface library that can be used on desktop platforms (such as Linux, MacOS and Windows).

#### ExampleDBCore.java (an implementation of abstract class [SQLiteDBCore](./src/main/java/com/vahabilisim/localdb/sqlite/SQLiteDBCore.java))
```java
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
    public void onCreate(LocalDBTrans trans) throws LocalDBException {
        // table "car" is from version 1
        trans.execSQL("CREATE TABLE car (id TEXT PRIMARY KEY, vendor TEXT, model TEXT, year TEXT)");

        // table "truck" is from version 2
        trans.execSQL("CREATE TABLE truck (id TEXT PRIMARY KEY, vendor TEXT, model TEXT, year TEXT)");

        // table "bike" is from this version = version 3
        trans.execSQL("CREATE TABLE bike (id TEXT PRIMARY KEY, vendor TEXT, model TEXT, year TEXT)");
    }

    @Override
    public void onUpgrade(LocalDBTrans trans, int oldVersion, int newVersion) throws LocalDBException {
        if (oldVersion < 2) {
            trans.execSQL("CREATE TABLE truck (id TEXT PRIMARY KEY, vendor TEXT, model TEXT, year TEXT)");
        }

        if (oldVersion < 3) {
            trans.execSQL("CREATE TABLE bike (id TEXT PRIMARY KEY, vendor TEXT, model TEXT, year TEXT)");
        }
    }
}
```

#### ExampleUsage.java (a basic example of how to use [vaha-localdb](https://github.com/vahithanoglu/vaha-localdb) and [vaha-localdb-sqlite](https://github.com/vahithanoglu/vaha-localdb-sqlite) together)
```java
package com.vahabilisim.localdb.sqlite.example;

import com.vahabilisim.localdb.LocalDBException;
import com.vahabilisim.localdb.example.Example;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ExampleUsage {

    public static void main(String[] args) throws LocalDBException {

        final File appDir = new File(System.getProperty("user.home"), "vaha-localdb-example-app");
        appDir.mkdirs();

        final ExampleDBCore core = new ExampleDBCore(appDir);
        final Example db = new Example(core);

        long id = 1L;
        final Example.Car car = new Example.Car(id++, "Audi", "A3", 2017);

        final List<Example.Car> cars = new LinkedList<>();
        cars.add(new Example.Car(id++, "Audi", "Q7", 2018));
        cars.add(new Example.Car(id++, "Toyota", "Yaris", 2017));
        cars.add(new Example.Car(id++, "Renault", "Captur", 2019));
        cars.add(new Example.Car(id++, "Renault", "Kadjar", 2019));

        System.out.println("Insert: " + db.insert(car));
        System.out.println("Insert2: " + db.insert2(cars));

        for (Example.Car c : db.queryAll()) {
            System.out.println(c.id + ", " + c.vendor + ", " + c.model + ", " + c.year);
        }
        System.out.println("*********************************************");

        car.year = 2019;
        System.out.println("Update: " + db.update(car));
        System.out.println("Update2: " + db.update2(2019, 2020));

        for (Example.Car c : db.queryAll()) {
            System.out.println(c.id + ", " + c.vendor + ", " + c.model + ", " + c.year);
        }
        System.out.println("*********************************************");

        System.out.println("Delete: " + db.delete(cars.get(0)));
        System.out.println("Delete2: " + db.delete2("Renault", "Kadjar"));

        for (Example.Car c : db.queryAll()) {
            System.out.println(c.id + ", " + c.vendor + ", " + c.model + ", " + c.year);
        }
        System.out.println("*********************************************");

    }
}
```
