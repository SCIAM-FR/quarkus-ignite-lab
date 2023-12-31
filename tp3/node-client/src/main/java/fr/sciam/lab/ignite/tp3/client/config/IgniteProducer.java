package fr.sciam.lab.ignite.tp3.client.config;


import io.quarkus.runtime.configuration.ProfileManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.util.Map;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class IgniteProducer {
    @ConfigProperty(name = "quarkus.http.port")
    Integer httpPort;

    @Produces
    @ApplicationScoped
    public Ignite ignite(IgniteConfiguration igniteConfiguration) {
        return Ignition.start(igniteConfiguration);
    }

    @Produces
    @ApplicationScoped
    public IgniteConfiguration igniteConfiguration() {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setUserAttributes(Map.of("nodeType", "client"));
        igniteConfiguration.setClientMode(true);
        igniteConfiguration.setUserAttributes(Map.of("httpPort", httpPort.toString()));
        igniteConfiguration.setClassLoader(Thread.currentThread().getContextClassLoader());
        igniteConfiguration.setPeerClassLoadingEnabled(true);
        if (ProfileManager.getLaunchMode().isDevOrTest()) {
            String workDirPath = new File(".", "target/ignite").getAbsolutePath();
            log.info("Ignite working dir set to {}", workDirPath);
            igniteConfiguration.setWorkDirectory(workDirPath);
        }
        return igniteConfiguration;
    }

    public void disposeIgniteClient(@Disposes Ignite ignite) {
        ignite.close();
    }
}
