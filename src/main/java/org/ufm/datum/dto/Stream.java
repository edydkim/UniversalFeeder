package org.ufm.datum.dto;

import javax.jms.TextMessage;


public class Stream {

    private TextMessage textMessage;

    public TextMessage getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(TextMessage textMessage) {
        this.textMessage = textMessage;
    }
}
