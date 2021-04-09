package com.aeloaiei.dissertation.domainexplorer.http;

import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

public class AppenderNodeVisitor implements NodeVisitor {
    private StringBuilder stringBuilder;

    public AppenderNodeVisitor() {
        stringBuilder = new StringBuilder();
    }

    @Override
    public void head(Node node, int depth) {
        if (node instanceof TextNode) {
            TextNode textNode = (TextNode) node;
            stringBuilder.append(textNode.getWholeText());
            stringBuilder.append(" ");
        }
    }

    @Override
    public void tail(Node node, int depth) {
    }

    public String reset() {
        String content = stringBuilder.toString();

        stringBuilder = new StringBuilder();
        return content;
    }
}
