package be.vinci.services;

import be.vinci.domain.Page;
import be.vinci.domain.User;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;

public class PageDataService {
  private static final String COLLECTION_NAME = "pages";
  private static Json<Page> jsonDB = new Json<>(Page.class);

  public List<Page> getAll(){
    var pages = jsonDB.parse(COLLECTION_NAME);
    return pages.stream()
            .filter(item -> item.getStatut().contentEquals("published"))
            .toList();
  }
  public List<Page> getAll(User authenticatedUser){
    var pages = jsonDB.parse(COLLECTION_NAME);
    return pages.stream().filter(item -> item.getStatut().contentEquals("published")
    || item.getAuthorId() == authenticatedUser.getId()).toList();
  }

  public Page getOne(int id){
    var pages = jsonDB.parse(COLLECTION_NAME);
    return pages.stream().filter(page -> (page.getId() == id)
            && (page.getStatut().contentEquals("published")))
            .findAny().orElse(null);
  }

  public Page getOne(int id, User authenticatedUser) {
    var pages = jsonDB.parse(COLLECTION_NAME);
    return pages.stream().filter(item -> (item.getId() == id)
                    && (item.getStatut().contentEquals("published")
                    || item.getAuthorId() == authenticatedUser.getId()))
            .findAny().orElse(null);
  }

  public Page createOne (Page page, User authencatedUser){
    var pages = jsonDB.parse(COLLECTION_NAME);
    page.setId(nextPageId());
    page.setTitre(StringEscapeUtils.escapeHtml4(page.getTitre()));
    page.setContenu(StringEscapeUtils.escapeHtml4(page.getContenu()));
    page.setURI(StringEscapeUtils.escapeHtml4(page.getURI()));
    page.setAuthorId(authencatedUser.getId());
    pages.add(page);
    jsonDB.serialize(pages, COLLECTION_NAME);
    return page;
  }

  private int nextPageId() {
    var pages = jsonDB.parse(COLLECTION_NAME);
    if(pages.size() == 0)
      return 1;
    return pages.get(pages.size()-1).getId() + 1;
  }

  public Page deleteOne(int id, User authenticatedUser){
    Page pageToDelete = getOne(id, authenticatedUser);
    var pages = jsonDB.parse(COLLECTION_NAME);
    if(pageToDelete == null)
      return null;
    if(pageToDelete.getAuthorId() != authenticatedUser.getId())
      throw new IllegalArgumentException("Forbidden");
    pages.remove(pageToDelete);
    jsonDB.serialize(pages, COLLECTION_NAME);
    return pageToDelete;
  }

  public Page updateOne(Page page, int id, User authenticatedUser){
    Page pageToUpdate = getOne(id, authenticatedUser);
    var pages = jsonDB.parse(COLLECTION_NAME);
    if(pageToUpdate == null)
      return null;
    if(pageToUpdate.getAuthorId() != authenticatedUser.getId())
      throw new IllegalArgumentException("Forbidden");

    pageToUpdate.setId(id);
    if(pageToUpdate == null)
      return null;
    if (page.getTitre() != null) {
      pageToUpdate.setTitre(StringEscapeUtils.escapeHtml4(page.getTitre()));
    }
    if (page.getURI() != null) {
      pageToUpdate.setURI(StringEscapeUtils.escapeHtml4(page.getURI()));
    }
    if (page.getContenu() != null) {
      pageToUpdate.setContenu(StringEscapeUtils.escapeHtml4(page.getContenu()));
    }
    if (page.getAuthorId() != 0) {
      pageToUpdate.setAuthorId(page.getAuthorId());
    }
    if (page.getStatut() != null) {
      pageToUpdate.setStatut(page.getStatut());
    }
    pages.remove(page);
    pages.add(pageToUpdate);
    jsonDB.serialize(pages, COLLECTION_NAME);
    return pageToUpdate;
  }
}
