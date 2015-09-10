package org.ufm.drools;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.*;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by edydkim on 2015/08.
 */
@ThreadSafe 
public class RulesEngineRunner {
    private static final Logger logger = LoggerFactory.getLogger(RulesEngineRunner.class);

    private static KnowledgeBase kbase;
    
    // Singleton
    private RulesEngineRunner() {
        setup();
    }

    private static class RuleEngineRunnerHolder {
        private static final RulesEngineRunner instance = new RulesEngineRunner();
    }

    public static RulesEngineRunner getInstance() {
        return RuleEngineRunnerHolder.instance;
    }

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

    public Collection<Object> doFact(VO vo) throws IOException, SAXException {
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        Collection<Object> collection = new ArrayList<>();
        
        // NOTE: <- KnowledgeRuntimeLogger krLogger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "basic");
        
        try {
            ksession.insert(vo);

            ksession.setGlobal("logger", logger);
            ksession.fireAllRules();
            
            collection.addAll(ksession.getObjects());
        } finally {
            ksession.dispose();
        }

        // <- logger.close();

        return collection;
    }

    public static void main(String[] args) {
        Collection<Object> objects = RulesEngineRunner.getInstance().doFact(new BasicVO(""));

        ksession.getObjects().stream().filter(o -> o instanceof BasicVO).forEach(o -> {
            assertEquals("Done.", ((BasicVO) o).getStringValue());
            logger.info("Transformed value: " + vo.getStringValue());
        });

        /* NOTE: for multiple VO
        for (Object o: objects) {
            if (o instanceof BasicVO) {
                logger.debug("Basic-Transaction-System Transformed value: " + ((BasicVO) o).getTransaction().getSystem());
            } else if (o instanceof AnotherVO) {
                logger.debug("Second Transformed value: " + ((AnotherVO) o).getStringValue());
            }
        }
        */
    }

    private static void assertEquals(String s, String stringValue) {
        if (!s.equals(stringValue)) throw new IllegalArgumentException("occurred assertEquals violation..");
    }
}
