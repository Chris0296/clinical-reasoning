package com.alphora.cql.service.provider;

import org.opencds.cqf.cql.terminology.TerminologyProvider;
import java.nio.file.Path;

public interface JsonDataProvider {

    Path path = null; // path to root patient data directory
    TerminologyProvider terminologyProvider = null;
    Object deserialize(String resource);
    boolean checkCodeMembership(Object codeObj, String vsId);

}
