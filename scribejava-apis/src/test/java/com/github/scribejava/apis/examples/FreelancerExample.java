package com.github.scribejava.apis.examples;

import java.util.Scanner;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.apis.FreelancerApi;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.SignatureType;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;

public abstract class FreelancerExample {

    private static final String NETWORK_NAME = "Freelancer";
    private static final String AUTHORIZE_URL
            = "http://www.sandbox.freelancer.com/users/api-token/auth.php?oauth_token=";
    private static final String PROTECTED_RESOURCE_URL = "http://api.sandbox.freelancer.com/Job/getJobList.json";
    private static final String SCOPE = "http://api.sandbox.freelancer.com";

    public static void main(final String... args) {
        final OAuthService service = new ServiceBuilder()
                .provider(FreelancerApi.Sandbox.instance())
                .signatureType(SignatureType.QueryString)
                .apiKey("your client id")
                .apiSecret("your client secret")
                .scope(SCOPE)
                .build();
        final Scanner in = new Scanner(System.in);

        System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
        System.out.println();

        // Obtain the Request Token
        System.out.println("Fetching the Request Token...");
        final Token requestToken = service.getRequestToken();
        System.out.println("Got the Request Token!");
        System.out.println("(if your curious it looks like this: " + requestToken + " )");
        System.out.println();

        System.out.println("Now go and authorize ScribeJava here:");
        System.out.println(AUTHORIZE_URL + requestToken.getToken());
        System.out.println("And paste the verifier here");
        System.out.print(">>");
        final Verifier verifier = new Verifier(in.nextLine());
        System.out.println();

        // Trade the Request Token and Verfier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        final Token accessToken = service.getAccessToken(requestToken, verifier);
        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: " + accessToken + " )");
        System.out.println();

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL, service);
        service.signRequest(accessToken, request);
        request.addHeader("GData-Version", "3.0");
        final Response response = request.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println(response.getCode());
        System.out.println(response.getBody());

        System.out.println();
        System.out.println("Thats it man! Go and build something awesome with ScribeJava! :)");
    }
}