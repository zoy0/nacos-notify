package com.lzy.nacosnotify.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class FeishuCard {

    public static final String GREEN = "green";

    public static final String RED = "red";

    private String msg_type;

    private Card card;

    @Data
    @AllArgsConstructor
    class Text {

        private String content;
        private String tag;
    }

    @Data
    @AllArgsConstructor
    class Elements {

        private String tag;
        private Text text;
    }

    @Data
    @AllArgsConstructor
    class Title {

        private String content;
        private String tag;
    }

    @Data
    @AllArgsConstructor
    class Header {

        private String template;
        private Title title;

    }

    @Data
    class Card {

        private List<Elements> elements;
        private Header header;

    }

    public FeishuCard generateCard(String header,String message){
        FeishuCard feishuCard = new FeishuCard();
        feishuCard.setMsg_type("interactive");
        Card card = new Card();
        card.setHeader(new Header(GREEN,new Title(header,"plain_text")));
        List<Elements> list = new LinkedList<>();
        list.add(new Elements("div",new Text(message,"lark_md")));
        card.setElements(list);
        feishuCard.setCard(card);
        return feishuCard;
    }

    public void setHeader(String header){
        this.card.header.title.content = header;
    }

    public void setCardHeaderColor(String color){
        this.card.header.template=color;
    }
}
