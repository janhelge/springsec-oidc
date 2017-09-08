package no.politiet.publikumstjenester.kjoreseddel;

import no.politiet.publikumstjenester.kjoreseddel.domainobject.KjoreseddelSoknadDomainObject;
import no.webtech.objectserializeutil.DomainObjectSerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;

import java.io.File;
import java.io.IOException;


public class KjoreseddelSoknadHandlerImpl implements KjoreseddelSoknadHandler {

    final static String ENV_VARIABLE_DIRECTORY_FOR_SERIALIZED_FILES = "DIRECTORY_FOR_SERIALIZED_FILES";

    private static final Logger log = LoggerFactory.getLogger(KjoreseddelSoknadHandlerImpl.class);
    private File dirForSerializedFiles = null;
    private String prefix;

    public KjoreseddelSoknadHandlerImpl() {
        String directoryName = System.getenv(ENV_VARIABLE_DIRECTORY_FOR_SERIALIZED_FILES);
        if (directoryName == null || directoryName.length() == 0)
            directoryName = "/tmp";
        this.dirForSerializedFiles = new File(directoryName);
        if (!(this.dirForSerializedFiles.exists()
                && this.dirForSerializedFiles.isDirectory()
                && this.dirForSerializedFiles.canWrite())) {
            log.error("FATAL ERROR: Cant create files in " + directoryName + " .");
            throw new BeanCreationException("FATAL ERROR: Cant create files in " + directoryName + " .");
        }
        this.prefix = System.getProperty("serialize.file.prefix", "ks_");
    }

    @Override
    public void handle(KjoreseddelSoknadDomainObject kjoreseddelSoknadDomainObject) {
        log.info("Handle: "+kjoreseddelSoknadDomainObject.toString());
        try {
            File serializeFile = File.createTempFile(prefix, ".ser", dirForSerializedFiles);
            DomainObjectSerializeUtil.dumpToFile(kjoreseddelSoknadDomainObject, serializeFile, kjoreseddelSoknadDomainObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot create serialize-file in directory " + dirForSerializedFiles.getName(), e);
        }
    }
}
