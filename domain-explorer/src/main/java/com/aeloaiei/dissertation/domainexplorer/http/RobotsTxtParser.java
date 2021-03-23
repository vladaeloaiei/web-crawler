package com.aeloaiei.dissertation.domainexplorer.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Scanner;

import static java.util.Objects.nonNull;

@Component
public class RobotsTxtParser {
    private static final Logger LOGGER = LogManager.getLogger(RobotsTxtParser.class);

    public RobotsPolicy parse(RawWebResource robotsTxt, String userAgent) {
        LOGGER.debug("Parsing robots.txt for domain: " + robotsTxt.getUrl().getDomain() + " location: " + robotsTxt.getUrl().getLocation());
        RobotsPolicy globalPolicy = null;
        RobotsPolicy identityPolicy = null;

        try (Scanner robotsTxtContent = new Scanner(robotsTxt.getContent())) {
            String line;
            /* Read file line by line */
            while (robotsTxtContent.hasNextLine()) {
                line = robotsTxtContent.nextLine();

                if (line.equals("User-agent: *")) {
                    globalPolicy = getPolicy(robotsTxtContent, userAgent);
                } else if (line.equals("User-agent: " + userAgent)) {
                    identityPolicy = getPolicy(robotsTxtContent, userAgent);
                    /* Ignore next lines */
                    break;
                }
            }
        }

        if (nonNull(identityPolicy)) {
            return identityPolicy;
        }

        if (nonNull(globalPolicy)) {
            return globalPolicy;
        }

        return new RobotsPolicy(userAgent);
    }

    private RobotsPolicy getPolicy(Scanner robotsTxtContent, String userAgent) {
        RobotsPolicy policy = new RobotsPolicy(userAgent);
        String line;

        while (robotsTxtContent.hasNextLine()) {
            line = robotsTxtContent.nextLine();

            if (line.contains("Allow: ")) {
                policy.getAllow().add(line.substring("Allow: ".length()));
            } else if (line.contains("Disallow: ")) {
                policy.getDisallow().add(line.substring("Disallow: ".length()));
            } else if (line.equals("")) {
                /* New line. No more info for this robot name */
                break;
            }
        }

        return policy;
    }
}
