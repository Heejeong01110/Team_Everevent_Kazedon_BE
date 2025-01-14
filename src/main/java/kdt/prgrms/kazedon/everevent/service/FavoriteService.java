package kdt.prgrms.kazedon.everevent.service;

import kdt.prgrms.kazedon.everevent.domain.favorite.Favorite;
import kdt.prgrms.kazedon.everevent.domain.favorite.dto.response.SimpleMarketFavorite;
import kdt.prgrms.kazedon.everevent.domain.favorite.dto.response.SimpleMarketFavoriteReadResponse;
import kdt.prgrms.kazedon.everevent.domain.favorite.repository.FavoriteRepository;
import kdt.prgrms.kazedon.everevent.domain.market.Market;
import kdt.prgrms.kazedon.everevent.domain.market.repository.MarketRepository;
import kdt.prgrms.kazedon.everevent.domain.user.User;
import kdt.prgrms.kazedon.everevent.exception.AlreadyFavoritedException;
import kdt.prgrms.kazedon.everevent.exception.ErrorMessage;
import kdt.prgrms.kazedon.everevent.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

  private final FavoriteRepository favoriteRepository;
  private final MarketRepository marketRepository;

  @Transactional
  public Long addFavorite(User user, Long marketId){
    Market market = marketRepository.findById(marketId)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.MARKET_NOT_FOUNDED, marketId));
    if(favoriteRepository.existsFavoriteByUserIdAndMarketId(user.getId(), marketId)){
      throw new AlreadyFavoritedException(ErrorMessage.DUPLICATE_FAVORITE_MARKET,user.getId());
    }
    Favorite favorite = Favorite.builder().user(user).market(market).build();
    favoriteRepository.save(favorite);
    market.plusOneFavorite();
    return favorite.getId();
  }

  @Transactional
  public Long deleteFavorite(User user, Long marketId) {
    Market market = marketRepository.findById(marketId)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.MARKET_NOT_FOUNDED, marketId));
    Favorite favorite = favoriteRepository.findByUserIdAndMarketId(user.getId(), marketId)
        .orElseThrow(
            () -> new AlreadyFavoritedException(ErrorMessage.FAVORITE_NOT_FOUNDED, marketId));
    if (!favoriteRepository.existsFavoriteByUserIdAndMarketId(user.getId(), marketId)) {
      throw new AlreadyFavoritedException(ErrorMessage.DUPLICATE_NOT_FAVORITE_MARKET,
          favorite.getId());
    }
    market.minusOneFavorite();
    favoriteRepository.deleteById(favorite.getId());
    return favorite.getId();

  }

  @Transactional(readOnly = true)
  public SimpleMarketFavoriteReadResponse getFavorites(Long memberId, Pageable pageable) {
    Page<SimpleMarketFavorite> simpleMarketFavorites = favoriteRepository
        .findSimpleFavoriteByUserId(memberId, pageable);

    return SimpleMarketFavoriteReadResponse.builder()
        .markets(simpleMarketFavorites)
        .build();
  }
}
