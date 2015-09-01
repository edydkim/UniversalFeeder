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
public class RulesEngineRunner {
    private static final Logger logger = LoggerFactory.getLogger(RulesEngineRunner.class);

    private static KnowledgeBase kbase;
    
    // Thread-Safe
    private RulesEngineRunner() {}

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
        if (kbase == null)  setup();

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        // NOTE: <- KnowledgeRuntimeLogger krLogger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "basic");

        ksession.insert(vo);

        ksession.setGlobal("logger", logger);
        ksession.fireAllRules();

        // <- logger.close();

        return ksession.getObjects();
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
