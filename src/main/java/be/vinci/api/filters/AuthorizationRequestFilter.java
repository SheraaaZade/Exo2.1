package be.vinci.api.filters;

import be.vinci.domain.Page;
import be.vinci.services.PageDataService;
import be.vinci.services.utils.Config;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Singleton
@Provider
@Authorize
public class AuthorizationRequestFilter implements ContainerRequestFilter {
    private final Algorithm jwtAlgorithm = Algorithm.HMAC256(Config.getProperty("JWTSecret"));
    private  final JWTVerifier jwtVerifier = JWT.require(this.jwtAlgorithm).withIssuer("auths0").build();
    private PageDataService myPageDataService = new PageDataService();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException{
        String token = requestContext.getHeaderString("Authorization");
        if (token == null) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("A token is needed to access this resource").build());
        } else {
            DecodedJWT decodedToken = null;
            try{
                decodedToken = this.jwtVerifier.verify(token);
            } catch (Exception e ){
                throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Malformed token : " + e.getMessage()).type("text/plain").build());
            }
            Page authenticatedUser = myPageDataService.getOne(decodedToken.getClaim("user").asInt());
            if(authenticatedUser == null){
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                        .entity("You are forbidden to access this resource").build());
            }
            requestContext.setProperty("user", authenticatedUser);
        }
    }
}