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
        
        FactHandle factHandle = null;
        try {
            // NOTE: multiple inserting may cause the Drools Rule parsing performance low
            factHandle = ksession.insert(vo);
            
            // logger
            ksession.setGlobal("logger", logger);
        
            ksession.fireAllRules();
            
            collection.addAll(ksession.getObjects());
        } finally {
            // IMPORTANT: PREVENTION FOR LEAK ISSUE
            if (factHandle != null) ksession.retract(factHandle);
            // NOTE: when inserting multiple vo to session
            // ksession.getFactHandles().forEach(ksession::retract);
            ksession.dispose();
        }

        // <- logger.close();

        return collection;
    }

    public static void main(String[] args) {
        Collection<Object> objects = RulesEngineRunner.getInstance().doFact(new BasicVO(""));
        // NOTE: In current version facts is not sharable, but if facts is volatile so that need to CompareAndGet  convert to Atomic - AtomicReference<Collection<Object>> facts = new AtomicReference<Collection<Object>>();
        /* e.g., 
        try {
            facts = ulesEngineRunner.getInstance().doFact(new BasicVO(""));
            ...
        } catch (Exception e) {
            // Fault-Tolerance 
            throw new UserDefinedException("Keep going on...", e);
        }
        */

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
