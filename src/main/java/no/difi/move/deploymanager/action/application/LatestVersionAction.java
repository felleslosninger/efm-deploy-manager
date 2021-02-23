package no.difi.move.deploymanager.action.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.difi.move.deploymanager.action.DeployActionException;
import no.difi.move.deploymanager.config.DeployManagerProperties;
import no.difi.move.deploymanager.domain.application.Application;
import no.difi.move.deploymanager.domain.application.ApplicationMetadata;
import no.difi.move.deploymanager.service.config.RefreshService;
import org.springframework.stereotype.Component;

/**
 * @author Nikolai Luthman <nikolai dot luthman at inmeta dot no>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LatestVersionAction implements ApplicationAction {

    private final DeployManagerProperties properties;
    private final RefreshService refreshService;

    @Override
    public Application apply(Application application) {
        log.debug("Running LatestVersionAction.");
        try {
            refreshService.refreshConfig();
            log.info("Getting latest version");
            application.setLatest(
                    new ApplicationMetadata()
                            .setVersion(properties.getIntegrasjonspunkt().getLatestVersion())
                            .setRepositoryId(properties.getRepository())
            );
            return application;
        } catch (Exception ex) {
            throw new DeployActionException("Error getting latest version", ex);
        }
    }
}
