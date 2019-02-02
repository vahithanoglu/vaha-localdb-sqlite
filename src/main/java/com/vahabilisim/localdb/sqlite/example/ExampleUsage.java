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
