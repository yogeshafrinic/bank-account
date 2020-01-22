package net.bank.account;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BuildInfoActuator implements InfoContributor {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${git.build.version}")
    private String buildVersion;

    @Value("${git.build.time}")
    private String buildTime;

    @Value("${git.branch}")
    private String branch;

    @Value("${git.tags}")
    private String tags;

    @Value("${git.closest.tag.name}")
    private String closestTag;

    @Value("${git.commit.id.abbrev}")
    private String commitId;

    @Value("${git.commit.message.short}")
    private String commitMessage;

    @Value("${git.commit.time}")
    private String commitTime;

    public Map<String, String> getBuildInfo() {
        Map<String, String> result = new HashMap<>();
        result.put("Application name", applicationName);
        result.put("Build version", buildVersion);
        result.put("Build time", buildTime);
        result.put("SVC branch", branch);
        result.put("SVC tags", tags);
        result.put("SVC closest tag", closestTag);
        result.put("Commit id", commitId);
        result.put("Commit time", commitTime);
        result.put("Commit message",commitMessage);
        return result;
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("build", getBuildInfo());
    }
}
