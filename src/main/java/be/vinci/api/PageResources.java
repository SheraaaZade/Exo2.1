package be.vinci.api;

import be.vinci.api.filters.AnonymousOrAuthorize;
import be.vinci.api.filters.Authorize;
import be.vinci.domain.Page;
import be.vinci.domain.User;
import be.vinci.services.PageDataService;
import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Singleton
@Path("pages")
public class PageResources {
    private PageDataService myPageDataService = new PageDataService();
    @AnonymousOrAuthorize
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Page> getAll(@Context ContainerRequestContext request) {
        User authenticatedUser = (User) request.getProperty("user");
        if (authenticatedUser == null)
            return myPageDataService.getAll();
        return myPageDataService.getAll(authenticatedUser);
    }

    @AnonymousOrAuthorize
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Page getOne(@PathParam("id") int id, @Context ContainerRequestContext request) {
        User authenticatedUser = (User) request.getProperty("user");
        Page pageFound = null;
        if (authenticatedUser == null)
            pageFound = myPageDataService.getOne(id);
        else
            pageFound = myPageDataService.getOne(id, authenticatedUser);

        if (pageFound == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Ressource not found").type("text/plain").build());
        return pageFound;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Authorize
    public Page createOne(Page page, @Context ContainerRequestContext request) {
        if (page == null || page.getTitre() == null || page.getTitre().isBlank()
                || page.getContenu() == null || page.getContenu().isBlank()
                || page.getStatut() == null || page.getStatut().isBlank()
                || page.getURI() == null || page.getURI().isBlank()) {
            throw new WebApplicationException("Lacks of mandatory info", Response.Status.BAD_REQUEST);
        }
        User authenticatedUser = (User) request.getProperty("user");
        return myPageDataService.createOne(page, authenticatedUser);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Authorize
    public Page deleteOne(@PathParam("id") int id, @Context ContainerRequestContext request) {
        if (id == 0)
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST).entity("Lacks of mandatory id info")
                            .type("text/plain").build());

        User authenticatedUser = (User) request.getProperty("user");

        Page pageToDelete = null;
        try {
            pageToDelete = myPageDataService.deleteOne(id, authenticatedUser);
        } catch (Exception e) {
            throw new WebApplicationException("You are not the author", e, Response.Status.FORBIDDEN);
        }
        if (pageToDelete == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Ressource not found").type("text/plain").build());
        return pageToDelete;
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Authorize
    public Page updateOne(Page page, @PathParam("id") int id, @Context ContainerRequestContext request) {
        if (id == 0 || page == null || page.getTitre() == null || page.getTitre().isBlank()
                || page.getContenu() == null || page.getContenu().isBlank()
                || page.getStatut() == null
                || page.getURI() == null || page.getURI().isBlank())
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST).entity("Lacks of mandatory info")
                            .type("text/plain").build());
        User authenticatedPage = (User) request.getProperty("user");
        Page pageToUpdate = null;
        try {
            pageToUpdate = myPageDataService.updateOne(page, id, authenticatedPage);
        } catch (Exception e) {
            throw new WebApplicationException("You are not the author", Response.Status.FORBIDDEN);
        }
        if (pageToUpdate == null)
            throw new WebApplicationException(
                    Response.status(Response.Status.NOT_FOUND)
                            .entity("Not found")
                            .type("text/plain")
                            .build());

        return pageToUpdate;
    }

}

