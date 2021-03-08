package Service;

import pojo.Person;

import java.util.ArrayList;
import java.util.List;

public class CustomUtils {

    public static List<Person> createDummyList(){
        List<Person> persons = new ArrayList<>();

        for (int i=0; i<5; i++){
            Person person = new Person("Person_"+i, i);
            persons.add(person);
        }

        return persons;
    }
}
