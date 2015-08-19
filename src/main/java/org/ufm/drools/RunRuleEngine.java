package org.ufm.drools;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.*;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by edydkim on 2015/08.
 */
public class RunRuleEngine {
    private static final Logger logger = LoggerFactory.getLogger(RunRuleEngine.class);

    private static KnowledgeBase kbase;

    public static void setup() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("basic.drl"), ResourceType.DRL);
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error: errors) {
                logger.error("KnowledgeBuilderError: ", error);
            }
            throw new IllegalArgumentException("Could not parse knowledge.");
        }
        kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
    }

    public static void main(String[] args) {
        setup();

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        // NOTE: <- KnowledgeRuntimeLogger krLogger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "basic");

        BasicVO vo = new BasicVO();
        vo.setStringValue("first");
        vo.setBooleanValue(true);
        ksession.insert(vo);
        
        // NOTE: you can add another VO object to same session; filtering to use instanceof VO type.
        /* <-
        AnotherVO avo = new AnotherVO();
        avo.setStringValue("second");
        avo.setBooleanValue(true);
        ksession.insert(avo);
        */
        
        ksession.setGlobal("logger", logger);
        ksession.fireAllRules();
        ksession.getObjects().stream().filter(o -> o instanceof RoolVO).forEach(o -> {
            assertEquals("Done.", ((RoolVO) o).getStringValue());
            logger.info("Transformed value: " + vo.getStringValue());
        });
        
        // NOTE: <- krLogger.close();
    }

    private static void assertEquals(String s, String stringValue) {
        if (!s.equals(stringValue)) throw new IllegalArgumentException("occurred assertEquals violation..");
    }
}
