package edu.cnm.deepdive.coffeeshop.controller;

import edu.cnm.deepdive.coffeeshop.controller.api.FavoriteApi;
import edu.cnm.deepdive.coffeeshop.model.dto.FavoriteRequestDto;
import edu.cnm.deepdive.coffeeshop.model.dto.ShopDto;
import edu.cnm.deepdive.coffeeshop.service.ContextProfileService;
import edu.cnm.deepdive.coffeeshop.service.FavoriteService;
import edu.cnm.deepdive.coffeeshop.service.ProfileService;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("service")
public class FavoriteController implements FavoriteApi {

  private final FavoriteService favoriteService;
  private final ContextProfileService contextProfileService;

  public FavoriteController(FavoriteService favoriteService,
      ContextProfileService contextProfileService) {
    this.favoriteService = favoriteService;
    this.contextProfileService = contextProfileService;
  }

  @Override
  public ResponseEntity<ShopDto> createFavorite(FavoriteRequestDto favoriteRequestDto) {
    ShopDto dto = favoriteService.saveFavorite(favoriteRequestDto,
        contextProfileService.getContextProfile());
    URI location = WebMvcLinkBuilder.linkTo(
        WebMvcLinkBuilder.methodOn(ShopController.class).getShopById(dto.getId())).toUri();
    return ResponseEntity.created(location).body(dto);
  }

  @Override
  public ResponseEntity<Void> deleteFavorite(UUID shopId) {
    favoriteService.removeFavorite(shopId, contextProfileService.getContextProfile());
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<List<ShopDto>> listMyFavorites() {
    return ResponseEntity.ok(
        favoriteService.getFavorites(contextProfileService.getContextProfile()));
  }

}
