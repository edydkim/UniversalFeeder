package org.ufm.enums;

/**
 * Created by edydkim on 2015/08.
 */
public enum TransactionEnum {
    TRANSACTION("Transaction", "")
    , TRANSACTION_SYSTEM("Transaction", "system")
    , TRANSACTION_USER("Transaction", "user")
    , TRANSACTION_VERSION("Transaction", "version")
    , ENTITY_V("Entity", "v")
    , PRICE_QUALIFIER("Price", "qualifier")
    , PRICE_TYPE("Price", "type")
    , PRICE_VALUE("Price", "value")
    , CHARGE_TYPE("Charge", "type")
    , CHARGE_AMOUNT("Charge", "amount")
    ;

    private String element;

    private String attribute;

    TransactionEnum(String element, String attribute) {
        this.element = element;
        this.attribute = attribute;
    }

    public String element() {
        return this.element;
    }
    public String attribute() {
        return this.attribute;
    }
}
