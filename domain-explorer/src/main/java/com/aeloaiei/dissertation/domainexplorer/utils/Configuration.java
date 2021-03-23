package com.aeloaiei.dissertation.domainexplorer.utils;

public class Configuration {
    public static final String USER_AGENT = "CasualBot";
    public static final int METADATA_UPDATE_WINDOW_IN_MILLISECONDS = 15_000;
    public static final int RESOURCE_REQUEST_DELAY_IN_MILLISECONDS = 3_000;
    public static final double PERCENTAGE_OF_RESOURCES_TO_CRAWL = 0.01D; //crawl 1% of a domain in an iteration
    public static final int CONCURRENT_DOMAINS_EXPLORER_COUNT = 3;

    private Configuration() {
    }
}
