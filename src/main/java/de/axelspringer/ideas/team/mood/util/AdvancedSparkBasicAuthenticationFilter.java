package de.axelspringer.ideas.team.mood.util;

import com.qmetric.spark.authentication.AuthenticationDetails;
import com.qmetric.spark.authentication.BasicAuthenticationFilter;
import spark.Request;
import spark.Response;

public class AdvancedSparkBasicAuthenticationFilter extends BasicAuthenticationFilter {

    public AdvancedSparkBasicAuthenticationFilter(AuthenticationDetails authenticationDetails) {
        super(authenticationDetails);
    }

    public AdvancedSparkBasicAuthenticationFilter(String path, AuthenticationDetails authenticationDetails) {
        super(path, authenticationDetails);
    }

    @Override
    public void handle(Request request, Response response) {
        String uri = request.uri();
        if (uri.contains("/week-by-secret-key/")) {
            return;
        }

        super.handle(request, response);
    }
}
