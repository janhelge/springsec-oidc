package no.politiet.publikumstjenester.kjoreseddel;

import no.politiet.publikumstjenester.kjoreseddel.domainobject.KjoreseddelSoknadDomainObject;
import no.webtech.objectserializeutil.DomainObjectSerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;

public class SerializeDeSerializeMyObject {

    private static Logger logger = LoggerFactory.getLogger(SerializeDeSerializeMyObject.class);
    public static void main(String[] a) {
        KjoreseddelSoknadDomainObject e = CreateExampleDomainObject.createExampleDomainObject();
        DomainObjectSerializeUtil.dumpToFile(e,new File("/tmp/tstf.ser"),"comment");
        Serializable serializable = DomainObjectSerializeUtil.loadFromFile("/tmp/tstf.ser");
        System.out.println(serializable.toString());
    }
}
