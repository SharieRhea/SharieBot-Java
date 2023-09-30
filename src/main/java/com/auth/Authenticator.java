package com.auth;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;

public class Authenticator {
    private final OAuth2Credential credential;
    private URI authURI;

    /**
     * Constructor for Authenticator. Tries to build an OAuth credential using the user's access token,
     * throws if no accessToken.txt file is found or if there is an IOException.
     * @throws IOException Some IO error.
     */
    public Authenticator() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/com/auth/accessToken.txt"))) {
            credential = new OAuth2Credential("twitch", reader.readLine());
        }
    }

    /**
     * Accessor for credential.
     * @return Credential.
     */
    public OAuth2Credential getCredential() {
        return credential;
    }

    /**
     * Accessor for authURI.
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
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/com/auth/uriInfo.txt"))) {
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
