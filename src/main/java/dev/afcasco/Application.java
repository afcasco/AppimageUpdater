package dev.afcasco;

import dev.afcasco.utils.ReleaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private final Logger LOG = LoggerFactory.getLogger(Application.class);
    private final ReleaseUtils utils;

    public Application(ReleaseUtils utils) {
        this.utils = utils;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("Running: AppImage updater");
        if(!Files.exists(utils.getDownloadPath()) || !Files.isDirectory(utils.getDownloadPath())){
            LOG.info("Specify a valid folder, or use the default one (home/user)");
        } else {
            boolean latestReleaseFound = utils.latestReleaseFound();
            if (!latestReleaseFound) {
                utils.downloadRelease();
                utils.moveAppimageToFolder();
                LOG.info("All done, enjoy!");
            } else {
                LOG.info("Latest release found, aborting update");
            }
        }
    }
}