package org.ufm.drools;

import org.ufm.enums.TransactionEnum;
import org.joox.JOOX;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.annotation.concurrent.ThreadSafe;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.joox.JOOX.$;

/**
 * Created by edydkim on 2015/08.
 */
@ThreadSafe
public class XMLBasedVO<T> implements VO<T> {
    // XML Message
    private String message;

    // Ref
    private String ref;

    // DOM
    private Document document;

    // Transaction
    private Transaction transaction;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public String getRef() {
        return this.ref;
    }

    // Thread-Safe
    public XMLBasedVO(String message) throws IOException, SAXException {
        this.message = message;

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(message.getBytes());
        try {
            this.document = JOOX.builder().parse(byteArrayInputStream);
        } finally {
            byteArrayInputStream.close();
        }
        
        this.transaction = new Transaction(document);

        // Init for filtering
        this.ref = this.getTransaction().getSystem();
    }

    public class Transaction {
        private String system;
        private String user;
        private String version;
        
        private Entity entity;
        private Price price;
        private Charge charge;

        // TODO: add more, see the matrix for further req..

        public Transaction(Document document) {
            this.system = $(document).filter(this.getClass().getSimpleName()).attr(TransactionEnum.TRANSACTION_SYSTEM.attribute());
            this.user = $(document).filter(this.getClass().getSimpleName()).attr(TransactionEnum.TRANSACTION_USER.attribute());
            this.version = $(document).filter(this.getClass().getSimpleName()).attr(TransactionEnum.TRANSACTION_VERSION.attribute());

            this.entity = new Entity(document);
            this.price = new Price(document);
            this.charge = new Charge(document);
        }

        public String getSystem() {
            return system;
        }

        public void setSystem(String system) {
            this.system = system;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Entity getEntity() {
            return entity;
        }

        public void setEntity(Entity entity) {
            this.entity = entity;
        }

        public Price getPrice() {
            return price;
        }

        public void setPrice(Price price) {
            this.price = price;
        }

        public Charge getCharge() {
            return charge;
        }

        public void setCharge(Charge charge) {
            this.charge = charge;
        }

        public class Entity {
            private String v;

            public Entity(Document document) {
                this.v = $(document).find(this.getClass().getSimpleName()).attr(TransactionEnum.ENTITY_V.attribute());
            }

            public String getV() {
                return v;
            }

            public void setV(String v) {
                this.v = v;
            }
        }

        public class Price {
            private String qualifier;
            private String type;
            private String value;

            public Price(Document document) {
                this.qualifier = $(document).find(this.getClass().getSimpleName()).attr(TransactionEnum.PRICE_QUALIFIER.attribute());
                this.type = $(document).find(this.getClass().getSimpleName()).attr(TransactionEnum.PRICE_TYPE.attribute());
                this.value = $(document).find(this.getClass().getSimpleName()).attr(TransactionEnum.PRICE_VALUE.attribute());
            }

            public String getQualifier() {
                return qualifier;
            }

            public void setQualifier(String qualifier) {
                this.qualifier = qualifier;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public class Charge {
            private Set<String> types;
            private Collection<BigDecimal> amounts;

            private Map<String, String> chargeMap = new HashMap<>();

            public Charge(Document document) {
                for (Element element : $(document).find(this.getClass().getSimpleName())) {
                    chargeMap.put(element.getAttribute(TransactionEnum.CHARGE_TYPE.attribute()), element.getAttribute(TransactionEnum.CHARGE_AMOUNT.attribute()));
                }
            }

            public Set<String> getTypes() {
                return chargeMap.keySet();
            }

            public void setTypes(Set<String> types) {
                this.types = types;
            }

            public Collection<BigDecimal> getAmounts() {
                Collection<BigDecimal> amounts = new ArrayList<>();
                chargeMap.values().forEach((String v) -> amounts.add(v == null ? new BigDecimal(0) : new BigDecimal(v)));
                return amounts;
            }

            public void setAmounts(Collection<BigDecimal> amounts) {
                this.amounts = amounts;
            }
        }
    }
}
