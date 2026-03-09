package io.truthencode.games.dragonheir.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Hero extends PanacheEntity {

    private String name;
    private int hp;
    private int enlightenment;

    private int accuracy;
    private int critRate;
    private int critDamage;
    private int health;
    private int defense;
    private int attack;
    private double atkInterval;
    private int mastery;
    private int resistance;
    private int skillHaste;
    private String imageUrl;

}