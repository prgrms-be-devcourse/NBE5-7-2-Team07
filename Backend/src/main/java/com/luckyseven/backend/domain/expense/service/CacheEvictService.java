package com.luckyseven.backend.domain.expense.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheEvictService {

  private final CacheManager cacheManager;

  public void evictByPrefix(String cacheName, String prefix) {
    Cache cache = cacheManager.getCache(cacheName);
    if (!(cache instanceof CaffeineCache caffeineCache)) {
      return;
    }
    com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
    nativeCache.asMap().keySet().stream()
        .map(Object::toString)
        .filter(key -> key.startsWith(prefix))
        .forEach(nativeCache::invalidate);
  }
}
