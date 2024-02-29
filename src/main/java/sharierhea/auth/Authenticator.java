package sharierhea.auth;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;

/**
 * An authentication class to handle authentication between Twitch and the bot. Although this class generates a URI,
 * this class does not actually use the URI. The class needs clientID, a list of scopes, and the user's access token
 * (found by visiting the authURI).
 */
public class Authenticator {
    private final OAuth2Credential credential;
    private final OAuth2Credential broadcasterCredential;
    private URI authURI;

    /**
     * Constructor for Authenticator. Tries to build an OAuth credential using the user's access token,
     * throws if no accessToken.txt file is found or if there is an IOException.
     */
    public Authenticator(String broadcasterToken, String botToken) {
        broadcasterCredential = new OAuth2Credential("twitch",broadcasterToken);
        credential = new OAuth2Credential("twitch", botToken);
    }

    /**
     * Accessor for credential.
     * @return Credential.
     */
    public OAuth2Credential getCredential() {
        return credential;
    }

    public OAuth2Credential getBroadcasterCredential() { return broadcasterCredential; }

    /**
     * Accessor for authURI.
     * todo: find a way to integrate this so it is conveniently shown to the user when they don't have a valid token
     * @return authURI
     */
    public URI getAuthURI() {
        return authURI;
    }

    /**
     * Creates the user's unique authorization URI based on their clientID. The access token generated upon redirection
     * includes all the scopes listed.
     * idea: create some interface to autogenerate scopes?
     */
    private void buildAuthURI() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/sharierhea/auth/uriInfo.txt"))) {
            String builder = "https://id.twitch.tv/oauth2/authorize?response_type=token&client_id=" +
                    // Append the user's clientID
                    reader.readLine() +
                    "&redirect_uri=http://localhost:3000&scope=" +
                    // Append the user's desired scope(s)
                    reader.readLine();
            authURI =  URI.create(builder);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
