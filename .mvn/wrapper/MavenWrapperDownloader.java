import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Properties;

public class MavenWrapperDownloader {
    private static final String WRAPPER_VERSION = '0.7.6';
    private static final URL DEFAULT_DOWNLOAD_URL = new URL('https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/' + WRAPPER_VERSION + '/maven-wrapper-' + WRAPPER_VERSION + '.jar');
    
    public static void main(String[] args) throws IOException {
        File mavenWrapperPropertyFile = new File('.mvn/wrapper/maven-wrapper.properties');
        if (mavenWrapperPropertyFile.exists()) {
            FileInputStream mavenWrapperPropertyFileInputStream = new FileInputStream(mavenWrapperPropertyFile);
            Properties mavenWrapperProperties = new Properties();
            mavenWrapperProperties.load(mavenWrapperPropertyFileInputStream);
            mavenWrapperPropertyFileInputStream.close();
        }
        URL wrapperUrl = DEFAULT_DOWNLOAD_URL;
        File wrapperJar = new File('.mvn/wrapper/maven-wrapper.jar');
        downloadFileFromURL(wrapperUrl.toString(), wrapperJar.getAbsolutePath());
    }
    
    private static void downloadFileFromURL(String urlString, String filename) throws IOException {
        URL website = new URL(urlString);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(filename);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }
}
