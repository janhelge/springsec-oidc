package no.politiet.publikumstjenester.kjoreseddel;

import no.politiet.publikumstjenester.kjoreseddel.domainobject.KjoreseddelSoknadDomainObject;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateExampleDomainObject {

    public static void main(String[] a) {
        System.out.println(createExampleDomainObject());
    }

    public static KjoreseddelSoknadDomainObject createExampleDomainObject() {
        KjoreseddelSoknadDomainObject e = new KjoreseddelSoknadDomainObject();

        e.put("Ex1", "val1");
        e.put("Ex2", "val2");
        e.put("created",  LocalDateTime.now());
        e.put("vedl",(new String("Attachment 1. ABC..XYZ").getBytes()));
        e.put("vedl1",null);

        return e;
    }
}
