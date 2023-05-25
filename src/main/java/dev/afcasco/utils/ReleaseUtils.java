package dev.afcasco.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;


@Component
public class ReleaseUtils {

    private final Logger LOG = LoggerFactory.getLogger(ReleaseUtils.class);
    private final Environment env;
    private final JsonNode rootNode;
    private final String releaseName;
    private final URL downloadLink;

    public ReleaseUtils(Environment env) throws IOException, URISyntaxException {
        this.env = env;
        String latest = "https://api.github.com/repos/" + env.getProperty("repo.owner") +
                "/" + env.getProperty("repo.name") + "/releases/latest";
        URL url = new URI(latest).toURL();
        this.rootNode = retrieveLatestReleaseData(url);
        this.releaseName = getLatestReleaseFileName();
        this.downloadLink = getLatestReleaseLink();
    }


    private JsonNode retrieveLatestReleaseData(URL url) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(url);
    }


    private URL getLatestReleaseLink() throws URISyntaxException, IOException {
        String latestReleaseDownloadLink = rootNode.get("assets").get(0).get("browser_download_url").toString().replace("\"", "");
        return new URI(latestReleaseDownloadLink).toURL();
    }

    private String getLatestReleaseFileName() {
        return rootNode.get("assets").get(0).get("name").toString().replace("\"", "");
    }

    public void downloadRelease() throws IOException {
        LOG.info("Downloading release: " + releaseName +"...");
        File file = new File(releaseName);
        FileUtils.copyURLToFile(downloadLink, file);
        LOG.info("Download complete");
    }

    public void moveAppimageToFolder() throws IOException {
        LOG.info("Moving AppImage to: " + env.getProperty("appimage.folder"));
        Path path = Path.of(env.getProperty("appimage.folder") + File.separator + releaseName);
        Files.move(Paths.get("./" + releaseName), path, StandardCopyOption.REPLACE_EXISTING);
    }

    public boolean latestReleaseFound() {
        File file = new File(env.getProperty("appimage.folder") + File.separator + releaseName);
        return (file.exists() && !file.isDirectory());
    }

    public Path getDownloadPath() {
        return Path.of(Objects.requireNonNull(env.getProperty("appimage.folder")));
    }
}