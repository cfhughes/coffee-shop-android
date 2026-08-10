package edu.cnm.deepdive.coffeeshop.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "visit")
public class Visit {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "shop_id", nullable = false)
  private Shop shop;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "profile_id", nullable = false)
  private Profile profile;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private OffsetDateTime createdAt;

  @OneToMany(mappedBy = "visit")
  private Set<Rating> ratings = new LinkedHashSet<>();

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Shop getShop() {
    return shop;
  }

  public void setShop(Shop shop) {
    this.shop = shop;
  }

  public Profile getProfile() {
    return profile;
  }

  public void setProfile(Profile profile) {
    this.profile = profile;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Set<Rating> getRatings() {
    return ratings;
  }

  public void setRatings(Set<Rating> ratings) {
    this.ratings = ratings;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || Hibernate.getClass(this) != Hibernate.getClass(obj)) {
      return false;
    }
    Visit other = (Visit) obj;
    return id != null && Objects.equals(id, other.id);
  }

  @Override
  public int hashCode() {
    return Hibernate.getClass(this).hashCode();
  }

  @Override
  public String toString() {
    return "Visit{id=" + id + ", createdAt=" + createdAt + "}";
  }
}
